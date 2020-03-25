package com.hirohiro716.gui.control;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import com.hirohiro716.gui.collection.AddListener;
import com.hirohiro716.gui.collection.Collection;
import com.hirohiro716.gui.collection.RemoveListener;

/**
 * グリッド状に配置するペインのクラス。
 * 
 * @author hiro
 *
 */
public class GridPane extends Pane {

    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected GridPane(JPanel innerInstance) {
        super(innerInstance);
        this.getChildren().addListener(this.addListener);
        this.getChildren().addListener(this.removeListener);
        this.getInnerInstance().setLayout(this.layout);
        GridPane pane = this;
        AddListener<Control> addListener = new AddListener<Control>() {

            @Override
            protected void added(Control added, int positionIndex) {
                pane.updateAllChildLayout();
            }
        };
        this.horizontalGrowableControls.addListener(addListener);
        this.verticalGrowableControls.addListener(addListener);
        RemoveListener<Control> removeListener = new RemoveListener<Control>() {

            @Override
            protected void removed(Control removed) {
                pane.updateAllChildLayout();
            }
        };
        this.horizontalGrowableControls.addListener(removeListener);
        this.verticalGrowableControls.addListener(removeListener);
        Dimension zero = new Dimension(0, 0);
        this.horizontalSpacer.setPreferredSize(zero);
        this.horizontalSpacer.setMinimumSize(zero);
        this.horizontalSpacer.setOpaque(false);
        this.getInnerInstance().add(this.horizontalSpacer);
        this.verticalSpacer.setPreferredSize(zero);
        this.verticalSpacer.setMinimumSize(zero);
        this.verticalSpacer.setOpaque(false);
        this.getInnerInstance().add(this.verticalSpacer);
    }
    
    /**
     * コンストラクタ。
     */
    public GridPane() {
        this(new JPanel());
    }

    private GridBagLayout layout = new GridBagLayout();
    
    private Map<Control, Boolean> mapControlVisible = new HashMap<>();
    
    private AddListener<Control> addListener = new AddListener<Control>() {
        
        @Override
        protected void added(Control added, int positionIndex) {
            GridPane pane = GridPane.this;
            pane.updateAllChildLayout();
        }
    };

    private RemoveListener<Control> removeListener = new RemoveListener<Control>() {

        @Override
        protected void removed(Control removed) {
            GridPane pane = GridPane.this;
            pane.horizontalGrowableControls.remove(removed);
            pane.verticalGrowableControls.remove(removed);
            pane.updateAllChildLayout();
        }
    };
    
    private int horizontalSpacing = 0;
    
    /**
     * このペインに配置されている子要素間の水平方向スペースを取得する。
     * 
     * @return 結果。
     */
    public int getHorizontalSpacing() {
        return this.horizontalSpacing;
    }
    
    /**
     * このペインに配置されている子要素間の水平方向スペースを指定する。
     * 
     * @param spacing
     */
    public void setHorizontalSpacing(int spacing) {
        this.horizontalSpacing = spacing;
        this.updateAllChildLayout();
    }

    private int verticalSpacing = 0;
    
    /**
     * このペインに配置されている子要素間の垂直方向スペースを取得する。
     * 
     * @return 結果。
     */
    public int getVerticalSpacing() {
        return this.verticalSpacing;
    }
    
    /**
     * このペインに配置されている子要素間の垂直方向スペースを指定する。
     * 
     * @param spacing
     */
    public void setVerticalSpacing(int spacing) {
        this.verticalSpacing = spacing;
        this.updateAllChildLayout();
    }
    
    /**
     * このペインに配置されている子要素間のスペースを指定する。
     * 
     * @param horizontalSpacing 水平方向のスペース。
     * @param verticalSpacing 垂直方向のスペース。
     */
    public void setSpacing(int horizontalSpacing, int verticalSpacing) {
        this.horizontalSpacing = horizontalSpacing;
        this.verticalSpacing = verticalSpacing;
        this.updateAllChildLayout();
    }
    
    private Map<Control, Integer> mapGridX = new HashMap<>();
    
    private Map<Control, Integer> mapGridY = new HashMap<>();
    
    private Map<Control, Integer> mapGridWidth = new HashMap<>();
    
    private Map<Control, Integer> mapGridHeight = new HashMap<>();
    
    private Map<Control, CellLayout> mapCellLayout = new HashMap<>();
    
    /**
     * このペインに配置したコントロールのレイアウトを設定する。
     * 
     * @param control 対象のコントロール。
     * @param gridX 水平方向表示位置。
     * @param gridY 垂直方向表示位置。
     * @param gridWidth 表示に使用するカラム数。
     * @param gridHeight 表示に使用する行数。
     * @param cellLayout セル内のコントロール配置方法。
     */
    public void setGridLayout(Control control, int gridX, int gridY, int gridWidth, int gridHeight, CellLayout cellLayout) {
        this.mapGridX.put(control, gridX);
        this.mapGridY.put(control, gridY);
        this.mapGridWidth.put(control, gridWidth);
        this.mapGridHeight.put(control, gridHeight);
        this.mapCellLayout.put(control, cellLayout);
        this.updateAllChildLayout();
    }

    /**
     * このペインに配置したコントロールのレイアウトを設定する。
     * 
     * @param control 対象のコントロール。
     * @param gridX 水平方向表示位置。
     * @param gridY 垂直方向表示位置。
     * @param cellLayout セル内のコントロール配置方法。
     */
    public void setGridLayout(Control control, int gridX, int gridY, CellLayout cellLayout) {
        this.setGridLayout(control, gridX, gridY, 1, 1, cellLayout);
    }

    /**
     * このペインに配置したコントロールのレイアウトを設定する。
     * 
     * @param control 対象のコントロール。
     * @param gridX 水平方向表示位置。
     * @param gridY 垂直方向表示位置。
     * @param gridWidth 表示に使用するカラム数。
     * @param gridHeight 表示に使用する行数。
     */
    public void setGridLayout(Control control, int gridX, int gridY, int gridWidth, int gridHeight) {
        this.setGridLayout(control, gridX, gridY, gridWidth, gridHeight, CellLayout.FILL);
    }
    
    /**
     * このペインに配置したコントロールのレイアウトを設定する。
     * 
     * @param control 対象のコントロール。
     * @param gridX 水平方向表示位置。
     * @param gridY 垂直方向表示位置。
     */
    public void setGridLayout(Control control, int gridX, int gridY) {
        this.setGridLayout(control, gridX, gridY, 1, 1, CellLayout.FILL);
    }
    
    private Collection<Control> horizontalGrowableControls = new Collection<>();
    
    /**
     * このペインの余ったスペースを使用して、水平方向に拡大することができる子要素のコレクションを取得する。
     * 
     * @return 結果。
     */
    public Collection<Control> getHorizontalGrowableControls() {
        return this.horizontalGrowableControls;
    }

    private Collection<Control> verticalGrowableControls = new Collection<>();
    
    /**
     * このペインの余ったスペースを使用して、垂直方向に拡大することができる子要素のコレクションを取得する。
     * 
     * @return 結果。
     */
    public Collection<Control> getVerticalGrowableControls() {
        return this.verticalGrowableControls;
    }
    
    /**
     * 指定された子要素のレイアウトを最新の状態にする。
     * 
     * @param control
     * @param index
     */
    private void updateChildLayout(Control control, int index) {
        this.layout.removeLayoutComponent(control.getInnerInstance());
        if (this.mapGridX.containsKey(control) == false) {
            this.mapControlVisible.put(control, control.isVisible());
            control.setVisible(false);
            return;
        }
        if (this.mapControlVisible.containsKey(control)) {
            control.setVisible(this.mapControlVisible.get(control));
            this.mapControlVisible.remove(control);
        }
        GridBagConstraints constraints = new GridBagConstraints();
        int gridX = this.mapGridX.get(control);
        int gridY = this.mapGridY.get(control);
        constraints.gridx = gridX;
        constraints.gridy = gridY;
        int gridWidth = this.mapGridWidth.get(control);
        int gridHeight = this.mapGridHeight.get(control);
        constraints.gridwidth = gridWidth;
        constraints.gridheight = gridHeight;
        if (this.horizontalGrowableControls.contains(control)) {
            constraints.weightx = 1;
        }
        if (this.verticalGrowableControls.contains(control)) {
            constraints.weighty = 1;
        }
        int horizontalSpacing = this.horizontalSpacing;
        if (gridX == 0) {
            horizontalSpacing = 0;
        }
        int verticalSpacing = this.verticalSpacing;
        if (gridY == 0) {
            verticalSpacing = 0;
        }
        constraints.insets = new Insets(verticalSpacing, horizontalSpacing, 0, 0);
        switch (this.mapCellLayout.get(control)) {
        case FILL:
            constraints.fill = GridBagConstraints.BOTH;
            break;
        case TOP:
            constraints.anchor = GridBagConstraints.NORTH;
            break;
        case TOP_LEFT:
            constraints.anchor = GridBagConstraints.NORTHWEST;
            break;
        case TOP_RIGHT:
            constraints.anchor = GridBagConstraints.NORTHEAST;
            break;
        case BOTTOM:
            constraints.anchor = GridBagConstraints.SOUTH;
            break;
        case BOTTOM_LEFT:
            constraints.anchor = GridBagConstraints.SOUTHWEST;
            break;
        case BOTTOM_RIGHT:
            constraints.anchor = GridBagConstraints.SOUTHEAST;
            break;
        case CENTER:
            constraints.anchor = GridBagConstraints.CENTER;
            break;
        case LEFT:
            constraints.anchor = GridBagConstraints.WEST;
            break;
        case RIGHT:
            constraints.anchor = GridBagConstraints.EAST;
            break;
        }
        this.layout.setConstraints(control.getInnerInstanceForLayout(), constraints);
    }
    
    private JPanel horizontalSpacer = new JPanel();
    
    private JPanel verticalSpacer = new JPanel();
    
    /**
     * すべての子要素のレイアウトを最新の状態にする。
     */
    private void updateAllChildLayout() {
        for (int index = 0; index < this.getChildren().size(); index++) {
            Control control = this.getChildren().get(index);
            this.updateChildLayout(control, index);
        }
        // Horizontal spacer
        this.layout.removeLayoutComponent(this.horizontalSpacer);
        GridBagConstraints horizontalConstraints = new GridBagConstraints();
        int maxGridX = 0;
        for (Integer gridX : this.mapGridX.values()) {
            if (gridX != null && gridX > maxGridX) {
                maxGridX = gridX;
            }
        }
        horizontalConstraints.gridx = maxGridX + 1;
        horizontalConstraints.gridy = 0;
        if (this.horizontalGrowableControls.size() == 0) {
            horizontalConstraints.weightx = 1;
        }
        horizontalConstraints.fill = GridBagConstraints.BOTH;
        this.layout.setConstraints(this.horizontalSpacer, horizontalConstraints);
        // Vertical spacer
        this.layout.removeLayoutComponent(this.verticalSpacer);
        GridBagConstraints verticalConstraints = new GridBagConstraints();
        int maxGridY = 0;
        for (Integer gridY : this.mapGridY.values()) {
            if (gridY != null && gridY > maxGridY) {
                maxGridY = gridY;
            }
        }
        verticalConstraints.gridx = 0;
        verticalConstraints.gridy = maxGridY + 1;
        if (this.verticalGrowableControls.size() == 0) {
            verticalConstraints.weighty = 1;
        }
        verticalConstraints.fill = GridBagConstraints.BOTH;
        this.layout.setConstraints(this.verticalSpacer, verticalConstraints);
    }
    
    /**
     * グリッドペインセルのコントロール表示方法の列挙型。
     * 
     * @author hiro
     *
     */
    public enum CellLayout {
        /**
         * セルいっぱいに表示。
         */
        FILL,
        /**
         * 垂直方向が上、水平方向が中央に表示。
         */
        TOP,
        /**
         * 垂直方向が上、水平方向が左に表示。
         */
        TOP_LEFT,
        /**
         * 垂直方向が上、水平方向が右に表示。
         */
        TOP_RIGHT,
        /**
         * 垂直方向、水平方向が中央に表示。
         */
        CENTER,
        /**
         * 垂直方向が中央、水平方向が左に表示。
         */
        LEFT,
        /**
         * 垂直方向が中央、水平方向が右に表示。
         */
        RIGHT,
        /**
         * 垂直方向が下、水平方向が中央に表示。
         */
        BOTTOM,
        /**
         * 垂直方向が下、水平方向が左に表示。
         */
        BOTTOM_LEFT,
        /**
         * 垂直方向が下、水平方向が右に表示。
         */
        BOTTOM_RIGHT,
    }
}
