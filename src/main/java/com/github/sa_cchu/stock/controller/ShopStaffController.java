package com.github.sa_cchu.stock.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.sa_cchu.stock.dto.UserFormDTO;
import com.github.sa_cchu.stock.entity.User;
import com.github.sa_cchu.stock.service.CustomUserDetailsService;

@Controller
@RequestMapping("/shop-staff")
public class ShopStaffController {

	private final CustomUserDetailsService userDetailsService;

	public ShopStaffController(CustomUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@GetMapping
	public String list(@AuthenticationPrincipal User operator,
			@RequestParam(name = "belongingId", required = false) Integer belongingId, Model model) {
		// 1. 指定がなければ「すべて(null)」として扱い、全件取得
		model.addAttribute("userList", userDetailsService.getUserListForOperator(operator, belongingId));
		// 2. プルダウンの選択肢
		model.addAttribute("belongings", userDetailsService.getBelongingList(operator));
		model.addAttribute("selectedId", belongingId);
		model.addAttribute("baseUrl", "/shop-staff");
		return "myTeamMG";
	}

	@GetMapping("/new")
	public String showRegistrationForm(@AuthenticationPrincipal User operator, Model model) {
		model.addAttribute("userForm", userDetailsService.createNewUserFormDTO(operator));
		model.addAttribute("belongingList", userDetailsService.getBelongingList(operator));
		model.addAttribute("baseUrl", "/shop-staff");
		return "myTeam-form";
	}

	@GetMapping("/edit/{userId}")
	public String showEditForm(@PathVariable("userId") Integer userId, @AuthenticationPrincipal User operator,
			Model model) {
		try {
			model.addAttribute("userForm", userDetailsService.getAuthorizedUserFormDTO(userId, operator));
			model.addAttribute("belongingList", userDetailsService.getBelongingList(operator));
			model.addAttribute("baseUrl", "/shop-staff");
			return "myTeam-form";

		} catch (SecurityException e) {
			return "redirect:/shop-staff?error=unauthorized";
		}
	}

	@PostMapping("/save")
	public String save(@Validated @ModelAttribute("userForm") UserFormDTO userForm, BindingResult result,
			@AuthenticationPrincipal User operator, Model model) {

// 1. 【重要】名前の重複チェック（DB照合）
// userNameに形式エラーがない場合のみ、DBに問い合わせる
		if (!result.hasFieldErrors("userName")) {
			if (userDetailsService.isUserNameExists(userForm.getUserName(), userForm.getUserId())) {
				result.rejectValue("userName", "duplicate", "このユーザー名は既に使用されています");
			}
		}

// 2. 【重要】バリデーションエラーまたは重複エラーがある場合
		if (result.hasErrors()) {
			// プルダウン（所属先リスト）が消えるのを防ぐため再セット
			model.addAttribute("belongingList", userDetailsService.getBelongingList(operator));
			model.addAttribute("baseUrl", "/shop-staff");

			// 入力された値（userForm）を保持したままフォーム画面に戻る
			return "myTeam-form";
		}

// 3. 保存実行
		try {
			// ★戻り値で保存後の最新Entityを受け取る
			User savedUser = userDetailsService.saveUserFormDTO(userForm);
			if (operator != null && savedUser.getUserId().equals(operator.getUserId())) {
				refreshSession(savedUser);
			}

		} catch (Exception e) {
			// 万が一のDBエラーに備える（実務的な保険）
			model.addAttribute("errorMessage", "保存中にエラーが発生しました");
			model.addAttribute("belongingList", userDetailsService.getBelongingList(operator));
			model.addAttribute("baseUrl", "/shop-staff");
			return "myTeam-form";
		}

// 4. 二重送信防止のため完了画面（または一覧）へリダイレクト
		return "redirect:/shop-staff";
	}

	/**
	 * セッション内の認証情報を最新のユーザー情報で上書きする
	 */
	private void refreshSession(User user) {
		Authentication newAuth = new UsernamePasswordAuthenticationToken(user, user.getPassword(),
				user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(newAuth);
	}

	@PostMapping("/delete/{id}")
	public String deleteUser(@PathVariable("id") Integer userId) {
		// Serviceを呼び出して削除処理を実行
		userDetailsService.deleteUser(userId);

		// 削除が終わったら一覧画面にリダイレクト
		return "redirect:/shop-staff";
	}

}
