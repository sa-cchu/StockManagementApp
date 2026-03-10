package com.github.sa_cchu.stock.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.RequestParam;

import com.github.sa_cchu.stock.dto.ShopStockDto;
import com.github.sa_cchu.stock.dto.ShopStockFormDto;
import com.github.sa_cchu.stock.entity.User;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.ShopStock;
import com.github.sa_cchu.stock.service.GoodsService;
import com.github.sa_cchu.stock.service.ShopStockService;

@Controller
@RequestMapping("/shop-stock")
public class ShopStockController {
    private final ShopStockService shopStockService;
    private final GoodsService goodsService;

    public ShopStockController(ShopStockService shopStockService, GoodsService goodsService) {
        this.shopStockService = shopStockService;
        this.goodsService = goodsService;
    }

    // 倉庫在庫一覧取得
    @GetMapping
    public String index(@AuthenticationPrincipal User user,
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            Model model) {

        Shop shop = user.getShop();
        if (shop == null) {
            model.addAttribute("errorMessage", "店舗が割り当てられていません。");
            return "error";
        }

        // プルダウン用カテゴリ一覧を取得
        model.addAttribute("categories", goodsService.getAllActiveCategories());

        // DTOで在庫リストを取得
        List<ShopStockDto> shopStocks = shopStockService.getShopStockDtoList(shop, categoryId);
        model.addAttribute("shopStocks", shopStocks);
        model.addAttribute("selectedId", categoryId);

        return "shop-stock";
    }

    // 商品在庫編集ページ遷移
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Integer shopStockId,
            @AuthenticationPrincipal User user,
            Model model) {
        Shop shop = user.getShop();
        ShopStock stock = shopStockService.getShopStock(shopStockId, shop);

        ShopStockFormDto form = new ShopStockFormDto();
        // 現在の在庫数を編集画面に反映
        form.setQuantity(stock.getShopStockQuantity());

        model.addAttribute("shopStockId", shopStockId);
        model.addAttribute("goodsName", stock.getGoodsId().getGoodsName());
        model.addAttribute("shopStockFormDto", form);

        return "shop-stock-form";
    }

    // 商品在庫更新処理
    @PostMapping("/edit/{id}")
    public String updateStock(@PathVariable("id") Integer shopStockId,
            @Validated @ModelAttribute("shopStockFormDto") ShopStockFormDto form,
            BindingResult result,
            @AuthenticationPrincipal User user,
            Model model) {

        Shop shop = user.getShop();

        // DTOでエラーが発生した場合、再度編集画面を表示。
        // ユーザーが入力した値、エラーメッセージを保持するため、@GetMapping("/edit/{id}")は再利用できない
        if (result.hasErrors()) {
            ShopStock stock = shopStockService.getShopStock(shopStockId, shop);
            model.addAttribute("shopStockId", shopStockId);
            model.addAttribute("goodsName", stock.getGoodsId().getGoodsName());
            return "shop-stock-form";
        }

        // 値を更新
        shopStockService.updateStockQuantity(shopStockId, form.getQuantity(), shop);

        return "redirect:/shop-stock";
    }
}
