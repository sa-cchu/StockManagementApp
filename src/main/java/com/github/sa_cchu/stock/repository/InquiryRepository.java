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
	* お問い合わせ一覧画面で表示する情報を取得する。
	* お問い合わテーブルの外部キーからユーザー情報（User）、店舗情報（Shop）、倉庫情報（Warehouse）と
	* 結合して取得した結果を {@link com.github.sa_cchu.stock.dto.InquiryListDto} にマッピングして返却する。
	* <p>Inquiryテーブルの権限ID（authorityId）を条件にログインユーザーと同じ権限宛かつ、所属宛の
	* お問い合わせ情報を取得し、問い合わせ日時の降順で一覧を取得する</p>
	* @param role ログインユーザー情報
	* @param status ログインユーザー情報
	* @param isAdmin 管理者権限情報（可否）
	* @param shopId 店舗ID
	* @param warehouseId 倉庫ID
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
    	    WHERE 
    			(
    				i.authority.authorityId = :role
    			)
    			AND 
    		 	(
					:isAdmin = TRUE
	    			OR (:shopId IS NOT NULL AND i.shop.shopId = :shopId)    
	    			OR (:warehouseId IS NOT NULL AND i.warehouse.warehouseId = :warehouseId)
				)
			 	AND 
			 	(
			 		:status IS NULL OR i.inqueryStatus = :status
			 	)
    	    ORDER BY i.inqueryDate DESC
    	""")
	List<InquiryListDto> findInquiryListByRoleAndOptionalStatusAndLocation(
	        Integer role,
	        String status,
	        Boolean isAdmin,
	        Integer shopId,
	        Integer warehouseId
    );
    
    /**
     * お問い合わせ詳細取得用
     * 
     * @param id
     * @return
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
    	    WHERE i.inqueryId = :id
    	""")
    	InquiryListDto findInquiryById(Integer id);
}
