package com.osio.productservice.domain.stock.repository;

import com.osio.productservice.domain.stock.entity.Stock;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.repository.CrudRepository;

@RedisHash("Stock")
public interface StockRepository extends CrudRepository<Stock, Long> {
}
