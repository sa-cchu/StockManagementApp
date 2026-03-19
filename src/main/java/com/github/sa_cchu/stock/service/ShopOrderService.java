package com.github.sa_cchu.stock.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.sa_cchu.stock.dto.OrderHistoryDto;
import com.github.sa_cchu.stock.dto.ShopOrderFormDto;
import com.github.sa_cchu.stock.dto.ShopOrderRowDto;
import com.github.sa_cchu.stock.dto.ShopOrderTargetDto;
import com.github.sa_cchu.stock.entity.Goods;
import com.github.sa_cchu.stock.entity.Orders;
import com.github.sa_cchu.stock.entity.Relation;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.ShopStock;
import com.github.sa_cchu.stock.entity.Warehouse;
import com.github.sa_cchu.stock.entity.WarehouseStock;
import com.github.sa_cchu.stock.repository.OrdersRepository;
import com.github.sa_cchu.stock.repository.RelationRepository;
import com.github.sa_cchu.stock.repository.ShopStockRepository;
import com.github.sa_cchu.stock.repository.WarehouseStockRepository;

@Service
public class ShopOrderService {

    private final WarehouseStockRepository warehouseStockRepository;
    private final GoodsService goodsService;
    private final WarehouseService warehouseService;
    private final RelationRepository relationRepository;
    private final OrdersRepository ordersRepository;
    private final ShopStockRepository shopStockRepository;

    public ShopOrderService(WarehouseStockRepository warehouseStockRepository, GoodsService goodsService,
            WarehouseService warehouseService,
            RelationRepository relationRepository, OrdersRepository ordersRepository, ShopStockRepository shopStockRepository) {
        this.warehouseStockRepository = warehouseStockRepository;
        this.goodsService = goodsService;
        this.warehouseService = warehouseService;
        this.relationRepository = relationRepository;
        this.ordersRepository = ordersRepository;
        this.shopStockRepository = shopStockRepository;
    }

    // 商品ごとの総在庫数DTOを作成
    public List<ShopOrderTargetDto> getShopOrderTargetDtoList(Shop shop, Integer categoryId) {
        List<Goods> goodsList = goodsService.getGoodsList(categoryId);

        // 連携倉庫の在庫合計を一括取得してマップ化 (goodsId -> totalQuantity)
        List<Object[]> stockSummaryList = warehouseStockRepository.getTotalStockMapByLinkedWarehouses(shop);
        Map<Integer, Integer> stockMap = stockSummaryList.stream()
            .collect(Collectors.toMap(
                row -> (Integer) row[0],
                row -> ((Number) row[1]).intValue()
            ));

        return goodsList.stream()
                .map(goods -> {
                    ShopOrderTargetDto dto = new ShopOrderTargetDto();
                    dto.setGoods(goods);
                    dto.setCategoryName(goods.getCategory().getCategoryName());

                    Integer totalQuantity = stockMap.get(goods.getGoodsId());
                    dto.setTotalStockQuantity(totalQuantity != null ? totalQuantity : 0);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 商品別発注画面用DTOを作成
    public ShopOrderFormDto createOrderFormDto(Shop shop, Goods goods) {
        ShopOrderFormDto form = new ShopOrderFormDto();
        form.setGoodsId(goods.getGoodsId());

        List<Relation> relations = relationRepository.findByShopAndDeleteFlag(shop, 0);
        if (relations == null || relations.isEmpty()) {
            return form;
        }

        for (Relation relation : relations) {
            Warehouse warehouse = relation.getWarehouse();
            WarehouseStock stock = warehouseStockRepository.findByWarehouseIdAndGoodsIdAndDeleteFlag(warehouse, goods,
                    0);
            ShopOrderRowDto row = new ShopOrderRowDto();
            row.setWarehouseId(warehouse.getWarehouseId());
            row.setWarehouseName(warehouse.getWarehouseName());
            if (stock != null) {
                row.setStockQuantity(stock.getWarehouseStockQuantity());
            } else {
                row.setStockQuantity(0);
            }
            form.getOrderRows().add(row);
        }
        return form;
    }

    // 発注保存、店舗在庫更新処理
    @Transactional
    public void executeOrder(Shop shop, Goods goods, ShopOrderFormDto form) throws Exception {

        int orderedCount = 0;
        int totalOrderedQuantity = 0;

        for (ShopOrderRowDto row : form.getOrderRows()) {
            if (row.getOrderQuantity() == null || row.getOrderQuantity() <= 0) {
                continue;
            }
            orderedCount++;

            Warehouse targetWarehouse = warehouseService.getWarehouse(row.getWarehouseId());
            if(!relationRepository.existsByShopAndWarehouseAndDeleteFlag(shop, targetWarehouse, 0)) {
                throw new Exception("連携していない倉庫への発注はできません。");
            }

            WarehouseStock latestStock = warehouseStockRepository
                    .findByWarehouseIdAndGoodsIdAndDeleteFlag(targetWarehouse, goods, 0);
            if (latestStock == null || latestStock.getWarehouseStockQuantity() < row.getOrderQuantity()) {
                throw new Exception("問題が発生したため、発注処理が完了できませんでした。");
            }

            int newQuantity = latestStock.getWarehouseStockQuantity() - row.getOrderQuantity();
            latestStock.setWarehouseStockQuantity(newQuantity);
            warehouseStockRepository.save(latestStock);

            Orders newOrder = new Orders();
            newOrder.setShop(shop);
            newOrder.setGoods(goods);
            newOrder.setWarehouse(targetWarehouse);
            newOrder.setOrderAmount(row.getOrderQuantity());
            newOrder.setOrderStatus("準備中");
            newOrder.setOrderDate(LocalDateTime.now());
            newOrder.setUpdateDate(LocalDateTime.now());
            newOrder.setDeleteFlag(0);

            ordersRepository.save(newOrder);
            
            totalOrderedQuantity += row.getOrderQuantity();
        }

        if (orderedCount == 0) {
            throw new Exception("発注数が1つも入力されていません。");
        }

        // 店舗在庫更新
        ShopStock shopStock = shopStockRepository.findByShopIdAndGoodsIdAndDeleteFlag(shop, goods, 0);
        if(shopStock == null) throw new Exception("店舗在庫データが見つかりません。");
        shopStock.setShopStockQuantity(shopStock.getShopStockQuantity() + totalOrderedQuantity);
        shopStockRepository.save(shopStock);
    }

    // 店舗の発注履歴一覧取得（ステータス＋期間絞り込み対応）
    public List<OrderHistoryDto> getOrderHistoryList(Shop shop, String status, LocalDateTime startDate, LocalDateTime endDate) {
        List<Orders> ordersList = ordersRepository.findByShopWithFilters(shop, status, startDate, endDate, 0);

        return ordersList.stream().map(order -> {
            OrderHistoryDto dto = new OrderHistoryDto();
            dto.setOrderId(order.getOrderId());
            dto.setGoodsName(order.getGoods().getGoodsName());
            dto.setWarehouseName(order.getWarehouse().getWarehouseName());
            dto.setOrderAmount(order.getOrderAmount());
            dto.setOrderStatus(order.getOrderStatus());
            dto.setOrderDate(order.getOrderDate());
            dto.setUpdateDate(order.getUpdateDate());
            return dto;
        }).collect(Collectors.toList());
    }
}
