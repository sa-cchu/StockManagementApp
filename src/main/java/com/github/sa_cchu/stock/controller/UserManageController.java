package com.github.sa_cchu.stock.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.sa_cchu.stock.entity.User;
import com.github.sa_cchu.stock.service.CustomUserDetailsService;

@Controller
@RequestMapping("/user/manage")
public class UserManageController {

	private final CustomUserDetailsService userDetailsService;

	// コンストラクタインジェクション（これがないと final フィールドが初期化されずエラーになります）
	public UserManageController(CustomUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;

	}
	@GetMapping
	public String list(@AuthenticationPrincipal User operator,
	                   @RequestParam(name = "belongingId", required = false) Integer belongingId,
	                   Model model) {
	    // 1. 指定がなければ「すべて(null)」として扱い、全件取得
	    model.addAttribute("userList", userDetailsService.getUserListForOperator(operator, belongingId));
	    // 2. プルダウンの選択肢
	    model.addAttribute("belongings", userDetailsService.getBelongingList(operator));
	    model.addAttribute("selectedId", belongingId);
	    return "myTeamMG";
	}

	@GetMapping("/new")
	public String showRegistrationForm(@AuthenticationPrincipal User operator, Model model) {
	    model.addAttribute("userForm", userDetailsService.createNewUserFormDTO(operator));
	    model.addAttribute("belongingList", userDetailsService.getBelongingList(operator));
	    return "myTeam-form";
	}

	@GetMapping("/edit/{userId}")
	public String showEditForm(@PathVariable("userId") Integer userId,
	                           @AuthenticationPrincipal User operator,
	                           Model model) {
	    try {
	        model.addAttribute("userForm", userDetailsService.getAuthorizedUserFormDTO(userId, operator));
	        model.addAttribute("belongingList", userDetailsService.getBelongingList(operator));
	        return "myTeam-form";
	    } catch (SecurityException e) {
	        return "redirect:/user/manage?error=unauthorized";
	    }
	}
}
