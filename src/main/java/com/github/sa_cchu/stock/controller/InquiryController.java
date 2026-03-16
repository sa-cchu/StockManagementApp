package com.github.sa_cchu.stock.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.github.sa_cchu.stock.dto.InquiryListDto;
import com.github.sa_cchu.stock.dto.InquiryRequestDto;
import com.github.sa_cchu.stock.entity.User;
import com.github.sa_cchu.stock.enums.AuthorityTypeEnum;
import com.github.sa_cchu.stock.enums.StatusEnum;
import com.github.sa_cchu.stock.form.InquiryForm;
import com.github.sa_cchu.stock.service.CustomUserDetailsService;
import com.github.sa_cchu.stock.service.InquiryService;
import com.github.sa_cchu.stock.service.RelationService;

import lombok.RequiredArgsConstructor;

/**
 * ログインユーザーのお問い合わせに関する制御を行う controller
 * <ul>
 *   <li>権限宛に届いたお問い合わせを取得し一覧で表示する</li>
 *   <li>お問い合わせフォームに遷移する</li>
 *   <li>お問い合わせフォームの入力情報を保存する</li>
 *   <li>お問い合わせ詳細画面に遷移する</li>
 * </ul>
 */
@RequiredArgsConstructor
@Controller
@RequestMapping("/inquiry")
public class InquiryController {
	private final RelationService relationService;
	private final CustomUserDetailsService customUserDetailsService;
	private final InquiryService inquiryService;
	// Enumから管理者の権限IDを取得する。
	private static final Integer ADMIN_ID = AuthorityTypeEnum.ADMIN.getAuthorityTypeId();
	/**
	 * お問い合わせ一覧を表示する。
	 * @param user ログイン中のユーザー情報
	 * @param targetId 所属フィルターで選択した所属（連携先）情報
	 * @param status ステータスフィルターで選択したステータス情報
	 * @param model 画面に渡すモデル
	 * @return お問い合わせ一覧画面
	 */
	@GetMapping("/list")
	public String viewInquiryList(@AuthenticationPrincipal User user, 
			@RequestParam(required = false) String status,
			Model model) {
		// ログインユーザーが管理者かを判定し、お問い合わせボタンを表示制御するために判定結果を画面に渡す
		model.addAttribute("isAdmin", isAdmin(user));	
		
		Integer role = user.getAuthority().getAuthorityId();
		// 連携している店舗・倉庫の選択肢を取得して画面に渡す。
		model.addAttribute("targets", relationService.getTargetsForUser(user));
		// Enumに記載しているステータス情報を選択肢として画面に渡す。
		model.addAttribute("statusList", StatusEnum.values());
	    // 権限宛のお問い合わせ一覧を取得する処理を呼び出す。
	    List<InquiryListDto> inquiryList = inquiryService.getInquiryListByTargetRole(role, status);
	    // 画面に取得した一覧情報を渡す。
	    model.addAttribute("inquiryList", inquiryList);
	    // フィルターで選択されているステータスの状況を保持して画面に渡す。
	    model.addAttribute("status", status);
	    // お問い合わせ一覧画面を返却する。
		return "/inquiry/list";
	}

	/**
	 * 選択肢を取得した状態でお問い合わせフォームを表示する
	 * @param inquiryForm 画面の入力情報
	 * @param model 画面に渡すモデル
	 * @param user ログイン中のユーザー情報
	 * @return 一覧画面
	 */
	@GetMapping("/create")
	public String viewInquiryCreate(@ModelAttribute InquiryForm inquiryForm,
			Model model, @AuthenticationPrincipal User user) {
		// ログインユーザーが管理者かを判定し、管理者の場合は一覧にリダイレクトさせる。
		if (isAdmin(user)) {
			return "redirect:/inquiry/list";
		}
		// 入力フォームの選択肢情報を取得する。
		setCommonModel(model, user);
		return "/inquiry/create";
	}

	/**
	 * お問い合わせ画面で入力した情報をテーブルへの保存する際の処理制御を行う。
	 * @param inquiryForm 画面の入力情報
	 * @param result フォームバインド
	 * @param model 画面に渡すモデル
	 * @param user ログイン中のユーザー情報
	 * @return 一覧画面
	 */
	@PostMapping("/create")
	public String sendInquiry(@Validated @ModelAttribute InquiryForm inquiryForm,
			BindingResult result, Model model, @AuthenticationPrincipal User user,
			RedirectAttributes redirectAttributes) {

		// 管理者宛てに送信する場合、Formクラスにバリデーションを実装すると
		// 不要なバリデーションが機能してしまうため、連携先選択のバリデーションをcontrollerで行う。
		if (!ADMIN_ID.equals(inquiryForm.getAuthorityId()) && inquiryForm.getTargetId() == null) {
			// 店舗・倉庫に送信する際に連携先が選択されていない場合は、エラーとしメッセージを表示する。
			result.rejectValue("target", null, "連携している店舗・倉庫の選択は必須です");
		}
		// バリデーションエラー時はフォームを戻す
		if (result.hasErrors()) {
			// 入力フォームの選択肢情報を取得する。
			setCommonModel(model, user);
			return "inquiry/create";
		}
		try {
			// 画面からの入力された情報をサービスに渡すための DTO 内で変換する。
			InquiryRequestDto request = InquiryRequestDto.from(inquiryForm);
			// ログイン中のユーザー情報とお問い合わせフォームでの入力情報を渡して保存処理を呼び出す。
			inquiryService.createInquiry(request, user);
		} catch (IllegalArgumentException e) {
			// フォーム全体のエラーとしてユーザー向けにエラーメッセージをセット
			result.reject(null, e.getMessage());
			// 入力フォームに戻す
			setCommonModel(model, user);
			return "inquiry/create";
		}
		// 登録が完了したらフラッシュメッセージを表示する。
	    redirectAttributes.addFlashAttribute("successMessage", "送信が完了しました");
		// 登録が完了したらリストを表示する。
		return "redirect:/inquiry/create";
	}

	// TODO あとで実装予定
	//	@GetMapping("/detail")
	//	public String viewInquiryCreate(@Validated @ModelAttribute InquiryForm inquiryForm) {
	//		return "/inquiry/create";
	//	}

	/**
	 * ログインユーザーが管理者かどうか判定するメソッド
	 * @param user ログイン中のユーザー情報
	 * @return 管理者なら true、それ以外は false
	 */
	private boolean isAdmin(User user) {
		return Optional.ofNullable(user)
				.map(User::getAuthority)
				.map(a -> a.getAuthorityId().equals(ADMIN_ID))
				.orElse(false);
	}

	/**
	 * 入力時の選択肢を表示するために必要な情報をまとめて取得するメソッド
	 * @param model 画面に渡すモデル
	 * @param user ログイン中のユーザー情報
	 */
	private void setCommonModel(Model model, User user) {
		// 全ての権限IDと権限名を取得し画面に渡す。
		model.addAttribute("authorities", customUserDetailsService.getAllAuthorities());
		// ログインユーザーの権限以外を送信先の選択肢として表示するため、ログインユーザーの権限IDを画面に渡す。※画面側で判定を行う。
		model.addAttribute("myAuthorityId", user.getAuthority().getAuthorityId());
		// 連携している店舗・倉庫の選択肢を取得して画面に渡す。
		model.addAttribute("targets", relationService.getTargetsForUser(user));
		// 管理者の権限IDを画面に渡す。（管理者以外の送信先を選択した場合に画面で表示制御を行う）
		model.addAttribute("adminId", ADMIN_ID);
	}

	//【テスト用あとで消去予定】
	@GetMapping("/test")
	@ResponseBody
	public String test(Authentication authentication) {

		return authentication.getName();
	}

}
