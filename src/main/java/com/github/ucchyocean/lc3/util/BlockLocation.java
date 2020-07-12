/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.util;

import org.jetbrains.annotations.Nullable;

/**
 * 座標を保持するためのオブジェクトクラス
 * @author ucchy
 */
public class BlockLocation {

    private String worldName;
    private int x;
    private int y;
    private int z;

    /**
     * コンストラクタ
     * @param worldName ワールド名
     * @param x X座標
     * @param y Y座標
     * @param z Z座標
     */
    public BlockLocation(String worldName, int x, int y, int z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * ワールド名を取得する
     * @return worldName
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * X座標を取得する
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * Y座標を取得する
     * @return y
     */
    public int getY() {
        return y;
    }

    /**
     * Z座標を取得する
     * @return z
     */
    public int getZ() {
        return z;
    }

    /**
     * 文字列に変換する
     * @return 文字列
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s,%d,%d,%d", worldName, x, y, z);
    }

    /**
     * 文字列からBlockLocationに変換する
     * @param str 文字列
     * @return BlockLocation
     */
    public static @Nullable BlockLocation fromString(String str) {
        if ( str == null ) return null;
        String[] temp = str.split(",");
        if ( temp.length < 4 ) return null;
        return new BlockLocation(
                temp[0], toInt(temp[1]), toInt(temp[2]), toInt(temp[3]));
    }

    private static int toInt(String str) {
        if ( !str.matches("-?[0-9]+") ) return 0;
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
