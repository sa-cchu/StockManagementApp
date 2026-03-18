package com.github.sa_cchu.stock.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.github.sa_cchu.stock.dto.InquiryListDto;
import com.github.sa_cchu.stock.dto.InquiryRequestDto;
import com.github.sa_cchu.stock.entity.Authority;
import com.github.sa_cchu.stock.entity.Inquery;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.User;
import com.github.sa_cchu.stock.entity.Warehouse;
import com.github.sa_cchu.stock.enums.AuthorityTypeEnum;
import com.github.sa_cchu.stock.enums.StatusEnum;
import com.github.sa_cchu.stock.repository.InquiryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * お問い合わせ機能に関する業務ロジックを実装する Service クラス。
 * <p>Inquiryテーブルからのお問い合わせ一覧取得処理および登録処理を呼び出す。</p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class InquiryService {
	private final InquiryRepository inquiryRepository;
	/**
	 * 画面で指定されたステータス状態とログインユーザーの権限と店舗ID/倉庫IDを条件に、
	 * Inquiryテーブルの権限ID、ログインユーザーと同じ権限 & 所属宛（店舗/倉庫の場合）のお問い合わせ一覧を送信日時の降順で取得する。
	 * @param user ログインユーザー情報
	 * @param status お問い合わせのステータス（未対応・対応中・対応済）。未指定の場合は全件取得
	 * @return 条件に一致するお問い合わせ一覧
	 */
	public List<InquiryListDto> getInquiryListByTargetRole(User user, StatusEnum statusEnum) {
		// ステータス情報をEnum → DB用文字列を取得する（未対応/対応中/対応済）
	    String status = statusEnum != null
	            ? statusEnum.getDisplayStatusName()
	            : null;
		// ログインユーザーの権限IDを取得する。
		Integer role = user.getAuthority().getAuthorityId();
		// 権限が管理者かを判定する。管理者は、権限IDのみで管理者宛のお問い合わせを抽出するため変数を取得時に渡す。
		Boolean isAdmin = role != null && role.intValue() == AuthorityTypeEnum.ADMIN.getAuthorityTypeId();
		// ログインユーザーの店舗ID・倉庫IDの有無を確認する。取得処理時にはどちらか null でない方を使用する。
	    Integer shopId = user.getShop() != null ? user.getShop().getShopId() : null;
	    Integer warehouseId = user.getWarehouse() != null ? user.getWarehouse().getWarehouseId() : null;
	    // 権限ID、ステータス、管理権限の判定結果、店舗ID、倉庫IDをもとにinquiryRepositoryで問い合わせリストを取得する。
	    List<InquiryListDto> list = inquiryRepository
	            .findInquiryListByRoleAndOptionalStatusAndLocation(
	                role,
	                status,
	                isAdmin,
	                shopId,
	                warehouseId
	            );
		// 取得結果を返却する。 
		return list;
	}

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
		AuthorityTypeEnum authorityType = AuthorityTypeEnum.fetchAuthorityType(inquiryRequestDto.getAuthorityId());
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
	
	/**
	 * お問い合わせ詳細を取得する。
	 * @param id お問い合わせID
	 * @return
	 */
	public InquiryListDto getInquiryById(Integer id){
		// 指定されたIDのお問い合わせ情報を取得する
		InquiryListDto dto = inquiryRepository.findInquiryById(id);
		// 取得結果を返却する。 
		return dto;
    }
	
	/**
	 * お問い合わせ詳細画面で変更されたステータスを更新する。
	 *  <p>画面から送信されたステータスは {@link StatusEnum} で受け取り、
	 * DBで管理している文字列形式（未対応 / 対応中 / 対応済）へ変換して対象のお問い合わせレコードを更新する。</p>
	 * @param id 更新対象のお問い合わせID
	 * @param statusEnum 更新するステータス（Enum）
	 */
	@Transactional
	public void updateStatus(Integer id, StatusEnum statusEnum) {
		// ステータス情報をEnum → DB用文字列に変換する（未対応/対応中/対応済）
	    String status = statusEnum != null
	            ? statusEnum.getDisplayStatusName()
	            : null;
	    // 指定されたIDのお問い合わせ情報を取得する
	    Inquery inquiry = inquiryRepository.findById(id).orElseThrow();
	    // ステータスを更新する
	    inquiry.setInqueryStatus(status);
	}
}
