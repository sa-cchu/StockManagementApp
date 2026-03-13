package com.github.sa_cchu.stock.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.User;
import com.github.sa_cchu.stock.entity.Warehouse;
import com.github.sa_cchu.stock.service.DashboardService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
	
	private final DashboardService dashboardService;
	@GetMapping("/")
	public String home(@AuthenticationPrincipal User user,  Model model) {

		// ===== グローバルでセットされたログインユーザー取得 =====
		User loginUser = (User) model.getAttribute("loginUser");

		// ===== ログインユーザーそのもの =====
		model.addAttribute("user", loginUser);

		// ===== 権限名（表示用に変換） =====
		String roleName;
		String authorityName = loginUser.getAuthority().getAuthorityName();

		switch (authorityName) {
		case "ROLE_ADMIN":
			roleName = "管理者";
			break;
		case "ROLE_SHOP":
			roleName = "店舗";
			break;
		case "ROLE_WAREHOUSE":
			roleName = "倉庫";
			break;
		default:
			roleName = "不明";
		}
		model.addAttribute("roleName", roleName);

		// ===== 所属名判定 =====
		String belongName;

		Shop shop = loginUser.getShop();
		Warehouse warehouse = loginUser.getWarehouse();

		if (shop != null) {
			belongName = shop.getShopName();
		} else if (warehouse != null) {
			belongName = warehouse.getWarehouseName();
		} else {
			belongName = "無";
		}

		model.addAttribute("belongName", belongName);

		// ===== home.html を表示 =====

		if (user != null && user.getShop() != null) {
			model.addAttribute("allRanking", dashboardService.getAllShopRanking());
			model.addAttribute("shopRanking", dashboardService.getMyShopRanking(user.getShop().getShopId()));
		}
		return "home";
	}

}
