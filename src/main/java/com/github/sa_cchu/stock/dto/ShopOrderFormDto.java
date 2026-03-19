package com.github.sa_cchu.stock.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.ArrayList;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopOrderFormDto {
    @NotNull(message = "商品が選択されていません")
    private Integer goodsId;

    @Valid
    private List<ShopOrderRowDto> orderRows = new ArrayList<>();
}
