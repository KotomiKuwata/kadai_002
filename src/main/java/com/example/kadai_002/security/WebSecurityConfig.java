package com.example.kadai_002.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests((requests) -> requests
						.requestMatchers("/css/**", "/images/**", "/js/**", "/storage/**", "/", "/signup/**", "/stores", "/stores/{id}", "/membership/**", "/stripe/webhook", "/reviews/**").permitAll() // すべてのユーザーにアクセスを許可するURL           "
						.requestMatchers("/admin/**", "/reviews/**").hasRole("ADMIN") // 管理者にのみアクセスを許可するURL
						//.requestMatchers().hasRole("GENERAL") //無料会員
						.requestMatchers("/paidmember/**", "/reviews/**").hasRole("PAID_MEMBER") //有料会員
						.anyRequest().authenticated() // 上記以外のURLはログインが必要（会員または管理者のどちらでもOK）
				)
				.formLogin((form) -> form
						.loginPage("/login") // ログインページのURL
						.loginProcessingUrl("/login") // ログインフォームの送信先URL
						.defaultSuccessUrl("/?loggedIn") // ログイン成功時のリダイレクト先URL
						.failureUrl("/login?error") // ログイン失敗時のリダイレクト先URL
						.permitAll())
				.logout((logout) -> logout
						.logoutSuccessUrl("/?loggedOut") // ログアウト時のリダイレクト先URL
						.permitAll())
				.csrf (csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/stripe/webhook")));

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
