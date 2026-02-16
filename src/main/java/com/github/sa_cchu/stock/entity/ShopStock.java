package com.github.sa_cchu.stock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "shop_stock")
public class ShopStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_stock_id")
    private Integer shopStockId;

    // 店舗（外部キー）
    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    // 商品（外部キー）
    @ManyToOne
    @JoinColumn(name = "goods_id", nullable = false)
    private Goods goods;

    @Column(name = "shop_stock", nullable = false)
    private Integer shopStock = 0;

    @Column(name = "delete_flag", nullable = false)
    private Integer deleteFlag = 0;

    // getter / setter

    public Integer getShopStockId() {
        return shopStockId;
    }

    public void setShopStockId(Integer shopStockId) {
        this.shopStockId = shopStockId;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Integer getShopStock() {
        return shopStock;
    }

    public void setShopStock(Integer shopStock) {
        this.shopStock = shopStock;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}
