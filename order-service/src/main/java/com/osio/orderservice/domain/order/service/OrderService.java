package com.osio.orderservice.domain.order.service;

import com.osio.orderservice.domain.client.product.ProductClient;
import com.osio.orderservice.domain.client.product.dto.ProductReqDto;
import com.osio.orderservice.domain.client.product.dto.ProductResDto;
import com.osio.orderservice.domain.client.product.dto.StockReqDto;
import com.osio.orderservice.domain.client.user.UserClient;
import com.osio.orderservice.domain.client.user.dto.UserResDto;
import com.osio.orderservice.domain.order.dto.OrderItemRequestDto;
import com.osio.orderservice.domain.order.dto.OrderItemResponseDto;
import com.osio.orderservice.domain.order.dto.OrderRequestDto;
import com.osio.orderservice.domain.order.dto.OrderResponseDto;
import com.osio.orderservice.domain.order.entity.Order;
import com.osio.orderservice.domain.order.entity.OrderItem;
import com.osio.orderservice.domain.order.entity.OrderStatus;
import com.osio.orderservice.domain.order.entity.OrderStatusEnum;
import com.osio.orderservice.domain.order.repository.OrderItemRepository;
import com.osio.orderservice.domain.order.repository.OrderRepository;
import com.osio.orderservice.domain.order.repository.OrderStatusRepository;
import com.osio.orderservice.domain.wishlist.entity.WishItem;
import com.osio.orderservice.domain.wishlist.entity.Wishlist;
import com.osio.orderservice.domain.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserClient userClient;
    private final ProductClient productClient;
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderItemRepository orderItemRepository;
    private final WishlistRepository wishlistRepository;

    // 상품 주문 진입
    @Transactional
    public long orderProduct(OrderItemRequestDto.OrderProductDto orderProductDto, Long userId) throws Exception {
        log.info("OrderService.orderProduct()");

        long orderProductId = orderProductDto.getProductId();
        int orderQuantity = orderProductDto.getQuantity();

        // 주문
        ProductResDto.ProductDetailDto product = productClient.getProductDetail(orderProductId);

        OrderStatus orderStatus = createOrderStatus();

        Order order = Order.builder()
                .userId(userId)
                .amount(product.getPrice() * orderQuantity)
                .orderStatus(orderStatus)
                .build();

        orderRepository.save(order);

        // 주문 상품
        createOrderProduct(order, orderProductId, orderQuantity);

        // 재고 확인 및 검사
        boolean check = productClient.decreaseStockQuantity(new StockReqDto.StockQuantityDto(orderProductId, orderQuantity, order.getOrderId()));

        if (!check) {
            order.getOrderStatus().updateFailed();
            throw new IllegalArgumentException("재고가 부족합니다.");
        }

        // 20% 고객 이탈
        if (isTwentyPercent()) {
            leaveOrder(order.getOrderId());
        }

        return order.getOrderId();
    }

    // 20% 계산
    @Transactional
    public boolean isTwentyPercent() {
        Random random = new Random();

        // 0에서 99 사이의 랜덤한 정수 생성
        int randomNumber = random.nextInt(100);

        // 20% 확률로 true 반환
        return randomNumber < 20;
    }

    // 이탈
    @Transactional
    public void leaveOrder(Long userId) {
        Order order = orderRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        stockRestore(order);
    }

    // 타임아웃 확인
    @Transactional
    public void checkFailedOrder() {
        // ORDERED("주문생성") 이후 10분이 넘은 주문들 -> FAILED("주문실패")
        List<Order> orderedOrders =
                orderRepository.findAllByOrderStatus_StatusAndChangedDateBefore(OrderStatusEnum.ORDERED, LocalDateTime.now().minusMinutes(10));

        for (Order order : orderedOrders) {
            stockRestore(order);
        }
    }

    // redis 재고 복구
    @Transactional
    public void stockRestore(Order order) {
        order.getOrderStatus().updateFailed();

        List<OrderItem> orderItems = orderItemRepository.findAllByOrder(order);
        for (OrderItem orderItem : orderItems) {
            long productId = orderItem.getProductId();
            int quantity = orderItem.getQuantity();

            productClient.increaseStockQuantity(new StockReqDto.StockQuantityDto(productId, quantity, order.getOrderId()));
        }
    }

    // 결제
    @Transactional
    public OrderResponseDto.OrderCheckDto orderPayment(Long orderId, Long userId) {
        log.info("OrderService.orderPayment()");

        // 20% 고객 이탈
        if (isTwentyPercent()) {
            leaveOrder(orderId);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // FAILED 되지 않은 주문만 결제진행
        if (order.getOrderStatus().getStatus() != OrderStatusEnum.ORDERED) {
            throw new IllegalArgumentException("Order not found");
        }
        order.getOrderStatus().updatePayment(); // 결제 완료

        // 주문서
        UserResDto.MyPageDto myPageDto = userClient.myPage(userId);;

        List<OrderItem> orderItems = orderItemRepository.findAllByOrder(order);

        ProductResDto.ProductDetailDto product = productClient.getProductDetail(orderItems.get(0).getProductId());

        List<OrderItemResponseDto.OrderItemCheckDto> orderItemCheckDtos = orderItems.stream()
                .map(orderItem -> OrderItemResponseDto.OrderItemCheckDto.fromEntityAndDto(orderItem, product))
                .toList();

        return OrderResponseDto.OrderCheckDto.fromEntity(order, myPageDto, orderItemCheckDtos);
    }

    // 일반 상품 주문
    @Transactional
    public OrderResponseDto.OrderCheckDto orderProductOrigin(OrderItemRequestDto.OrderProductDto orderProductDto, Long userId) {
        log.info("OrderService.orderProductOrigin()");

        // 재고 확인
        long orderProductId = orderProductDto.getProductId();
        ProductResDto.ProductDetailDto product = productClient.getProductDetail(orderProductId);

        int orderQuantity = orderProductDto.getQuantity();
        if (product.getQuantity() < orderQuantity) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }

        // 주문
        OrderStatus orderStatus = createOrderStatus();

        Order order = Order.builder()
                .userId(userId)
                .amount(product.getPrice() * orderQuantity)
                .orderStatus(orderStatus)
                .build();

        orderRepository.save(order);

        // 주문 상품
//        Order currentOrder = orderRepository.findFirstByUserIdOrderByPurchaseDateDesc(userId)
//                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // 상품 재고 감소
        productClient.decreaseQuantity(new ProductReqDto.ProductQuantityDto(orderProductId, orderQuantity));

        createOrderProduct(currentOrder, orderProductId, orderQuantity);

        order.getOrderStatus().updatePayment(); // 결제 완료

        // 주문서
        UserResDto.MyPageDto myPageDto = userClient.myPage(userId);

        List<OrderItem> orderItems = orderItemRepository.findAllByOrder(order);

        List<OrderItemResponseDto.OrderItemCheckDto> orderItemCheckDtos = orderItems.stream()
                .map(orderItem -> OrderItemResponseDto.OrderItemCheckDto.fromEntityAndDto(orderItem, product))
                .toList();

        return OrderResponseDto.OrderCheckDto.fromEntity(order, myPageDto, orderItemCheckDtos);
    }

    // 주문 상태 추가
    @Transactional
    protected OrderStatus createOrderStatus() {
        OrderStatus orderStatus = OrderStatus.builder()
                .status(OrderStatusEnum.ORDERED)
                .build();

        orderStatusRepository.save(orderStatus);
        return orderStatus;
    }

    // 주문 상품 추가
    @Transactional
    protected OrderItem createOrderProduct(Order currentOrder, long productId, int orderQuantity) {
        OrderItem orderItem = OrderItem.builder()
                .order(currentOrder)
                .productId(productId)
                .quantity(orderQuantity)
                .build();

        orderItemRepository.save(orderItem);
        return orderItem;
    }

    // 주문 전체 조회
    @Transactional
    public List<OrderResponseDto.OrderListDto> orderList(Long userId) {
        log.info("OrderService.orderList()");

        List<Order> orders = orderRepository.findAllByUserIdOrderByPurchaseDateDesc(userId);

        List<OrderResponseDto.OrderListDto> orderListDtos = new ArrayList<>();
        for (Order currentOrder : orders) {
            List<OrderItemResponseDto.OrderItemCheckDto> orderItemCheckDtos = createOrderItemCheckDtos(currentOrder);

            OrderResponseDto.OrderListDto orderCheckDto =
                    OrderResponseDto.OrderListDto.fromEntity(currentOrder, orderItemCheckDtos);

            orderListDtos.add(orderCheckDto);
        }
        return orderListDtos;
    }

    // 주문 상품 목록
    @Transactional
    protected List<OrderItemResponseDto.OrderItemCheckDto> createOrderItemCheckDtos(Order currentOrder) {
        List<OrderItem> orderItems = currentOrder.getOrderItems();

        List<OrderItemResponseDto.OrderItemCheckDto> orderItemCheckDtos = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            // 각 상품에 대한 세부 정보
            ProductResDto.ProductDetailDto product = productClient.getProductDetail(orderItem.getProductId());

            // OrderItemCheckDto 를 생성하여 리스트에 추가
            OrderItemResponseDto.OrderItemCheckDto orderItemCheckDto =
                    OrderItemResponseDto.OrderItemCheckDto.fromEntityAndDto(orderItem, product);

            orderItemCheckDtos.add(orderItemCheckDto);
        }
        return orderItemCheckDtos;
    }
    
    // 주문 상세 조회
    @Transactional
    public OrderResponseDto.OrderCheckDto orderDetail(OrderRequestDto.OrderDetailDto orderDetailDto, Long userId) {
        log.info("OrderService.orderDetail()");

        UserResDto.MyPageDto myPageDto = userClient.myPage(userId);

        Order currentOrder = orderRepository.findById(orderDetailDto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        List<OrderItemResponseDto.OrderItemCheckDto> orderItemCheckDtos = createOrderItemCheckDtos(currentOrder);

        return OrderResponseDto.OrderCheckDto.fromEntity(currentOrder, myPageDto, orderItemCheckDtos);
    }

    // 장바구니 주문
    @Transactional
    public OrderResponseDto.OrderCheckDto orderWishlist(Long userId) {
        log.info("OrderService.orderWishlist()");

        // 장바구니
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("Wishlist not found " + userId));

        List<WishItem> wishItems = wishlist.getWishItems();

        List<WishItem> currentWishItems = wishItems.stream()
                .filter(wishItem -> !wishItem.isDeleted()).toList();

        // 주문
        OrderStatus orderStatus = createOrderStatus();

        Order order = Order.builder()
                .userId(userId)
                .orderStatus(orderStatus)
                .build();

        orderRepository.save(order);

        // 주문 상품
//        Order currentOrder = orderRepository.findFirstByUserIdOrderByPurchaseDateDesc(userId)
//                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        List<OrderItem> orderItems = new ArrayList<>();
        int total = 0;
        for (WishItem wishItem : currentWishItems) {
            // 재고 확인
            long orderProductId = wishItem.getProductId();
            ProductResDto.ProductDetailDto product = productClient.getProductDetail(orderProductId);

            int orderQuantity = wishItem.getQuantity();
            if (product.getQuantity() < orderQuantity) {
                throw new IllegalArgumentException("재고가 부족합니다.");
            }

            // 상품 재고 감소
            productClient.decreaseQuantity(new ProductReqDto.ProductQuantityDto(orderProductId, orderQuantity));

            order.getOrderStatus().updatePayment(); // 결제 완료

            OrderItem orderItem = createOrderProduct(order, orderProductId, orderQuantity);
            orderItems.add(orderItem);
            total += (product.getPrice() * orderQuantity);

            wishItem.updateDeleted();   // 장바구니에서 삭제
        }
        order.updateAmount(total);   // 총 금액 업데이트

        // 주문서
        UserResDto.MyPageDto myPageDto = userClient.myPage(userId);

        List<OrderItemResponseDto.OrderItemCheckDto> orderItemCheckDtos = createOrderItemCheckDtos(order);

        return OrderResponseDto.OrderCheckDto.fromEntity(order, myPageDto, orderItemCheckDtos);
    }

    // 주문 취소
    @Transactional
    public void cancelOrder(Long orderId) throws Exception {
        log.info("OrderService.cancelOrder()");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        OrderStatus orderStatus = order.getOrderStatus();

        if (orderStatus.getStatus() != OrderStatusEnum.PAYMENT) {
            throw new Exception("cannot cancel order");
        }
        orderStatus.cancelOrder();  // 주문 취소

        // 상품 재고 복구
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
//            ProductResDto.ProductDetailDto product = productClient.getProductDetail(orderItem.getProductId());

            productClient.increaseQuantity(new ProductReqDto.ProductQuantityDto(orderItem.getProductId(), orderItem.getQuantity()));
        }
    }

    // 상품 반품
    @Transactional
    public void returnOrder(Long orderId) throws Exception {
        log.info("OrderService.returnOrder()");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        OrderStatus orderStatus = order.getOrderStatus();
        if (orderStatus.getStatus() != OrderStatusEnum.COMPLETED) {
            throw new Exception("cannot cancel order");
        }

        if (LocalDateTime.now().isAfter(order.getChangedDate().plusDays(1))) {  // 해당 주문이 변경된 후 24시간이 지났는지를 판단
            throw new Exception("cannot return order");
        }
        orderStatus.returnOrder();  // 상품 반품
    }

    // 주문 상태 변경 - 자정마다 실행
    @Transactional
    public void updateOrderStatus() {
        // PAYMENT("결제완료") 이후 1일이 넘은 주문들 -> SHIPPING("배송중")
        List<Order> orderedOrders =
                orderRepository.findAllByOrderStatus_StatusAndChangedDateBefore(OrderStatusEnum.PAYMENT, LocalDateTime.now().minusDays(1));

        for (Order order : orderedOrders) {
            order.getOrderStatus().updateShipping();
        }

        // SHIPPING("배송중") 이후 1일이 넘은 주문들 -> COMPLETED("배송완료")
        List<Order> shippingOrders =
                orderRepository.findAllByOrderStatus_StatusAndChangedDateBefore(OrderStatusEnum.SHIPPING, LocalDateTime.now().minusDays(1));

        for (Order order : shippingOrders) {
            order.getOrderStatus().updateCompleted();
        }

        // RETURNING("반품중") 이후 1일이 넘은 주문들 -> RETURNED("반품완료")
        List<Order> returningOrders =
                orderRepository.findAllByOrderStatus_StatusAndChangedDateBefore(OrderStatusEnum.RETURNING, LocalDateTime.now().minusDays(1));

        // 재고 복구
        for (Order order : returningOrders) {
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                productClient.increaseQuantity(new ProductReqDto.ProductQuantityDto(orderItem.getProductId(), orderItem.getQuantity()));
            }
            order.getOrderStatus().updateReturned();
        }
    }

}
