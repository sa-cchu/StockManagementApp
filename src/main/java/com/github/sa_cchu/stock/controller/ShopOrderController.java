package com.github.sa_cchu.stock.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.github.sa_cchu.stock.dto.ShopOrderTargetDto;
import com.github.sa_cchu.stock.dto.ShopOrderFormDto;
import com.github.sa_cchu.stock.dto.OrderHistoryDto;
import com.github.sa_cchu.stock.entity.Category;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.Goods;
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

    @GetMapping("/shop-order/form")
    public String showOrderForm(@AuthenticationPrincipal User user, @RequestParam(name = "goodsId", required = false) Integer goodsId, Model model) {
        Shop shop = user.getShop();
        if (shop == null) return "redirect:/error";
        if (goodsId == null) return "redirect:/shop-order/goods-list";

        Goods selectedGoods = goodsService.getGoods(goodsId);
        if(selectedGoods == null) return "redirect:/shop-order/goods-list";
        model.addAttribute("goods", selectedGoods);

        ShopOrderFormDto form = shopOrderService.createOrderFormDto(shop, selectedGoods);
        model.addAttribute("shopOrderFormDto", form);
        return "order-form";
    }

    @PostMapping("/shop-order/execute")
    public String executeOrder(@AuthenticationPrincipal User user, @Validated @ModelAttribute("shopOrderFormDto") ShopOrderFormDto form, BindingResult result, Model model, RedirectAttributes redirectAttributes){
        Shop shop = user.getShop();
        if(shop == null) return "redirect:/error";

        Goods goods = goodsService.getGoods(form.getGoodsId());
        if(goods == null) return "redirect:/shop-order/goods-list";
        
        if(result.hasErrors()){
            model.addAttribute("goods", goods);
            return "order-form";
        }

        try {
            shopOrderService.executeOrder(shop, goods, form);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("goods", goods);
            return "order-form";
        }

        redirectAttributes.addFlashAttribute("successMessage", "発注が完了しました!");
        return "redirect:/shop-order/goods-list";
    }

    @GetMapping("/shop-order/history")
    public String showOrderHistory(
            @AuthenticationPrincipal User user,
            @RequestParam(name = "status", required = false) String status,
            Model model) {

        Shop shop = user.getShop();
        if (shop == null) return "redirect:/error";

        List<OrderHistoryDto> orderHistoryList = shopOrderService.getOrderHistoryList(shop, status);
        model.addAttribute("orderHistoryList", orderHistoryList);
        model.addAttribute("selectedStatus", status);
        return "order-history";
    }
}
