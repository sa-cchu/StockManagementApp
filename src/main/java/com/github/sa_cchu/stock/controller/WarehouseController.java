package com.github.sa_cchu.stock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	//一覧表示
	@GetMapping
	public String list(Model model) {
		model.addAttribute("warehouses", warehouseService.getAllWarehouses());
		return "warehouseMG";
	}
	
	//新規作成画面
	@GetMapping("/new")
	public String newFrom(Model model) {
		model.addAttribute("warehouse", new Warehouse());
		return "warehouse-form";
	}
	
	//新規追加
	@PostMapping("/add")
	public String add(@ModelAttribute Warehouse warehouse,Model model) {
		//フォームで入力された値をエンティティ入れてメソッドに渡す　//HTMLに渡すための箱
		try {		
			warehouseService.addWarehouse(warehouse);
			return "redirect:/warehouse";
		}catch(IllegalArgumentException e){
			model.addAttribute("errorMessage", e.getMessage());//エラーメッセージを渡す
			model.addAttribute("warehouse",warehouse);//入力済みの値をそのまま渡す
			return "warehouse-form";
		}
	}
	
		// 編集画面
		@GetMapping("/edit/{warehouseId}")
		public String edit(@PathVariable Integer warehouseId, Model model) {
			model.addAttribute("warehouse",
					warehouseService.getWarehouse(warehouseId));
			return "warehouse-form";
		}

		// 更新
		@PostMapping("/update")
		public String update(@ModelAttribute Warehouse warehouse, Model model) {
			try {
				warehouseService.updateWarehouse(warehouse);
				return "redirect:/warehouse";
			} catch (IllegalArgumentException e) {
				model.addAttribute("errorMessage", e.getMessage());
				model.addAttribute("warehouse", warehouse);
				return "warehouse-form";
			}
		}
		
		//削除
		@PostMapping("/delete/{warehouseId}")
		public String delete(@PathVariable Integer warehouseId) {
			warehouseService.deleteWarehouse(warehouseId);
			return "redirect:/warehouse";
		}
	

}
