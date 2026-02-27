
package com.github.sa_cchu.stock.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.github.sa_cchu.stock.entity.Authority;
import com.github.sa_cchu.stock.entity.User;
import com.github.sa_cchu.stock.repository.AuthorityRepository;
import com.github.sa_cchu.stock.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final AuthorityRepository authorityRepository;

	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository, AuthorityRepository authorityRepository) {
		this.userRepository = userRepository;
		this.authorityRepository = authorityRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		return userRepository.findByUserName(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	// 権限絞り込み
	public List<Authority> getAllAuthorities() {
		return authorityRepository.findByDeleteFlag(0);
	}

	public List<User> getUserList(Integer authorityId) {
		if (authorityId == null || authorityId == 0) {
			return userRepository.findByDeleteFlag(0);
		}

		Authority authority = new Authority();
		authority.setAuthorityId(authorityId);
		return userRepository.findByAuthorityAndDeleteFlag(authority, 0);
	}

	@Transactional
	public void saveUser(User user) {
		userRepository.save(user);
	}

	// IDでユーザーを1件取得（編集画面を表示する時に使う）
	public User getUserById(Integer id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("指定されたユーザーは見つかりません。ID: " + id));
	}

	// ユーザー更新処理（パスワード更新なし版）
	@Transactional
	public void updateUser(User user) { // 引数をuserだけに！
		// 1. DBから現在のデータを取得
		User existingUser = userRepository.findById(user.getUserId())
				.orElseThrow(() -> new IllegalArgumentException("更新対象のユーザーが見つかりません。"));

		// 2. 基本情報の詰め替え（パスワードは既存の existingUser のものを保持）
		existingUser.setUserName(user.getUserName());
		existingUser.setUserGender(user.getUserGender());
		existingUser.setAuthority(user.getAuthority());
		existingUser.setShop(user.getShop());
		existingUser.setWarehouse(user.getWarehouse());

		// userRepository.save(existingUser); // @Transactionalがあるので省略可能ですが書いてもOK
	}

	public boolean isUserNameExists(String userName, Integer userId) {
		if (userId == null) {
			// 新規登録時
			return userRepository.existsByUserName(userName);
		} else {
			// 更新時（自分以外のIDで同じ名前があるか）
			return userRepository.existsByUserNameAndUserIdNot(userName, userId);
		}
	}

	public String getAuthorityNameById(Integer id) {
		return authorityRepository.findById(id).map(Authority::getAuthorityName).orElse("");
	}

	// 論理削除
	public void deleteUser(Integer userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("ユーザーが存在しません"));
		user.setDeleteFlag(1);
		userRepository.save(user);
	}
}
