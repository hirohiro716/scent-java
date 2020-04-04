package com.hirohiro716.gui.control;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SpringLayout;

import com.hirohiro716.gui.Component;
import com.hirohiro716.gui.GUI;
import com.hirohiro716.gui.collection.AddListener;
import com.hirohiro716.gui.collection.RemoveListener;
import com.hirohiro716.gui.event.ChangeListener;

/**
 * 左から右に向かって折り返しながら配置するペインのクラス。
 * 
 * @author hiro
 *
 */
public class FlowPane extends Pane {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected FlowPane(JPanel innerInstance) {
        super(innerInstance);
        this.layout = new SpringLayout();
        this.getInnerInstance().setLayout(this.layout);
        
        FlowPane pane = this;
        ChangeListener<Point> locationChangeListener = new ChangeListener<>() {

            @Override
            protected void changed(Component<?> component, Point changedValue, Point valueBeforeChange) {
                pane.updateAllChildLayout();
            }
        };
        ChangeListener<Dimension> sizeChangeListener = new ChangeListener<>() {

            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension valueBeforeChange) {
                pane.updateAllChildLayout();
                if (pane.isSizeInitialized == false) {
                    pane.updateLayout();
                }
            }
        };
        this.addSizeChangeListener(sizeChangeListener);
        this.getChildren().addListener(new AddListener<Control>() {
            
            @Override
            protected void added(Control added, int positionIndex) {
                added.addLocationChangeListener(locationChangeListener);
                added.addSizeChangeListener(sizeChangeListener);
            }
        });
        this.getChildren().addListener(new RemoveListener<Control>() {
            
            @Override
            protected void removed(Control removed) {
                removed.removeChangeListener(locationChangeListener);
                removed.removeChangeListener(sizeChangeListener);
            }
        });
    }
    
    /**
     * コンストラクタ。
     */
    public FlowPane() {
        this(new JPanel());
    }
    
    private SpringLayout layout;
    

    private int horizontalSpacing = 0;
    
    /**
     * このペインに配置されている子要素間の水平方向スペースを取得する。
     * 
     * @return 結果。
     */
    public int getHorizontalSpacing() {
        return this.horizontalSpacing;
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
    
    /**
     * このペインに配置されている子要素間のスペースを指定する。
     * 
     * @param spacing
     */
    public final void setSpacing(int spacing) {
        this.setSpacing(spacing, spacing);
    }
    
    /**
     * このペインに配置されている子要素間の水平方向スペースを指定する。
     * 
     * @param spacing
     */
    public final void setHorizontalSpacing(int spacing) {
        this.setSpacing(spacing, this.verticalSpacing);
    }

    /**
     * このペインに配置されている子要素間の垂直方向スペースを指定する。
     * 
     * @param spacing
     */
    public final void setVerticalSpacing(int spacing) {
        this.setSpacing(this.horizontalSpacing, spacing);
    }
    
    private boolean isSizeInitialized = false;
    
    private boolean isStartedUpdateLayout = false;
    
    /**
     * すべての子要素のレイアウトを最新の状態にする。
     */
    private void updateAllChildLayout() {
        if (this.getChildren().size() == 0 || this.getParent() == null || this.isStartedUpdateLayout) {
            return;
        }
        this.isStartedUpdateLayout = true;
        // Maximum width
        int maximumWidth = this.getParent().getWidth();
        if (this.getParent() instanceof Control) {
            Control parent = this.getParent();
            maximumWidth -= parent.getPadding().getLeft();
            maximumWidth -= parent.getPadding().getRight();
        }
        if (maximumWidth > this.getMaximumWidth()) {
            maximumWidth = this.getMaximumWidth();
        }
        System.out.println(maximumWidth); // TODO
        // Define variables
        int height = 0;
        Control baseY = null;
        int lineWidth = 0;
        List<Control> lineControls = new ArrayList<>();
        for (Control control : this.getChildren()) {
            this.layout.removeLayoutComponent(control.getInnerInstanceForLayout());
            // Initialize variables
            if (maximumWidth < lineWidth + this.horizontalSpacing + control.getWidth() && lineControls.size() > 0) {
                lineWidth = 0;
                Control maxHeightControl = null;
                for (Control lineControl : lineControls) {
                    if (maxHeightControl == null || maxHeightControl.getHeight() < lineControl.getHeight()) {
                        maxHeightControl = lineControl;
                    }
                }
                baseY = maxHeightControl;
                height += this.verticalSpacing;
                height += baseY.getHeight();
                lineControls.clear();
            }
            // Add paddings
            if (lineWidth == 0) {
                lineWidth += this.getPadding().getLeft();
                lineWidth += this.getPadding().getRight();
            }
            // Horizontal layout
            if (lineControls.size() > 0) {
                Control baseControl = lineControls.get(lineControls.size() - 1);
                this.layout.putConstraint(SpringLayout.WEST, control.getInnerInstanceForLayout(), this.horizontalSpacing, SpringLayout.EAST, baseControl.getInnerInstanceForLayout());
            }
            // Vertical layout
            if (baseY != null) {
                this.layout.putConstraint(SpringLayout.NORTH, control.getInnerInstanceForLayout(), this.verticalSpacing, SpringLayout.SOUTH, baseY.getInnerInstanceForLayout());
            }
            // Addition
            lineControls.add(control);
            lineWidth += this.horizontalSpacing;
            lineWidth += control.getWidth();
            if (this.getWidth() < lineWidth) {
                this.setWidth(lineWidth);
            }
        }
        if (baseY != null) {
            height += this.verticalSpacing;
            height += baseY.getHeight();
        }
        if (height > this.getMaximumHeight()) {
            height = this.getMaximumHeight();
        }
        this.setHeight(height);
        GUI.executeLater(new Runnable() {
            
            @Override
            public void run() {
                FlowPane pane = FlowPane.this;
                pane.isStartedUpdateLayout = false;
            }
        });
    }
}
