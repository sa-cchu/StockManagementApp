package com.github.sa_cchu.stock.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.RequestParam;

import com.github.sa_cchu.stock.dto.WarehouseStockDto;
import com.github.sa_cchu.stock.dto.WarehouseStockFormDto;
import com.github.sa_cchu.stock.entity.User;
import com.github.sa_cchu.stock.entity.Warehouse;
import com.github.sa_cchu.stock.entity.WarehouseStock;
import com.github.sa_cchu.stock.service.GoodsService;
import com.github.sa_cchu.stock.service.WarehouseStockService;

@Controller
@RequestMapping("/warehouse-stock")
public class WarehouseStockController {
    private final WarehouseStockService warehouseStockService;
    private final GoodsService goodsService;

    public WarehouseStockController(WarehouseStockService warehouseStockService, GoodsService goodsService) {
        this.warehouseStockService = warehouseStockService;
        this.goodsService = goodsService;
    }

    // 倉庫在庫一覧取得
    @GetMapping
    public String index(@AuthenticationPrincipal User user,
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            Model model) {

        Warehouse warehouse = user.getWarehouse();
        if (warehouse == null) {
            model.addAttribute("errorMessage", "倉庫が割り当てられていません。");
            return "error";
        }

        // プルダウン用カテゴリ一覧を取得
        model.addAttribute("categories", goodsService.getAllActiveCategories());

        // DTOで在庫リストを取得
        List<WarehouseStockDto> warehouseStocks = warehouseStockService.getWarehouseStockDtoList(warehouse, categoryId);
        model.addAttribute("warehouseStocks", warehouseStocks);
        model.addAttribute("selectedId", categoryId);

        return "warehouse-stock";
    }

    // 倉庫在庫編集ページ遷移
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Integer warehouseStockId,
            @AuthenticationPrincipal User user,
            Model model) {
        Warehouse warehouse = user.getWarehouse();
        WarehouseStock stock = warehouseStockService.getWarehouseStock(warehouseStockId, warehouse);

        WarehouseStockFormDto form = new WarehouseStockFormDto();
        // 現在の在庫数を編集画面に反映
        form.setQuantity(stock.getWarehouseStockQuantity());

        model.addAttribute("warehouseStockId", warehouseStockId);
        model.addAttribute("goodsName", stock.getGoodsId().getGoodsName());
        model.addAttribute("warehouseStockFormDto", form);

        return "warehouse-stock-form";
    }

    // 倉庫在庫更新処理
    @PostMapping("/edit/{id}")
    public String updateStock(@PathVariable("id") Integer warehouseStockId,
            @Validated @ModelAttribute("warehouseStockFormDto") WarehouseStockFormDto form,
            BindingResult result,
            @AuthenticationPrincipal User user,
            Model model) {

        Warehouse warehouse = user.getWarehouse();

        // DTOでエラーが発生した場合、再度編集画面を表示。
        // ユーザーが入力した値、エラーメッセージを保持するため、@GetMapping("/edit/{id}")は再利用できない
        if (result.hasErrors()) {
            WarehouseStock stock = warehouseStockService.getWarehouseStock(warehouseStockId, warehouse);
            model.addAttribute("warehouseStockId", warehouseStockId);
            model.addAttribute("goodsName", stock.getGoodsId().getGoodsName());
            return "warehouse-stock-form";
        }

        // 値を更新
        warehouseStockService.updateStockQuantity(warehouseStockId, form.getQuantity(), warehouse);

        return "redirect:/warehouse-stock";
    }
}
