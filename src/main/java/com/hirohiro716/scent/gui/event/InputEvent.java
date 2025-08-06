package com.hirohiro716.scent.gui.event;

import com.hirohiro716.scent.gui.control.Control;

/**
 * GUIのすべての入力イベントの抽象クラス。
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
     * @return
     */
    public abstract boolean isConsumed();
    
    @Override
    @SuppressWarnings("unchecked")
    public Control getSource() {
        return super.getSource();
    }
}
