package com.github.sa_cchu.stock.enums;

/**
 * 権限種別を管理するEnum。
 * <p>アプリケーションで使用するユーザーの権限（管理者・店舗・倉庫）を一元管理するための列挙型。</p>
 * <p>
 * このEnumは以下の用途で使用する。
 * <ul>
 *   <li>DBに保存されている権限IDとアプリケーションの権限種別を対応付ける</li>
 *   <li>Spring Securityで使用するロール名（ROLE_XXX）を管理する</li>
 *   <li>Controller / Service 層で権限判定を行う際に使用する</li>
 * </ul></p>
 */
public enum AuthorityTypeEnum {
	/** 管理者権限 */
	ADMIN(1, "ROLE_ADMIN", "管理者"),
	/** 店舗ユーザー権限 */
    SHOP(2, "ROLE_SHOP", "店舗"),
    /** 倉庫ユーザー権限 */
    WAREHOUSE(3, "ROLE_WAREHOUSE", "倉庫");
	/** 権限ID（DBの authority_id と対応） */
    private final int authorityTypeId;
    /** Spring Securityで使用するロール名（DBの authorityName と対応） */
    private final String authorityTypeIdName;
    /** 画面表示用の物理名（DBの authorityName と対応） */
    private final String displayAuthorityName;

    /**
     * コンストラクタ
     *
     * @param authorityTypeId 権限ID
     * @param authorityTypeIdName Spring Securityのロール名
     */
    AuthorityTypeEnum(int authorityTypeId, String authorityTypeIdName, String displayName) {
        this.authorityTypeId = authorityTypeId;
        this.authorityTypeIdName = authorityTypeIdName;
        this.displayAuthorityName = displayName;
    }

    public int getAuthorityTypeId() { return authorityTypeId; }
    public String getAuthorityTypeName() { return authorityTypeIdName; }
    public String getDisplayName() { return displayAuthorityName; }
    
    /**
     * 権限IDからEnumを取得する
     * <p>
     * 入力情報やDBから取得した権限IDをEnumへ変換する際に使用する。
     * </p>
     * @param id 権限ID
     * @return 対応するAuthorityTypeEnum
     * @throws IllegalArgumentException 不正なIDが指定された場合
     */
    public static AuthorityTypeEnum fetchAuthorityType(int id) {
        for (AuthorityTypeEnum type : values()) {
        	// 引数で取得した権限IDをもとに一致した権限情報を返却する。
            if (type.authorityTypeId == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid authorityTypeId: " + id);
    }
}
