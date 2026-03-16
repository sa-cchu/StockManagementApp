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
import com.github.sa_cchu.stock.repository.InquiryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * お問い合わせ機能に関する業務ロジックを実装する Service クラス。
 * <p>Inquiryテーブルへの登録処理およびお問い合わせ一覧取得処理を呼び出す。</p>
 * <ul><li>ログインユーザーと同じ権限宛てのお問い合わせ一覧を取得する。</li>
 * <li>送信先によって保存するカラム（店舗・倉庫・管理者宛）を切り替えながら、お問い合わせ情報を DB に登録する。</li></ul>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class InquiryService {
	private final InquiryRepository inquiryRepository;
	/**
	 * Inquiryテーブルの権限ID（authorityId）を条件に、ログインユーザーと同じ権限宛のお問い合わせ一覧を取得する。
	 * <p>statusが未指定の場合は全ステータスを取得し、指定されている場合は該当ステータスで絞り込んだ
	 * お問い合わせ一覧を送信日時の降順で取得する。</p>
	 * <p>権限ID（authorityId）はEnum（AuthorityTypeEnum）を使用して画面表示用の権限名へ変換する。</p>
	 * @param role ログインユーザーの権限
	 * @param targetId 所属（連携先）の選択情報。未指定の場合は全件取得
	 * @param status お問い合わせのステータス（未対応・対応中・対応済）。未指定の場合は全件取得
	 * @return 条件に一致するお問い合わせ一覧
	 */
	public List<InquiryListDto> getInquiryListByTargetRole(Integer role, String status) {
		// 空のDTOを準備しておく。
		List<InquiryListDto> list; 
		// ステータス条件が指定されていない場合、
		if (status == null || status.isEmpty()) { 
			
			// ログインユーザーの権限IDを条件に、お問い合わせ一覧を取得する。 
			list = inquiryRepository.findInquiryListByRole(role); 
			// ステータス条件が指定されている場合、
			} else { 
				// ログインユーザーの権限IDとステータス状態を条件に、お問い合わせ一覧を取得する。 
				list = inquiryRepository.findInquiryListByRoleAndStatus(role, status); 
			} 
		
		// DTOに含まれる authorityId（数値）をもとに、Enumを使用して画面表示用の権限名へ変換する 
		for (InquiryListDto dto : list) { 
			String authorityName = AuthorityTypeEnum 
			// 権限IDからEnumを取得する
			.fetchAuthorityType(dto.getAuthorityId()) 
			// 権限IDからEnumを取得する。 
			.getDisplayName(); 
			// 画面表示用の名称を取得する。 
			// DTOに表示用の権限名をセットする。 
			dto.setDisplayAuthorityName(authorityName); 
		} 
		// 変換済みのDTOを返却する。 
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
}
