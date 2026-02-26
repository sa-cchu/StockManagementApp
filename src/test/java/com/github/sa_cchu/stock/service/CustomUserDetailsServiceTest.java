package com.github.sa_cchu.stock.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.github.sa_cchu.stock.entity.Authority;
import com.github.sa_cchu.stock.entity.User;
import com.github.sa_cchu.stock.repository.AuthorityRepository;
import com.github.sa_cchu.stock.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private AuthorityRepository authorityRepository;

	@InjectMocks
	private CustomUserDetailsService service;

	// 1. ログイン処理のテスト
	@Test
	@DisplayName("loadUserByUsername: ユーザーが存在する場合、UserDetailsを返す")
	void loadUserByUsername_Success() {
		User user = new User();
		user.setUserName("testUser");
		user.setUserPassword("hashed_password");
		user.setAuthority(createAuthority(1, "ROLE_USER"));

		when(userRepository.findByUserName("testUser")).thenReturn(Optional.of(user));

		UserDetails result = service.loadUserByUsername("testUser");
		assertEquals("testUser", result.getUsername());
	}

	@Test
	@DisplayName("loadUserByUsername: ユーザーが存在しない場合、例外がスローされること")
	void loadUserByUsername_UserNotFound() {
		// 1. 準備：リポジトリが Empty を返すように設定
		when(userRepository.findByUserName("unknownUser")).thenReturn(Optional.empty());

		// 2. & 3. 実行と検証：UsernameNotFoundException が投げられることを確認
		assertThrows(UsernameNotFoundException.class, () -> {
			service.loadUserByUsername("unknownUser");
		});
	}

	// 2. ユーザー一覧取得のテスト
	@Test
	@DisplayName("getUserList: 権限IDが0の場合は全件取得する")
	void getUserList_ReturnsAll_WhenIdIsZero() {
		when(userRepository.findByDeleteFlag(0)).thenReturn(List.of(new User(), new User()));

		List<User> result = service.getUserList(0);
		assertEquals(2, result.size());
		verify(userRepository, times(1)).findByDeleteFlag(0);
	}

	// 3. ユーザー更新のテスト（ロジックの詰め替え検証）
	@Test
	@DisplayName("updateUser: 既存のパスワードを維持したまま他の項目が更新されること")
	void updateUser_MaintainsPassword() {
		// 準備
		User existingUser = new User();
		existingUser.setUserId(10);
		existingUser.setUserPassword("keep_this_password");

		User updateData = new User();
		updateData.setUserId(10);
		updateData.setUserName("newName");
		updateData.setUserGender("Male");

		when(userRepository.findById(10)).thenReturn(Optional.of(existingUser));

		// 実行
		service.updateUser(updateData);

		// 検証
		assertEquals("newName", existingUser.getUserName());
		assertEquals("keep_this_password", existingUser.getUserPassword()); // パスワードが変わっていない
	}

	// 4. 重複チェックのテスト
	@Test
	@DisplayName("isUserNameExists: 新規登録で名前が重複しているならtrue")
	void isUserNameExists_NewUser_Duplicate() {
		when(userRepository.existsByUserName("duplicateName")).thenReturn(true);

		boolean result = service.isUserNameExists("duplicateName", null);
		assertTrue(result);
	}

	// --- ヘルパーメソッド ---
	private Authority createAuthority(Integer id, String name) {
		Authority auth = new Authority();
		auth.setAuthorityId(id);
		auth.setAuthorityName(name);
		return auth;
	}
}