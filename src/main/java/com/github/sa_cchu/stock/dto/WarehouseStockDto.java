package com.github.sa_cchu.stock.dto;

public class WarehouseStockDto {
    private Integer warehouseStockId;
    private String warehouseName;
    private String categoryName;
    private String goodsName;
    private Integer warehouseStockQuantity;

    public WarehouseStockDto(Integer warehouseStockId, String warehouseName, String categoryName, String goodsName,
            Integer warehouseStockQuantity) {
        this.warehouseStockId = warehouseStockId;
        this.warehouseName = warehouseName;
        this.categoryName = categoryName;
        this.goodsName = goodsName;
        this.warehouseStockQuantity = warehouseStockQuantity;
    }

    public Integer getWarehouseStockId() {
        return warehouseStockId;
    }

    public void setWarehouseStockId(Integer warehouseStockId) {
        this.warehouseStockId = warehouseStockId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
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

    public Integer getWarehouseStockQuantity() {
        return warehouseStockQuantity;
    }

    public void setWarehouseStockQuantity(Integer warehouseStockQuantity) {
        this.warehouseStockQuantity = warehouseStockQuantity;
    }
}
