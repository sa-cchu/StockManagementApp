package com.github.sa_cchu.stock.dto;

import com.github.sa_cchu.stock.form.InquiryForm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 連携先として取得した選択肢を店舗・倉庫で共通の値にして受け渡すためのDTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryRequestDto {
	
	/** 送り先の権限ID */
	private Integer authorityId; // ROLE_ADMIN, ROLE_SHOP, ROLE_WAREHOUSE
	/**	連携先の店舗ID/倉庫ID */
	private Integer targetId;
	/**	内容 */
    private String inquiryDetail;
    /**
     * 
     * @param form 入力情報
     * @return 変換後の値
     */
    public static InquiryRequestDto from(InquiryForm form) {
        return new InquiryRequestDto(
                form.getAuthorityId(),
                form.getTargetId(),
                form.getInquiryDetail()
        );
    }

}
