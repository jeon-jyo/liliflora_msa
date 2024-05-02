package com.osio.orderservice.domain.wishlist.repository;

import com.osio.orderservice.domain.wishlist.entity.WishItem;
import com.osio.orderservice.domain.wishlist.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WishItemRepository extends JpaRepository<WishItem, Long> {
//    Optional<WishItem> findWishItemByWishlistAndProductAndDeletedFalse(Wishlist wishlist, Product product);
    Optional<WishItem> findWishItemByWishlistAndProductIdAndDeletedFalse(Wishlist wishlist, long productId);
}
