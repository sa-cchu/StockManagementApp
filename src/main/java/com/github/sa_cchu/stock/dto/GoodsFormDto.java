package com.github.sa_cchu.stock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新商品追加の画面の入力値を受け取るクラス
 * （他のクラスと統一性を持たせるためformクラスではなくDTOとして実装）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsFormDto {
	/** 商品ID */
    private Integer goodsId;
    /** 商品名 */
    @NotBlank(message = "商品名の入力は必須です")
    @Size(max = 255, message = "商品名は255文字以内で入力してください")
    private String goodsName;
    /** カテゴリー 外部キー（category_id） */
    @NotNull(message = "カテゴリーの選択は必須です")
    private Integer categoryId;
}
