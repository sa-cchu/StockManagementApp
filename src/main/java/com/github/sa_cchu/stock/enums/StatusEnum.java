package com.github.sa_cchu.stock.enums;


/**
 * 
 */
public enum StatusEnum {
	/**	未対応 */
    UNHANDLED(1, "未対応"),
    /**	対応中 */
    IN_PROGRESS(2, "対応中"),
    /**	対応済 */
    COMPLETED(3, "対応済");

    private final int statusId;
    private final String displayStatusName;
    
    /**
     * コンストラクタ
     * @param statusId ステータスId
     * @param displayName 画面に渡す用のステータス名
     */
    StatusEnum(int statusId, String displayStatusName) {
        this.statusId = statusId;
        this.displayStatusName = displayStatusName;
    }

    public int getStatusId() {return statusId;}
    public String getDisplayStatusName() {return displayStatusName;}
}
