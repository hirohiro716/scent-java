package com.hirohiro716.gui.control;

import javax.swing.JPanel;
import javax.swing.OverlayLayout;

/**
 * フレームのルートペインのクラス。
 * 
 * @author hiro
 *
 */
public class RootPane extends Pane {

    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    public RootPane(JPanel innerInstance) {
        super(innerInstance);
        this.getInnerInstance().setLayout(new OverlayLayout(this.getInnerInstance()));
    }
}
