package com.osio.orderservice.domain.order.controller;

import com.osio.orderservice.domain.order.dto.OrderItemRequestDto;
import com.osio.orderservice.domain.order.dto.OrderRequestDto;
import com.osio.orderservice.domain.order.dto.OrderResponseDto;
import com.osio.orderservice.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 상품 주문 진입
    @PostMapping("/product")
    public long orderProduct(@RequestBody OrderItemRequestDto.OrderProductDto orderProductDto,
                             @RequestHeader(value = "userId") String userId) throws Exception {
        log.info("OrderController.orderProduct()");

        return orderService.orderProduct(orderProductDto, Long.valueOf(userId));
    }

    // 상품 주문 진입 - 테스트
    @PostMapping("/product/pro")
    public long orderProductPro(@RequestBody OrderItemRequestDto.OrderProductDto orderProductDto,
                                @RequestHeader(value = "userId") String userId) throws Exception {
        log.info("OrderController.orderProductPro()");

        return orderService.orderProduct(orderProductDto, Long.valueOf(userId));
    }

    // 결제
    @PostMapping("/payment/{orderId}")
    public OrderResponseDto.OrderCheckDto orderPayment(@PathVariable("orderId") Long orderId,
                                                       @RequestHeader(value = "userId") String userId) {
        log.info("OrderController.orderPayment()");

        return orderService.orderPayment(orderId, Long.valueOf(userId));
    }

    // 일반 상품 주문
    @PostMapping("/productOrigin")
    public OrderResponseDto.OrderCheckDto orderProductOrigin(@RequestBody OrderItemRequestDto.OrderProductDto orderProductDto,
                                                             @RequestHeader(value = "userId") String userId) {
        log.info("OrderController.orderProductOrigin()");

        return orderService.orderProductOrigin(orderProductDto, Long.valueOf(userId));
    }

    // 주문 전체 조회
    @GetMapping("/list")
    public List<OrderResponseDto.OrderListDto> orderList(@RequestHeader(value = "userId") String userId) {
        log.info("OrderController.orderList()");

        return orderService.orderList(Long.valueOf(userId));
    }

    // 주문 상세 조회
    @GetMapping("/detail")
    public OrderResponseDto.OrderCheckDto orderDetail(@RequestBody OrderRequestDto.OrderDetailDto orderDetailDto,
                                                      @RequestHeader(value = "userId") String userId) {
        log.info("OrderController.orderDetail()");

        return orderService.orderDetail(orderDetailDto, Long.valueOf(userId));
    }

    // 장바구니 주문
    @PostMapping("/wishlist")
    public OrderResponseDto.OrderCheckDto orderWishlist(@RequestHeader(value = "userId") String userId) {
        log.info("OrderController.orderWishlist()");

        return orderService.orderWishlist(Long.valueOf(userId));
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
