package com.osio.productservice.domain.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table
public class Product {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long productId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String category;

    @Column
    private String description;


    public void decreaseQuantity(int quantity) {
        this.quantity -= quantity;
    }

    public void increaseQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void syncQuantity(int quantity) {
        this.quantity = quantity;
    }
}
