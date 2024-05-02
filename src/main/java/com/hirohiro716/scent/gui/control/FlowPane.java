package com.hirohiro716.scent.gui.control;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SpringLayout;

import com.hirohiro716.scent.Dimension;
import com.hirohiro716.scent.StringObject;
import com.hirohiro716.scent.gui.Component;
import com.hirohiro716.scent.gui.GUI;
import com.hirohiro716.scent.gui.collection.AddListener;
import com.hirohiro716.scent.gui.collection.RemoveListener;
import com.hirohiro716.scent.gui.event.ChangeListener;

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
            protected void changed(Component<?> component, Point changedValue, Point previousValue) {
                pane.updateLayout();
            }
        };
        ChangeListener<Dimension> sizeChangeListener = new ChangeListener<>() {

            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension previousValue) {
                pane.updateLayout();
            }
        };
        ChangeListener<Boolean> visibleChangeListener = new ChangeListener<Boolean>() {

            @Override
            protected void changed(Component<?> component, Boolean changedValue, Boolean previousValue) {
                pane.updateLayout();
            }
        };
        this.addSizeChangeListener(sizeChangeListener);
        this.getChildren().addListener(new AddListener<Control>() {
            
            @Override
            protected void added(Control added, int positionIndex) {
                for (Control child : pane.getChildren()) {
                    child.removeChangeListener(locationChangeListener);
                    child.removeChangeListener(sizeChangeListener);
                    child.removeChangeListener(visibleChangeListener);
                }
                added.addLocationChangeListener(locationChangeListener);
                added.addSizeChangeListener(sizeChangeListener);
                added.addVisibleChangeListener(visibleChangeListener);
                pane.numberOfLayoutUpdates = 0;
            }
        });
        this.getChildren().addListener(new RemoveListener<Control>() {
            
            @Override
            protected void removed(Control removed) {
                removed.removeChangeListener(locationChangeListener);
                removed.removeChangeListener(sizeChangeListener);
                removed.removeChangeListener(visibleChangeListener);
                pane.numberOfLayoutUpdates = 0;
            }
        });
        this.setMinimumSize(-1, -1);
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
    
    private boolean isFillChildToRowHeight = false;
    
    /**
     * このペインに配置されている子要素の高さを、その子要素が表示されている行の高さまで拡大する場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isFillChildToRowHeight() {
        return this.isFillChildToRowHeight;
    }
    
    /**
     * このペインに配置されている子要素の高さを、その子要素が表示されている行の高さまで拡大する場合はtrueをセットする。初期値はfalse。
     * 
     * @param isFillChildToRowHeight
     */
    public void setFillChildToRowHeight(boolean isFillChildToRowHeight) {
        this.isFillChildToRowHeight = isFillChildToRowHeight;
    }

    private String sizeStringOfLayoutUpdate = null;
    
    private String childrenSizeStringOfLayoutUpdate = null;
    
    private int numberOfLayoutUpdates = 0;
    
    private boolean isStartedLayoutUpdate = false;
    
    /**
     * すべての子要素のレイアウトを最新の状態にする。
     */
    private void updateAllChildLayout() {
        // Up to 2 times with the same size
        StringObject sizeString = StringObject.join(this.getSize(), ":", this.getMinimumSize(), ":", this.getMaximumSize());
        if (this.getFrame() != null) {
            sizeString.append(";");
            sizeString.append(this.getFrame().getSize());
        }
        StringObject childrenSizeString = new StringObject();
        for (Control control : this.getChildren()) {
            if (control.isVisible() == false) {
                continue;
            }
            if (childrenSizeString.length() > 0) {
                childrenSizeString.append(";");
            }
            StringObject name = new StringObject(control.getName());
            if (name.length() > 0) {
                childrenSizeString.append(name);
                childrenSizeString.append(":");
            }
            childrenSizeString.append(this.getSize());
            childrenSizeString.append(":");
            childrenSizeString.append(this.getSize());
        }
        if (sizeString.equals(this.sizeStringOfLayoutUpdate) && childrenSizeString.equals(this.childrenSizeStringOfLayoutUpdate)) {
            if (this.numberOfLayoutUpdates > 2) {
                return;
            }
        } else {
            this.sizeStringOfLayoutUpdate = sizeString.toString();
            this.childrenSizeStringOfLayoutUpdate = childrenSizeString.toString();
            this.numberOfLayoutUpdates = 0;
        }
        this.numberOfLayoutUpdates++;
        // Start layout
        if (this.getChildren().size() == 0 || this.getParent() == null || this.isStartedLayoutUpdate) {
            return;
        }
        for (Control control : this.getChildren()) {
            if (control.getWidth() == 0 || control.getHeight() == 0) {
                return;
            }
        }
        this.isStartedLayoutUpdate = true;
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
        maximumWidth -= this.getPadding().getLeft();
        maximumWidth -= this.getPadding().getRight();
        // Define variables
        int height = 0;
        int lineWidth = 0;
        List<Control> lineControls = new ArrayList<>();
        for (Control control : this.getChildren()) {
            if (control.isVisible() == false) {
                continue;
            }
            this.layout.removeLayoutComponent(control.getInnerInstanceForLayout());
            // Initialize variables
            if (maximumWidth < lineWidth + this.horizontalSpacing + control.getWidth() && lineControls.size() > 0) {
                lineWidth = 0;
                int controlHeight = this.getMaximumHeightOfControls(lineControls);
                if (this.isFillChildToRowHeight) {
                    this.setHeightOfControls(lineControls, controlHeight);
                }
                height += this.verticalSpacing;
                height += controlHeight;
                lineControls.clear();
            }
            // Horizontal layout
            this.layout.putConstraint(SpringLayout.WEST, control.getInnerInstanceForLayout(), lineWidth, SpringLayout.WEST, this.getInnerInstanceForLayout());
            // Vertical layout
            this.layout.putConstraint(SpringLayout.NORTH, control.getInnerInstanceForLayout(), height, SpringLayout.NORTH, this.getInnerInstanceForLayout());
            // Addition
            lineControls.add(control);
            lineWidth += this.horizontalSpacing;
            lineWidth += control.getWidth();
            if (this.getWidth() < lineWidth) {
                this.setWidth(lineWidth);
            }
        }
        // Adjust pane height
        int controlHeight = this.getMaximumHeightOfControls(lineControls);
        height += controlHeight;
        height += this.getPadding().getTop();
        height += this.getPadding().getBottom();
        if (height < this.getMinimumHeight()) {
            height = this.getMinimumHeight();
        }
        if (height > this.getMaximumHeight()) {
            height = this.getMaximumHeight();
        }
        final int finalHeight = height;
        GUI.executeLater(new Runnable() {
            
            @Override
            public void run() {
                FlowPane pane = FlowPane.this;
                pane.isStartedLayoutUpdate = false;
                pane.setHeight(finalHeight);
            }
        });
    }
    
    /**
     * 指定されたすべてのコントロールの最大高さを取得する。
     * 
     * @param controls
     * @return 結果。
     */
    private int getMaximumHeightOfControls(List<Control> controls) {
        int maximumHeight = 0;
        for (Control control : controls) {
            if (maximumHeight < control.getHeight()) {
                maximumHeight = control.getHeight();
            }
        }
        if (this.isFillChildToRowHeight) {
            for (Control control : controls) {
                control.setHeight(maximumHeight);
            }
        }
        return maximumHeight;
    }

    /**
     * 指定されたすべてのコントロールの高さを変更する。
     * 
     * @param controls
     * @param height
     */
    private void setHeightOfControls(List<Control> controls, int height) {
        for (Control control : controls) {
            control.setHeight(height);
        }
    }
    
    @Override
    public void updateLayout() {
        this.numberOfLayoutUpdates = 0;
        this.updateAllChildLayout();
        super.updateLayout();
    }
    
    @Override
    public void updateDisplay() {
        this.updateAllChildLayout();
        super.updateDisplay();
    }
}
