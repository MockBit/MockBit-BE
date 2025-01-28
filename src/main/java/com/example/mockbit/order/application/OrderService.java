package com.example.mockbit.order.application;

import com.example.mockbit.order.domain.Order;
import com.example.mockbit.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    public Order saveOrder(Long userId, String price, String btcPrice, String orderPrice, int leverage,
                           String position, String sellOrBuy) {
        Order order = Order.builder()
                .id(Long.valueOf(userId + ":" + price))
                .price(price)
                .userId(userId)
                .createdAt(Instant.now())
                .btcPrice(btcPrice)
                .orderPrice(orderPrice)
                .leverage(leverage)
                .position(position)
                .sellOrBuy(sellOrBuy)
                .build();

        return orderRepository.save(order);
    }

    public Optional<Order> findOrderById(String id) {
        return orderRepository.findById(id);
    }

    public void deleteOrderById(String id) {
        orderRepository.deleteById(id);
    }
}
