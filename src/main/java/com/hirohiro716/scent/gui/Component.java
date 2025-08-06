package com.hirohiro716.scent.gui;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hirohiro716.scent.Bounds;
import com.hirohiro716.scent.Dimension;
import com.hirohiro716.scent.gui.event.ChangeListener;
import com.hirohiro716.scent.gui.event.InnerInstanceCreator;
import com.hirohiro716.scent.image.Image;
import com.hirohiro716.scent.image.Image.ImageFormat;

/**
 * GUIのすべてのコンポーネントの抽象クラス。
 * 
 * @param <T> 内部で使用されるGUIライブラリに依存したインスタンスの型。
 *
 */
public abstract class Component<T extends java.awt.Component> {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスと、<br>
     * コンポーネントのレイアウトに使用する、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     * @param innerInstanceForLayout 
     */
    public Component(T innerInstance, java.awt.Component innerInstanceForLayout) {
        this.innerInstance = innerInstance;
        this.innerInstanceForLayout = innerInstanceForLayout;
        this.innerInstanceForLayout.setMaximumSize(new java.awt.Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
    }
    
    private T innerInstance;
    
    /**
     * このコンポーネントがラップしている、GUIライブラリに依存したインスタンスを取得する。
     * 
     * @return
     */
    public T getInnerInstance() {
        return this.innerInstance;
    }
    
    private java.awt.Component innerInstanceForLayout;
    
    /**
     * このコンポーネントのレイアウトに使用する、GUIライブラリに依存したインスタンスを取得する。
     * 
     * @return
     */
    public java.awt.Component getInnerInstanceForLayout() {
        return this.innerInstanceForLayout;
    }
    
    @Override
    public String toString() {
        return this.getInnerInstance().toString();
    }
    
    /**
     * このコンポーネントの名前を取得する。
     * 
     * @return
     */
    public String getName() {
        return this.getInnerInstance().getName();
    }
    
    /**
     * このコンポーネントに名前をセットする。
     * 
     * @param name
     */
    public void setName(String name) {
        this.getInnerInstance().setName(name);
    }
    
    /**
     * このコンポーネントの背景色を取得する。
     * 
     * @return
     */
    public abstract Color getBackgroundColor();
    
    /**
     * このコンポーネントに背景色をセットする。
     * 
     * @param color
     */
    public abstract void setBackgroundColor(Color color);
    
    /**
     * このコンポーネントの位置を取得する。
     * 
     * @return
     */
    public Point getLocation() {
        return this.getInnerInstanceForLayout().getLocation();
    }
    
    /**
     * このコンポーネントの位置をセットする。
     * 
     * @param point
     */
    public void setLocation(Point point) {
        this.getInnerInstanceForLayout().setLocation(point);
    }
    
    /**
     * このコンポーネントの位置をセットする。
     * 
     * @param x
     * @param y
     */
    public final void setLocation(int x, int y) {
        this.setLocation(new Point(x, y));
    }
    
    /**
     * このコンポーネントの水平方向位置を取得する。
     * 
     * @return
     */
    public int getX() {
        return (int) this.getLocation().getX();
    }
    
    /**
     * このコンポーネントの水平方向位置をセットする。
     * 
     * @param x
     */
    public final void setX(int x) {
        this.setLocation(x, this.getY());
    }
    
    /**
     * このコンポーネントの垂直方向位置を取得する。
     * 
     * @return
     */
    public int getY() {
        return (int) this.getLocation().getY();
    }
    
    /**
     * このコンポーネントの垂直方向位置をセットする。
     * 
     * @param y
     */
    public final void setY(int y) {
        this.setLocation(this.getX(), y);
    }
    
    /**
     * このコンポーネントの画面上の位置を取得する。<br>
     * コンポーネントが画面に表示されていない場合はnullを返す。
     * 
     * @return
     */
    public Point getLocationOnScreen() {
        try {
            if (this.findPlacedGraphicsDevice() != null) {
                return this.getInnerInstanceForLayout().getLocationOnScreen();
            }
        } catch (Exception exception) {
        }
        return null;
    }
    
    private Dimension size = new Dimension(0, 0);
    
    /**
     * このコンポーネントのサイズを取得する。
     * 
     * @return
     */
    public Dimension getSize() {
        java.awt.Dimension awtDimension = this.getInnerInstanceForLayout().getSize();
        if (this.size.getWidth() != awtDimension.getWidth() || this.size.getHeight() != awtDimension.getHeight()) {
            this.size = new Dimension(awtDimension.width, awtDimension.height);
        }
        return this.size;
    }
    
    /**
     * このコンポーネントのサイズをセットする。
     * 
     * @param dimension
     */
    public void setSize(Dimension dimension) {
        java.awt.Dimension awtDimension = new java.awt.Dimension(dimension.getIntegerWidth(), dimension.getIntegerHeight());
        this.getInnerInstanceForLayout().setSize(awtDimension);
        this.getInnerInstanceForLayout().setPreferredSize(awtDimension);
    }
    
    /**
     * このコンポーネントのサイズをセットする。
     * 
     * @param width
     * @param height
     */
    public final void setSize(int width, int height) {
        Dimension dimension = new Dimension(width, height);
        this.setSize(dimension);
    }
    
    /**
     * このコンポーネントの幅を取得する。
     * 
     * @return
     */
    public final int getWidth() {
        return (int) this.getSize().getWidth();
    }
    
    /**
     * このコンポーネントの幅をセットする。
     * 
     * @param width
     */
    public final void setWidth(int width) {
        int height = this.getInnerInstanceForLayout().getPreferredSize().height;
        if (height < this.getInnerInstanceForLayout().getSize().getHeight()) {
            height = this.getInnerInstanceForLayout().getSize().height;
        }
        this.setSize(width, height);
    }
    
    /**
     * このコンポーネントの高さを取得する。
     * 
     * @return
     */
    public final int getHeight() {
        return (int) this.getSize().getHeight();
    }
    
    /**
     * このコンポーネントの高さをセットする。
     * 
     * @param height
     */
    public final void setHeight(int height) {
        int width = this.getInnerInstanceForLayout().getPreferredSize().width;
        if (width < this.getInnerInstanceForLayout().getSize().getWidth()) {
            width = this.getInnerInstanceForLayout().getSize().width;
        }
        this.setSize(width, height);
    }
    
    /**
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスの幅をセットする。
     * 
     * @param width
     */
    protected final void setWidthToInnerInstance(int width) {
        int height = this.getInnerInstanceForLayout().getPreferredSize().height;
        if (height < this.getInnerInstanceForLayout().getSize().getHeight()) {
            height = this.getInnerInstanceForLayout().getSize().height;
        }
        java.awt.Dimension dimension = new java.awt.Dimension(width, height);
        this.getInnerInstanceForLayout().setSize(dimension);
        this.getInnerInstanceForLayout().setPreferredSize(dimension);
    }

    /**
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスの高さをセットする。
     * 
     * @param height
     */
    protected final void setHeightToInnerInstance(int height) {
        int width = this.getInnerInstanceForLayout().getPreferredSize().width;
        if (width < this.getInnerInstanceForLayout().getSize().getWidth()) {
            width = this.getInnerInstanceForLayout().getSize().width;
        }
        java.awt.Dimension dimension = new java.awt.Dimension(width, height);
        this.getInnerInstanceForLayout().setSize(dimension);
        this.getInnerInstanceForLayout().setPreferredSize(dimension);
    }
    
    private Dimension minimumSize = null;
    
    /**
     * このコンポーネントの最小サイズを取得する。
     * 
     * @return
     */
    public Dimension getMinimumSize() {
        if (this.minimumSize == null) {
            java.awt.Dimension awtDimension = this.getInnerInstanceForLayout().getMinimumSize();
            this.minimumSize = new Dimension(awtDimension.width, awtDimension.height);
        }
        return this.minimumSize;
    }

    /**
     * このコンポーネントの最小サイズをセットする。初期値は無制限。
     * 
     * @param dimension
     */
    public void setMinimumSize(Dimension dimension) {
        this.minimumSize = dimension;
        java.awt.Dimension awtDimension = new java.awt.Dimension(dimension.getIntegerWidth(), dimension.getIntegerHeight());
        this.getInnerInstanceForLayout().setMinimumSize(awtDimension);
    }
    
    /**
     * このコンポーネントの最小サイズをセットする。初期値は無制限。
     * 
     * @param width
     * @param height
     */
    public final void setMinimumSize(int width, int height) {
        this.setMinimumSize(new Dimension(width, height));
    }
    
    /**
     * このコンポーネントの最小幅を取得する。
     * 
     * @return
     */
    public final int getMinimumWidth() {
        return this.getMinimumSize().getIntegerWidth();
    }
    
    /**
     * このコンポーネントの最小幅をセットする。
     * 
     * @param width
     */
    public final void setMinimumWidth(int width) {
        this.setMinimumSize(width, this.getMinimumHeight());
    }
    
    /**
     * このコンポーネントの最小高さを取得する。
     * 
     * @return
     */
    public final int getMinimumHeight() {
        return this.getMinimumSize().getIntegerHeight();
    }
    
    /**
     * このコンポーネントの最小高さをセットする。
     * 
     * @param height
     */
    public final void setMinimumHeight(int height) {
        this.setMinimumSize(this.getMinimumWidth(), height);
    }

    private Dimension maximumSize = null;
    
    /**
     * このコンポーネントの最大サイズを取得する。
     * 
     * @return
     */
    public Dimension getMaximumSize() {
        if (this.maximumSize == null) {
            java.awt.Dimension awtDimension = this.getInnerInstanceForLayout().getMaximumSize();
            this.maximumSize = new Dimension(awtDimension.width, awtDimension.height);
        }
        return this.maximumSize;
    }

    /**
     * このコンポーネントの最大サイズをセットする。初期値は無制限。
     * 
     * @param dimension
     */
    public void setMaximumSize(Dimension dimension) {
        this.maximumSize = dimension;
        java.awt.Dimension awtDimension = new java.awt.Dimension(dimension.getIntegerWidth(), dimension.getIntegerHeight());
        this.getInnerInstanceForLayout().setMaximumSize(awtDimension);
    }
    
    /**
     * このコンポーネントの最大サイズをセットする。初期値は無制限。
     * 
     * @param width
     * @param height
     */
    public final void setMaximumSize(int width, int height) {
        this.setMaximumSize(new Dimension(width, height));
    }

    /**
     * このコンポーネントの最大幅を取得する。
     * 
     * @return
     */
    public final int getMaximumWidth() {
        return this.getMaximumSize().getIntegerWidth();
    }
    
    /**
     * このコンポーネントの最大幅をセットする。
     * 
     * @param width
     */
    public final void setMaximumWidth(int width) {
        this.setMaximumSize(width, this.getMaximumHeight());
    }
    
    /**
     * このコンポーネントの最大高さを取得する。
     * 
     * @return
     */
    public final int getMaximumHeight() {
        return this.getMaximumSize().getIntegerHeight();
    }
    
    /**
     * このコンポーネントの最大高さをセットする。
     * 
     * @param height
     */
    public final void setMaximumHeight(int height) {
        this.setMaximumSize(this.getMaximumWidth(), height);
    }
    
    private Bounds bounds = new Bounds(0, 0, 0, 0);
    
    /**
     * このコンポーネントの位置とサイズを取得する。
     * 
     * @return
     */
    public Bounds getBounds() {
        if (this.bounds.getX() != this.getX() || this.bounds.getY() != this.getY()
                || this.bounds.getWidth() != this.getWidth() || this.bounds.getHeight() != this.getHeight()) {
            this.bounds = new Bounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }
        return this.bounds;
    }
    
    /**
     * このコンポーネントの位置とサイズをBoundsインスタンスから取り込む。
     */
    private void importFromBounds() {
        this.setX((int) this.bounds.getX());
        this.setY((int) this.bounds.getY());
        this.setWidth((int) this.bounds.getWidth());
        this.setHeight((int) this.bounds.getHeight());
    }
    
    /**
     * このコンポーネントの位置とサイズをセットする。
     * 
     * @param bounds
     */
    public final void setBounds(Bounds bounds) {
        this.bounds = bounds;
        this.importFromBounds();
    }

    /**
     * このコンポーネントの位置とサイズをセットする。
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public final void setBounds(int x, int y, int width, int height) {
        this.bounds = new Bounds(x, y, width, height);
        this.importFromBounds();
    }
    
    /**
     * このコンポーネントのレイアウトを更新する。
     */
    public void updateLayout() {
        this.getInnerInstance().revalidate();
        if (this.getInnerInstance() != this.getInnerInstanceForLayout()) {
            this.getInnerInstanceForLayout().revalidate();
        }
    }
    
    /**
     * このコンポーネントの描画を更新する。
     */
    public void updateDisplay() {
        this.getInnerInstance().repaint();
        if (this.getInnerInstance() != this.getInnerInstanceForLayout()) {
            this.getInnerInstanceForLayout().repaint();
        }
    }
    
    /**
     * このコンポーネントにフォーカスできる場合はtrueを返す。
     * 
     * @return
     */
    public boolean isFocusable() {
        return this.getInnerInstance().isFocusable();
    }
    
    /**
     * このコンポーネントにフォーカスできるようにする場合はtrueをセットする。
     * 
     * @param isFocusable
     */
    public void setFocusable(boolean isFocusable) {
        this.getInnerInstance().setFocusable(isFocusable);
    }
    
    /**
     * このコンポーネントをフォーカスする。
     */
    public void requestFocus() {
        this.getInnerInstance().requestFocus();
    }
    
    /**
     * このコンポーネントがフォーカスされている場合はtrueを返す。
     * 
     * @return
     */
    public boolean isFocused() {
        return this.getInnerInstance().isFocusOwner();
    }
    
    private Object instanceForUseLater;
    
    /**
     * 後で取得できる任意のインスタンスをセットする。
     * 
     * @param anyObject
     */
    public void setInstanceForUseLater(Object anyObject) {
        this.instanceForUseLater = anyObject;
    }
    
    /**
     * 以前セットした任意のインスタンスを取得する。
     * 
     * @param <I>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <I> I getInstanceForUseLater() {
        return (I) this.instanceForUseLater;
    }
    
    /**
     * このコンポーネント上でのマウスカーソルの種類をセットする。
     * 
     * @param mouseCursor
     */
    public void setMouseCursor(MouseCursor mouseCursor) {
        this.getInnerInstanceForLayout().setCursor(mouseCursor.createInnerInstance());
    }
    
    /**
     * このコンポーネントのスクリーンショットを作成する。
     * 
     * @return
     * @throws IOException
     */
    public Image screenshot() throws IOException {
        BufferedImage bufferedImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        this.getInnerInstanceForLayout().print(bufferedImage.getGraphics());
        return new Image(ImageFormat.PNG, bufferedImage);
    }
    
    /**
     * このコンポーネントが表示されているグラフィックデバイスを検索する。該当するものがない場合はnullを返す。
     * 
     * @return
     */
    public GraphicsDevice findPlacedGraphicsDevice() {
        try {
            GraphicsDevice[] devices = GUI.getGraphicsDevices();
            Point point = this.getInnerInstanceForLayout().getLocationOnScreen();
            point.setLocation(point.getX() + this.getWidth() / 2, point.getY() + this.getHeight() / 2);
            for (GraphicsDevice device: devices) {
                Rectangle rectangle = device.getDefaultConfiguration().getBounds();
                if (rectangle.getMinX() <= point.getX() && point.getX() <= rectangle.getMaxX()
                        && rectangle.getMinY() <= point.getY() && point.getY() <= rectangle.getMaxY()) {
                    return device;
                }
            }
        } catch (Exception exception) {
        }
        return null;
    }

    private List<ChangeListener<Boolean>> visibleChangeListeners = new ArrayList<>();
    
    /**
     * このコントロールの表示状態が変化した際のリスナーを追加する。
     * 
     * @param changeListener
     */
    public void addVisibleChangeListener(ChangeListener<Boolean> changeListener) {
        this.visibleChangeListeners.add(changeListener);
    }

    /**
     * このコンポーネントが表示されている場合はtrueを返す。
     * 
     * @return
     */
    public boolean isVisible() {
        return this.getInnerInstanceForLayout().isVisible();
    }
    
    /**
     * このコンポーネントを表示する場合はtrueを、非表示にする場合はfalseをセットする。
     * 
     * @param isVisible
     */
    public void setVisible(boolean isVisible) {
        this.getInnerInstanceForLayout().setVisible(isVisible);
        for (ChangeListener<Boolean> changeListener: this.visibleChangeListeners) {
            changeListener.executeWhenChanged(this, isVisible);
        }
    }
    
    private List<ChangeListener<Boolean>> disabledChangeListeners = new ArrayList<>();
    
    /**
     * このコントロールの無効状態が変化した際のリスナーを追加する。
     * 
     * @param changeListener
     */
    public void addDisabledChangeListener(ChangeListener<Boolean> changeListener) {
        this.disabledChangeListeners.add(changeListener);
    }

    /**
     * このコンポーネントが無効な状態の場合はtrueを返す。
     * 
     * @return
     */
    public boolean isDisabled() {
        return ! this.getInnerInstance().isEnabled();
    }
    
    /**
     * このコンポーネントを無効にする場合はtrueをセットする。
     * 
     * @param isDisabled
     */
    public void setDisabled(boolean isDisabled) {
        this.getInnerInstance().setEnabled(! isDisabled);
        for (ChangeListener<Boolean> changeListener: this.disabledChangeListeners) {
            changeListener.executeWhenChanged(this, isDisabled);
        }
    }
    
    /**
     * このコンポーネントの位置が変更された際のリスナーを追加する。
     * 
     * @param changeListener
     */
    public void addLocationChangeListener(ChangeListener<Point> changeListener) {
        Component<T> component = this;
        ComponentListener innerInstance = changeListener.createInnerInstance(component, new InnerInstanceCreator<>() {

            @Override
            public ComponentListener create() {
                return new ComponentListener() {
                    
                    @Override
                    public void componentShown(ComponentEvent event) {
                    }
                    
                    @Override
                    public void componentResized(ComponentEvent event) {
                    }
                    
                    @Override
                    public void componentMoved(ComponentEvent event) {
                        changeListener.executeWhenChanged(component, component.getLocation());
                    }
                    
                    @Override
                    public void componentHidden(ComponentEvent event) {
                    }
                };
            }
        });
        this.getInnerInstanceForLayout().addComponentListener(innerInstance);
    }
    
    /**
     * このコンポーネントのサイズが変更された際のリスナーを追加する。
     * 
     * @param changeListener
     */
    public void addSizeChangeListener(ChangeListener<Dimension> changeListener) {
        Component<T> component = this;
        ComponentListener innerInstance = changeListener.createInnerInstance(component, new InnerInstanceCreator<>() {

            @Override
            public ComponentListener create() {
                return new ComponentListener() {
                    
                    @Override
                    public void componentShown(ComponentEvent event) {
                    }
                    
                    @Override
                    public void componentResized(ComponentEvent event) {
                        changeListener.executeWhenChanged(component, component.getSize());
                    }
                    
                    @Override
                    public void componentMoved(ComponentEvent event) {
                    }
                    
                    @Override
                    public void componentHidden(ComponentEvent event) {
                    }
                };
            }
        });
        this.getInnerInstanceForLayout().addComponentListener(innerInstance);
    }
    
    /**
     * このコンポーネントに追加されているリスナーを削除する。
     * 
     * @param changeListener
     */
    public void removeChangeListener(ChangeListener<?> changeListener) {
        for (Object innerInstance: changeListener.getInnerInstances(this)) {
            if (innerInstance instanceof ComponentListener) {
                this.getInnerInstance().removeComponentListener((ComponentListener) innerInstance);
                this.getInnerInstanceForLayout().removeComponentListener((ComponentListener) innerInstance);
            }
        }
        this.visibleChangeListeners.remove(changeListener);
        this.disabledChangeListeners.remove(changeListener);
    }
}
