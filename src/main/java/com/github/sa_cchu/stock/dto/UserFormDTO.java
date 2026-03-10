package com.github.sa_cchu.stock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data // Getter, Setter, toString, equals, hashCodeをすべて自動生成
public class UserFormDTO {

    private Integer userId;

    @NotBlank(message = "ユーザー名を入力してください")
    private String userName;

    // 入力時のバリデーション（生パスワードの長さチェック）
    @Size(min = 8, message = "パスワードは8文字以上で入力してください")
    private String userPassword;

    @NotBlank(message = "性別を選択してください")
    private String userGender;

    @NotNull(message = "権限を選択してください")
    private Integer authorityId;

    private Integer shopId;
    private Integer warehouseId;
}