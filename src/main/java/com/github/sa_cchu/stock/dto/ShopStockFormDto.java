package com.github.sa_cchu.stock.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopStockFormDto {

    @NotNull(message = "数量を入力してください")
    @Min(value = 0, message = "数量は0以上を入力してください")
    private Integer quantity;
}
