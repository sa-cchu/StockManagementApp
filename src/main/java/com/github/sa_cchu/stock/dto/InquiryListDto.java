package com.github.sa_cchu.stock.dto;

import java.time.LocalDateTime;

import com.github.sa_cchu.stock.enums.AuthorityTypeEnum;

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

	/** 送信元の権限ID（AuthorityTypeEnumに対応） */
	private Integer authorityId;

	/**	所属（店舗名 / 倉庫名） */
	private String locationName;

	/** 送信者名 */
	private String userName; // 

	/**	お問い合わせステータス */
	private String inquiryStatus;

	/**
	 * JPQL（お問い合わせ一覧取得時のメソッド：findInquiryListByRoleAndOptionalStatusAndLocation）の
	 * DTOコンストラクタ式で使用するコンストラクタ。
	 * <p>Repositoryの以下のクエリで使用される。</p>
	 * <pre>SELECT new InquiryListDto(...)</pre>
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
	
	/**
	 * 権限IDに応じた表示用の権限名を取得する。
	 * <p> authorityId が null の場合は「ゲスト」を返却する。
	 * それ以外の場合は {@link AuthorityTypeEnum} から対応する表示名を取得する。 </p>
	 * @return 表示用の権限名（例：管理者、店舗、倉庫、ゲスト）
	 */
	public String getAuthorityDisplayName() {
        if (authorityId == null) {
            return "ゲスト";
        }
        return AuthorityTypeEnum
                .fetchAuthorityType(authorityId)
                .getDisplayName();
    }
}
