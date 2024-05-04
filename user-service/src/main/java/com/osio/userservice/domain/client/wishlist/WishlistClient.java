package com.osio.userservice.domain.client.wishlist;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "order-service")
public interface WishlistClient {

    // 장바구니 생성
    @PostMapping("/api/internal/wishlist/create/{userId}")
    void createWishlist(@PathVariable("userId") long userId);
}
