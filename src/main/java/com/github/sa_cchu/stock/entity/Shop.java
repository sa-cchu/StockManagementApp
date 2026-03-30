package com.github.sa_cchu.stock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "shop", uniqueConstraints = {
		@UniqueConstraint(columnNames = "shop_name")
})
public class Shop {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "shop_id")
	private Integer shopId;
	
	@Size(max = 255, message = "店舗名は255文字以内で入力してください")
	@NotBlank(message = "店舗名を入力してください")
	@Column(name = "shop_name", nullable = false, length = 255)
	private String shopName;
	
	@NotBlank(message = "住所を入力してください")
	@Size(max = 255, message = "住所は255文字以内で入力してください")
	@Column(name = "shop_address", nullable = false, length = 255)
	private String shopAddress;

	@Column(name = "delete_flag", nullable = false)
	private Integer deleteFlag = 0;

	public Integer getShopId() {
		return shopId;
	}

	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getShopAddress() {
		return shopAddress;
	}

	public void setShopAddress(String shopAddress) {
		this.shopAddress = shopAddress;
	}

	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
}
