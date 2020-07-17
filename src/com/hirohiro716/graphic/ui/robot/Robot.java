package com.hirohiro716.graphic.ui.robot;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import com.hirohiro716.graphic.ui.KeyCode;

/**
 * マウスやキーボードのネイティブなシステム入力イベントを生成し、アプリケーションを自動操作するクラス。
 * 
 * @author hiro
 *
 */
public class Robot extends java.awt.Robot {

    /**
     * コンストラクタ。<br>
     * 指定されたスクリーンデバイスの座標システムでオブジェクトを構築する。
     * 
     * @param screen
     * @throws AWTException
     */
    public Robot(GraphicsDevice screen) throws AWTException {
        super(screen);
    }
    
    /**
     * コンストラクタ。<br>
     * プライマリスクリーンの座標システムでオブジェクトを構築する。
     * 
     * @throws AWTException
     */
    public Robot() throws AWTException {
        this(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
    }
    
    /**
     * 指定されたキーを押して離す。同時にキー入力を行う場合は複数の値を指定する。
     * 
     * @param keyCodes
     */
    public void KeyType(Integer... keyCodes) {
        for (Integer key : keyCodes) {
            this.keyPress(key);
        }
        for (Integer key : keyCodes) {
            this.keyRelease(key);
        }
    }
    
    /**
     * 指定されたキーを押して離す。同時にキー入力を行う場合は複数の値を指定する。
     * 
     * @param keyCodes
     */
    public void KeyType(KeyCode... keyCodes) {
        for (KeyCode keyCode : keyCodes) {
            this.keyPress(keyCode.getKeyCodeAWT());
        }
        for (KeyCode keyCode : keyCodes) {
            this.keyRelease(keyCode.getKeyCodeAWT());
        }
    }
}
