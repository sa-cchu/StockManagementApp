package com.github.sa_cchu.stock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.service.ShopService;

@Controller
@RequestMapping("/shop")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    // 一覧表示
    @GetMapping
    public String list(Model model) {
        model.addAttribute("shops", shopService.getAllShop()); // ← 複数形に統一
        return "shopMG";
    }

    // 追加画面表示
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("shop", new Shop());
        return "shop-form";
    }

    // 保存
    @PostMapping("/add")
    public String save(@ModelAttribute Shop shop) {
        shopService.addShop(shop);
        return "redirect:/shop";
    }
}
