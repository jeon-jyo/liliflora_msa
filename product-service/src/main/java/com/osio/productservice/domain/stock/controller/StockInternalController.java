package com.osio.productservice.domain.stock.controller;

import com.osio.productservice.domain.client.order.dto.ProductReqDto;
import com.osio.productservice.domain.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/internal/stock")
@RequiredArgsConstructor
public class StockInternalController {

    private final StockService stockService;

    // redis 재고 감소
    @PostMapping("/decrease")
    boolean decreaseStockQuantity(@RequestBody ProductReqDto.ProductQuantityDto productQuantityDto) throws Exception {
        log.info("StockInternalController.decreaseStockQuantity()");

        return stockService.decreaseStockQuantity(productQuantityDto);
    }

    // redis 재고 복구
    @PostMapping("/increase")
    void increaseStockQuantity(@RequestBody ProductReqDto.ProductQuantityDto productQuantityDto) {
        log.info("StockInternalController.increaseStockQuantity()");

        stockService.increaseStockQuantity(productQuantityDto);
    }
}
