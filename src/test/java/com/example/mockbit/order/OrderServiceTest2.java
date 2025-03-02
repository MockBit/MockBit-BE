//package com.example.mockbit.order;
//
//import com.example.mockbit.account.application.AccountService;
//import com.example.mockbit.common.infrastructure.redis.RedisService;
//import com.example.mockbit.order.application.OrderService;
//import com.example.mockbit.order.domain.Order;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.time.Instant;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class OrderServiceTest2 {
//
//    @InjectMocks
//    private OrderService orderService;
//
//    @Mock
//    private RedisService redisService;
//
//    @Mock
//    private AccountService accountService;
//
//    private Order testOrder;
//    private String expectedOrderId;
//
//    @BeforeEach
//    void setUp() {
//        expectedOrderId = "Orders:100000:1";
//        testOrder = Order.builder()
//                .id(expectedOrderId)
//                .price("100000")
//                .userId(1L)
//                .orderedAt(String.valueOf(Instant.now()))
//                .btcPrice("110000")
//                .orderPrice("1000000")
//                .leverage(10)
//                .position("LONG")
//                .sellOrBuy("BUY")
//                .build();
//    }
//
//    @Test
//    void 지정가_주문_테스트() {
//        // given
//        lenient().when(redisService.getData(eq(expectedOrderId))).thenReturn(null);
//        doNothing().when(redisService).saveData(eq(expectedOrderId), any(Order.class));
//        doNothing().when(accountService).processOrder(eq(testOrder.getUserId()), eq(new BigDecimal(testOrder.getOrderPrice())));
//
//        // when
//        Order savedOrder = orderService.saveOrder(
//                testOrder.getUserId(),
//                testOrder.getPrice(),
//                testOrder.getBtcPrice(),
//                testOrder.getOrderPrice(),
//                testOrder.getLeverage(),
//                testOrder.getPosition(),
//                testOrder.getSellOrBuy()
//        );
//
//        // then
//        assertEquals(expectedOrderId, savedOrder.getId());
//        verify(redisService, times(1)).saveData(eq(expectedOrderId), any(Order.class));
//        verify(accountService, times(1)).processOrder(eq(testOrder.getUserId()), eq(new BigDecimal(testOrder.getOrderPrice())));
//    }
//
//    @Test
//    void 지정가_주문_조회_테스트() {
//        // given
//        when(redisService.getData(eq(expectedOrderId))).thenReturn(testOrder);
//
//        // when
//        Optional<Order> foundOrder = orderService.findOrderById(expectedOrderId);
//
//        // then
//        assertTrue(foundOrder.isPresent());
//        assertEquals(expectedOrderId, foundOrder.get().getId());
//    }
//
//    @Test
//    void 지정가_주문_취소_테스트() {
//        // given
//        when(redisService.getData(eq(expectedOrderId))).thenReturn(testOrder);
//        doNothing().when(redisService).deleteData(eq(expectedOrderId));
//        doNothing().when(accountService).cancelOrder(eq(testOrder.getUserId()), eq(new BigDecimal(testOrder.getOrderPrice())));
//
//        // when
//        orderService.deleteOrderById(expectedOrderId);
//
//        // then
//        verify(redisService, times(1)).deleteData(eq(expectedOrderId));
//        verify(accountService, times(1)).cancelOrder(eq(testOrder.getUserId()), eq(new BigDecimal(testOrder.getOrderPrice())));
//    }
//}
