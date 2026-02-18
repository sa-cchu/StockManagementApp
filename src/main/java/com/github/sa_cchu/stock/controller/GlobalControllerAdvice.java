package com.github.sa_cchu.stock.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.github.sa_cchu.stock.entity.User;

@ControllerAdvice//全コントローラ共通で処理を差し込むクラスであることを Spring に伝える
public class GlobalControllerAdvice {

    @ModelAttribute("loginUser")//全リクエストの Model に loginUser という名前で値を入れる
    public User loginUser(@AuthenticationPrincipal User user) {
        return user;
    }
}
