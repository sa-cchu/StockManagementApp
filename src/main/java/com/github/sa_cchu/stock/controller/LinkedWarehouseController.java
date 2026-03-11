package com.github.sa_cchu.stock.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.github.sa_cchu.stock.dto.LinkedWarehouseDto;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.User;
import com.github.sa_cchu.stock.service.LinkedWarehouseService;

@Controller
public class LinkedWarehouseController {

    private final LinkedWarehouseService linkedWarehouseService;

	public LinkedWarehouseController(LinkedWarehouseService linkedWarehouseService) {
		this.linkedWarehouseService = linkedWarehouseService;
	}

	@GetMapping("/linked-warehouses")
	public String showLinkedWarehouses(@AuthenticationPrincipal User user, Model model) {
		Shop shop = user.getShop();
		if (shop == null) return "error";
		List<LinkedWarehouseDto> linkedWarehouses = linkedWarehouseService.getLinkedWarehouses(shop);
		model.addAttribute("linkedWarehouses", linkedWarehouses);
		return "linked-warehouses";
	}
}
