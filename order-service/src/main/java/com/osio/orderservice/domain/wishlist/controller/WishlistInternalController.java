package com.osio.orderservice.domain.wishlist.controller;

import com.osio.orderservice.domain.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/internal/wishlist")
@RequiredArgsConstructor
public class WishlistInternalController {

    private final WishlistService wishlistService;

    // 장바구니 생성
    @PostMapping("/create/{userId}")
    public void createWishlist(@PathVariable("userId") long userId) {
        log.info("WishlistInternalController.createWishlist()");

        wishlistService.createWishlist(userId);
    }

}
