package com.osio.orderservice.domain.wishlist.service;

import com.osio.orderservice.domain.wishlist.dto.WishItemRequestDto;
import com.osio.orderservice.domain.wishlist.dto.WishItemResponseDto;
import com.osio.orderservice.domain.wishlist.entity.WishItem;
import com.osio.orderservice.domain.wishlist.entity.Wishlist;
import com.osio.orderservice.domain.wishlist.repository.WishItemRepository;
import com.osio.orderservice.domain.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishItemRepository wishItemRepository;

    // 장바구니 상품 추가
    @Transactional
    public WishItemResponseDto.WishItemCheckDto addWishlist(WishItemRequestDto.AddWishItemDto addWishlistDto, Long userId) {
        log.info("WishlistService.addWishlist()");

        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist not found " + userId));

        WishItem wishItem = confirmWishItem(addWishlistDto, wishlist, addWishlistDto.getProductId());
        return WishItemResponseDto.WishItemCheckDto.fromEntity(wishItem);
    }

    // 장바구니 상품 확인 - 추가 및 수정
    @Transactional
    protected WishItem confirmWishItem(WishItemRequestDto.AddWishItemDto addWishlistDto, Wishlist wishlist, long productId) {
        Optional<WishItem> currentWishItem =
                wishItemRepository.findWishItemByWishlistAndProductIdAndDeletedFalse(wishlist, productId);

        WishItem wishItem;
        if (currentWishItem.isPresent()) {  // 장바구니에 해당 상품이 존재한다면 수량 업데이트
            wishItem = currentWishItem.get();   // getOne()은 엔티티를 가져오는 동안 지연 로딩을 허용하기 때문에 사용자 식별자로 엔티티를 가져올 때 효율적
            wishItem.increaseQuantity(addWishlistDto.getQuantity());
        } else {    // 존재하지 않는다면 장바구니 추가
            wishItem = WishItem.builder()
                    .wishlist(wishlist)
                    .productId(productId)
                    .quantity(addWishlistDto.getQuantity())
                    .build();
        }
        wishItemRepository.save(wishItem);
        return wishItem;
    }

    // 장바구니 조회
    @Transactional
    public List<WishItemResponseDto.WishItemCheckDto> myWishlist(Long userId) {
        log.info("WishlistService.myWishlist()");

        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist not found " + userId));

        // List<WishItem> 가 자동으로 불러와짐
        List<WishItem> wishItems = wishlist.getWishItems();

        List<WishItem> currentWishItems = wishItems.stream()    // 리스트 -> 스트림
                .filter(wishItem -> !wishItem.isDeleted())  // wishItem 객체에 대해 삭제되지 않은 경우만 필터
                .toList();  // 리스트로 수집

        // map() : 각 WishItem 객체를 새로운 요소(fromEntity)로 매핑
        return currentWishItems.stream().map(WishItemResponseDto.WishItemCheckDto::fromEntity).toList();
    }

    // 장바구니 수량 변경
    @Transactional
    public WishItemResponseDto.WishItemCheckDto updateWishlist(WishItemRequestDto.UpdateWishItemDto updateWishItemDto, Long userId) {
        log.info("WishlistService.updateWishlist()");

        WishItem wishItem = wishItemRepository.findById(updateWishItemDto.getWishItemId())
                .orElseThrow(() -> new IllegalArgumentException("WishItem not found " + userId));

        wishItem.updateQuantity(updateWishItemDto.getQuantity());
        wishItemRepository.save(wishItem);
        return WishItemResponseDto.WishItemCheckDto.fromEntity(wishItem);
    }

    // 장바구니 삭제
    @Transactional
    public void deleteWishlist(WishItemRequestDto.UpdateWishItemDto updateWishItemDto, Long userId) {
        log.info("WishlistService.deleteWishlist()");

        WishItem wishItem = wishItemRepository.findById(updateWishItemDto.getWishItemId())
                .orElseThrow(() -> new IllegalArgumentException("WishItem not found " + userId));

        wishItem.updateDeleted();
    }

    // feign

    // 장바구니 생성
    @Transactional
    public void createWishlist(long userId) {
        log.info("WishlistService.createWishlist()");

        // Wishlist 생성 및 연결
        Wishlist wishlist = Wishlist.builder()
                .userId(userId)
                .build();
        wishlistRepository.save(wishlist);
    }

}
