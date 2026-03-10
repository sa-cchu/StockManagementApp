package com.github.sa_cchu.stock.dto;

public class ShopStockDto {
    private Integer shopStockId;
    private String shopName;
    private String categoryName;
    private String goodsName;
    private Integer shopStockQuantity;

    public ShopStockDto(Integer shopStockId, String shopName, String categoryName, String goodsName,
            Integer shopStockQuantity) {
        this.shopStockId = shopStockId;
        this.shopName = shopName;
        this.categoryName = categoryName;
        this.goodsName = goodsName;
        this.shopStockQuantity = shopStockQuantity;
    }

    public Integer getShopStockId() {
        return shopStockId;
    }

    public void setShopStockId(Integer shopStockId) {
        this.shopStockId = shopStockId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getShopStockQuantity() {
        return shopStockQuantity;
    }

    public void setShopStockQuantity(Integer shopStockQuantity) {
        this.shopStockQuantity = shopStockQuantity;
    }
}
