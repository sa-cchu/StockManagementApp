package com.github.sa_cchu.stock.service;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.github.sa_cchu.stock.dto.InquiryRequestDto;
import com.github.sa_cchu.stock.entity.Authority;
import com.github.sa_cchu.stock.entity.Inquery;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.User;
import com.github.sa_cchu.stock.entity.Warehouse;
import com.github.sa_cchu.stock.enums.AuthorityTypeEnum;
import com.github.sa_cchu.stock.repository.InquiryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * お問い合わせの入力情報をDBに保存する Service
 * <p>
 * 送信先によって保存するカラム（店舗・倉庫・管理者宛）を切り替えながら、
 * お問い合わせ情報を DB に登録する。
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class InquiryService {
	
	private final InquiryRepository inquiryRepository;
	
	/**
	 * 送信先で選択した権限によって入力情報以外のカラムに保存する内容を変更してテーブルに全項目を保存する。
	 * @param inquiryRequestDto 画面から送信されたお問い合わせ情報
	 * @param user ログイン中のユーザー情報
	 */
	@Transactional
	public void createInquiry(InquiryRequestDto inquiryRequestDto, User user) {
		// targetId（連携先の店舗ID・倉庫ID）が null だった場合はログに出力する。
		if ((inquiryRequestDto.getAuthorityId() == AuthorityTypeEnum.SHOP.getAuthorityTypeId()
             || inquiryRequestDto.getAuthorityId() == AuthorityTypeEnum.WAREHOUSE.getAuthorityTypeId())
             && inquiryRequestDto.getTargetId() == null) {
            log.warn("targetId が null です。for authorityId={} userId={}",
                     inquiryRequestDto.getAuthorityId(), user.getUserId());
            // ユーザー向けエラーを通知する。
            throw new IllegalArgumentException("連携先の選択が不正です。管理者にお問い合わせください。");
	    }
		// エンティティを新規作成する。
		Inquery inquiry = new Inquery();
		inquiry.setUser(user);
		inquiry.setInqueryDetail(inquiryRequestDto.getInquiryDetail());
		
		// 送信先に選択された権限IDを取得する。
		AuthorityTypeEnum authorityType = AuthorityTypeEnum.fromId(inquiryRequestDto.getAuthorityId());
		// Authority エンティティを作成してEnumに取得した権限IDをセットする。
		Authority authority = new Authority();
		authority.setAuthorityId(authorityType.getAuthorityTypeId());
		inquiry.setAuthority(authority);
		
		// 送信先に応じて、店舗ID・倉庫IDカラムに保存する値を設定する。
		switch (authorityType) {
		case ADMIN:
			// 管理者宛の場合、店舗。ID・倉庫IDは null で設定する
			inquiry.setShop(null);
			inquiry.setWarehouse(null);
			break;

		case SHOP:
			// 店舗宛の場合、店舗IDのみセットし、倉庫IDは null で設定する。
			Shop shop = new Shop();
			shop.setShopId(inquiryRequestDto.getTargetId());
			inquiry.setShop(shop);
			inquiry.setWarehouse(null);
			break;
			
		case WAREHOUSE:
			// 倉庫宛の場合、倉庫IDのみセット、店舗IDは null で設定する。
			Warehouse warehouse = new Warehouse();
			warehouse.setWarehouseId(inquiryRequestDto.getTargetId());
			inquiry.setWarehouse(warehouse);
			inquiry.setShop(null);
			break;
		}
		// お問い合わせ情報をDBに登録する。
		inquiryRepository.save(inquiry);
	}
}
