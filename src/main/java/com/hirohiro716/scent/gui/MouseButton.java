package com.hirohiro716.scent.gui;

/**
 * マウスのボタンを表す列挙型。
 */
public enum MouseButton {
    /**
     * ボタン1、通常のマウスボタン配置で左側。
     */
    BUTTON1,
    /**
     * ボタン2、通常のマウスボタン配置で中央。
     */
    BUTTON2,
    /**
     * ボタン3、通常のマウスボタン配置で右側。
     */
    BUTTON3,
    ;

    /**
     * AWTのボタン定数を取得する。
     * 
     * @return
     */
    public int getButtonOfAWT() {
        switch (this) {
            case BUTTON1:
                return java.awt.event.InputEvent.BUTTON1_DOWN_MASK;
            case BUTTON2:
                return java.awt.event.InputEvent.BUTTON2_DOWN_MASK;
            case BUTTON3:
                return java.awt.event.InputEvent.BUTTON3_DOWN_MASK;
        }
        return 0;
    }
}
