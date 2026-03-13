package com.github.sa_cchu.stock.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.github.sa_cchu.stock.entity.Goods;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopOrderTargetDto {
    private Goods goods;
    private String categoryName;
    private Integer totalStockQuantity;
}
