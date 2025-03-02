//package com.example.mockbit.order;
//
//import com.example.mockbit.common.infrastructure.redis.RedisService;
//import com.example.mockbit.order.application.OrderService;
//import com.example.mockbit.order.domain.Order;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class OrderServiceTest {
//
//    @Mock
//    private RedisService redisService;
//
//    @InjectMocks
//    private OrderService orderService;
//
//    private Order sampleOrder;
//    private final Long userId = 123L;
//    private final String price = "100000000";
//    private final String orderId = "Orders:100000000:123";
//
//    @BeforeEach
//    void setUp() {
//        sampleOrder = Order.builder()
//                .id(orderId)
//                .price(price)
//                .userId(userId)
//                .orderedAt("2024-01-30T12:00:00Z")
//                .btcPrice("110000000")
//                .orderPrice("1000000")
//                .leverage(30)
//                .position("Long")
//                .sellOrBuy("Buy")
//                .build();
//    }
//
//    @Test
//    void 주문_저장() {
//        // given
//        doNothing().when(redisService).saveData(eq(orderId), any());
//
//        // when
//        Order savedOrder = orderService.saveOrder(userId, price, "110000000", "1000000", 30, "Long", "Buy");
//        System.out.println(savedOrder);
//        // then
//        assertThat(savedOrder).isNotNull();
//        verify(redisService, times(1)).saveData(eq(orderId), any());
//    }
//
//    @Test
//    void 주문_조회_성공() {
//        // given
//        when(redisService.getData(orderId)).thenReturn(sampleOrder);
//
//        // when
//        Optional<Order> foundOrder = orderService.findOrderById(orderId);
//        System.out.println(foundOrder);
//        // then
//        assertThat(foundOrder).isPresent();
//        assertThat(foundOrder.get().getId()).isEqualTo(orderId);
//    }
//
//    @Test
//    void 사용자의_모든_주문_조회() {
//        // given
//        String pattern = "Orders:*:" + userId;
//        Set<String> mockKeys = Set.of(orderId);
//        when(redisService.getKeys(pattern)).thenReturn(mockKeys);
//        when(redisService.getData(orderId)).thenReturn(sampleOrder);
//
//        // when
//        List<Order> userOrders = orderService.findOrderByUserId(userId);
//        System.out.println(userOrders);
//        // then
//        assertThat(userOrders).hasSize(1);
//        assertThat(userOrders.get(0).getId()).isEqualTo(orderId);
//    }
//
//    @Test
//    void 주문_삭제() {
//        // when
//        orderService.deleteOrderById(orderId);
//
//        // then
//        verify(redisService, times(1)).deleteData(orderId);
//    }
//}
//
//
