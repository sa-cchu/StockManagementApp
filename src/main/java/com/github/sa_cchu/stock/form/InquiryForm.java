package com.github.sa_cchu.stock.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;

/**
 * お問い合わせ送信画面で入力した内容を controllerに渡すformクラス
 */
@Data
public class InquiryForm {
	/** 送信先の権限ID */
	@NotNull(message = "送信先の選択は必須です")
	private Integer authorityId; // 1:管理者、2:店舗、3:倉庫
	/**	連携先の店舗ID/倉庫ID */
	// 選択不備バリデーションはcontrollerで制御する
    private Integer targetId;
	/**	お問い合わせ内容 */
    @Size(max = 255, message = "お問い合わせ内容は255文字以内で入力してください")
    @Pattern(regexp = ".*[^\\s　].*", message = "お問い合わせ内容の入力は必須です（※スペースのみの入力不可）")
    private String inquiryDetail;
    
}