package com.github.sa_cchu.stock.controller;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	/**
     * 一括発注用エクセルテンプレートのダウンロード
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadExcel(@AuthenticationPrincipal User user) {
        try {
            // 1. ログインユーザーの店舗情報を取得
            Shop loginShop = user.getShop();
            if (loginShop == null) {
                return ResponseEntity.badRequest().build();
            }

            // 2. サービスを呼び出してエクセル（バイト配列）を生成
            byte[] excelContent = orderImportService.generateOrderExcel(loginShop);

            // 3. ファイル名の設定（日本語が含まれる場合のエンコード処理）
            String fileName = URLEncoder.encode("一括発注シート.xlsx", StandardCharsets.UTF_8).replace("+", "%20");

            // 4. HTTPレスポンスとして返却
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(excelContent.length)
                    .body(excelContent);

        } catch (Exception e) {
            e.printStackTrace();
            // 実務ではログ出力後、エラー画面へ遷移させるか500エラーを返します
            return ResponseEntity.internalServerError().build();
        }
    }
    
    
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

			// Controller
		} catch (Exception e) {
		    // 改行タグ入りのエラーリストを渡す
		    redirectAttributes.addFlashAttribute("errorMsg", "インポートを中断しました。以下のエラーを修正してください。<br>" + e.getMessage());
		}

		return "redirect:/shop-order/goods-list"; // 完了後のリダイレクト先
	}
}