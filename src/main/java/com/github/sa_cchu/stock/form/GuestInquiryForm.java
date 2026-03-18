package com.github.sa_cchu.stock.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;

/**
 * ゲスト用お問い合わせフォーム
 * - 送信先は必ず管理者なので authorityIdにバリデーション は不要
 * - ログイン時と同じInquiryServiceで保存処理を進めるため targetId はnullで渡す想定
 */
@Data
public class GuestInquiryForm {

    /** お問い合わせ内容 */
    @NotBlank(message = "お問い合わせ内容の入力は必須です")
    @Size(max = 255, message = "お問い合わせ内容は255文字以内で入力してください")
    private String inquiryDetail;
    /**	連携先の店舗ID/倉庫ID ※管理者宛のため、常にnull*/
    private Integer targetId;
    /** hiddenで送信する管理者IDはControllerで設定 */
    private Integer authorityId;
}