package com.hirohiro716.gui.event;

import com.hirohiro716.gui.Component;

/**
 * コントロール独自のアクションイベントのクラス。
 * 
 * @author hiro
 *
 */
public class ActionEvent extends Event<java.awt.event.ActionEvent> {
    
    /**
     * コンストラクタ。<br>
     * イベントの発生元のコントロールと、GUIライブラリに依存したイベントのインスタンスを指定する。
     * 
     * @param component
     * @param innerInstance
     */
    public ActionEvent(Component<?> component, java.awt.event.ActionEvent innerInstance) {
        super(component, innerInstance);
    }
}
