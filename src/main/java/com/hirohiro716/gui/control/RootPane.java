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
    protected RootPane(JPanel innerInstance) {
        super(innerInstance);
        this.getInnerInstance().setLayout(new OverlayLayout(this.getInnerInstance()));
    }

    /**
     * このメソッドはコンストラクタの呼び出しと同じで、新しいインスタンスを作成する。
     * 
     * @param innerInstance GUIライブラリに依存したインスタンス。
     * @return 新しいインスタンス。
     */
    public static RootPane newInstance(JPanel innerInstance) {
        return new RootPane(innerInstance);
    }
}
