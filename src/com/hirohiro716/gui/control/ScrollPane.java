package com.hirohiro716.gui.control;

import java.awt.Rectangle;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * スクロールバーを表示するペインのクラス。
 * 
 * @author hiro
 *
 */
public class ScrollPane extends Control {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected ScrollPane(JScrollPane innerInstance) {
        super(innerInstance);
        this.horizontalScrollBar = new ScrollBar(this.getInnerInstance().getHorizontalScrollBar());
        this.verticalScrollBar = new ScrollBar(this.getInnerInstance().getVerticalScrollBar());
    }

    private ScrollBar horizontalScrollBar;
    
    private ScrollBar verticalScrollBar;
    
    /**
     * コンストラクタ。
     */
    public ScrollPane() {
        this(new JScrollPane());
    }
    
    /**
     * コンストラクタ。<br>
     * このスクロールペインのビューポートに表示するコントロールを指定する。
     * 
     * @param control
     */
    public ScrollPane(Control control) {
        this();
        this.setContent(control);
    }

    /**
     * このメソッドはコンストラクタの呼び出しと同じで、新しいインスタンスを作成する。
     * 
     * @param innerInstance GUIライブラリに依存したインスタンス。
     * @return 新しいインスタンス。
     */
    public static ScrollPane newInstance(JScrollPane innerInstance) {
        return new ScrollPane(innerInstance);
    }
    
    @Override
    public JScrollPane getInnerInstance() {
        return (JScrollPane) super.getInnerInstance();
    }

    @Override
    public void setDisabled(boolean isDisabled) {
        super.setDisabled(isDisabled);
        if (this.content != null) {
            this.content.setDisabled(isDisabled);
        }
    }

    private Control content = null;
    
    /**
     * このスクロールペインに表示するコントロールを取得する。
     * 
     * @param <C>
     * @return 結果。
     */
    @SuppressWarnings("unchecked")
    public <C extends Control> C getContent() {
        return (C) this.content;
    }
    
    /**
     * このスクロールペインに表示するコントロールをセットする。
     * 
     * @param control
     */
    public void setContent(Control control) {
        this.content = control;
        this.content.setParent(this);
        this.content.setDisabled(this.isDisabled());
        this.getInnerInstance().setViewportView(control.getInnerInstanceForLayout());
    }
    
    /**
     * このスクロールペインに配置されているコントロールが表示されている場合はtrueを返す。
     * 
     * @param control
     * @return 結果。
     */
    public boolean isDisplayedEntireControl(Control control) {
        int targetX = control.getX();
        int targetY = control.getY();
        boolean isBelong = false;
        Control parent = control.getParent();
        while (parent != null) {
            if (parent == this.getContent()) {
                isBelong = true;
                break;
            }
            targetX += parent.getX();
            targetY += parent.getY();
            parent = parent.getParent();
        }
        if (isBelong) {
            return this.isDisplayedBounds(targetX, targetY, control.getWidth(), control.getHeight());
        }
        return false;
    }
    
    /**
     * このスクロールペインに指定の領域が表示されている場合はtrueを返す。
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     * @return 結果。
     */
    public boolean isDisplayedBounds(int x, int y, int width, int height) {
        int targetX = x;
        int targetY = y;
        Rectangle rectangle = this.getInnerInstance().getViewport().getViewRect();
        if (rectangle.getMinX() <= targetX && rectangle.getMinY() <= targetY && targetX + width <= rectangle.getMaxX() && targetY + height <= rectangle.getMaxY()) {
            return true;
        }
        return false;
    }
    
    /**
     * このスクロールペインに配置されているコントロールにスクロールする。
     * 
     * @param control
     */
    public void scrollTo(Control control) {
        int x = control.getX();
        int y = control.getY();
        boolean isBelong = false;
        Control parent = control.getParent();
        while (parent != null) {
            if (parent == this.getContent()) {
                isBelong = true;
                break;
            }
            x += parent.getX();
            y += parent.getY();
            parent = parent.getParent();
        }
        if (isBelong) {
            this.getVerticalScrollBar().setScrollPosition(y - 5);
            this.getHorizontalScrollBar().setScrollPosition(x - 5);
        }
    }
    
    /**
     * このスクロールペインの水平方向のスクロールバーを取得する。
     * 
     * @return 結果。
     */
    public ScrollBar getHorizontalScrollBar() {
        return this.horizontalScrollBar;
    }
    
    /**
     * このスクロールペインの垂直方向のスクロールバーを取得する。
     * 
     * @return 結果。
     */
    public ScrollBar getVerticalScrollBar() {
        return this.verticalScrollBar;
    }
    
    /**
     * 水平方向のスクロールバー表示ポリシーを取得する。
     * 
     * @return 結果。
     */
    public ScrollBarDisplayPolicy getHorizontalScrollBarDisplayPolicy() {
        switch (this.getInnerInstance().getHorizontalScrollBarPolicy()) {
        case ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED:
            return ScrollBarDisplayPolicy.WHEN_NEEDED;
        case ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER:
            return ScrollBarDisplayPolicy.NEVER;
        case ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS:
            return ScrollBarDisplayPolicy.ALWAYS;
        }
        return null;
    }
    
    /**
     * 水平方向のスクロールバー表示ポリシーをセットする。
     * 
     * @param scrollBarDisplayPolicy
     */
    public void setHorizontalScrollBarDisplayPolicy(ScrollBarDisplayPolicy scrollBarDisplayPolicy) {
        switch (scrollBarDisplayPolicy) {
        case WHEN_NEEDED:
            this.getInnerInstance().setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            break;
        case NEVER:
            this.getInnerInstance().setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            break;
        case ALWAYS:
            this.getInnerInstance().setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            break;
        }
    }

    /**
     * 垂直方向のスクロールバー表示ポリシーを取得する。
     * 
     * @return 結果。
     */
    public ScrollBarDisplayPolicy getVerticalScrollBarDisplayPolicy() {
        switch (this.getInnerInstance().getVerticalScrollBarPolicy()) {
        case ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED:
            return ScrollBarDisplayPolicy.WHEN_NEEDED;
        case ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER:
            return ScrollBarDisplayPolicy.NEVER;
        case ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS:
            return ScrollBarDisplayPolicy.ALWAYS;
        }
        return null;
    }
    
    /**
     * 垂直方向のスクロールバー表示ポリシーをセットする。
     * 
     * @param scrollBarDisplayPolicy
     */
    public void setVerticalScrollBarDisplayPolicy(ScrollBarDisplayPolicy scrollBarDisplayPolicy) {
        switch (scrollBarDisplayPolicy) {
        case WHEN_NEEDED:
            this.getInnerInstance().setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            break;
        case NEVER:
            this.getInnerInstance().setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            break;
        case ALWAYS:
            this.getInnerInstance().setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            break;
        }
    }
    
    /**
     * スクロールバー表示ポリシーの列挙型。
     * 
     * @author hiro
     */
    public enum ScrollBarDisplayPolicy {
        /**
         * スクロールバーを必要なときに表示する。
         */
        WHEN_NEEDED,
        /**
         * スクロールバーを表示しない。
         */
        NEVER,
        /**
         * スクロールバーを常に表示する。
         */
        ALWAYS,
    }
}
