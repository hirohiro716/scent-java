package com.hirohiro716.gui.event;

import com.hirohiro716.gui.control.Control;

/**
 * GUIのすべての入力イベントの根底クラス。
 * 
 * @author hiro
 *
 * @param <T>
 */
public abstract class InputEvent<T extends java.awt.event.InputEvent> extends Event<T> {
    
    /**
     * コンストラクタ。<br>
     * イベントの発生元のコントロールと、GUIライブラリに依存したイベントのインスタンスを指定する。
     * 
     * @param control
     * @param innerInstance
     */
    public InputEvent(Control control, T innerInstance) {
        super(control, innerInstance);
    }
    
    /**
     * このイベントを消費済みとしてマークする。
     */
    public abstract void consume();
    
    /**
     * このイベントが消費済みの場合はtrueを返す。
     * 
     * @return 結果。
     */
    public abstract boolean isConsumed();
    
    @Override
    @SuppressWarnings("unchecked")
    public Control getSource() {
        return super.getSource();
    }
}
