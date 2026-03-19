package com.github.sa_cchu.stock.controller;

import java.time.LocalDateTime;
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
import com.github.sa_cchu.stock.util.CsvExportUtil;
import com.github.sa_cchu.stock.util.DateTimeUtil;
import com.github.sa_cchu.stock.util.PdfExportUtil;

import jakarta.servlet.http.HttpServletResponse;

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
            @RequestParam(name = "startDate", required = false) String startDateStr,
            @RequestParam(name = "endDate", required = false) String endDateStr,
            Model model) {

        Warehouse warehouse = user.getWarehouse();
        if (warehouse == null) return "redirect:/error";

        LocalDateTime startDate = com.github.sa_cchu.stock.util.DateTimeUtil.parseStartDate(startDateStr);
        LocalDateTime endDate = com.github.sa_cchu.stock.util.DateTimeUtil.parseEndDate(endDateStr);

        List<OrderHistoryDto> orderHistoryList = warehouseOrderService.getOrderHistoryList(warehouse, status, startDate, endDate);
        model.addAttribute("orderHistoryList", orderHistoryList);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("startDate", startDateStr);
        model.addAttribute("endDate", endDateStr);
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

    // CSV エクスポート
    @GetMapping("/warehouse-order/list/csv")
    public void exportCsv(
            @AuthenticationPrincipal User user,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "startDate", required = false) String startDateStr,
            @RequestParam(name = "endDate", required = false) String endDateStr,
            HttpServletResponse response) throws Exception {

        Warehouse warehouse = user.getWarehouse();

        LocalDateTime startDate = DateTimeUtil.parseStartDate(startDateStr);
        LocalDateTime endDate = DateTimeUtil.parseEndDate(endDateStr);

        List<OrderHistoryDto> list = warehouseOrderService.getOrderHistoryList(warehouse, status, startDate, endDate);

        CsvExportUtil.exportOrderHistoryCsv(response, "received_order_list.csv", list, false);
    }

    // PDFエクスポート
    @GetMapping("/warehouse-order/list/pdf")
    public void exportPdf(
            @AuthenticationPrincipal User user,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "startDate", required = false) String startDateStr,
            @RequestParam(name = "endDate", required = false) String endDateStr,
            HttpServletResponse response) throws Exception {

        Warehouse warehouse = user.getWarehouse();

        LocalDateTime startDate = DateTimeUtil.parseStartDate(startDateStr);
        LocalDateTime endDate = DateTimeUtil.parseEndDate(endDateStr);

        List<OrderHistoryDto> list = warehouseOrderService.getOrderHistoryList(warehouse, status, startDate, endDate);

        PdfExportUtil.exportOrderHistoryPdf(response, "received_order_list.pdf", list, false);
    }
}
