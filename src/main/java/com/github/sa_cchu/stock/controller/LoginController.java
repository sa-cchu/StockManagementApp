package com.github.sa_cchu.stock.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller//画面用のコントローラーと知らせるアノテーション
public class LoginController {

    @GetMapping("/login")//()のゲットリクエスト受け取るアノテーション
    public String login(Authentication authentication) {
        //ログイン済みの場合、トップページにリダイレクト
        if(authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }
        return "login";//返り値はビュー名（login.html)
    }
}
