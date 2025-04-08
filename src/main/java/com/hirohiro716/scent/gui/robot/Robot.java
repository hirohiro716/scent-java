package com.hirohiro716.scent.gui.robot;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
     * 指定されたキーを押す。同時にキー入力を行う場合は複数の値を指定する。
     * 
     * @param keyCodes
     */
    public synchronized void keyPress(Integer... keyCodes) {
        for (Integer key: keyCodes) {
            super.keyPress(key);
        }
    }
    
    /**
     * 指定されたキーを押す。同時にキー入力を行う場合は複数の値を指定する。
     * 
     * @param keyCodes
     */
    public synchronized void keyPress(KeyCode... keyCodes) {
        for (KeyCode keyCode: keyCodes) {
            super.keyPress(keyCode.getKeyCodeAWT());
        }
    }

    /**
     * 指定されたキーを離す。同時にキー入力を行う場合は複数の値を指定する。
     * 
     * @param keyCodes
     */
    public synchronized void keyRelease(Integer... keyCodes) {
        for (Integer key: keyCodes) {
            super.keyRelease(key);
        }
    }
    
    /**
     * 指定されたキーを離す。同時にキー入力を行う場合は複数の値を指定する。
     * 
     * @param keyCodes
     */
    public synchronized void keyRelease(KeyCode... keyCodes) {
        for (KeyCode keyCode: keyCodes) {
            super.keyRelease(keyCode.getKeyCodeAWT());
        }
    }
    
    /**
     * 指定されたキーを押して離す。同時にキー入力を行う場合は複数の値を指定する。
     * 
     * @param keyCodes
     */
    public synchronized void keyType(Integer... keyCodes) {
        for (Integer key: keyCodes) {
            super.keyPress(key);
        }
        for (Integer key: keyCodes) {
            super.keyRelease(key);
        }
    }
    
    /**
     * 指定されたキーを押して離す。同時にキー入力を行う場合は複数の値を指定する。
     * 
     * @param keyCodes
     */
    public synchronized void keyType(KeyCode... keyCodes) {
        for (KeyCode keyCode: keyCodes) {
            super.keyPress(keyCode.getKeyCodeAWT());
        }
        List<KeyCode> reversedKeyCode = new ArrayList<>(Arrays.asList(keyCodes));
        Collections.reverse(reversedKeyCode);
        for (KeyCode keyCode: reversedKeyCode) {
            super.keyRelease(keyCode.getKeyCodeAWT());
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
