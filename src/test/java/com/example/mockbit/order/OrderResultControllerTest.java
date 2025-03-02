//package com.example.mockbit.order;
//
//import com.example.mockbit.common.infrastructure.redis.RedisService;
//import com.example.mockbit.order.application.OrderResultService;
//import com.example.mockbit.order.application.request.MarketOrderAppRequest;
//import com.example.mockbit.order.domain.OrderResult;
//import com.example.mockbit.order.presentation.OrderResultController;
//import com.fasterxml.jackson.databind.ObjectMapper;
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
//@WebMvcTest(OrderResultController.class)
//@ExtendWith(MockitoExtension.class)
//class OrderResultControllerTest {
//
//    @MockBean
//    private RedisService redisService;
//
//    @Autowired
//    private MockMvc mockMvc;
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
//    void setUp() {
//        session = new MockHttpSession();
//        session.setAttribute("userId", 123L);
//        when(redisService.getData("current-btc-price")).thenReturn("1000000");
//    }
//
//    @Test
//    void 현재가_주문_API_테스트() throws Exception {
//        // given
//        MarketOrderAppRequest request = MarketOrderAppRequest.builder()
//                .orderPrice("1000000")
//                .leverage(30)
//                .position("Long")
//                .sellOrBuy("Buy")
//                .build();
//
//        OrderResult mockOrderResult = new OrderResult(
//                123L, (String) redisService.getData("current-btc-price"), "2024-01-30T12:00:00Z",
//                (String) redisService.getData("current-btc-price"), "1000000", 30, "Long", "Buy"
//        );
//
//        when(orderResultService.executeMarketOrder(anyLong(), any(), anyInt(), any(), any()))
//                .thenReturn(mockOrderResult);
//
//        String requestBody = objectMapper.writeValueAsString(request);
//
//        // when & then
//        mockMvc.perform(post("/api/orders/market/order")
//                        .session(session)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(mockOrderResult.getId()))
//                .andExpect(jsonPath("$.price").value(mockOrderResult.getPrice()))
//                .andExpect(jsonPath("$.userId").value(mockOrderResult.getUserId()))
//                .andDo(print());
//    }
//}
