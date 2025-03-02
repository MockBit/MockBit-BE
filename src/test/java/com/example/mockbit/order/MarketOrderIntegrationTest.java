//package com.example.mockbit.order;
//
//import com.example.mockbit.user.application.UserService;
//import com.example.mockbit.user.application.request.UserJoinAppRequest;
//import com.example.mockbit.order.application.OrderResultService;
//import com.example.mockbit.order.application.request.MarketOrderAppRequest;
//import com.example.mockbit.order.application.response.MarketOrderAppResponse;
//import com.example.mockbit.order.domain.OrderResult;
//import com.example.mockbit.order.presentation.OrderResultController;
//import com.example.mockbit.user.presentation.UserController;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.http.HttpSession;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpSession;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest({OrderResultController.class, UserController.class})
//@ExtendWith(MockitoExtension.class)
//class MarketOrderIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private UserService userService;
//
//    @MockBean
//    private OrderResultService orderResultService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private MockHttpSession session;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        session = new MockHttpSession();
//        session.setAttribute("userId", 1L); // 회원가입 후 userId 1L 할당
//    }
//
//    @Test
//    void 회원가입_및_현재가_주문_테스트() throws Exception {
//        System.out.println("\n=== 회원가입 및 현재가 주문 테스트 시작 ===");
//
//        // 회원가입 요청 Body 생성
//        UserJoinAppRequest joinRequest = new UserJoinAppRequest("testUser", "testPassword1!", "testNick");
//
//        // 회원가입 Mock 응답 설정 (userId 직접 할당)
//        when(userService.join(any(UserJoinAppRequest.class))).thenReturn(1L);
//
//        // 현재가 주문 요청 Body 생성
//        MarketOrderAppRequest orderRequest = MarketOrderAppRequest.builder()
//                .orderPrice("1000000")
//                .leverage(30)
//                .position("LONG")
//                .sellOrBuy("BUY")
//                .build();
//
//        OrderResult mockOrderResult = new OrderResult(
//                1L, "1000000", "2024-01-30T12:00:00Z",
//                "1000000", "1000000", 30, "LONG", "BUY"
//        );
//        mockOrderResult.setId(1L); // ID 직접 할당
//
//        // 주문 Mock 응답 설정
//        when(orderResultService.executeMarketOrder(anyLong(), any(), anyInt(), any(), any()))
//                .thenReturn(mockOrderResult);
//
//        // 회원가입 요청
//        mockMvc.perform(post("/api/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(joinRequest)))
//                .andExpect(status().isOk());
//
//        // 현재가 주문 요청 실행
//        mockMvc.perform(post("/api/orders/market/order")
//                        .session(session) // 세션에 userId 포함
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(orderRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(mockOrderResult.getId()))
//                .andExpect(jsonPath("$.userId").value(mockOrderResult.getUserId()))
//                .andExpect(jsonPath("$.orderPrice").value(mockOrderResult.getOrderPrice()))
//                .andExpect(jsonPath("$.position").value(mockOrderResult.getPosition()))
//                .andExpect(jsonPath("$.sellOrBuy").value(mockOrderResult.getSellOrBuy()))
//                .andDo(print());
//
//        System.out.println("=== 회원가입 및 현재가 주문 테스트 완료 ===\n");
//    }
//}
