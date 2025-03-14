package com.hirohiro716.scent.gui.robot;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import com.hirohiro716.scent.gui.KeyCode;
import com.hirohiro716.scent.gui.MouseButton;

/**
 * マウスやキーボードのネイティブなシステム入力イベントを生成し、アプリケーションを自動操作するクラス。
 * 
 * @author hiro
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
    public void keyType(Integer... keyCodes) {
        for (Integer key: keyCodes) {
            this.keyPress(key);
        }
        for (Integer key: keyCodes) {
            this.keyRelease(key);
        }
    }
    
    /**
     * 指定されたキーを押して離す。同時にキー入力を行う場合は複数の値を指定する。
     * 
     * @param keyCodes
     */
    public void keyType(KeyCode... keyCodes) {
        for (KeyCode keyCode: keyCodes) {
            this.keyPress(keyCode.getKeyCodeAWT());
        }
        for (KeyCode keyCode: keyCodes) {
            this.keyRelease(keyCode.getKeyCodeAWT());
        }
    }

    /**
     * 指定されたマウスボタンを押して離す。同時に入力を行う場合は複数の値を指定する。
     * 
     * @param buttons
     */
    public synchronized void mouseClick(MouseButton... buttons) {
        for (MouseButton mouseButton: buttons) {
            super.mousePress(mouseButton.getButtonOfAWT());
        }
        for (MouseButton mouseButton: buttons) {
            super.mouseRelease(mouseButton.getButtonOfAWT());
        }
    }

    /**
     * 指定されたマウスボタンを押して離す。同時に入力を行う場合は複数の値を指定する。
     * 
     * @param buttons
     */
    public synchronized void mouseClick(Integer... buttons) {
        for (Integer button: buttons) {
            super.mousePress(button);
        }
        for (Integer button: buttons) {
            super.mouseRelease(button);
        }
    }
}
