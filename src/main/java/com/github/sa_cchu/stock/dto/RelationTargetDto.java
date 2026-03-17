package com.github.sa_cchu.stock.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 連携先として取得した選択肢を店舗・倉庫で共通の値にして受け渡すためのDTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelationTargetDto {
	
	 /** 連携先の店舗ID・倉庫名ID */
	 private Integer relationTargetId;
	 /** 連携先の店舗名・倉庫名 */
	 private String relationTargetName;
	
}
