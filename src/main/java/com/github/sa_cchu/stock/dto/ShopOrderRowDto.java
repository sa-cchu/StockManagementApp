package com.github.sa_cchu.stock.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopOrderRowDto {
    private Integer warehouseId;

    private String warehouseName;

    private Integer stockQuantity;

    @Min(value = 1, message = "発注数は1以上を入力してください")
    private Integer orderQuantity;
}
