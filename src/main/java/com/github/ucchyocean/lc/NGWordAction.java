/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;


/**
 * NGワードを発言した人に与えるアクションの設定の種類
 * @author ucchy
 */
public enum NGWordAction {

    /** マスクするのみ */
    MASK("mask"),

    /** マスクしつつ、チャンネルからMuteする */
    MUTE("mute"),

    /** マスクしつつ、チャンネルからキックする */
    KICK("kick"),

    /** マスクしつつ、チャンネルからBANする */
    BAN("ban");

    private String id;

    /**
     * コンストラクタ
     * @param id ID
     */
    NGWordAction(String id) {
        this.id = id;
    }

    /**
     * 識別文字列を返す
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return id;
    }

    /**
     * 識別文字列から、NGWordActionを作成して返す。
     * 無効な文字列が指定された場合は、nullが返される。
     * @param id 識別文字列
     * @return 対応したNGWordAction
     */
    public static NGWordAction fromID(String id) {
        if ( id == null ) {
            return null;
        }
        for (NGWordAction value : values()) {
            if (id.equalsIgnoreCase(value.id)) {
                return value;
            }
        }
        return null;
    }
}
