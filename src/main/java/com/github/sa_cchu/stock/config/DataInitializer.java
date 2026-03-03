package com.github.sa_cchu.stock.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.github.sa_cchu.stock.entity.Authority;
import com.github.sa_cchu.stock.entity.Category;
import com.github.sa_cchu.stock.entity.User;
import com.github.sa_cchu.stock.repository.AuthorityRepository;
import com.github.sa_cchu.stock.repository.CategoryRepository;
import com.github.sa_cchu.stock.repository.UserRepository;

@Component //起動時にインスタンス化してくれている　DIかってにNEwしてくれる
public class DataInitializer implements CommandLineRunner {//起動後に一回だけrunの処理を行う

	//komatsu test
	//メンバ変数にする
	private final AuthorityRepository authorityRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final CategoryRepository categoryRepository;

	//コンストラクタ
	public DataInitializer(
			AuthorityRepository authorityRepository,
			UserRepository userRepository,
			CategoryRepository categoryRepository,
			PasswordEncoder passwordEncoder) {
		this.authorityRepository = authorityRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.categoryRepository = categoryRepository;
	}

	private Authority createAuthorityIfNotExists(String roleName) {
		return authorityRepository.findByAuthorityName(roleName)
				.orElseGet(() -> {
					Authority authority = new Authority();
					authority.setAuthorityName(roleName);
					return authorityRepository.save(authority);
				});
	}

	private void createCategoryIfNotExists(String categoryName) {
		categoryRepository.findByCategoryName(categoryName)
				.orElseGet(() -> {
					Category category = new Category();
					category.setCategoryName(categoryName);
					return categoryRepository.save(category);
				});
	}

	@Override
	public void run(String... args) {//runのメソッドは一クラスに一つ

		// ===== 権限初期データ =====
		Authority admin = createAuthorityIfNotExists("ROLE_ADMIN");
		createAuthorityIfNotExists("ROLE_SHOP");
		createAuthorityIfNotExists("ROLE_WAREHOUSE");
		
		
		// ===== カテゴリ初期データ =====
        createCategoryIfNotExists("食品");
        createCategoryIfNotExists("日用品");
        createCategoryIfNotExists("家電");
        createCategoryIfNotExists("衣料品");
        createCategoryIfNotExists("その他");
        


		// ===== ユーザー初期データ =====
		userRepository.findByUserName("admin")//リポジトリからメソッドを読んで引数をDBから探して変数に代入
				.orElseGet(() -> {
					User user = new User();
					user.setUserName("admin"); // ログインID
					user.setUserPassword(
							passwordEncoder.encode("password"));
					user.setUserGender("M");
					user.setAuthority(admin);
					user.setDeleteFlag(0);
					return userRepository.save(user);//インスタンスをインサートしている
				});
	}
}
