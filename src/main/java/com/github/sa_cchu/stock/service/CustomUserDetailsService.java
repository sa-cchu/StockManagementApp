
package com.github.sa_cchu.stock.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.sa_cchu.stock.dto.UserFormDTO;
import com.github.sa_cchu.stock.dto.UserListDTO;
import com.github.sa_cchu.stock.entity.Authority;
import com.github.sa_cchu.stock.entity.User;
import com.github.sa_cchu.stock.repository.AuthorityRepository;
import com.github.sa_cchu.stock.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final AuthorityRepository authorityRepository;
	private final UserRepository userRepository;
	private final ShopService shopService;
	private final WarehouseService warehouseService;
	private final PasswordEncoder passwordEncoder;

	public CustomUserDetailsService(UserRepository userRepository, AuthorityRepository authorityRepository,
			ShopService shopService, WarehouseService warehouseService, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.authorityRepository = authorityRepository;
		this.shopService = shopService;
		this.warehouseService = warehouseService;
		this.passwordEncoder = passwordEncoder;
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

		// ★ パスワードが null でない（入力があった）場合のみ更新
		if (user.getUserPassword() != null) {
			existingUser.setUserPassword(user.getUserPassword());
		}

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
	///////////////////////////////////////////////////////////////////////////////

	// DTOバージョンで作成//

	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 1. 【一覧用】所属リスト（プルダウン）の取得
	 */
	public List<?> getBelongingList(User operator) {
		if (operator.getAuthority().getAuthorityName().contains("SHOP")) {
			return shopService.getAllShopDTOs();
		}
		return warehouseService.getAllWarehouseDTOs();
	}

	/**
	 * 【一覧表示用】管轄権限（SHOPまたはWAREHOUSE）を持つ全ユーザーを取得
	 */
	public List<UserListDTO> getUserListForOperator(User operator, Integer belongingId) {
		String authName = operator.getAuthority().getAuthorityName();
		List<User> users;

		// 「すべて」が選択されている、またはID指定がない場合の全件取得ロジック
		if (authName.contains("SHOP")) {
			if (belongingId == null || belongingId == 0) { // 「すべて」の場合
				// ROLE_SHOP を持つ全ユーザーを取得（リポジトリにメソッド追加が必要）
				users = userRepository.findByAuthority_AuthorityNameContainingAndDeleteFlag("SHOP", 0);
			} else {
				
				users = userRepository.findByShop_ShopIdAndDeleteFlag(belongingId, 0);
			}
		} else {
			if (belongingId == null || belongingId == 0) { // 「すべて」の場合
				// ROLE_WAREHOUSE を持つ全ユーザーを取得
				users = userRepository.findByAuthority_AuthorityNameContainingAndDeleteFlag("WAREHOUSE", 0);
			} else {
				users = userRepository.findByWarehouse_WarehouseIdAndDeleteFlag(belongingId, 0);
			}
		}

		return users.stream().map(this::mapToUserListDTO).toList();
	}

	/**
	 * 一覧用DTOへの変換（性別・所属名を網羅）
	 */
	private UserListDTO mapToUserListDTO(User user) {
		UserListDTO dto = new UserListDTO();
		dto.setUserId(user.getUserId());
		dto.setUserName(user.getUserName());
		dto.setUserGender(user.getUserGender()); // M or F

		// 所属名をセット（SHOP/WAREHOUSEどちらでも対応）
		if (user.getShop() != null) {
			dto.setBelongingName(user.getShop().getShopName());
		} else if (user.getWarehouse() != null) {
			dto.setBelongingName(user.getWarehouse().getWarehouseName());
		} else {
			dto.setBelongingName("未所属");
		}
		return dto;
	}

	/**
	 * 3. 【新規用】完全に初期化された空のDTOを作成
	 */
	public UserFormDTO createNewUserFormDTO(User operator) {
		UserFormDTO dto = new UserFormDTO();

		// 自分の情報は一切入れない。
		// 唯一、これから作るユーザーの「デフォルト権限」だけをセットする。
		dto.setAuthorityId(operator.getAuthority().getAuthorityId());
		dto.setAuthorityName(operator.getAuthority().getAuthorityName());

		// 他のフィールドは明示的に空（null）であることを確定させる
		dto.setUserId(null);
		dto.setUserName("");
		dto.setUserPassword("");
		dto.setUserGender(null);
		dto.setBelongingId(null);

		return dto;
	}

	/**
	 * 4. 【編集用】所属に関係なく、指定されたIDのユーザーを取得する
	 */
	public UserFormDTO getAuthorizedUserFormDTO(Integer userId, User operator) {
		// 1. IDで検索（所属チェックを外しました）
		User target = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("対象が見つかりません ID:" + userId));

		// 2. ターゲットの情報をDTOに移し替える
		UserFormDTO dto = new UserFormDTO();
		dto.setUserId(target.getUserId());
		dto.setUserName(target.getUserName());
		dto.setUserGender(target.getUserGender());

		// 現在の権限名をセット
		if (target.getAuthority() != null) {
			dto.setAuthorityId(target.getAuthority().getAuthorityId());
			dto.setAuthorityName(target.getAuthority().getAuthorityName());
		}

		// 現在の所属IDをセット
		if (target.getShop() != null) {
			dto.setBelongingId(target.getShop().getShopId());
		
		} else if (target.getWarehouse() != null) {
			dto.setBelongingId(target.getWarehouse().getWarehouseId());
			
		}

		// パスワードは編集不可なのでセットしない
		return dto;
	}

	@Transactional
	public User saveUserFormDTO(UserFormDTO dto) {
		User user;

		if (dto.getUserId() == null) {
			user = new User();
			user.setUserPassword(passwordEncoder.encode(dto.getUserPassword()));
		} else {
			user = userRepository.findById(dto.getUserId())
					.orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));
		}
		
		user.setUserName(dto.getUserName());
		user.setUserGender(dto.getUserGender());
		
		Authority authority = authorityRepository.findById(dto.getAuthorityId()).orElseThrow();
		user.setAuthority(authority);
		
		if (authority.getAuthorityName().contains("SHOP")) {
	        user.setShop(shopService.getShop(dto.getBelongingId()));
	        user.setWarehouse(null); // 片方をセットしたら、もう片方は必ずクリア
	    } else {
	        user.setWarehouse(warehouseService.getWarehouse(dto.getBelongingId()));
	        user.setShop(null);
	    }

	    return userRepository.save(user);
		
	}

}