package com.github.sa_cchu.stock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class UserFormDTO {

    private Integer userId;

    @NotBlank(message = "ユーザー名を入力してください")
    private String userName;

    // パスワード（編集時は空でもOKとするため、バリデーションはService側で行うのが実務的です）
    @Size(min = 8, message = "パスワードは8文字以上で入力してください")
    private String userPassword;

    @NotBlank(message = "性別を選択してください")
    private String userGender;

    // 権限や所属の表示用
    
    private Integer authorityId;
    private String authorityName;
    private String belongingName;

    /**
     * 追加：所属先（ShopId または WarehouseId）の値を保持する
     * Thymeleafのプルダウン選択値を受け取ります
     */
    @NotNull(message = "所属先を選択してください")
    private Integer belongingId;
}