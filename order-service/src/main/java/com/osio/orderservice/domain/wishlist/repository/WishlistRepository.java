package com.osio.orderservice.domain.wishlist.repository;

import com.osio.orderservice.domain.wishlist.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
//    Optional<Wishlist> findByUser(User user);
}