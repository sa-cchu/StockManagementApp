package com.github.sa_cchu.stock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.service.ShopService;

@Controller
@RequestMapping("/shop") //コントローラー全体の基本パスを設定
public class ShopController {

	private final ShopService shopService;

	public ShopController(ShopService shopService) {
		this.shopService = shopService;
	}

	// 一覧表示
	@GetMapping //店舗リストモデルに入れてshopMGに画面遷移させる
	public String list(Model model) {
		model.addAttribute("shops", shopService.getAllShop()); // 
		return "shopMG";
	}

	// 追加画面表示
	@GetMapping("/new")
	public String newForm(Model model) {
		model.addAttribute("shop", new Shop());//入力値を受け取る値を空箱を作っている
		//紐づけ先　//入力値を受け取る空箱
		return "shop-form";
	}

	// 保存
	@PostMapping("/add")
	public String save(@ModelAttribute Shop shop, Model model) {
		try {
			shopService.addShop(shop);
			return "redirect:/shop";
		} catch (IllegalArgumentException e) {
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("shop", shop);
			return "shop-form";
		}

	}

	//編集画面
	@GetMapping("/edit/{shopId}")
	public String edit(@PathVariable Integer shopId, Model model) {
		model.addAttribute("shop", shopService.getShop(shopId));
		return "shop-form";
	}

	//更新
	@PostMapping("/update")
	public String update(@ModelAttribute Shop shop, Model model) {
		try {
			shopService.updateShop(shop);
			return "redirect:/shop";
		} catch (IllegalArgumentException e) {
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("shop", shop);
			return "shop-form";
		}
	}

	//削除
	@PostMapping("/delete/{shopId}")
	public String delete(@PathVariable Integer shopId) {
		shopService.deleteShop(shopId);
		return "redirec:/shop";
	}
}
