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
    @Column(name = "warehouse_stock_id")
    private Integer warehouseStockId;

    // 倉庫（外部キー）
    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouseId;

    // 商品（外部キー）
    @ManyToOne
    @JoinColumn(name = "goods_id", nullable = false)
    private Goods goodsId;

    @Column(name = "warehouse_stock_quantity", nullable = false)
    private Integer warehouseStockQuantity = 0;

    @Column(name = "delete_flag", nullable = false)
    private Integer deleteFlag = 0;

	public Integer getWarehouseStockId() {
		return warehouseStockId;
	}

	public void setWarehouseStockId(Integer warehouseStockId) {
		this.warehouseStockId = warehouseStockId;
	}

	public Warehouse getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Warehouse warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Goods getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Goods goodsId) {
		this.goodsId = goodsId;
	}

	public Integer getWarehouseStockQuantity() {
		return warehouseStockQuantity;
	}

	public void setWarehouseStockQuantity(Integer warehouseStockQuantity) {
		this.warehouseStockQuantity = warehouseStockQuantity;
	}

	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
    
    
    
}

   
