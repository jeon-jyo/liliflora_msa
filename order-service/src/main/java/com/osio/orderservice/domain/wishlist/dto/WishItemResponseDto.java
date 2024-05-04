package com.osio.orderservice.domain.wishlist.dto;

import com.osio.orderservice.domain.wishlist.entity.WishItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class WishItemResponseDto {

    @Getter
//    @Setter
    @Builder
    public static class WishItemCheckDto {
        private long wishItemId;

        private long wishlistId;

        private long productId;

        private int quantity;

        public static WishItemCheckDto fromEntity(WishItem wishItem) {
            return WishItemCheckDto.builder()
                    .wishItemId(wishItem.getWishItemId())
                    .wishlistId(wishItem.getWishlist().getWishlistId())
                    .productId(wishItem.getProductId())
                    .quantity(wishItem.getQuantity())
                    .build();
        }
    }
}
