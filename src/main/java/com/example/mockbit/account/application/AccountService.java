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
        Account account = new Account(user, BigDecimal.valueOf(10000000));
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
        BigDecimal leverage = BigDecimal.valueOf(orderResult.getLeverage());
        BigDecimal btcPrice = BigDecimal.valueOf(Long.parseLong(orderResult.getPrice()));
        BigDecimal btcAmount = orderPrice.multiply(leverage)
                .divide(btcPrice, 8, RoundingMode.HALF_UP);

        if (account.getBalance().compareTo(orderPrice) < 0) {
            throw new MockBitException(MockbitErrorCode.NOT_ENOUGH_BALANCE);
        }

        account.deductBalance(orderPrice);
        btc.updateBtcBalance(btcAmount);
    }

    @Transactional
    public void processOrder(Long userId, BigDecimal orderPrice) {
        Account account = accountRepository.findByUserId(userId);
        account.deductBalance(orderPrice);
    }

    @Transactional
    public void completeOrder(Order order) {
        Btc btc = btcRepository.findByUserId(order.getUserId());

        int leverage = order.getLeverage();
        BigDecimal orderPrice = BigDecimal.valueOf(Long.parseLong(order.getOrderPrice()));
        BigDecimal btcPrice = BigDecimal.valueOf(Long.parseLong(order.getPrice()));

        BigDecimal btcAmount = orderPrice.multiply(BigDecimal.valueOf(leverage))
                .divide(btcPrice, 8, BigDecimal.ROUND_HALF_UP);

        btc.updateBtcBalance(btcAmount);
    }

    @Transactional
    public void cancelOrder(Long userId, BigDecimal orderPrice) {
        Account account = accountRepository.findByUserId(userId);
        account.refundBalance(orderPrice);
    }
}
