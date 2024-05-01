package com.hirohiro716.gui.control;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import com.hirohiro716.gui.VerticalAlignment;
import com.hirohiro716.gui.collection.AddListener;
import com.hirohiro716.gui.collection.Collection;
import com.hirohiro716.gui.collection.RemoveListener;

/**
 * 水平に配置するペインのクラス。
 * 
 * @author hiro
 *
 */
public class HorizontalPane extends Pane {

    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected HorizontalPane(JPanel innerInstance) {
        super(innerInstance);
        this.getChildren().addListener(this.addListener);
        this.getChildren().addListener(this.removeListener);
        this.getInnerInstance().setLayout(this.layout);
        HorizontalPane pane = HorizontalPane.this;
        this.growableControls.addListener(new AddListener<Control>() {
            
            @Override
            protected void added(Control added, int positionIndex) {
                pane.updateAllChildLayout();
            }
        });
        this.growableControls.addListener(new RemoveListener<Control>() {
            
            @Override
            protected void removed(Control removed) {
                pane.updateAllChildLayout();
            }
        });
        Dimension zero = new Dimension(0, 0);
        this.spacer.setPreferredSize(zero);
        this.spacer.setMinimumSize(zero);
        this.spacer.setOpaque(false);
        this.getInnerInstance().add(this.spacer);
    }
    
    private GridBagLayout layout = new GridBagLayout();
    
    /**
     * コンストラクタ。<br>
     * このペイン内での垂直方向配置基準を指定する。
     * 
     * @param verticalAlignment 
     */
    public HorizontalPane(VerticalAlignment verticalAlignment) {
        this(new JPanel());
        if (verticalAlignment != null) {
            this.verticalAlignment = verticalAlignment;
        }
    }
    
    /**
     * コンストラクタ。
     */
    public HorizontalPane() {
        this(new JPanel());
    }
    
    private VerticalAlignment verticalAlignment = VerticalAlignment.TOP;
    
    private boolean isFillChildToPaneHeight = false;
    
    /**
     * 子要素がペインの高さまで拡大される場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isFillChildToPaneHeight() {
        return this.isFillChildToPaneHeight;
    }
    
    /**
     * 子要素をペインの高さまで拡大する場合はtrueをセットする。初期値はfalse。
     * 
     * @param isFillChildToPaneHeight
     */
    public void setFillChildToPaneHeight(boolean isFillChildToPaneHeight) {
        this.isFillChildToPaneHeight = isFillChildToPaneHeight;
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
            HorizontalPane pane = HorizontalPane.this;
            pane.updateAllChildLayout();
        }
    };
    
    private RemoveListener<Control> removeListener = new RemoveListener<Control>() {

        @Override
        protected void removed(Control removed) {
            HorizontalPane pane = HorizontalPane.this;
            pane.growableControls.remove(removed);
            pane.updateAllChildLayout();
        }
    };
    
    private Collection<Control> growableControls = new Collection<>();
    
    /**
     * このペインの余ったスペースを使用して、水平方向に拡大することができる子要素のコレクションを取得する。
     * 
     * @return 結果。
     */
    public Collection<Control> getGrowableControls() {
        return this.growableControls;
    }

    /**
     * このペインの余ったスペースを使用して、水平方向に拡大するスペーサーを子要素として末尾に追加する。
     * 
     * @param numberOfSpacers
     */
    public void addStretchableSpacer(int numberOfSpacers) {
        for (int number = 0; number <= numberOfSpacers; number++) {
            Spacer spacer = new Spacer(0, 0);
            this.getChildren().add(spacer);
            this.getGrowableControls().add(spacer);
        }
    }

    /**
     * このペインの余ったスペースを使用して、水平方向に拡大するスペーサーを子要素として指定された位置に追加する。
     * 
     * @param numberOfSpacers
     * @param positionIndex
     */
    public void addStretchableSpacer(int numberOfSpacers, int positionIndex) {
        for (int number = 0; number <= numberOfSpacers; number++) {
            Spacer spacer = new Spacer(0, 0);
            this.getChildren().add(spacer, positionIndex);
            this.getGrowableControls().add(spacer, positionIndex);
        }
    }
    
    /**
     * 指定された子要素のレイアウトを最新の状態にする。
     * 
     * @param control
     * @param index
     */
    private void updateChildLayout(Control control, int index) {
        this.layout.removeLayoutComponent(control.getInnerInstanceForLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = index;
        constraints.gridy = 0;
        if (index > 0) {
            constraints.insets = new Insets(0, this.spacing, 0, 0);
        }
        constraints.weighty = 1;
        if (this.isFillChildToPaneHeight) {
            constraints.fill = GridBagConstraints.VERTICAL;
        }
        if (this.growableControls.contains(control)) {
            constraints.weightx = 1;
            if (this.isFillChildToPaneHeight) {
                constraints.fill = GridBagConstraints.BOTH;
            } else {
                constraints.fill = GridBagConstraints.HORIZONTAL;
            }
        }
        switch (this.verticalAlignment) {
        case TOP:
            constraints.anchor = GridBagConstraints.NORTH;
            break;
        case CENTER:
            constraints.anchor = GridBagConstraints.CENTER;
            break;
        case BOTTOM:
            constraints.anchor = GridBagConstraints.SOUTH;
            break;
        }
        this.layout.setConstraints(control.getInnerInstanceForLayout(), constraints);
    }
    
    private JPanel spacer = new JPanel();
    
    /**
     * すべての子要素のレイアウトを最新の状態にする。
     */
    private void updateAllChildLayout() {
        for (int index = 0; index < this.getChildren().size(); index++) {
            Control control = this.getChildren().get(index);
            this.updateChildLayout(control, index);
        }
        this.layout.removeLayoutComponent(this.spacer);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = this.getChildren().size();
        constraints.gridy = 0;
        if (this.growableControls.size() == 0) {
            constraints.weightx = 1;
        }
        constraints.fill = GridBagConstraints.BOTH;
        this.layout.setConstraints(this.spacer, constraints);
    }
}
