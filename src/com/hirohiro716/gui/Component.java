package com.hirohiro716.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import com.hirohiro716.gui.event.ChangeListener;
import com.hirohiro716.gui.event.InnerInstanceCreator;
import com.hirohiro716.image.Image;
import com.hirohiro716.image.Image.ImageFormatName;

/**
 * GUIのすべてのコンポーネントの抽象クラス。
 * 
 * @author hiro
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
    }
    
    private T innerInstance;
    
    /**
     * このコンポーネントがラップしている、GUIライブラリに依存したインスタンスを取得する。
     * 
     * @return 結果。
     */
    public T getInnerInstance() {
        return this.innerInstance;
    }
    
    private java.awt.Component innerInstanceForLayout;
    
    /**
     * このコンポーネントのレイアウトに使用する、GUIライブラリに依存したインスタンスを取得する。
     * 
     * @return 結果。
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
     * @return 結果。
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
     * @return 結果。
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
     * @return 結果。
     */
    public Point getLocation() {
        return this.getInnerInstanceForLayout().getLocation();
    }
    
    /**
     * このコンポーネントの位置のセットする。
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
    public void setLocation(int x, int y) {
        this.getInnerInstanceForLayout().setLocation(x, y);
    }
    
    /**
     * このコンポーネントの水平方向位置を取得する。
     * 
     * @return 結果。
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
     * @return 結果。
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
     * @return 結果。
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
    
    /**
     * このコンポーネントのサイズを取得する。
     * 
     * @return 結果。
     */
    public Dimension getSize() {
        return this.getInnerInstanceForLayout().getSize();
    }
    
    /**
     * このコンポーネントのサイズをセットする。
     * 
     * @param dimension
     */
    public void setSize(Dimension dimension) {
        this.getInnerInstanceForLayout().setSize(dimension);
        this.getInnerInstanceForLayout().setPreferredSize(dimension);
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
     * @return 結果。
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
        int height = this.getInnerInstance().getPreferredSize().height;
        if (height < this.getInnerInstance().getSize().getHeight()) {
            height = this.getInnerInstance().getSize().height;
        }
        this.setSize(width, height);
    }
    
    /**
     * このコンポーネントの高さを取得する。
     * 
     * @return 結果。
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
        int width = this.getInnerInstance().getPreferredSize().width;
        if (width < this.getInnerInstance().getSize().getWidth()) {
            width = this.getInnerInstance().getSize().width;
        }
        this.setSize(width, height);
    }
    
    /**
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスの幅をセットする。
     * 
     * @param width
     */
    protected final void setWidthToInnerInstance(int width) {
        int height = this.getInnerInstance().getPreferredSize().height;
        if (height < this.getInnerInstance().getSize().getHeight()) {
            height = this.getInnerInstance().getSize().height;
        }
        Dimension dimension = new Dimension(width, height);
        this.getInnerInstanceForLayout().setSize(dimension);
        this.getInnerInstanceForLayout().setPreferredSize(dimension);
    }

    /**
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスの高さをセットする。
     * 
     * @param height
     */
    protected final void setHeightToInnerInstance(int height) {
        int width = this.getInnerInstance().getPreferredSize().width;
        if (width < this.getInnerInstance().getSize().getWidth()) {
            width = this.getInnerInstance().getSize().width;
        }
        Dimension dimension = new Dimension(width, height);
        this.getInnerInstanceForLayout().setSize(dimension);
        this.getInnerInstanceForLayout().setPreferredSize(dimension);
    }
    
    private Dimension minimumSize = null;
    
    /**
     * このコンポーネントの最小サイズを取得する。
     * 
     * @return 結果。
     */
    public Dimension getMinimumSize() {
        if (this.minimumSize == null) {
            this.minimumSize = this.getInnerInstanceForLayout().getMinimumSize();
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
        this.getInnerInstanceForLayout().setMinimumSize(this.minimumSize);
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
     * @return 結果。
     */
    public final int getMinimumWidth() {
        return this.getMinimumSize().width;
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
     * @return 結果。
     */
    public final int getMinimumHeight() {
        return this.getMinimumSize().height;
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
     * @return 結果。
     */
    public Dimension getMaximumSize() {
        if (this.maximumSize == null) {
            this.maximumSize = this.getInnerInstanceForLayout().getMaximumSize();
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
        this.getInnerInstanceForLayout().setMaximumSize(this.maximumSize);
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
     * @return 結果。
     */
    public final int getMaximumWidth() {
        return this.getMaximumSize().width;
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
     * @return 結果。
     */
    public final int getMaximumHeight() {
        return this.getMaximumSize().height;
    }
    
    /**
     * このコンポーネントの最大高さをセットする。
     * 
     * @param height
     */
    public final void setMaximumHeight(int height) {
        this.setMaximumSize(this.getMaximumWidth(), height);
    }
    
    /**
     * 最大サイズ、最小サイズをセットして、このコンポーネントのサイズを固定する。
     * 
     * @param width
     * @param height
     */
    public final void setFixedSize(int width, int height) {
        this.setMinimumSize(width, height);
        this.setMaximumSize(width, height);
        this.setSize(width, height);
    }
    
    private Bounds bounds = new Bounds();
    
    /**
     * このコンポーネントの位置とサイズを取得する。
     * 
     * @return 結果。
     */
    public Bounds getBounds() {
        this.bounds.setX(this.getX());
        this.bounds.setY(this.getY());
        this.bounds.setWidth(this.getWidth());
        this.bounds.setHeight(this.getHeight());
        return this.bounds;
    }
    
    /**
     * このコンポーネントの位置とサイズをBoundsインスタンスから取り込む。
     */
    private void importFromBounds() {
        this.setX(this.bounds.getX());
        this.setY(this.bounds.getY());
        this.setWidth(this.bounds.getWidth());
        this.setHeight(this.bounds.getHeight());
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
        this.bounds.setX(x);
        this.bounds.setY(y);
        this.bounds.setWidth(width);
        this.bounds.setHeight(height);
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
     * @return 結果。
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
     * @return 結果。
     */
    public boolean isFocused() {
        return this.getInnerInstance().isFocusOwner();
    }

    /**
     * このコンポーネントが無効な状態の場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isDisabled() {
        return !this.getInnerInstance().isEnabled();
    }
    
    /**
     * このコンポーネントを無効にする場合はtrueをセットする。
     * 
     * @param isDisabled
     */
    public void setDisabled(boolean isDisabled) {
        this.getInnerInstance().setEnabled(!isDisabled);
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
     * @return 結果。
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
     * @return 結果。
     * @throws IOException
     */
    public Image screenshot() throws IOException {
        BufferedImage bufferedImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        this.getInnerInstanceForLayout().print(bufferedImage.getGraphics());
        return new Image(ImageFormatName.PNG, bufferedImage);
    }
    
    /**
     * このコンポーネントが表示されているグラフィックデバイスを検索する。該当するものがない場合はnullを返す。
     * 
     * @return 結果。
     */
    public GraphicsDevice findPlacedGraphicsDevice() {
        GraphicsDevice[] devices = GUI.getGraphicsDevices();
        Point point = this.getInnerInstanceForLayout().getLocationOnScreen();
        point.setLocation(point.getX() + this.getWidth() / 2, point.getY() + this.getHeight() / 2);
        for (GraphicsDevice device : devices) {
            Rectangle rectangle = device.getDefaultConfiguration().getBounds();
            if (rectangle.getMinX() <= point.getX() && point.getX() <= rectangle.getMaxX()
                    && rectangle.getMinY() <= point.getY() && point.getY() <= rectangle.getMaxY()) {
                return device;
            }
        }
        return null;
    }

    /**
     * このコンポーネントが表示されている場合はtrueを返す。
     * 
     * @return 結果。
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
        for (Object innerInstance : changeListener.getInnerInstances(this)) {
            if (innerInstance instanceof ComponentListener) {
                this.getInnerInstance().removeComponentListener((ComponentListener) innerInstance);
            }
        }
    }
}
