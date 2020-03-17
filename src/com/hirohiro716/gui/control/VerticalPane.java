package com.hirohiro716.gui.control;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;

import com.hirohiro716.gui.HorizontalAlignment;
import com.hirohiro716.gui.collection.AddListener;
import com.hirohiro716.gui.collection.Collection;
import com.hirohiro716.gui.collection.RemoveListener;

/**
 * 垂直に配置するペインのクラス。
 * 
 * @author hiro
 *
 */
public class VerticalPane extends Pane {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected VerticalPane(JPanel innerInstance) {
        super(innerInstance);
        this.getChildren().addListener(this.addListener);
        this.getInnerInstance().setLayout(this.layout);
        VerticalPane pane = VerticalPane.this;
        this.growableControls.addListener(new AddListener<Control>() {

            @Override
            protected void added(Control added, int positionIndex) {
                pane.updateChildLayout(added, pane.getChildren().indexOf(added));
            }
        });
        this.growableControls.addListener(new RemoveListener<Control>() {

            @Override
            protected void removed(Control removed) {
                pane.updateChildLayout(removed, pane.getChildren().indexOf(removed));
            }
        });
    }
    
    private GridBagLayout layout = new GridBagLayout();
    
    /**
     * コンストラクタ。<br>
     * このペイン内での水平方向配置基準を指定する。
     * 
     * @param horizontalAlignment 
     */
    public VerticalPane(HorizontalAlignment horizontalAlignment) {
        this(new JPanel());
        if (horizontalAlignment != null) {
            this.horizontalAlignment = horizontalAlignment;
        }
    }
    
    /**
     * コンストラクタ。
     */
    public VerticalPane() {
        this(new JPanel());
    }
    
    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
    
    private boolean isFillChildWidth = false;
    
    /**
     * 子要素がペインの幅まで拡大される場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isFillChildWidth() {
        return this.isFillChildWidth;
    }
    
    /**
     * 子要素をペインの幅まで拡大する場合はtrueをセットする。初期値はfalse。
     * 
     * @param isFillChildWidth
     */
    public void setFillChildWidth(boolean isFillChildWidth) {
        this.isFillChildWidth = isFillChildWidth;
        this.updateAllChildLayout();
    }

    private int spacing = 0;
    
    /**
     * このペインに配置されている子要素間のスペースを取得する。
     * 
     * @return 結果。
     */
    public int getSpacing() {
        return this.spacing;
    }
    
    /**
     * このペインに配置されている子要素間のスペースを指定する。
     * 
     * @param spacing
     */
    public void setSpacing(int spacing) {
        this.spacing = spacing;
        this.updateAllChildLayout();
    }
    
    private AddListener<Control> addListener = new AddListener<Control>() {
        
        @Override
        protected void added(Control added, int positionIndex) {
            VerticalPane pane = VerticalPane.this;
            pane.updateAllChildLayout();
        }
    };
    
    private Collection<Control> growableControls = new Collection<>();
    
    /**
     * このペインの余ったスペースを使用して、垂直方向に拡大することができる子要素のコレクションを取得する。
     * 
     * @return 結果。
     */
    public Collection<Control> getGrowableControls() {
        return this.growableControls;
    }
    
    /**
     * 指定された子要素のレイアウトを最新の状態にする。
     * 
     * @param control
     * @param index
     */
    private void updateChildLayout(Control control, int index) {
        this.layout.removeLayoutComponent(control.getInnerInstance());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = index;
        if (index > 0) {
            constraints.insets = new Insets(this.spacing, 0, 0, 0);
        }
        constraints.weightx = 1;
        if (this.isFillChildWidth) {
            constraints.fill = GridBagConstraints.HORIZONTAL;
        }
        if (this.growableControls.contains(control)) {
            constraints.weighty = 1;
            constraints.fill = GridBagConstraints.BOTH;
        }
        switch (this.horizontalAlignment) {
        case LEFT:
            constraints.anchor = GridBagConstraints.WEST;
            break;
        case CENTER:
            constraints.anchor = GridBagConstraints.CENTER;
            break;
        case RIGHT:
            constraints.anchor = GridBagConstraints.EAST;
            break;
        
        }
        this.layout.setConstraints(control.getInnerInstanceForLayout(), constraints);
    }
    
    /**
     * すべての子要素のレイアウトを最新の状態にする。
     */
    private void updateAllChildLayout() {
        for (int index = 0; index < this.getChildren().size(); index++) {
            Control control = this.getChildren().get(index);
            this.updateChildLayout(control, index);
        }
    }
}
