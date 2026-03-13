package com.github.sa_cchu.stock.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.github.sa_cchu.stock.dto.ShopOrderTargetDto;
import com.github.sa_cchu.stock.repository.WarehouseStockRepository;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.Goods;

@Service
public class ShopOrderService {

    private final WarehouseStockRepository warehouseStockRepository;
    private final GoodsService goodsService;

    public ShopOrderService(WarehouseStockRepository warehouseStockRepository, GoodsService goodsService) {
        this.warehouseStockRepository = warehouseStockRepository;
        this.goodsService = goodsService;
    }

    public List<ShopOrderTargetDto> getShopOrderTargetDtoList(Shop shop, Integer categoryId) {
        List<Goods> goodsList = goodsService.getGoodsList(categoryId);
        List<ShopOrderTargetDto> shopOrderTargetDtoList = new ArrayList<>();
        for (Goods goods : goodsList) {
            ShopOrderTargetDto shopOrderTargetDto = new ShopOrderTargetDto();
            shopOrderTargetDto.setGoods(goods);
            shopOrderTargetDto.setCategoryName(goods.getCategory().getCategoryName());

            Integer totalQuantity = warehouseStockRepository.getTotalStockByLinkedWarehouses(shop, goods);
            shopOrderTargetDto.setTotalStockQuantity(totalQuantity != null ? totalQuantity : 0);

            shopOrderTargetDtoList.add(shopOrderTargetDto);
        }
        return shopOrderTargetDtoList;
    }
}
