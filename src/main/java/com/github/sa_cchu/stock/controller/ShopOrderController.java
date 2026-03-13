package com.github.sa_cchu.stock.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.sa_cchu.stock.dto.ShopOrderTargetDto;
import com.github.sa_cchu.stock.entity.Category;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.User;
import com.github.sa_cchu.stock.service.GoodsService;
import com.github.sa_cchu.stock.service.ShopOrderService;

@Controller
public class ShopOrderController {

    private final ShopOrderService shopOrderService;
    private final GoodsService goodsService; // カテゴリ一覧を取得するために必要

    public ShopOrderController(ShopOrderService shopOrderService, GoodsService goodsService) {
        this.shopOrderService = shopOrderService;
        this.goodsService = goodsService;
    }

    @GetMapping("/shop-order/goods-list")
    public String showOrderGoodsList(@RequestParam(name = "categoryId", required = false) Integer categoryId,
            @AuthenticationPrincipal User user, Model model) {

        Shop shop = user.getShop();
        if (shop == null) {
            return "redirect:/error";
        }  
        List<Category> categories = goodsService.getAllActiveCategories();
        List<ShopOrderTargetDto> targetGoodsList = shopOrderService.getShopOrderTargetDtoList(shop, categoryId);

        model.addAttribute("categories", categories);
        model.addAttribute("targetGoodsList", targetGoodsList);
        model.addAttribute("selectedCategoryId", categoryId);

        return "order-goods-list";
    }
}
