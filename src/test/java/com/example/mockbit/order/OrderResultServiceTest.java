package com.example.mockbit.order;

import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.infrastructure.redis.RedisService;
import com.example.mockbit.order.application.OrderResultService;
import com.example.mockbit.order.domain.OrderResult;
import com.example.mockbit.order.domain.repository.OrderResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderResultServiceTest {

    @Mock
    private RedisService redisService;

    @Mock
    private OrderResultRepository orderResultRepository;

    @InjectMocks
    private OrderResultService orderResultService;

    private final Long userId = 123L;
    private final String orderPrice = "1000000";
    private final int leverage = 30;
    private final String position = "Long";
    private final String sellOrBuy = "Buy";
    private final String currentPriceKey = "current-btc-price";
    private final String testPrice = "110000000";

    @BeforeEach
    void setUp() {
        lenient().doReturn(testPrice).when(redisService).getData(currentPriceKey);
    }

    @Test
    void 현재가_주문_정상_처리() {
        // given
        OrderResult mockOrderResult = new OrderResult(
                userId, (String) redisService.getData(currentPriceKey), "2024-01-30T12:00:00Z",
                (String) redisService.getData(currentPriceKey), orderPrice, leverage, position, sellOrBuy
        );
        ReflectionTestUtils.setField(mockOrderResult, "id", 1L);

        doAnswer(invocation -> {
            OrderResult savedOrder = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedOrder, "id", 1L);
            return savedOrder;
        }).when(orderResultRepository).save(any());

        // when
        OrderResult result = orderResultService.executeMarketOrder(userId, orderPrice, leverage, position, sellOrBuy);
        System.out.println(result);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getPrice()).isEqualTo(testPrice);
        verify(redisService, atLeastOnce()).getData(currentPriceKey);
        verify(orderResultRepository, times(1)).save(any());
    }

    @Test
    void 현재가_주문_실패_실시간_가격_없음() {
        // given
        when(redisService.getData(currentPriceKey)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> orderResultService.executeMarketOrder(userId, orderPrice, leverage, position, sellOrBuy))
                .isInstanceOf(MockBitException.class)
                .hasMessageContaining("주문 처리 중 오류가 발생했습니다.");

        verify(redisService, times(1)).getData(currentPriceKey);
        verify(orderResultRepository, never()).save(any());
    }
}

