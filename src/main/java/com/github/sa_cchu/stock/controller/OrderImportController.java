package com.github.sa_cchu.stock.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.github.sa_cchu.stock.dto.OrderImportDto;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.User;
import com.github.sa_cchu.stock.service.OrderImportService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderImportController {

	private final OrderImportService orderImportService;

	@PostMapping("/import")
	public String importExcel(
	        @RequestParam("file") MultipartFile file, 
	        @AuthenticationPrincipal User user, 
	        RedirectAttributes redirectAttributes) {

	    Shop loginShop = user.getShop();
	    if (loginShop == null) {
	        redirectAttributes.addFlashAttribute("errorMsg", "店舗情報が見つかりません。");
	        return "redirect:/shop-order/goods-list";
	    }

		

		// 2. ファイルが空でないかチェック
		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMsg", "ファイルを選択してください。");
			return "redirect:/shop-order/goods-list"; // 任意の遷移先
		}

		try {
			// 3. Excel解析 → 一括実行
			List<OrderImportDto> dtoList = orderImportService.parseExcel(file);
			orderImportService.executeImport(loginShop, dtoList);

			// 成功メッセージ
			redirectAttributes.addFlashAttribute("successMsg", "一括発注インポートが正常に完了しました。");

		} catch (Exception e) {
			// 4. サービスから飛んできた Exception (行番号入り) をキャッチして画面に表示
			// 全ロールバックされているので、DBは安全です
			redirectAttributes.addFlashAttribute("errorMsg", "インポートに失敗しました。全処理を中断しました。理由: " + e.getMessage());
		}

		return "redirect:/shop-order/goods-list"; // 完了後のリダイレクト先
	}
}