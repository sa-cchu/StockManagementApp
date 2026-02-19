package com.github.sa_cchu.stock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.sa_cchu.stock.entity.Warehouse;
import com.github.sa_cchu.stock.service.WarehouseService;

@Controller
@RequestMapping("/warehouse")
public class WarehouseController {

	private final WarehouseService warehouseService;

	public WarehouseController(WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}

	@GetMapping
	public String list(Model model) {
		model.addAttribute("warehouses", warehouseService.getAllWarehouses());
		return "warehouseMG";
	}

	@GetMapping("/new")
	public String newFrom(Model model) {
		model.addAttribute("warehouse", new Warehouse());
		return "warehouse-form";
	}

	@PostMapping("/add")
	public String save(@ModelAttribute Warehouse warehouse,Model model) {
		try {
			warehouseService.addWarehouse(warehouse);
			return "redirect:/warehouse";
		}catch(IllegalArgumentException e){
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("warehouse",warehouse);
			return "warehouse-form";
		}
	}

}
