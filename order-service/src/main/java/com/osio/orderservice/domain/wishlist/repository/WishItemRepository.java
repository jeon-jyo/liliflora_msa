package com.osio.orderservice.domain.wishlist.repository;

import com.osio.orderservice.domain.wishlist.entity.WishItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishItemRepository extends JpaRepository<WishItem, Long> {
//    Optional<WishItem> findWishItemByWishlistAndProductAndDeletedFalse(Wishlist wishlist, Product product);
}
