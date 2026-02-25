package com.github.sa_cchu.stock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;


@Configuration//Beanがあるクラスには必須アノテーション
public class SecurityConfig {
	@Bean
	public SpringSecurityDialect springSecurityDialect() {
	    return new SpringSecurityDialect();
	}
	
	
    @Bean//Spring管理のオブジェクトとして登録するアノテーション
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
        		//URLのアクセス制御
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login").permitAll()//フルアクセス
                .anyRequest().authenticated()
               //その他すべて　　//ログインユーザーのみアクセス可能
            )
            
            .formLogin(login -> login//フィルターが自動で作られ＠Serviceで取って着た値と入力値をBCyryptした値と照合
                .loginPage("/login")//loginページのformからうけとる
                .defaultSuccessUrl("/", true)//成功したら指定URLに飛ばす
                .permitAll()
                //ログイン失敗したら自動で戻してくれる
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
            );

        return http.build();
    }

    @Bean//登録時に呼ばれて使われている
    
    
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
