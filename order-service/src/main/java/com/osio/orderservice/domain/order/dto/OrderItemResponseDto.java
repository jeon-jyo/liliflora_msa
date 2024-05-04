package com.osio.orderservice.domain.order.dto;

import com.osio.orderservice.domain.client.product.dto.ProductResDto;
import com.osio.orderservice.domain.order.entity.OrderItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class OrderItemResponseDto {

//    @Setter
    @Getter
    @Builder
    public static class OrderItemCheckDto {
        private long orderItemId;

        private long productId;

        private String name;

        private int quantity;

        public static OrderItemCheckDto fromEntityAndDto(OrderItem orderItem, ProductResDto.ProductDetailDto productDetailDto) {
            return OrderItemCheckDto.builder()
                    .orderItemId(orderItem.getOrderItemId())
                    .productId(orderItem.getProductId())
                    .name(productDetailDto.getName())
                    .quantity(orderItem.getQuantity())
                    .build();
        }
    }
}
