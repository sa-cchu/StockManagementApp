package com.github.sa_cchu.stock.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.sa_cchu.stock.dto.UserListDTO;
import com.github.sa_cchu.stock.entity.User;
import com.github.sa_cchu.stock.service.CustomUserDetailsService;
import com.github.sa_cchu.stock.service.ShopService;
import com.github.sa_cchu.stock.service.WarehouseService;

@Controller
@RequestMapping("/user/manage")
public class UserManageController {

	private final CustomUserDetailsService userDetailsService;
	private final ShopService shopService;
	private final WarehouseService warehouseService;

	// その他必要なService
	public UserManageController(CustomUserDetailsService userDetailsService, ShopService shopService,
			WarehouseService warehouseService) {
		this.userDetailsService = userDetailsService;
		this.shopService = shopService;
		this.warehouseService = warehouseService;
	}

	@GetMapping
	public String list(@AuthenticationPrincipal User operator,
			@RequestParam(name = "belongingId", required = false) Integer belongingId, Model model) {

		// DTOを使って、自分と同じ権限のユーザーのみ取得
		List<UserListDTO> dtoList = userDetailsService.getMyTeamUserList(operator, belongingId);

		// 所属絞り込み用プルダウンのデータ準備
		if (operator.getAuthority().getAuthorityName().contains("SHOP")) {
			model.addAttribute("belongings", shopService.getAllShopDTOs()); // IDと名前の共通DTOを渡すのが実務的
		} else {
			model.addAttribute("belongings", warehouseService.getAllWarehouseDTOs());
		}

		model.addAttribute("userList", dtoList);
		model.addAttribute("selectedId", belongingId);

		return "myTeamMG"; // 先ほど作成したHTML
	}
}