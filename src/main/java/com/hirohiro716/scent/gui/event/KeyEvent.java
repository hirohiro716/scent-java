package com.hirohiro716.scent.gui.event;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyListener;

import com.hirohiro716.scent.IdentifiableEnum;
import com.hirohiro716.scent.gui.KeyCode;
import com.hirohiro716.scent.gui.control.Control;

/**
 * キーストロークイベントのクラス。
 * 
 * @author hiro
*/
public class KeyEvent extends InputEvent<java.awt.event.KeyEvent> {
    
    /**
     * コンストラクタ。<br>
     * イベントの発生元のコントロールと、GUIライブラリに依存したイベントのインスタンスを指定する。
     * 
     * @param control
     * @param innerInstance
     */
    public KeyEvent(Control control, java.awt.event.KeyEvent innerInstance) {
        super(control, innerInstance);
    }

    @Override
    public void consume() {
        this.getInnerInstance().consume();
    }
    
    @Override
    public boolean isConsumed() {
        return this.getInnerInstance().isConsumed();
    }
    
    /**
     * このイベントのキーに関連付けられたKeyCodeを取得する。
     * 
     * @return 結果。
     */
    public KeyCode getKeyCode() {
        KeyCode keyCode = IdentifiableEnum.enumOf(this.getInnerInstance().getKeyCode(), KeyCode.class);
        if (keyCode == null) {
            keyCode = KeyCode.UNDEFINED;
        }
        return keyCode;
    }
    
    /**
     * このイベントのキーに関連付けられた文字を返す。
     * 
     * @return 結果。
     */
    public String getKeyCharacter() {
        return String.valueOf(this.getInnerInstance().getKeyChar());
    }
    
    /**
     * このイベントでShift修飾キーが押されていた場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isShiftDown() {
        return this.getInnerInstance().isShiftDown();
    }
    
    /**
     * このイベントでAlt修飾キーが押されていた場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isAltDown() {
        return this.getInnerInstance().isAltDown();
    }
    
    /**
     * このイベントでCtrl修飾キーが押されていた場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isControlDown() {
        return this.getInnerInstance().isControlDown();
    }

    /**
     * キーをタイプした際のイベントを実際に処理する、GUIライブラリに依存したイベントハンドラを作成する。
     * 
     * @param control
     * @param eventHandler
     * @return 結果。
     */
    public static KeyListener createInnerKeyTypedEventHandler(Control control, EventHandler<KeyEvent> eventHandler) {
        return eventHandler.createInnerInstance(control, new InnerInstanceCreator<>() {

            @Override
            public KeyListener create() {
                return new KeyAdapter() {

                    @Override
                    public void keyTyped(java.awt.event.KeyEvent event) {
                        eventHandler.executeWhenControlEnabled(new KeyEvent(control, event));
                    }
                };
            }
        });
    }

    /**
     * キーを押した際のイベントを実際に処理する、GUIライブラリに依存したイベントハンドラを作成する。
     * 
     * @param control
     * @param eventHandler
     * @return 結果。
     */
    public static KeyListener createInnerKeyPressedEventHandler(Control control, EventHandler<KeyEvent> eventHandler) {
        return eventHandler.createInnerInstance(control, new InnerInstanceCreator<>() {

            @Override
            public KeyListener create() {
                return new KeyAdapter() {
                    
                    @Override
                    public void keyPressed(java.awt.event.KeyEvent event) {
                        eventHandler.executeWhenControlEnabled(new KeyEvent(control, event));
                    }
                };
            }
        });
    }

    /**
     * キーを離した際のイベントを実際に処理する、GUIライブラリに依存したイベントハンドラを作成する。
     * 
     * @param control
     * @param eventHandler
     * @return 結果。
     */
    public static KeyListener createInnerKeyReleasedEventHandler(Control control, EventHandler<KeyEvent> eventHandler) {
        return eventHandler.createInnerInstance(control, new InnerInstanceCreator<>() {

            @Override
            public KeyListener create() {
                return new KeyAdapter() {

                    @Override
                    public void keyReleased(java.awt.event.KeyEvent event) {
                        eventHandler.executeWhenControlEnabled(new KeyEvent(control, event));
                    }
                };
            }
        });
    }
}
