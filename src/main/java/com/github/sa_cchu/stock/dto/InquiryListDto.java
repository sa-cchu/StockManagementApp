package com.github.sa_cchu.stock.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * お問い合わせ一覧取得結果を必要項目だけに絞り画面側に受け渡すためのDTO
 */
@Data
@NoArgsConstructor
public class InquiryListDto {

	/**	問い合わせID */
	private Integer inquiryId;

	/**	問い合わせ内容 */
	private String inquiryDetail;

	/**	送信日時 */
	private LocalDateTime inquiryDate;

	/** 送信元の権限ID */
	private Integer authorityId; // 1:管理者, 2:店舗, 3:倉庫

	/**	所属（店舗名 / 倉庫名） */
	private String locationName;

	/** 送信者の名前 */
	private String userName; // 

	/**	お問い合わせステータス */
	private String inquiryStatus;

	/** 画面表示用の権限名（Service層でEnumを使用して設定する） */
	private String displayAuthorityName;

	/**
	 * JPQL（お問い合わせ一覧取得時のメソッド：findInquiryListByRole）の
	 * DTOコンストラクタ式で使用するコンストラクタ。
	 * <p>
	 * Repositoryの以下のクエリで使用される。
	 * </p>
	 * <pre>
	 * SELECT new InquiryListDto(...)
	 * </pre>
	 * authorityName はService層でEnum変換して設定するため
	 * 引数には含めていない。
	 */
	public InquiryListDto(
			Integer inquiryId,
			String inquiryDetail,
			LocalDateTime inquiryDate,
			Integer authorityId,
			String locationName,
			String userName,
			String inquiryStatus) {
		this.inquiryId = inquiryId;
		this.inquiryDetail = inquiryDetail;
		this.inquiryDate = inquiryDate;
		this.authorityId = authorityId;
		this.locationName = locationName;
		this.userName = userName;
		this.inquiryStatus = inquiryStatus;
	}

}
