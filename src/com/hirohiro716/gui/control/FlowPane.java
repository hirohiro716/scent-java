package com.hirohiro716.gui.control;

import java.awt.FlowLayout;

import javax.swing.JPanel;

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
        this.layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
        this.getInnerInstance().setLayout(this.layout);
    }
    
    /**
     * コンストラクタ。
     */
    public FlowPane() {
        this(new JPanel());
    }
    
    private FlowLayout layout;
    
    @Override
    protected void adjustSize() {
        if (this.getChildren().size() == 0) {
            return;
        }
        if (this.getWidth() > this.getMaximumWidth()) {
            this.setWidth(this.getMaximumWidth());
            return;
        }
        Control lastControl = this.getChildren().get(this.getChildren().size() - 1);
        int lastControlHeight = lastControl.getY() + lastControl.getHeight();
        if (this.getHeight() < lastControlHeight) {
            if (this.getMaximumHeight() > lastControlHeight) {
                this.setHeight(lastControlHeight);
            } else {
                this.setHeight(this.getMaximumHeight());
            }
        }
    }

    /**
     * コンポーネント間の水平方向スペースをセットする。
     * 
     * @param spacing
     */
    public void setHorizontalSpacing(int spacing) {
        this.layout.setHgap(spacing);
        this.adjustPadding();
    }
    
    /**
     * コンポーネント間の垂直方向スペースをセットする。
     * 
     * @param spacing
     */
    public void setVerticalSpacing(int spacing) {
        this.layout.setVgap(spacing);
        this.adjustPadding();
    }

    /**
     * コンポーネント間のスペースを指定する。
     * 
     * @param horizontalSpacing 水平方向のスペース。
     * @param verticalSpacing 垂直方向のスペース。
     */
    public void setSpacing(int horizontalSpacing, int verticalSpacing) {
        this.layout.setHgap(horizontalSpacing);
        this.layout.setVgap(verticalSpacing);
        this.adjustPadding();
    }

    /**
     * このペインに配置されている子要素間のスペースを指定する。
     * 
     * @param spacing
     */
    public void setSpacing(int spacing) {
        this.setSpacing(spacing, spacing);
    }
    
    private int paddingTop = 0;
    
    private int paddingRight = 0;
    
    private int paddingBottom = 0;
    
    private int paddingLeft = 0;
    
    /**
     * コンポーネント間ギャップなのに端にもギャップができるのでネガティブパディングで相殺する。
     */
    private void adjustPadding() {
        int offsetX = this.layout.getHgap() * -1;
        int offsetY = this.layout.getVgap() * -1;
        super.setPadding(this.paddingTop + offsetY, this.paddingRight + offsetX, this.paddingBottom + offsetY, this.paddingLeft + offsetX);
    }
    
    @Override
    public void setPadding(int top, int right, int bottom, int left) {
        this.paddingTop = top;
        this.paddingRight = right;
        this.paddingBottom = bottom;
        this.paddingLeft = left;
        this.adjustPadding();
    }
    
    @Override
    public void setPadding(int topAndBottom, int rightAndLeft) {
        this.paddingTop = topAndBottom;
        this.paddingRight = rightAndLeft;
        this.paddingBottom = topAndBottom;
        this.paddingLeft = rightAndLeft;
        this.adjustPadding();
    }
    
    @Override
    public void setPadding(int padding) {
        this.paddingTop = padding;
        this.paddingRight = padding;
        this.paddingBottom = padding;
        this.paddingLeft = padding;
        this.adjustPadding();
    }
    
}
