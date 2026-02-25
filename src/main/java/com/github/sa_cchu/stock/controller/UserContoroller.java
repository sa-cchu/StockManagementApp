package com.github.sa_cchu.stock.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
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

import com.github.sa_cchu.stock.entity.User;
import com.github.sa_cchu.stock.service.CustomUserDetailsService;
import com.github.sa_cchu.stock.service.ShopService;
import com.github.sa_cchu.stock.service.WarehouseService;

@Controller
@RequestMapping("/user")
public class UserContoroller {

	private final CustomUserDetailsService customUserDetailsService;
	private final ShopService shopService;
	private final WarehouseService warehouseService;
	private final PasswordEncoder passwordEncoder;

	public UserContoroller(CustomUserDetailsService customUserDetailsService, ShopService shopService,
			WarehouseService warehouseService, PasswordEncoder passwordEncoder) {
		this.customUserDetailsService = customUserDetailsService;
		this.shopService = shopService;
		this.warehouseService = warehouseService;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping
	public String listUser(@RequestParam(name = "authorityId", required = false) Integer authorityId, Model model) {
		model.addAttribute("authorities", customUserDetailsService.getAllAuthorities());
		model.addAttribute("userList", customUserDetailsService.getUserList(authorityId));
		model.addAttribute("selectedId", authorityId);
		return "userMG";
	}

	@GetMapping("/new")
	public String newUser(Model model) {
		model.addAttribute("user", new User());
		reloadModel(model);
		return "user-form";
	}

	// 登録処理（Add）
	@PostMapping("/add")
	public String addUser(@Validated @ModelAttribute("user") User user, BindingResult result, Model model) {

		// 1. ユーザー名重複チェック
		if (customUserDetailsService.isUserNameExists(user.getUserName(), null)) {
			result.rejectValue("userName", "error.userName", "このユーザー名は既に使用されています");
		}

		// 2. パスワード文字数チェック（新規のみ）
		if (user.getUserPassword() == null || user.getUserPassword().length() < 8) {
			result.rejectValue("userPassword", "error.userPassword", "パスワードは8文字以上で入力してください");
		}

		// 3. 所属チェック（権限に応じて）
		validateBelonging(user, result);

		// エラーがある場合はフォームに戻る
		if (result.hasErrors()) {
			reloadModel(model);
			return "user-form";
		}

		try {
			// パスワードを暗号化
			user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
			user.setDeleteFlag(0);
			customUserDetailsService.saveUser(user);
			return "redirect:/user";
		} catch (Exception e) {
			model.addAttribute("errorMessage", "登録に失敗しました。");
			reloadModel(model);
			return "user-form";
		}
	}

	@GetMapping("/edit/{id}")
	public String editUser(@PathVariable("id") Integer id, Model model) {
		User user = customUserDetailsService.getUserById(id);
		model.addAttribute("user", user);
		reloadModel(model);
		return "user-form";
	}

	// 更新処理（Update）
	@PostMapping("/update")
	public String updateUser(@Validated @ModelAttribute("user") User user, BindingResult result, Model model) {

		// 1. ユーザー名重複チェック（自分以外のIDで重複がないか）
		if (customUserDetailsService.isUserNameExists(user.getUserName(), user.getUserId())) {
			result.rejectValue("userName", "error.userName", "このユーザー名は既に使用されています");
		}

		// 2. 所属チェック
		validateBelonging(user, result);

		// エラーがある場合はフォームに戻る
		if (result.hasErrors()) {
			reloadModel(model);
			return "user-form";
		}

		try {
			customUserDetailsService.updateUser(user);
			return "redirect:/user";
		} catch (Exception e) {
			model.addAttribute("errorMessage", "更新に失敗しました。");
			reloadModel(model);
			return "user-form";
		}
	}

	/**
	 * 権限に基づいた所属（店舗・倉庫）の相関バリデーション
	 */
	private void validateBelonging(User user, BindingResult result) {
		if (user.getAuthority() != null && user.getAuthority().getAuthorityId() != null) {
			// 選択された権限の名前を取得
			String authName = customUserDetailsService.getAuthorityNameById(user.getAuthority().getAuthorityId());

			if (authName.contains("SHOP")) {
				if (user.getShop() == null || user.getShop().getShopId() == null) {
					result.rejectValue("shop", "error.shop", "所属店舗を選択してください");
				}
			} else if (authName.contains("WAREHOUSE")) {
				if (user.getWarehouse() == null || user.getWarehouse().getWarehouseId() == null) {
					result.rejectValue("warehouse", "error.warehouse", "所属倉庫を選択してください");
				}
			}
		}
	}

	/**
	 * フォーム表示に必要なデータをModelにセットする共通メソッド
	 */
	private void reloadModel(Model model) {
		model.addAttribute("authorities", customUserDetailsService.getAllAuthorities());
		model.addAttribute("shops", shopService.getAllShop());
		model.addAttribute("warehouses", warehouseService.getAllWarehouses());
	}
}