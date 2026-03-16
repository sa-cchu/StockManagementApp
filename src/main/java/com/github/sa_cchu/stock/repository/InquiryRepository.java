package com.github.sa_cchu.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.github.sa_cchu.stock.dto.InquiryListDto;
import com.github.sa_cchu.stock.entity.Inquery;

/**
 * お問い合わせ情報を管理する Repository インターフェース。
 */
@Repository
public interface InquiryRepository extends JpaRepository<Inquery, Integer> {

	/**
	* お問い合わせ一覧画面で使用するデータを取得するため、
	* ユーザー情報（User）、店舗情報（Shop）、倉庫情報（Warehouse）と
	* 結合して取得した結果を {@link com.github.sa_cchu.stock.dto.InquiryListDto} に
	* マッピングして返却する。
	*
	* <p>
	* Inquiryテーブルの権限ID（authorityId）を条件にログインユーザーと同じ権限宛の
	* お問い合わせ情報を取得し、問い合わせ日時の降順で一覧を取得する
	* </p>
	 * 
	 * @param role ログインユーザーの権限
	 * @return お問い合わせ一覧の取得結果
	 */
	@Query("""
			SELECT new com.github.sa_cchu.stock.dto.InquiryListDto(
			    i.inqueryId,
			    i.inqueryDetail,
			    i.inqueryDate,
			    u.authority.authorityId,
			    COALESCE(s.shopName, w.warehouseName),
			    u.userName,
			    i.inqueryStatus
			)
			FROM Inquery i
			JOIN i.user u
			LEFT JOIN u.shop s
			LEFT JOIN u.warehouse w
			WHERE i.authority.authorityId = :role
			ORDER BY i.inqueryDate DESC
			""")
	List<InquiryListDto> findInquiryListByRole(Integer role);


    @Query("""
    SELECT new com.github.sa_cchu.stock.dto.InquiryListDto(
        i.inqueryId,
        i.inqueryDetail,
        i.inqueryDate,
        u.authority.authorityId,
        COALESCE(s.shopName, w.warehouseName),
        u.userName,
        i.inqueryStatus
    )
    FROM Inquery i
    JOIN i.user u
    LEFT JOIN u.shop s
    LEFT JOIN u.warehouse w
    WHERE i.authority.authorityId = :role
    AND i.inqueryStatus = :status
    ORDER BY i.inqueryDate DESC
    """)
    List<InquiryListDto> findInquiryListByRoleAndStatus(Integer role, String status);

}
