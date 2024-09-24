package com.hirohiro716.scent.gui;

import java.awt.Cursor;

/**
 * マウスカーソルの列挙型。
 * 
 * @author hiro
*/
public enum MouseCursor {
    /**
     * デフォルトカーソル。
     */
    DEFAULT,
    /**
     * 指差しカーソル。
     */
    FINGER,
    /**
     * 移動カーソル。
     */
    MOVE,
    /**
     * テキストカーソル。
     */
    TEXT,
    /**
     * 待ち状態のカーソル。
     */
    WAIT,
    /**
     * 十字のカーソル。
     */
    CROSSHAIR,
    /**
     * 北方向サイズ変更のカーソル。
     */
    RESIZE_NORTH,
    /**
     * 東方向サイズ変更のカーソル。
     */
    RESIZE_EAST,
    /**
     * 南方向サイズ変更のカーソル。
     */
    RESIZE_SOUTH,
    /**
     * 西方向サイズ変更のカーソル
     */
    RESIZE_WEST,
    /**
     * 北東方向サイズ変更のカーソル。
     */
    RESIZE_NORTH_EAST,
    /**
     * 南東方向サイズ変更のカーソル。
     */
    RESIZE_SOUTH_EAST,
    /**
     * 南西方向サイズ変更のカーソル。
     */
    RESIZE_SOUTH_WEST,
    /**
     * 北西方向サイズ変更のカーソル。
     */
    RESIZE_NORTH_WEST,
    ;
    
    /**
     * 内部で使用されるGUIライブラリに依存したインスタンスを作成する。
     * 
     * @return
     */
    public Cursor createInnerInstance() {
        switch (this) {
        case DEFAULT:
            return Cursor.getDefaultCursor();
        case FINGER:
            return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        case MOVE:
            return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
        case TEXT:
            return Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
        case WAIT:
            return Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
        case CROSSHAIR:
            return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
        case RESIZE_NORTH:
            return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
        case RESIZE_EAST:
            return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
        case RESIZE_SOUTH:
            return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
        case RESIZE_WEST:
            return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
        case RESIZE_NORTH_EAST:
            return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
        case RESIZE_SOUTH_EAST:
            return Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
        case RESIZE_SOUTH_WEST:
            return Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
        case RESIZE_NORTH_WEST:
            return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
        }
        return null;
    }
}
