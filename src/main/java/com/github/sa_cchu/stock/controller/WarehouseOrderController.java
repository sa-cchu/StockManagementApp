package com.github.sa_cchu.stock.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.github.sa_cchu.stock.dto.OrderHistoryDto;
import com.github.sa_cchu.stock.entity.User;
import com.github.sa_cchu.stock.entity.Warehouse;
import com.github.sa_cchu.stock.service.WarehouseOrderService;

@Controller
public class WarehouseOrderController {

    private final WarehouseOrderService warehouseOrderService;

    public WarehouseOrderController(WarehouseOrderService warehouseOrderService) {
        this.warehouseOrderService = warehouseOrderService;
    }

    // 受注一覧表示
    @GetMapping("/warehouse-order/list")
    public String showOrderList(
            @AuthenticationPrincipal User user,
            @RequestParam(name = "status", required = false) String status,
            Model model) {

        Warehouse warehouse = user.getWarehouse();
        if (warehouse == null) return "redirect:/error";

        List<OrderHistoryDto> orderHistoryList = warehouseOrderService.getOrderHistoryList(warehouse, status);
        model.addAttribute("orderHistoryList", orderHistoryList);
        model.addAttribute("selectedStatus", status);
        return "warehouse-order-list";
    }

    // ステータス変更
    @PostMapping("/warehouse-order/update-status")
    public String updateStatus(
            @AuthenticationPrincipal User user,
            @RequestParam("orderId") Integer orderId,
            RedirectAttributes redirectAttributes) {

        Warehouse warehouse = user.getWarehouse();
        if (warehouse == null) return "redirect:/error";

        try {
            warehouseOrderService.updateOrderStatus(orderId, warehouse);
            redirectAttributes.addFlashAttribute("successMessage", "ステータスを更新しました。");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/warehouse-order/list";
    }
}
