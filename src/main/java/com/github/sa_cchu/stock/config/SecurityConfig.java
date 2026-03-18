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
						// 「/login」へのアクセスは、誰でも（未ログインでも）許可します
						.requestMatchers("/login").permitAll()
						// 「/inquiry/guestCreate」（お問い合わせ）へのアクセスは、誰でも（未ログインでも）許可します
						.requestMatchers("/inquiry/guest/create/**").permitAll()
						// 静的ファイルの読み込みを許可する
					    .requestMatchers("/webjars/**").permitAll()
					    .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
					    // エラー時のアクセスを許可する。
					    .requestMatchers("/error").permitAll()
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
						.logoutSuccessUrl("/login?logout"));

		// 設定を組み立てて完成した「ルール一式」をSpringに返します
		return http.build();
	}

	@Bean // パスワードを「生」ではなく「ハッシュ化（暗号化）」して保存・比較する部品を登録します
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}