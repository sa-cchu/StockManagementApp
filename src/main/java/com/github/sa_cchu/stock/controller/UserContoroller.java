package com.github.sa_cchu.stock.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
		// 1. フォームと紐付けるための空のUserオブジェクト
		model.addAttribute("user", new User());

		// 2. 権限（Admin, Shop, Warehouseなど）のリスト
		model.addAttribute("authorities", customUserDetailsService.getAllAuthorities());

		// 3. 店舗のリスト（ShopServiceなどは適宜あなたの環境に合わせてください）
		model.addAttribute("shops", shopService.getAllShop());

		// 4. 倉庫のリスト
		model.addAttribute("warehouses", warehouseService.getAllWarehouses());

		return "user-form";
	}

	// 登録処理（Add）
	@PostMapping("/add")
	public String addUser(@ModelAttribute User user, Model model) {
		try {
			// ★ パスワードを暗号化してからセット
			String encodedPassword = passwordEncoder.encode(user.getUserPassword());
			user.setUserPassword(encodedPassword);

			// 削除フラグ 0 (有効)
			user.setDeleteFlag(0);

			// 保存実行
			customUserDetailsService.saveUser(user);

			return "redirect:/user";
		} catch (Exception e) {
			model.addAttribute("errorMessage", "登録に失敗しました: " + e.getMessage());
			// 画面を戻すためのデータ再セット
			model.addAttribute("authorities", customUserDetailsService.getAllAuthorities());
			model.addAttribute("shops", shopService.getAllShop());
			model.addAttribute("warehouses", warehouseService.getAllWarehouses());
			return "user-form";
		}
	}

	// 編集画面を表示する
	@GetMapping("/edit/{id}")
	public String editUser(@PathVariable("id") Integer id, Model model) {
		// IDを元にユーザー情報を取得（いなければ一覧へ戻す）
		User user = customUserDetailsService.getUserById(id);

		model.addAttribute("user", user);
		model.addAttribute("authorities", customUserDetailsService.getAllAuthorities());
		model.addAttribute("shops", shopService.getAllShop());
		model.addAttribute("warehouses", warehouseService.getAllWarehouses());

		return "user-form"; // 新規と同じHTMLを使う
	}

	// 更新処理を実行する
	// 更新処理を実行する
	@PostMapping("/update")
	public String updateUser(@ModelAttribute User user, Model model) {
		try {
			// Service側の引数を1つに修正したため、これでコンパイルが通ります
			customUserDetailsService.updateUser(user);
			return "redirect:/user";
		} catch (Exception e) {
			model.addAttribute("errorMessage", "更新に失敗しました。");
			model.addAttribute("authorities", customUserDetailsService.getAllAuthorities());
			model.addAttribute("shops", shopService.getAllShop());
			model.addAttribute("warehouses", warehouseService.getAllWarehouses());
			return "user-form";
		}
	}

}
