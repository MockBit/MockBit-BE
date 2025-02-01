package com.example.mockbit.order;

import com.example.mockbit.account.application.AccountService;
import com.example.mockbit.account.domain.Account;
import com.example.mockbit.account.domain.Btc;
import com.example.mockbit.common.infrastructure.redis.RedisService;
import com.example.mockbit.order.application.OrderResultService;
import com.example.mockbit.order.application.request.MarketOrderAppRequest;
import com.example.mockbit.order.domain.OrderResult;
import com.example.mockbit.user.application.UserService;
import com.example.mockbit.user.application.request.UserJoinAppRequest;
import com.example.mockbit.user.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({com.example.mockbit.user.presentation.UserController.class,
        com.example.mockbit.order.presentation.OrderResultController.class})
@ExtendWith(MockitoExtension.class)
class MarketOrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private OrderResultService orderResultService;

    @MockBean
    private AccountService accountService;

    @MockBean
    private RedisService redisService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpSession session;
    private User testUser;
    private Account testAccount;
    private Btc testBtc;
    private OrderResult testOrderResult;

    @BeforeEach
    void setUp() {
        // ✅ 테스트 유저 정보 생성
        testUser = new User("testuser", "TestPassword1!", "TestNickname");

        // ✅ 초기 Account & BTC 정보
        testAccount = new Account(testUser, new BigDecimal("10000000"));
        testBtc = new Btc(testUser);
        testBtc.updateBtcBalance(BigDecimal.ZERO); // 초기 BTC 보유량: 0
        doReturn(testAccount).when(accountService).getAccountByUserId(1L);
        doReturn(testBtc).when(accountService).getBtcByUserId(1L);



        // ✅ 시장가 주문 결과 예상값 설정
        testOrderResult = new OrderResult(
                1L,
                "110000",
                "2025-02-01T12:30:00Z",
                "110000",
                "1000000",
                10,
                "LONG",
                "BUY"
        );

        session = new MockHttpSession();
        session.setAttribute("userId", 1L);
    }

    @Test
    /** ✅ 회원가입 요청 테스트 */
    void 회원가입_테스트() throws Exception {
        // ✅ 회원가입 요청 객체 생성
        UserJoinAppRequest userJoinRequest = new UserJoinAppRequest(
                testUser.getUserid(),
                testUser.getNickname().getValue(),
                testUser.getPassword().getValue()
        );

        String requestBody = objectMapper.writeValueAsString(userJoinRequest);

        // ✅ Mock 설정: 회원가입 시 User ID = 1 반환
        when(userService.join(any(UserJoinAppRequest.class))).thenReturn(1L);

        // ✅ 회원가입 요청 수행
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        // ✅ 회원가입 후 계좌(Account) & BTC 정보 조회
        testAccount = accountService.getAccountByUserId(1L);
        testBtc = accountService.getBtcByUserId(1L);

        // ✅ 계좌 및 BTC 상태 출력
        printAccountStatus("📌 회원가입 후 상태", testAccount, testBtc);
    }

    /** ✅ 계좌 및 BTC 상태 출력 메서드 */
    private void printAccountStatus(String title, Account account, Btc btc) {
        System.out.println("\n" + title);
        System.out.println("✅ 계좌 잔액: " + (account != null ? account.getBalance() : "N/A"));
        System.out.println("✅ BTC 잔고: " + (btc != null ? btc.getBtcBalance() : "N/A") + "\n");
    }


    /** ✅ 시장가 주문 요청 테스트 */
    @Test
    void 시장가_주문_테스트() throws Exception {
        // ✅ 시장가 주문 요청 객체 생성
        MarketOrderAppRequest marketRequest = MarketOrderAppRequest.builder()
                .orderPrice(testOrderResult.getOrderPrice())
                .leverage(testOrderResult.getLeverage())
                .position(testOrderResult.getPosition())
                .sellOrBuy(testOrderResult.getSellOrBuy())
                .build();

        String marketRequestBody = objectMapper.writeValueAsString(marketRequest);

        // ✅ Mock 설정: 주문 실행 시 예상된 OrderResult 반환
        when(orderResultService.executeMarketOrder(any(Long.class), any(String.class), anyInt(), any(String.class), any(String.class)))
                .thenReturn(testOrderResult);

        // ✅ 주문 전 계좌 및 BTC 상태 출력
        testAccount = accountService.getAccountByUserId(1L);
        testBtc = accountService.getBtcByUserId(1L);
        printAccountStatus("📌 주문 전 상태", testAccount, testBtc);

        // ✅ 시장가 주문 요청 수행
        mockMvc.perform(post("/api/orders/market/order")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(marketRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testOrderResult.getUserId()))
                .andExpect(jsonPath("$.price").value(testOrderResult.getPrice()));

        // ✅ 주문 후 계좌(Account) 및 BTC 잔고 다시 조회
        testAccount = accountService.getAccountByUserId(1L);
        testBtc = accountService.getBtcByUserId(1L);

        // ✅ 주문 후 상태 출력
        printAccountStatus("📌 주문 후 상태", testAccount, testBtc);
    }

}
