package com.hirohiro716.scent.gui.control;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import com.hirohiro716.scent.gui.collection.AddListener;

/**
 * 中央に1つのコントロールを配置するペインのクラス。
 * 
 * @author hiro
 *
 */
public class CenterPane extends Pane {

    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected CenterPane(JPanel innerInstance) {
        super(innerInstance);
        this.getChildren().addListener(this.addListener);
        this.getInnerInstance().setLayout(this.layout);
    }

    /**
     * このメソッドはコンストラクタの呼び出しと同じで、新しいインスタンスを作成する。
     * 
     * @param innerInstance GUIライブラリに依存したインスタンス。
     * @return 新しいインスタンス。
     */
    public static CenterPane newInstance(JPanel innerInstance) {
        return new CenterPane(innerInstance);
    }
    
    /**
     * コンストラクタ。
     */
    public CenterPane() {
        this(new JPanel());
    }
    
    private GridBagLayout layout = new GridBagLayout();
    
    /**
     * このペインの中央に表示するコントロールをセットする。
     * 
     * @param control
     */
    public void setControl(Control control) {
        this.getChildren().clear();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        this.getChildren().add(control);
        this.layout.setConstraints(control.getInnerInstanceForLayout(), constraints);
    }
    
    private AddListener<Control> addListener = new AddListener<Control>() {

        @Override
        protected void added(Control added, int positionIndex) {
            CenterPane pane = CenterPane.this;
            for (Control control : pane.getChildren().toArray()) {
                if (control != added) {
                    pane.getChildren().remove(control);
                }
            }
        }
    };
}
