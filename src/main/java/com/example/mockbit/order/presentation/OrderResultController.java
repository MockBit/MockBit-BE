package com.example.mockbit.order.presentation;

import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.order.application.OrderResultService;
import com.example.mockbit.order.application.request.MarketOrderAppRequest;
import com.example.mockbit.order.application.response.MarketOrderAppResponse;
import com.example.mockbit.order.domain.OrderResult;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/orders/market")
public class OrderResultController {

    private final OrderResultService orderResultService;
    private final HttpSession session;

    @PostMapping("/order")
    public ResponseEntity<MarketOrderAppResponse> marketOrder(
            @Valid @RequestBody MarketOrderAppRequest request
    ) {

        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            throw new MockBitException(MockbitErrorCode.ONLY_FOR_MEMBER);
        }

        OrderResult orderResult = orderResultService.executeMarketOrder(
                userId,
                request.orderPrice(),
                request.leverage(),
                request.position(),
                request.sellOrBuy()
        );

        return ResponseEntity.ok(MarketOrderAppResponse.from(orderResult));
    }
}
