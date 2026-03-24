package com.github.sa_cchu.stock.form;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;

/**
 * ゲスト用お問い合わせフォーム
 * - 送信先は必ず管理者なので authorityIdにバリデーション は不要
 * - ログイン時と同じInquiryServiceで保存処理を進めるため targetId はnullで渡す想定
 */
@Data
public class GuestInquiryForm {
	/**  送信先の権限ID 管理者宛で固定しているため、画面からADMIN_ID：1で受け取る*/
	private Integer authorityId;
	/**	連携先の店舗ID/倉庫ID ※管理者宛のため、常にnull*/
	private Integer targetId;
    /** お問い合わせ内容 */
    @Size(max = 255, message = "お問い合わせ内容は255文字以内で入力してください")
    @Pattern(regexp = "(?s).*[^\\s　].*", message = "お問い合わせ内容の入力は必須です（※スペースのみの入力不可）")
    private String inquiryDetail;
}