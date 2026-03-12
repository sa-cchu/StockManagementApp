package com.github.sa_cchu.stock.enums;

/**
 * 
 */
public enum AuthorityTypeEnum {
	ADMIN(1, "ROLE_ADMIN"),
    SHOP(2, "ROLE_SHOP"),
    WAREHOUSE(3, "ROLE_WAREHOUSE");

    private final int authorityTypeId;
    private final String authorityTypeIdName;

    AuthorityTypeEnum(int authorityTypeId, String authorityTypeIdName) {
        this.authorityTypeId = authorityTypeId;
        this.authorityTypeIdName = authorityTypeIdName;
    }

    public int getAuthorityTypeId() { return authorityTypeId; }
    public String getAuthorityTypeName() { return authorityTypeIdName; }
    
    // 
    public static AuthorityTypeEnum fromId(int id) {
        for (AuthorityTypeEnum type : values()) {
            if (type.authorityTypeId == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid authorityTypeId: " + id);
    }
    
}
