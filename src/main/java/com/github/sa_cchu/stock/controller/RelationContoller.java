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

import com.github.sa_cchu.stock.entity.Relation;
import com.github.sa_cchu.stock.service.RelationService;

@Controller
@RequestMapping("/relation")
public class RelationContoller {

	private final RelationService relationService;

	public RelationContoller(RelationService relationService) {
		this.relationService = relationService;

	}

	@GetMapping
	public String listRelation(@RequestParam(name = "shopId", required = false) Integer shopId, Model model) {

		model.addAttribute("shop", relationService.getAllActiveShops());
		model.addAttribute("relationList", relationService.getRelationList(shopId));
		model.addAttribute("selected", shopId);

		return "relationMG";

	}

	@GetMapping("/new")
	public String newRelation(Model model) {
		model.addAttribute("relation", new Relation());
		model.addAttribute("shop", relationService.getAllActiveShops());
		model.addAttribute("warehouse", relationService.getAllActivesWarehouses());

		return "relation-form";
	}

	@PostMapping("/save")
	public String saveRelation(@ModelAttribute("relation") Relation relation, Model model) {

		// 1. 店舗IDが未選択(null)または0でないか手動でチェック
		if (relation.getShop() == null || relation.getShop().getShopId() == null) {
			model.addAttribute("errorMessage", "店舗を選択してください");
			return reloadForm(model);
		}

		// 2. 倉庫IDが未選択(null)または0でないか手動でチェック
		if (relation.getWarehouse() == null || relation.getWarehouse().getWarehouseId() == null) {
			model.addAttribute("errorMessage", "倉庫を選択してください");
			return reloadForm(model);
		}

		// 3. 重複チェック & 保存
		try {
			// Service側で重複チェックを行い、問題なければ保存する
			relationService.saveRelation(relation);
		} catch (Exception e) {
			// Serviceから投げられた「既に登録されています」などのメッセージを表示
			model.addAttribute("errorMessage", e.getMessage());
			return reloadForm(model);
		}
		return "redirect:/relation";
	}

	@GetMapping("/edit/{id}")
	public String editRelation(@PathVariable("id") Integer id, Model model) {
		Relation relation = relationService.getRelationById(id);
		model.addAttribute("relation", relation);
		model.addAttribute("shop", relationService.getAllActiveShops());
		model.addAttribute("warehouse", relationService.getAllActivesWarehouses());
		return "relation-form";

	}

	// 共通の再読み込み処理
	private String reloadForm(Model model) {
		model.addAttribute("shop", relationService.getAllActiveShops());
		model.addAttribute("warehouse", relationService.getAllActivesWarehouses());
		// relationIdがない場合は新規なので、空のrelationも必要に応じて調整
		return "relation-form";
	}

	@PostMapping("delete/{relationId}")
	public String delete(@PathVariable("relationId") Integer relationId, RedirectAttributes redirectAttributes) {
		try {
			relationService.deleteRelation(relationId);
			redirectAttributes.addFlashAttribute("Massege", "連携削除しました");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "削除に失敗しました");
		}
		return "redirect:/relation";
	}

}