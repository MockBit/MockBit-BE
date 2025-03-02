//package com.example.mockbit.account;
//
//import com.example.mockbit.account.application.AccountService;
//import com.example.mockbit.account.domain.Account;
//import com.example.mockbit.account.domain.Btc;
//import com.example.mockbit.account.domain.repository.AccountRepository;
//import com.example.mockbit.account.domain.repository.BtcRepository;
//import com.example.mockbit.common.exception.MockBitException;
//import com.example.mockbit.common.exception.MockbitErrorCode;
//import com.example.mockbit.order.domain.OrderResult;
//import com.example.mockbit.user.domain.User;
//import com.example.mockbit.user.domain.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AccountServiceTest {
//
//    @InjectMocks
//    private AccountService accountService;
//
//    @Mock
//    private AccountRepository accountRepository;
//
//    @Mock
//    private BtcRepository btcRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    private User testUser;
//    private Account testAccount;
//    private Btc testBtc;
//
//    @BeforeEach
//    void setUp() {
//        testUser = new User("testUser", "testPassword1!", "testNick");
//        testAccount = new Account(testUser);
//        testBtc = new Btc(testUser);
//    }
//
//    @Test
//    void 시장가_주문_테스트() {
//        OrderResult orderResult = new OrderResult(1L, "100000", "2024-01-01T12:00:00Z", "110000", "1000000", 10, "LONG", "BUY");
//
//        when(userRepository.findById(orderResult.getUserId())).thenReturn(Optional.of(testUser));
//        when(accountRepository.findByUserId(testUser.getId())).thenReturn(testAccount);
//        when(btcRepository.findByUserId(testUser.getId())).thenReturn(testBtc);
//
//        accountService.processMarketOrder(orderResult);
//
//        assertEquals(BigDecimal.valueOf(9000000), testAccount.getBalance());
//        assertTrue(testBtc.getBtcBalance().compareTo(BigDecimal.ZERO) > 0);
//    }
//
//    @Test
//    void 보유_원화보다_주문_가격이_클_때의_시장가_주문_테스트() {
//        OrderResult orderResult = new OrderResult(1L, "100000", "2024-01-01T12:00:00Z", "110000", "20000000", 10, "LONG", "BUY");
//        when(userRepository.findById(orderResult.getUserId())).thenReturn(Optional.of(testUser));
//        when(accountRepository.findByUserId(testUser.getId())).thenReturn(testAccount);
//        MockBitException thrownException = assertThrows(MockBitException.class, () -> accountService.processMarketOrder(orderResult));
//        assertEquals(MockbitErrorCode.NOT_ENOUGH_BALANCE, thrownException.getErrorCode());
//    }
//
//    @Test
//    void 지정가_주문_취소_테스트() {
//        when(accountRepository.findByUserId(testUser.getId())).thenReturn(testAccount);
//        accountService.cancelOrder(testUser.getId(), BigDecimal.valueOf(1000000));
//        assertEquals(BigDecimal.valueOf(11000000), testAccount.getBalance());
//    }
//}
