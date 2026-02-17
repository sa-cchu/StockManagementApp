package com.github.sa_cchu.stock.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.github.sa_cchu.stock.entity.Authority;
import com.github.sa_cchu.stock.entity.User;
import com.github.sa_cchu.stock.repository.AuthorityRepository;
import com.github.sa_cchu.stock.repository.UserRepository;

@Component //起動時にインスタンス化してくれている　DIかってにNEwしてくれる
public class DataInitializer implements CommandLineRunner {//起動後に一回だけrunの処理を行う

	//メンバ変数にする
	private final AuthorityRepository authorityRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	//コンストラクタ
	public DataInitializer(
			AuthorityRepository authorityRepository,
			UserRepository userRepository,
			PasswordEncoder passwordEncoder) {
		this.authorityRepository = authorityRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {//runのメソッドは一クラスに一つ

		// ===== 権限初期データ =====
		Authority adminAuthority = authorityRepository
				.findByAuthorityName("ROLE_ADMIN")//リポジトリからメソッドを読んで引数をDBから探して変数に代入
				
				.orElseGet(() -> {//こいつは見つからなかった時だけ下の処理をするようにラムだ式
					Authority authority = new Authority();//インスタンス化して
					authority.setAuthorityName("ROLE_ADMIN");//作ったインスタンスに値を入れている　//ROLE_がないと画面制御がだるい
					return authorityRepository.save(authority);//インスタンスをインサートしている
				});

		// ===== ユーザー初期データ =====
		userRepository.findByUserName("admin")
				.orElseGet(() -> {
					User user = new User();
					user.setUserName("admin"); // ログインID
					user.setUserPassword(
							passwordEncoder.encode("password"));
					user.setUserGender("M");
					user.setAuthority(adminAuthority);
					user.setDeleteFlag(0);
					return userRepository.save(user);
				});
	}
}
