package com.osio.orderservice.domain.wishlist.controller;

import com.osio.orderservice.domain.wishlist.dto.WishItemRequestDto;
import com.osio.orderservice.domain.wishlist.dto.WishItemResponseDto;
import com.osio.orderservice.domain.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    // 장바구니 추가
    @PostMapping("/add")
    public WishItemResponseDto.WishItemCheckDto addWishlist(@RequestBody WishItemRequestDto.AddWishItemDto addWishlistDto,
                                                            @RequestHeader(value = "userId") String userId) {
        log.info("WishlistController.addWishlist()");

        return wishlistService.addWishlist(addWishlistDto, Long.valueOf(userId));
    }

    // 장바구니 조회
    @GetMapping("/my")
    public List<WishItemResponseDto.WishItemCheckDto> myWishlist(@RequestHeader(value = "userId") String userId) {
        log.info("WishlistController.myWishlist()");

        return wishlistService.myWishlist(Long.valueOf(userId));
    }

    // 장바구니 수량 변경
    @PutMapping("/update")
    public WishItemResponseDto.WishItemCheckDto updateWishlist(@RequestBody WishItemRequestDto.UpdateWishItemDto updateWishItemDto,
                                                               @RequestHeader(value = "userId") String userId) {
        log.info("WishlistController.updateWishlist()");

        return wishlistService.updateWishlist(updateWishItemDto, Long.valueOf(userId));
    }

    // 장바구니 삭제
    @PutMapping("/delete")
    public boolean deleteWishlist(@RequestBody WishItemRequestDto.UpdateWishItemDto updateWishItemDto,
                                  @RequestHeader(value = "userId") String userId) {
        log.info("WishlistController.deleteWishlist()");

        wishlistService.deleteWishlist(updateWishItemDto, Long.valueOf(userId));
        return true;
    }

}
