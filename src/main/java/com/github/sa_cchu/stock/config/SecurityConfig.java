package com.github.sa_cchu.stock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

@Configuration // このクラスが「設定用クラス」であることをSpringに知らせます
public class SecurityConfig {

	@Bean // Thymeleafで「ログイン中か？」等の判定を使えるようにする部品を登録します
	public SpringSecurityDialect springSecurityDialect() {
		return new SpringSecurityDialect();
	}

	@Bean // セキュリティのメインルール（フィルターチェーン）を構築します
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
				// 1. URLごとのアクセス権限を設定開始
				.authorizeHttpRequests(auth -> auth

						// 「/login」や静的リソースへのアクセスは、誰でも（未ログインでも）許可します
						.requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/webjars/**","/inquiry/guest/create/**","/error/**").permitAll()
						// ADMIN権限のみアクセス可能なURL
						.requestMatchers("/user/**", "/shop/**", "/warehouse/**", "/goods/**", "/relation/**").hasRole("ADMIN")
						// SHOP権限のみアクセス可能なURL
						.requestMatchers("/shop-staff/**", "/shop-stock/**", "/linked-warehouses/**", "/shop-order/**", "/order/**").hasRole("SHOP")
						// WAREHOUSE権限のみアクセス可能なURL
						.requestMatchers("/warehouse-staff/**", "/warehouse-stock/**", "/warehouse-order/**").hasRole("WAREHOUSE")
						// 今後、どの権限でもアクセスできるがログインは必要な問い合わせなどはanyRequestで拾うか、.hasAnyRoleで複数割り当て可能

						// それ以外のすべてのリクエストは、ログイン済みユーザーのみ許可します
						.anyRequest().authenticated())

				// 2. フォームログイン（画面からのログイン）の設定開始
				.formLogin(login -> login
						// 自作のログイン画面（URL: /login）を使うよう指定します
						.loginPage("/login")
						// ログイン成功後、必ずトップページ（/）に飛ばします
						.defaultSuccessUrl("/", true)
						// ログイン画面自体は誰でもアクセスできるようにします
						.permitAll())

				// 3. ログアウトの設定開始
				.logout(logout -> logout
						// ログアウト成功後、「?logout」という印を付けてログイン画面へ戻します
						.logoutSuccessUrl("/login?logout")
						.invalidateHttpSession(true) // セッションを無効にする
					    .deleteCookies("JSESSIONID") // クッキーも削除する
					    .permitAll())
				
				// 4. セッション管理の設定
				.sessionManagement(session -> session
						// セッションが切れた（無効になった）時の遷移先を指定 → ログイン画面に戻す
						.invalidSessionUrl("/login?timeout"));

		// 設定を組み立てて完成した「ルール一式」をSpringに返します
		return http.build();
	}

	@Bean // パスワードを「生」ではなく「ハッシュ化（暗号化）」して保存・比較する部品を登録します
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}