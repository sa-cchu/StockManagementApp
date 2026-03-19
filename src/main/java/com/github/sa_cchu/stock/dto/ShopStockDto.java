package com.github.sa_cchu.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopStockDto {
    private Integer shopStockId;
    private String shopName;
    private String categoryName;
    private String goodsName;
    private Integer shopStockQuantity;
}
