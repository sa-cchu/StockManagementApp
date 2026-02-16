package com.github.sa_cchu.stock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "authority")
public class Authority {

	//権限IDのカラム設定
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)//SERIALにしてくれる
	@Column(name = "authority_id")
	private Integer authorityId;

	//権限名のカラム設定
	@Column(name = "authority_name", nullable = false, length = 255)
	private String authorityName;

	//デリートフラグのカラム設定
	@Column(name = "delete_flag", nullable = false)
	private Integer deleteFlag = 0;

	public Integer getAuthorityId() {
		return authorityId;
	}

	public void setAuthorityId(Integer authorityId) {
		this.authorityId = authorityId;
	}

	public String getAuthorityName() {
		return authorityName;
	}

	public void setAuthorityName(String authorityName) {
		this.authorityName = authorityName;
	}

	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

}
