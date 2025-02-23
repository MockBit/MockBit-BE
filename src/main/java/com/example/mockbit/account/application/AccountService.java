package com.example.mockbit.account.application;

import com.example.mockbit.account.domain.Account;
import com.example.mockbit.account.domain.Btc;
import com.example.mockbit.account.domain.repository.AccountRepository;
import com.example.mockbit.account.domain.repository.BtcRepository;
import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.order.domain.Order;
import com.example.mockbit.order.domain.OrderResult;
import com.example.mockbit.user.domain.User;
import com.example.mockbit.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final BtcRepository btcRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createAccountForUser(User user) {
        Account account = new Account(user);
        Btc btc = new Btc(user);
        accountRepository.save(account);
        btcRepository.save(btc);
    }

    public Account getAccountByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public Btc getBtcByUserId(Long userId) {
        return btcRepository.findByUserId(userId);
    }

    @Transactional
    public void processMarketOrder(OrderResult orderResult) {
        User user = userRepository.findById(orderResult.getUserId())
                .orElseThrow(() -> new MockBitException(MockbitErrorCode.USER_NOT_FOUND));

        Account account = accountRepository.findByUserId(user.getId());
        Btc btc = btcRepository.findByUserId(user.getId());

        BigDecimal orderPrice = BigDecimal.valueOf(Long.parseLong(orderResult.getOrderPrice()));
        BigDecimal btcPrice = BigDecimal.valueOf(Long.parseLong(orderResult.getPrice()));

        if ("BUY".equalsIgnoreCase(orderResult.getSellOrBuy())) {
            int leverage = orderResult.getLeverage();
            BigDecimal btcAmount = orderPrice.multiply(BigDecimal.valueOf(leverage))
                    .divide(btcPrice, 8, RoundingMode.HALF_UP);

            account.deductBalance(orderPrice);
            buyBtc(btc, btcAmount, btcPrice, leverage, orderResult.getPosition());
        } else if ("SELL".equalsIgnoreCase(orderResult.getSellOrBuy())) {
            BigDecimal btcAmount = orderPrice.divide(btcPrice, 8, RoundingMode.HALF_UP);

            account.refundBalance(orderPrice);
            sellBtc(btc, btcAmount);
        } else {
            throw new MockBitException(MockbitErrorCode.INVALID_ORDER_TYPE);
        }
    }

    @Transactional
    public void processOrder(Long userId, BigDecimal orderPrice) {
        Account account = accountRepository.findByUserId(userId);
        account.deductBalance(orderPrice);
    }

    @Transactional
    public void completeOrder(OrderResult orderResult) {
        processMarketOrder(orderResult);
    }

    @Transactional
    public void cancelOrder(Long userId, BigDecimal orderPrice) {
        Account account = accountRepository.findByUserId(userId);
        account.refundBalance(orderPrice);
    }

    /**
     * 매수 시 평단가 및 평균 레버리지 업데이트
     */
    private void buyBtc(Btc btc, BigDecimal newBtc, BigDecimal newEntryPrice, int newLeverage, String position) {
        BigDecimal oldBalance = btc.getBtcBalance();
        BigDecimal oldEntryPrice = btc.getAvgEntryPrice();
        BigDecimal oldLeverage = btc.getAvgLeverage();

        BigDecimal totalAmountOld = oldBalance.multiply(oldEntryPrice);
        BigDecimal totalAmountNew = newBtc.multiply(newEntryPrice);
        BigDecimal newTotalAmount = totalAmountOld.add(totalAmountNew);

        // 새로운 보유량
        BigDecimal newBalance = oldBalance.add(newBtc).setScale(8, RoundingMode.HALF_UP);

        // 새로운 평단가 계산
        BigDecimal newAvgEntryPrice = newTotalAmount.divide(newBalance, 8, RoundingMode.HALF_UP);

        // 새로운 평균 레버리지 계산
        BigDecimal weightedLeverage = (totalAmountOld.multiply(oldLeverage))
                .add(totalAmountNew.multiply(BigDecimal.valueOf(newLeverage)))
                .divide(newTotalAmount, 8, RoundingMode.HALF_UP);

        btc.updateBtcBalance(newBtc);
        btc.updateAvgEntryPrice(newAvgEntryPrice);
        btc.updateAvgLeverage(weightedLeverage);
        btc.updatePosition(position);

        btcRepository.save(btc);
    }

    /**
     * 매도 시 평단가는 유지, 평균 레버리지는 업데이트
     */
    private void sellBtc(Btc btc, BigDecimal sellBtc) {
        if (sellBtc.compareTo(btc.getBtcBalance()) > 0) {
            throw new MockBitException(MockbitErrorCode.NOT_ENOUGH_BALANCE);
        }

        BigDecimal oldBalance = btc.getBtcBalance();
        BigDecimal oldEntryPrice = btc.getAvgEntryPrice();
        BigDecimal oldLeverage = btc.getAvgLeverage();

        // 매도 후 남은 진입금액
        BigDecimal soldAmount = sellBtc.multiply(oldEntryPrice);
        BigDecimal remainingAmount = oldBalance.multiply(oldEntryPrice).subtract(soldAmount);

        // BTC 잔액 차감
        BigDecimal newBalance = oldBalance.subtract(sellBtc).setScale(8, RoundingMode.HALF_UP);

        // 새로운 평균 레버리지 (남은 진입금액 기준으로 조정)
        BigDecimal newAvgLeverage = (newBalance.compareTo(BigDecimal.ZERO) > 0)
                ? remainingAmount.multiply(oldLeverage).divide(remainingAmount, 8, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        btc.updateBtcBalance(newBalance.negate());
        btc.updateAvgLeverage(newAvgLeverage);

        // 포지션 초기화
        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
            btc.updatePosition("NONE");
            btc.updateAvgEntryPrice(BigDecimal.ZERO);
            btc.updateAvgLeverage(BigDecimal.ZERO);
        }

        btcRepository.save(btc);
    }
}
