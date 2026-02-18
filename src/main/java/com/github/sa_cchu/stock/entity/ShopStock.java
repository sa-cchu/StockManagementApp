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
	private Shop shopId;

	// 商品（外部キー）
	@ManyToOne
	@JoinColumn(name = "goods_id", nullable = false)
	private Goods goodsId;

	@Column(name = "quantity", nullable = false)
	private Integer quantity = 0;

	@Column(name = "delete_flag", nullable = false)
	private Integer deleteFlag = 0;

	public Integer getShopStockId() {
		return shopStockId;
	}

	public void setShopStockId(Integer shopStockId) {
		this.shopStockId = shopStockId;
	}

	public Shop getShopId() {
		return shopId;
	}

	public void setShopId(Shop shopId) {
		this.shopId = shopId;
	}

	public Goods getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Goods goodsId) {
		this.goodsId = goodsId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	
	
}
	