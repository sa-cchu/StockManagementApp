package com.github.sa_cchu.stock.dto;

import lombok.Data;

@Data
public class UserListDTO {
    private Integer userId;
    private String userName;
    private String userGender;
    private String belongingName; // 店舗名または倉庫名をここに入れる
}