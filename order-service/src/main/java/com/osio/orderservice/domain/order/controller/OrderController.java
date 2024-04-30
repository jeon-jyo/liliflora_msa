package com.osio.orderservice.domain.order.controller;

import com.osio.orderservice.domain.order.dto.OrderItemRequestDto;
import com.osio.orderservice.domain.order.dto.OrderRequestDto;
import com.osio.orderservice.domain.order.dto.OrderResponseDto;
import com.osio.orderservice.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 상품 주문
    @PostMapping("/product")
    public OrderResponseDto.OrderCheckDto orderProduct(@RequestBody OrderItemRequestDto.OrderProductDto orderProductDto,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        log.info("OrderController.orderProduct()");

        return orderService.orderProduct(orderProductDto, Long.valueOf(userDetails.getUsername()));
    }

    // 주문 전체 조회
    @GetMapping("/list")
    public List<OrderResponseDto.OrderListDto> orderList(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("OrderController.orderList()");

        return orderService.orderList(Long.valueOf(userDetails.getUsername()));
    }

    // 주문 상세 조회
    @GetMapping("/detail")
    public OrderResponseDto.OrderCheckDto orderDetail(@RequestBody OrderRequestDto.OrderDetailDto orderDetailDto,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        log.info("OrderController.orderDetail()");

        return orderService.orderDetail(orderDetailDto, Long.valueOf(userDetails.getUsername()));
    }

    // 장바구니 주문
    @PostMapping("/wishlist")
    public OrderResponseDto.OrderCheckDto orderWishlist(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("OrderController.orderWishlist()");

        return orderService.orderWishlist(Long.valueOf(userDetails.getUsername()));
    }

    // 주문 취소
    @PutMapping("/cancel/{orderId}")
    public boolean cancelOrder(@PathVariable("orderId") Long orderId) throws Exception {
        log.info("OrderController.cancelOrder()");

        orderService.cancelOrder(orderId);
        return true;
    }

    // 상품 반품
    @PutMapping("/return/{orderId}")
    public boolean returnOrder(@PathVariable("orderId") Long orderId) throws Exception {
        log.info("OrderController.returnOrder()");

        orderService.returnOrder(orderId);
        return true;
    }

}
