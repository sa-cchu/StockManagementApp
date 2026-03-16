package com.github.sa_cchu.stock.enums;


/**
 * お問い合わせやタスクのステータスを表す Enum。
 * <p>画面表示用の名称と、DB や処理で使用する整数 ID を紐付けて管理する。</p>
 */
public enum StatusEnum {
	/** 未対応のステータス */
    UNHANDLED(1, "未対応"),
    /**	対応中のステータス */
    IN_PROGRESS(2, "対応中"),
    /**	対応済のステータス */
    COMPLETED(3, "対応済");

	/** ステータス ID（DB などで管理される整数値） */
    private final int statusId;
    /** 画面表示用のステータス名称 */
    private final String displayStatusName;
    
    /**
     * コンストラクタ
     * @param statusId DB などで使用するステータス ID
     * @param displayName 画面に表示するステータス名称
     */
    StatusEnum(int statusId, String displayStatusName) {
        this.statusId = statusId;
        this.displayStatusName = displayStatusName;
    }
    
    /**
     * ステータス ID を取得する。
     * @return ステータス ID
     */
    public int getStatusId() {return statusId;}
    /**
     * 画面表示用のステータス名を取得する。
     * @return 画面表示用名称
     */
    public String getDisplayStatusName() {return displayStatusName;}
    
    /**
     * ステータス ID から Enum を取得するユーティリティ。
     * @param id ステータス ID
     * @return 対応する StatusEnum
     * @throws IllegalArgumentException ID が存在しない場合
     */
    public static StatusEnum fromId(int id) {
        for (StatusEnum status : values()) {
            if (status.getStatusId() == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("不正なステータスID: " + id);
    }
}
