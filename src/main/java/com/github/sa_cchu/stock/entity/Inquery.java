package com.github.sa_cchu.stock.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inquery")
public class Inquery {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "inquery_id")
	private Integer inqueryId;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@Column(name = "inquery_detail",nullable =  false)
	private String inqueryDetail;
	
	@Column(name = "inquery_date",nullable = false)
	private LocalDateTime inqueryDate = LocalDateTime.now();
	
	@Column(name = "inquery_status",nullable = false,length = 255)
	private String inqueryStatus = "未対応";
	
	@ManyToOne
	@JoinColumn(name = "authority_id",nullable = false)
	private Authority authority;
	
	@ManyToOne
	@JoinColumn(name = "shop_id")
	private Shop shop;
	
	@ManyToOne
	@JoinColumn(name = "wh_id")
	private Warehouse warehouse;
	

    @Column(name = "delete_flag", nullable = false)
    private Integer deleteFlag = 0;


	public Integer getInqueryId() {
		return inqueryId;
	}


	public void setInqueryId(Integer inqueryId) {
		this.inqueryId = inqueryId;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public String getInqueryDetail() {
		return inqueryDetail;
	}


	public void setInqueryDetail(String inqueryDetail) {
		this.inqueryDetail = inqueryDetail;
	}


	public LocalDateTime getInqueryDate() {
		return inqueryDate;
	}


	public void setInqueryDate(LocalDateTime inqueryDate) {
		this.inqueryDate = inqueryDate;
	}


	public String getInqueryStatus() {
		return inqueryStatus;
	}


	public void setInqueryStatus(String inqueryStatus) {
		this.inqueryStatus = inqueryStatus;
	}


	public Authority getAuthority() {
		return authority;
	}


	public void setAuthority(Authority authority) {
		this.authority = authority;
	}


	public Shop getShop() {
		return shop;
	}


	public void setShop(Shop shop) {
		this.shop = shop;
	}


	public Warehouse getWarehouse() {
		return warehouse;
	}


	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}


	public Integer getDeleteFlag() {
		return deleteFlag;
	}


	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
}
