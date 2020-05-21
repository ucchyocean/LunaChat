/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc;

/**
 * コマンド実行者のインターフェイス
 * @author ucchy
 */
public interface CommandSenderInterface {

    /**
     * 同一のオブジェクトかどうかを返す
     * @param other 他方のオブジェクト
     * @return 同一かどうか
     */
    public boolean equals(Object other);

    /**
     * OPかどうかを返す
     * @return OPかどうか
     */
    public boolean isOp(); // TODO BungeeCordにはOPの概念が無いようなので、このメソッドは不要かも。

    /**
     * コマンド実行者が指定のパーミッションについて指定されているかどうかを返す
     * @param permission パーミッション
     * @return パーミッションを指定されているかどうか
     */
    public boolean isPermissionSet(String permission);

    /**
     * コマンド実行者が指定のパーミッションを持っているかどうかを判定する
     * @param permission パーミッション
     * @return パーミッションを持っているかどうか
     */
    public boolean hasPermission(String permission);

    /**
     * コマンド実行者にメッセージを送る
     * @param message メッセージ
     */
    public void sendMessage(String message);
}
