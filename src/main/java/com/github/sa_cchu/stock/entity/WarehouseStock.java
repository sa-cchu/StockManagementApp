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
@Table(name = "warehouse_stock")
public class WarehouseStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wh_stock_id")
    private Integer whStockId;

    // 倉庫（外部キー）
    @ManyToOne
    @JoinColumn(name = "wh_id", nullable = false)
    private Warehouse warehouse;

    // 商品（外部キー）
    @ManyToOne
    @JoinColumn(name = "goods_id", nullable = false)
    private Goods goods;

    @Column(name = "wh_stock", nullable = false)
    private Integer whStock = 0;

    @Column(name = "delete_flag", nullable = false)
    private Integer deleteFlag = 0;

    // ===== getter / setter =====

    public Integer getWhStockId() {
        return whStockId;
    }

    public void setWhStockId(Integer whStockId) {
        this.whStockId = whStockId;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Integer getWhStock() {
        return whStock;
    }

    public void setWhStock(Integer whStock) {
        this.whStock = whStock;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}
