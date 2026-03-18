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
import com.github.sa_cchu.stock.form.GuestInquiryForm;
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
	 * @param status ステータスフィルターで選択したステータス情報
	 * @param model 画面に表示する情報
	 * @return お問い合わせ一覧画面
	 */
	@GetMapping("/list")
	public String viewInquiryList(@RequestParam(required = false) Integer status,
			@AuthenticationPrincipal User user, Model model) {
		// ログインユーザーが管理者かを判定し、お問い合わせボタンを表示制御するために判定結果を画面に渡す
		model.addAttribute("isAdmin", isAdmin(user));	
		// Enumに記載しているステータス情報を選択肢として画面に渡す。
		model.addAttribute("statusList", StatusEnum.values());
		// statusId → Enum に変換する。（すべてを選択している場合はnullで使用）
	    StatusEnum statusEnum = null;
	    if (status != null) {
	        statusEnum = StatusEnum.fromId(status);
	    }
	    // ログインユーザーと同じ権限（店舗・倉庫の場合は同じ所属）宛へのお問い合わせ一覧を取得する処理を呼び出す。
	    List<InquiryListDto> inquiryList = inquiryService.getInquiryListByTargetRole(user, statusEnum);
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
			@AuthenticationPrincipal User user, Model model) {
		// ログインユーザーが管理者かを判定し、管理者の場合は一覧にリダイレクトさせる。
		if (isAdmin(user)) {
			return "redirect:/inquiry/list";
		}
		// 入力フォームの選択肢情報を取得する。
		setCommonModel(model, user);
		return "/inquiry/create";
	}
	
	/**
	 * お問い合わせ送信画面で入力した情報をテーブルへの保存する際の処理制御を行う。
	 * @param inquiryForm 画面の入力情報
	 * @param result フォームバインド
	 * @param model 画面に渡すモデル
	 * @param user ログイン中のユーザー情報
	 * @return 一覧画面
	 */
	@PostMapping("/create")
	public String sendInquiry(@Validated @ModelAttribute InquiryForm inquiryForm,
			BindingResult result, @AuthenticationPrincipal User user,
			RedirectAttributes redirectAttributes, Model model) {
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
			// 画面からの入力された情報を DTO 内で変換し、
			// ログイン中のユーザー情報とお問い合わせフォームでの入力情報を渡して保存処理を呼び出す。
			inquiryService.createInquiry(InquiryRequestDto.fromLoggedInUser(inquiryForm), user);
		} catch (IllegalArgumentException e) {
			// フォーム全体のエラーとしてユーザー向けにエラーメッセージをセット
			result.reject(null, e.getMessage());
			// 入力フォームに戻す
			setCommonModel(model, user);
			return "inquiry/create";
		}
		// 保存が完了したらフラッシュメッセージを表示する。
	    redirectAttributes.addFlashAttribute("successMessage", "送信が完了しました");
		// 保存が完了したら送信画面を再度表示する。
		return "redirect:/inquiry/create";
	}
	
	/**
	 * 未ログイン状態でお問い合わせ送信画面のフォームを表示する
	 * @param guestInquiryForm 未ログイン入力情報
	 * @param model 画面に渡すモデル
	 * @return 一覧画面
	 */
	@GetMapping("/guest/create")
	public String viewGuesInquiryCreate(@ModelAttribute GuestInquiryForm guestInquiryForm, Model model) {
		// 初回だけデフォルト値のADMIN_IDをセットして表示する。
	    if (guestInquiryForm.getAuthorityId() == null) {
	        guestInquiryForm.setAuthorityId(ADMIN_ID);
	    }
		// 画面にEnumから管理者の選択肢のみを渡す
		model.addAttribute("adminLabel", AuthorityTypeEnum.ADMIN.getDisplayName());
		return "inquiry/guestCreate";
	}
	
	/**
	 * 未ログイン状態でお問い合わせ送信画面で入力した情報をテーブルへの保存する際の処理制御を行う。
	 * @param inquiryForm 画面の入力情報
	 * @param result フォームバインド
	 * @param model 画面に渡すモデル
	 * @param user ログイン中のユーザー情報
	 * @return 一覧画面
	 */
	@PostMapping("/guest/create")
	public String sendGuestInquiry(@Validated @ModelAttribute GuestInquiryForm guestInquiryForm,
	        BindingResult result, Model model, RedirectAttributes redirectAttributes) {
		// 画面にEnumから管理者の選択肢のみを渡す
		model.addAttribute("adminLabel", AuthorityTypeEnum.ADMIN.getDisplayName());
		// エラー時がある場合、画面バリデーションを発生させて選択肢の情報を再度渡す。
	    if (result.hasErrors()) {
	        return "inquiry/guestCreate";
	    }
	    try {
			// 画面からの入力された情報を DTO 内で変換し、
			// ログイン中のユーザー情報（未ログインのためnull）とお問い合わせフォームでの入力情報を渡して保存処理を呼び出す。
		    inquiryService.createInquiry(InquiryRequestDto.fromGuestUser(guestInquiryForm), null);
	    } catch (IllegalArgumentException e) {
			// フォーム全体のエラーとしてユーザー向けにエラーメッセージをセット
			result.reject(null, e.getMessage());
			// 入力フォームに戻す
			return "inquiry/guestCreate";
		}
	    // 保存が完了したらフラッシュメッセージを表示する。
	    redirectAttributes.addFlashAttribute("successMessage", "送信が完了しました");
		// 保存が完了したら送信画面を再度表示する。
	    return "redirect:/inquiry/guest/create";
	}
	
    /**
     * お問い合わせIDをもとにお問い合わせ詳細を表示する
     * @param inquiryForm
     * @param id
     * @param model
     * @return idに紐づいたお問い合わせ詳細画面
     */
	@GetMapping("/detail")
	public String viewInquiryCreate(@RequestParam("id") Integer id, Model model){
		// Enumに記載しているステータス情報を選択肢として画面に渡す。
		model.addAttribute("statusList", StatusEnum.values());
		InquiryListDto inquiry = inquiryService.getInquiryById(id);
	    model.addAttribute("inquiry", inquiry);
		// 詳細情報を取得してを表示する。
		return "/inquiry/detail";
	}
	/**
	 * お問い合わせ詳細画面で行われたステータス変更を更新する。
	 * @param id
	 * @param status
	 * @return 更新完了メッセージと詳細画面
	 */
	@PostMapping("/updateStatus")
	public String updateStatus(@RequestParam Integer id, @RequestParam Integer status,
			RedirectAttributes redirectAttributes){
		try {
			// statusId → Enum に変換する。（すべてを選択している場合はnullで使用）
		    StatusEnum statusEnum = null;
		    if (status != null) {
		        statusEnum = StatusEnum.fromId(status);
		    }
		    // 問い合わせIDに紐づくお問い合わせのステータスを更新する。
		    inquiryService.updateStatus(id, statusEnum);
		    // 更新が成功した場合、メッセージを追加し画面で表示する。
		    redirectAttributes.addFlashAttribute("successMessage", "ステータスを更新しました");
		} catch (Exception e) {
			// 空データだった場合やデータ取得に失敗した場合、メッセージを追加し画面で表示する。
	        redirectAttributes.addFlashAttribute("errorMessage", "ステータスの更新に失敗しました");
	        // ログ出力する。
	        e.printStackTrace();
		}
	    // 更新完了メッセージを出力して再度詳細画面を表示する。
	    return "redirect:/inquiry/detail?id=" + id;
	}

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
		// 未ログイン対策
	    if (user != null) {
		// ログインユーザーの権限以外を送信先の選択肢として表示するため、ログインユーザーの権限IDを画面に渡す。※画面側で判定を行う。
		model.addAttribute("myAuthorityId", user.getAuthority().getAuthorityId());
		// 連携している店舗・倉庫の選択肢を取得して画面に渡す。
		model.addAttribute("targets", relationService.getTargetsForUser(user));
	    }
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
