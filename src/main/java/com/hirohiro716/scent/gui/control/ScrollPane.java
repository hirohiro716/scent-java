package com.hirohiro716.scent.gui.control;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.hirohiro716.scent.gui.collection.AddListener;
import com.hirohiro716.scent.gui.collection.RemoveListener;
import com.hirohiro716.scent.gui.event.EventHandler;
import com.hirohiro716.scent.gui.event.MouseEvent;
import com.hirohiro716.scent.gui.MouseButton;

/**
 * スクロールバーを表示するペインのクラス。
 * 
 * @author hiro
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
     * @param content スクロールペインのビューポートに表示するコントロール。
     * @return 新しいインスタンス。
     */
    public static ScrollPane newInstance(JScrollPane innerInstance, Control content) {
        ScrollPane scrollPane = new ScrollPane(innerInstance);
        content.addMousePressedEventHandler(MouseButton.BUTTON1, scrollPane.touchScrollMousePressedEventHandler);
        content.addMouseDraggedEventHandler(scrollPane.touchScrollMouseDraggedEventHandler);
        content.addMouseReleasedEventHandler(MouseButton.BUTTON1, scrollPane.touchScrollMouseReleasedEventHandler);
        scrollPane.content = content;
        return scrollPane;
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

    private boolean isTouchScrollDisabled = false;

    /**
     * タッチスクロールが無効になっている場合はtrueを返す。
     * 
     * @return
     */
    public boolean isTouchScrollDisabled() {
        return this.isTouchScrollDisabled;
    }

    /**
     * タッチスクロールを無効にする場合はtrueをセットする。
     * 
     * @param isTouchScrollDisabled
     */
    public void setTouchScrollDisabled(boolean isTouchScrollDisabled) {
        this.isTouchScrollDisabled = isTouchScrollDisabled;
    }

    private boolean isTouchScrollStarted = false;

    private int touchStartedX;

    private int touchStartedHorizontalScrollBarPosition;
    
    private int touchStartedY;

    private int touchStartedVerticalScrollBarPosition;

    private List<Control> touchScrollEventHandlerAddedControls = new ArrayList<>();
    
    /**
     * タッチスクロールのマウスのボタンを押した際のイベントハンドラー。
     */
    private EventHandler<MouseEvent> touchScrollMousePressedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        protected void handle(MouseEvent event) {
            ScrollPane scrollPane = ScrollPane.this;
            if (scrollPane.isTouchScrollStarted) {
                return;
            }
            scrollPane.isTouchScrollStarted = true;
            scrollPane.touchStartedX = event.getScreenX();
            scrollPane.touchStartedHorizontalScrollBarPosition = scrollPane.getHorizontalScrollBar().getScrollPosition();
            scrollPane.touchStartedY = event.getScreenY();
            scrollPane.touchStartedVerticalScrollBarPosition = scrollPane.getVerticalScrollBar().getScrollPosition();
        }
    };

    /**
     * タッチスクロールのマウスでドラッグした際のイベントハンドラー。
     */
    private EventHandler<MouseEvent> touchScrollMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        protected void handle(MouseEvent event) {
            ScrollPane scrollPane = ScrollPane.this;
            if (scrollPane.isTouchScrollDisabled || scrollPane.isTouchScrollStarted == false) {
                return;
            }
            scrollPane.getHorizontalScrollBar().setScrollPosition(scrollPane.touchStartedHorizontalScrollBarPosition + (scrollPane.touchStartedX - event.getScreenX()) * 2);
            scrollPane.getVerticalScrollBar().setScrollPosition(scrollPane.touchStartedVerticalScrollBarPosition + (scrollPane.touchStartedY - event.getScreenY()) * 2);
        }
    };

    /**
     * タッチスクロールのマウスのボタンを押した際のイベントハンドラー。
     */
    private EventHandler<MouseEvent> touchScrollMouseReleasedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        protected void handle(MouseEvent event) {
            ScrollPane scrollPane = ScrollPane.this;
            scrollPane.isTouchScrollStarted = false;
        }
    };

    private Control content = null;
    
    /**
     * このスクロールペインに表示するコントロールを取得する。
     * 
     * @param <C>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <C extends Control> C getContent() {
        return (C) this.content;
    }

    /**
     * 指定されたラベルにタッチスクロールのイベントハンドラーを追加する。
     * 
     * @param label
     */
    private void addTouchScrollEventHandlerToLabel(Label label) {
        ScrollPane scrollPane = this;
        if (this.touchScrollEventHandlerAddedControls.contains(label)) {
            return;
        }
        label.addMousePressedEventHandler(MouseButton.BUTTON1, scrollPane.touchScrollMousePressedEventHandler);
        label.addMouseDraggedEventHandler(scrollPane.touchScrollMouseDraggedEventHandler);
        label.addMouseReleasedEventHandler(MouseButton.BUTTON1, scrollPane.touchScrollMouseReleasedEventHandler);
        scrollPane.touchScrollEventHandlerAddedControls.add(label);
    }
    
    /**
     * 指定されたペインにタッチスクロールのイベントハンドラーを追加する。
     * 
     * @param pane
     */
    private void addTouchScrollEventHandlerToPaen(Pane pane) {
        ScrollPane scrollPane = this;
        if (this.touchScrollEventHandlerAddedControls.contains(pane)) {
            return;
        }
        pane.addMousePressedEventHandler(MouseButton.BUTTON1, scrollPane.touchScrollMousePressedEventHandler);
        pane.addMouseDraggedEventHandler(scrollPane.touchScrollMouseDraggedEventHandler);
        pane.addMouseReleasedEventHandler(MouseButton.BUTTON1, scrollPane.touchScrollMouseReleasedEventHandler);
        scrollPane.touchScrollEventHandlerAddedControls.add(pane);
        for (Control child: pane.getChildren().findAll()) {
            if (child instanceof Label) {
                this.addTouchScrollEventHandlerToLabel((Label) child);
            }
            if (child instanceof Pane) {
                this.addTouchScrollEventHandlerToPaen((Pane) child);
            }
        }
        pane.getChildren().addListener(new AddListener<Control>() {

            @Override
            protected void added(Control added, int positionIndex) {
                if (added instanceof Label) {
                    if (scrollPane.touchScrollEventHandlerAddedControls.contains(added)) {
                        return;
                    }
                    added.addMousePressedEventHandler(MouseButton.BUTTON1, scrollPane.touchScrollMousePressedEventHandler);
                    added.addMouseDraggedEventHandler(scrollPane.touchScrollMouseDraggedEventHandler);
                    added.addMouseReleasedEventHandler(MouseButton.BUTTON1, scrollPane.touchScrollMouseReleasedEventHandler);
                    scrollPane.touchScrollEventHandlerAddedControls.add(added);
                }
                if (added instanceof Pane) {
                    if (scrollPane.touchScrollEventHandlerAddedControls.contains(added)) {
                        return;
                    }
                    added.addMousePressedEventHandler(MouseButton.BUTTON1, scrollPane.touchScrollMousePressedEventHandler);
                    added.addMouseDraggedEventHandler(scrollPane.touchScrollMouseDraggedEventHandler);
                    added.addMouseReleasedEventHandler(MouseButton.BUTTON1, scrollPane.touchScrollMouseReleasedEventHandler);
                    scrollPane.touchScrollEventHandlerAddedControls.add(added);
                    scrollPane.addTouchScrollEventHandlerToPaen((Pane) added);
                }
            }
        });
        pane.getChildren().addListener(new RemoveListener<Control>() {

            @Override
            protected void removed(Control removed) {
                removed.removeEventHandler(scrollPane.touchScrollMousePressedEventHandler);
                removed.removeEventHandler(scrollPane.touchScrollMouseDraggedEventHandler);
                removed.removeEventHandler(scrollPane.touchScrollMouseReleasedEventHandler);
                scrollPane.touchScrollEventHandlerAddedControls.remove(removed);
            }
        });
    }
    
    /**
     * このスクロールペインに表示するコントロールをセットする。
     * 
     * @param control
     */
    public void setContent(Control control) {
        if (this.content != null) {
            this.content.removeEventHandler(this.touchScrollMousePressedEventHandler);
            this.content.removeEventHandler(this.touchScrollMouseDraggedEventHandler);
            this.content.removeEventHandler(this.touchScrollMouseReleasedEventHandler);
            this.touchScrollEventHandlerAddedControls.remove(this.content);
            if (this.content instanceof Pane) {
                Pane pane = (Pane) this.content;
                for (Control child: pane.getChildren().findAll()) {
                    child.removeEventHandler(this.touchScrollMousePressedEventHandler);
                    child.removeEventHandler(this.touchScrollMouseDraggedEventHandler);
                    child.removeEventHandler(this.touchScrollMouseReleasedEventHandler);
                    this.touchScrollEventHandlerAddedControls.remove(child);
                }
            }
        }
        this.content = control;
        this.content.setParent(this);
        this.content.setDisabled(this.isDisabled());
        this.content.addMousePressedEventHandler(MouseButton.BUTTON1, this.touchScrollMousePressedEventHandler);
        this.content.addMouseDraggedEventHandler(this.touchScrollMouseDraggedEventHandler);
        this.content.addMouseReleasedEventHandler(MouseButton.BUTTON1, this.touchScrollMouseReleasedEventHandler);
        if (this.content instanceof Pane) {
            this.addTouchScrollEventHandlerToPaen((Pane) this.content);
        }
        this.getInnerInstance().setViewportView(control.getInnerInstanceForLayout());
    }
    
    /**
     * このスクロールペインに配置されているコントロールが表示されている場合はtrueを返す。
     * 
     * @param control
     * @return
     */
    public boolean isDisplayEntireControl(Control control) {
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
     * @return
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
     * @return
     */
    public ScrollBar getHorizontalScrollBar() {
        return this.horizontalScrollBar;
    }
    
    /**
     * このスクロールペインの垂直方向のスクロールバーを取得する。
     * 
     * @return
     */
    public ScrollBar getVerticalScrollBar() {
        return this.verticalScrollBar;
    }
    
    /**
     * 水平方向のスクロールバー表示ポリシーを取得する。
     * 
     * @return
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
     * @return
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
