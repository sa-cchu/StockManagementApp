package com.github.sa_cchu.stock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.github.sa_cchu.stock.entity.Goods;
import com.github.sa_cchu.stock.service.GoodsService;

@Controller
@RequestMapping("/goods") // URLの始まりを /goods に固定
public class GoodsController {

	private final GoodsService goodsService;

	// 推奨されるコンストラクタ注入
	public GoodsController(GoodsService goodsService) {
		this.goodsService = goodsService;
	}

	@GetMapping // NULLでも対応してって指示
	public String listGoods(@RequestParam(name = "categoryId", required = false) Integer categoryId, Model model) {
		// プルダウン用のカテゴリー一覧
		model.addAttribute("categories", goodsService.getAllActiveCategories());

		// 商品一覧（絞り込み対応）
		// HTML側の th:each="goods : ${goodsList}" と合わせる
		model.addAttribute("goodsList", goodsService.getGoodsList(categoryId));

		// 選択されたIDを保持（プルダウンの選択状態用）
		model.addAttribute("selectedId", categoryId);

		return "goodsMG";
	}

	// --- 新規登録画面の表示 ---
	@GetMapping("/new")
	public String newGoods(Model model) {
		// フォームで入力値を保持するための空のGoodsオブジェクトを渡す
		model.addAttribute("goods", new Goods());
		// プルダウン用にカテゴリー一覧も渡す
		model.addAttribute("categories", goodsService.getAllActiveCategories());
		return "goods-form";
	}

	// --- 保存処理 ---
	@PostMapping("/save")
	public String saveGoods(@ModelAttribute Goods goods) {
		// Serviceを呼び出す（ここで店舗・倉庫在庫も一緒に作られる）
		goodsService.saveGoods(goods);
		// 保存が終わったら一覧画面にリダイレクトする
		return "redirect:/goods";
	}

	// 論理削除
	@PostMapping("/delete/{goodsId}")
	public String delete(@PathVariable("goodsId") Integer goodsId, RedirectAttributes redirectAttributes) {
		try {
			goodsService.delelteGoods(goodsId);
			redirectAttributes.addFlashAttribute("Message", "商品を削除しました");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "削除に失敗しました");

		}
		return "redirect:/goods";
	}

}