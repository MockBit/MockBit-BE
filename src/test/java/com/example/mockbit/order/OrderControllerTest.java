package com.example.mockbit.order;

import com.example.mockbit.order.application.OrderService;
import com.example.mockbit.order.application.request.OrderAppRequest;
import com.example.mockbit.order.domain.Order;
import com.example.mockbit.order.presentation.OrderController;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private Order sampleOrder;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        sampleOrder = Order.builder()
                .id("Orders:100000000:123")
                .price("100000000")
                .userId(123L)
                .orderedAt("2024-01-30T12:00:00Z")
                .btcPrice("110000000")
                .orderPrice("1000000")
                .leverage(30)
                .position("Long")
                .sellOrBuy("Buy")
                .build();

        session = new MockHttpSession();
        session.setAttribute("userId", 123L);
    }

    @Test
    void 주문_저장_API_테스트() throws Exception {
        // given
        when(orderService.saveOrder(anyLong(), any(), any(), any(), anyInt(), any(), any()))
                .thenReturn(sampleOrder);

        OrderAppRequest request = OrderAppRequest.builder()
                .price(sampleOrder.getPrice())
                .btcPrice(sampleOrder.getBtcPrice())
                .orderPrice(sampleOrder.getOrderPrice())
                .leverage(sampleOrder.getLeverage())
                .position(sampleOrder.getPosition())
                .sellOrBuy(sampleOrder.getSellOrBuy())
                .build();

        String requestBody = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/api/orders/order")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleOrder.getId()))
                .andExpect(jsonPath("$.price").value(sampleOrder.getPrice()))
                .andExpect(jsonPath("$.userId").value(sampleOrder.getUserId()))
                .andDo(print());
    }

    @Test
    void 사용자_주문_조회_API_테스트() throws Exception {
        // given
        when(orderService.findOrderByUserId(anyLong())).thenReturn(List.of(sampleOrder));

        // when & then
        mockMvc.perform(get("/api/orders/user_{userId}", sampleOrder.getUserId())
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(sampleOrder.getId()))
                .andExpect(jsonPath("$[0].price").value(sampleOrder.getPrice()))
                .andExpect(jsonPath("$[0].userId").value(sampleOrder.getUserId()))
                .andDo(print());
    }
}
