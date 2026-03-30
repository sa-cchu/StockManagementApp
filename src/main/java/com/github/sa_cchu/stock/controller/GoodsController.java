package com.github.sa_cchu.stock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.github.sa_cchu.stock.dto.GoodsFormDto;
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
	public String newGoods(@ModelAttribute GoodsFormDto goodsFormDto, Model model) {
		// リダイレクト時に goods が送られてきていなければ、新規作成
	    if (!model.containsAttribute("goodsFormDto")) {
	    	// フォームで入力値を保持するための空のGoodsオブジェクトを渡す
	        model.addAttribute("goodsFormDto", new GoodsFormDto());
	    }
		// プルダウン用にカテゴリー一覧も渡す
		model.addAttribute("categories", goodsService.getAllActiveCategories());
		return "goods-form";
	}

	// --- 保存処理 ---
	@PostMapping("/save")
	public String saveGoods(@Validated @ModelAttribute GoodsFormDto goodsFormDto,
			BindingResult result, RedirectAttributes redirectAttributes, Model model) {
		
		// バリデーションエラー時はフォームを戻す
		if (result.hasErrors()) {
			// 入力した内容を保持したまま、登録画面に戻す
	        redirectAttributes.addFlashAttribute("goodsFormDto", goodsFormDto);
	     // プルダウン用にカテゴリー一覧も渡す
			model.addAttribute("categories", goodsService.getAllActiveCategories());
			return "goods-form";
		}
		
		try {
			// Serviceを呼び出す（ここで店舗・倉庫在庫も一緒に作られる）
			goodsService.saveGoods(goodsFormDto);
			// 保存が終わったら一覧画面にリダイレクトする
			return "redirect:/goods";
		} catch (RuntimeException e) {
			// Serviceで投げた「商品名は既に登録されています」というメッセージを取得
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			// 入力した内容を保持したまま、登録画面に戻す
	        redirectAttributes.addFlashAttribute("goodsFormDto", goodsFormDto);
	        // エラーメッセージを表示してフォームに戻す
	     	return "redirect:/goods/new";
		}
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