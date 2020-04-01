package com.hirohiro716.gui.control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import com.hirohiro716.gui.Border;
import com.hirohiro716.gui.Component;
import com.hirohiro716.gui.Frame;
import com.hirohiro716.gui.GUI;
import com.hirohiro716.gui.event.ChangeListener;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.InnerInstanceCreator;
import com.hirohiro716.gui.event.KeyEvent;
import com.hirohiro716.gui.event.MouseEvent;
import com.hirohiro716.gui.event.MouseEvent.MouseButton;

/**
 * GUIのすべてのコントロールの抽象クラス。
 * 
 * @author hiro
 *
 */
public abstract class Control extends Component<JComponent> {

    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスと、<br>
     * コンポーネントのレイアウトに使用する、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     * @param innerInstanceForLayout 
     */
    protected Control(JComponent innerInstance, JComponent innerInstanceForLayout) {
        super(innerInstance, innerInstanceForLayout);
        Font font = this.getFont();
        if (font != null) {
            if (GUI.getFontName() != null) {
                font = new Font(GUI.getFontName(), font.getStyle(), font.getSize());
            }
            if (GUI.getFontSizeToAdd() != 0) {
                font = font.deriveFont(font.getSize2D() + GUI.getFontSizeToAdd());
            }
            this.setFont(font);
        }
        ChangeListener<Dimension> sizeChangeListener = this.createBugFixChangeListener();
        if (sizeChangeListener == null) {
            sizeChangeListener = new ChangeListener<Dimension>() {

                @Override
                protected void changed(Component<?> component, Dimension changedValue, Dimension valueBeforeChange) {
                    Control control = Control.this;
                    if (control.isSizeInitialized == false) {
                        if (control.defaultMinimumSize != null && changedValue.width == control.defaultMinimumSize.width && changedValue.height == control.defaultMinimumSize.height) {
                            control.setMinimumSize(control.defaultMinimumSize.width, control.defaultMinimumSize.height);
                            control.isSizeInitialized = true;
                        }
                    }
                    control.adjustSize();
                }
            };
        }
        this.addSizeChangeListener(sizeChangeListener);
    }
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected Control(JComponent innerInstance) {
        this(innerInstance, innerInstance);
    }
    
    @Override
    public JComponent getInnerInstance() {
        return super.getInnerInstance();
    }
    
    @Override
    public JComponent getInnerInstanceForLayout() {
        return (JComponent) super.getInnerInstanceForLayout();
    }
    
    private Component<?> component;
    
    /**
     * このコントロールの親にあたるコントロールを取得する。
     * 
     * @return 結果。
     */
    @SuppressWarnings("unchecked")
    public <T extends Component<?>> T getParent() {
        return (T) this.component;
    }
    
    /**
     * このコントロールの親にあたるコンポーネントをセットする。存在しない場合はnullを返す。
     * 
     * @param component
     */
    public void setParent(Component<?> component) {
        this.component = component;
    }
    
    /**
     * このコントロールが配置されているフレームを取得する。存在しない場合はnullを返す。
     * 
     * @return 結果。
     */
    public Frame<?> getFrame() {
        Component<?> component = this.getParent();
        while (component != null) {
            if (component instanceof Frame) {
                return (Frame<?>) component;
            }
            if (component instanceof Control) {
                Control control = (Control) component;
                component = control.getParent();
            } else {
                break;
            }
        }
        return null;
    }

    private boolean isSizeInitialized = false;
    
    private Dimension defaultMinimumSize = null;
    
    /**
     * このコンポーネントがGUIライブラリによって自動調整されたサイズを更に調整する。
     */
    private void adjustSize() {
        if (this.getWidth() < this.getMinimumWidth() && this.getMinimumWidth() < this.getMaximumWidth()) {
            this.setWidth(this.getMinimumWidth());
            return;
        }
        if (this.getHeight() < this.getMinimumHeight() && this.getMinimumHeight() < this.getMaximumHeight()) {
            this.setHeight(this.getMinimumHeight());
            return;
        }
        
        if (this.getWidth() > this.getMaximumWidth() && this.getMinimumWidth() < this.getMaximumWidth()) {
            this.setWidth(this.getMaximumWidth());
            return;
        }
        if (this.getHeight() > this.getMaximumHeight() && this.getMinimumHeight() < this.getMaximumHeight()) {
            this.setHeight(this.getMaximumHeight());
            return;
        }
    }
    
    @Override
    public void setSize(Dimension dimension) {
        if (this.isSizeInitialized == false) {
            this.defaultMinimumSize = this.getMinimumSize();
            super.setMinimumSize(dimension.width, dimension.height);
        }
        super.setSize(dimension);
        this.adjustSize();
    }
    
    @Override
    public void setSize(int width, int height) {
        if (this.isSizeInitialized == false) {
            this.defaultMinimumSize = this.getMinimumSize();
            super.setMinimumSize(width, height);
        }
        super.setSize(width, height);
        this.adjustSize();
    }
    
    @Override
    public void setWidth(int width) {
        if (this.isSizeInitialized == false) {
            this.defaultMinimumSize = this.getMinimumSize();
            super.setMinimumWidth(width);
        }
        super.setWidth(width);
        this.adjustSize();
    }
    
    @Override
    public void setHeight(int height) {
        if (this.isSizeInitialized == false) {
            this.defaultMinimumSize = this.getMinimumSize();
            super.setMinimumHeight(height);
        }
        super.setHeight(height);
        this.adjustSize();
    }

    @Override
    public void setMinimumSize(int width, int height) {
        this.isSizeInitialized = true;
        super.setMinimumSize(width, height);
        this.adjustSize();
    }
    
    @Override
    public void setMinimumWidth(int width) {
        this.isSizeInitialized = true;
        super.setMinimumWidth(width);
        this.adjustSize();
    }
    
    @Override
    public void setMinimumHeight(int height) {
        this.isSizeInitialized = true;
        super.setMinimumHeight(height);
        this.adjustSize();
    }

    @Override
    public void setMaximumSize(int width, int height) {
        super.setMaximumSize(width, height);
        this.adjustSize();
    }

    @Override
    public void setMaximumWidth(int width) {
        super.setMaximumWidth(width);
        this.adjustSize();
    }

    @Override
    public void setMaximumHeight(int height) {
        super.setMaximumHeight(height);
        this.adjustSize();
    }

    /**
     * このコンポーネントがGUIライブラリによって自動調整されたサイズを調整するためのリスナーを作成する。必要ない場合はnullを返す。
     * 
     * @return 結果。
     */
    protected abstract ChangeListener<Dimension> createBugFixChangeListener();
    
    /**
     * このコントロールで使用されているフォントを取得する。
     * 
     * @return 結果。
     */
    public Font getFont() {
        return this.getInnerInstance().getFont();
    }
    
    /**
     * このコントロールで使用するフォントをセットする。
     * 
     * @param font
     */
    public void setFont(Font font) {
        this.getInnerInstance().setFont(font);
    }
    
    @Override
    public Color getBackgroundColor() {
        return this.getInnerInstance().getBackground();
    }

    @Override
    public void setBackgroundColor(Color color) {
        this.getInnerInstance().setBackground(color);
        this.getInnerInstance().setOpaque(color != null);
    }
    
    /**
     * このコントロールの前景色を取得する。
     * 
     * @return 結果。
     */
    public Color getForegroundColor() {
        return this.getInnerInstance().getForeground();
    }
    
    /**
     * このコントロールに前景色をセットする。
     * 
     * @param color
     */
    public void setForegroundColor(Color color) {
        this.getInnerInstance().setForeground(color);
    }
    
    private javax.swing.border.Border border = null;
    
    /**
     * このコントロールに境界線をセットする。
     * 
     * @param border
     */
    public void setBorder(Border border) {
        if (border == null) {
            this.border = null;
        } else {
            this.border = border.getInnerInstance();
        }
        this.getInnerInstance().setBorder(new CompoundBorder(this.border, this.padding));
    }
    
    private EmptyBorder padding = null;
    
    /**
     * このコントロールにパディングをセットする。
     * 
     * @param top 
     * @param right 
     * @param bottom 
     * @param left 
     */
    public void setPadding(int top, int right, int bottom, int left) {
        Insets insets = new Insets(top, left, bottom, right);
        this.padding = new EmptyBorder(insets);
        this.getInnerInstance().setBorder(new CompoundBorder(this.border, this.padding));
    }

    /**
     * このコントロールにパディングをセットする。
     * 
     * @param topAndBottom 
     * @param rightAndLeft 
     */
    public void setPadding(int topAndBottom, int rightAndLeft) {
        this.setPadding(topAndBottom, rightAndLeft, topAndBottom, rightAndLeft);
    }
    
    /**
     * このコントロールにパディングをセットする。
     * 
     * @param padding
     */
    public void setPadding(int padding) {
        this.setPadding(padding, padding);
    }
    
    /**
     * このコントロールでのインプットメソッドを無効にする場合はtrueをセットする。
     * 
     * @param isDisableInputMethod
     */
    public void setDisableInputMethod(boolean isDisableInputMethod) {
        this.getInnerInstance().enableInputMethods(!isDisableInputMethod);
    }
    
    /**
     * このコントロールにマウスクリックのイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addMouseClickedEventHandler(EventHandler<MouseEvent> eventHandler) {
        MouseListener innerInstance = MouseEvent.createInnerMouseClickedEventHandler(this, eventHandler);
        this.getInnerInstance().addMouseListener(innerInstance);
    }
    
    /**
     * このコントロールにマウスの指定ボタンクリックのイベントハンドラを追加する。
     * 
     * @param mouseButton 
     * @param eventHandler
     */
    public void addMouseClickedEventHandler(MouseButton mouseButton, EventHandler<MouseEvent> eventHandler) {
        MouseListener innerInstance = MouseEvent.createInnerMouseClickedEventHandler(this, mouseButton, eventHandler);
        this.getInnerInstance().addMouseListener(innerInstance);
    }
    
    /**
     * このコントロールにマウスのボタンを押した際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addMousePressedEventHandler(EventHandler<MouseEvent> eventHandler) {
        MouseListener innerInstance = MouseEvent.createInnerMousePressedEventHandler(this, eventHandler);
        this.getInnerInstance().addMouseListener(innerInstance);
    }

    /**
     * このコントロールにマウスの指定ボタンを押した際のイベントハンドラを追加する。
     * 
     * @param mouseButton 
     * @param eventHandler
     */
    public void addMousePressedEventHandler(MouseButton mouseButton, EventHandler<MouseEvent> eventHandler) {
        MouseListener innerInstance = MouseEvent.createInnerMousePressedEventHandler(this, mouseButton, eventHandler);
        this.getInnerInstance().addMouseListener(innerInstance);
    }
    
    /**
     * このコントロールにマウスのボタンを離した際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addMouseReleasedEventHandler(EventHandler<MouseEvent> eventHandler) {
        MouseListener innerInstance = MouseEvent.createInnerMouseReleasedEventHandler(this, eventHandler);
        this.getInnerInstance().addMouseListener(innerInstance);
    }

    /**
     * このコントロールにマウスの指定ボタンを離した際のイベントハンドラを追加する。
     * 
     * @param mouseButton 
     * @param eventHandler
     */
    public void addMouseReleasedEventHandler(MouseButton mouseButton, EventHandler<MouseEvent> eventHandler) {
        MouseListener innerInstance = MouseEvent.createInnerMouseReleasedEventHandler(this, mouseButton, eventHandler);
        this.getInnerInstance().addMouseListener(innerInstance);
    }
    
    /**
     * このコントロールの領域にマウスのポインターが入った際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addMouseEnteredEventHandler(EventHandler<MouseEvent> eventHandler) {
        MouseListener innerInstance = MouseEvent.createInnerMouseEnteredEventHandler(this, eventHandler);
        this.getInnerInstance().addMouseListener(innerInstance);
    }
    
    /**
     * このコントロールの領域からマウスのポインターが出た際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addMouseExitedEventHandler(EventHandler<MouseEvent> eventHandler) {
        MouseListener innerInstance = MouseEvent.createInnerMouseExitedEventHandler(this, eventHandler);
        this.getInnerInstance().addMouseListener(innerInstance);
    }
    
    /**
     * このコントロールの領域内でマウスのポインターが動いた際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addMouseMovedEventHandler(EventHandler<MouseEvent> eventHandler) {
        MouseMotionListener innerInstance = MouseEvent.createInnerMouseMovedEventHandler(this, eventHandler);
        this.getInnerInstance().addMouseMotionListener(innerInstance);
    }
    
    /**
     * このコントロールの領域内からマウスでドラッグした際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addMouseDraggedEventHandler(EventHandler<MouseEvent> eventHandler) {
        MouseMotionListener innerInstance = MouseEvent.createInnerMouseDraggedEventHandler(this, eventHandler);
        this.getInnerInstance().addMouseMotionListener(innerInstance);
    }
    
    /**
     * このコントロールの領域内でマウスホイールが回転した際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addMouseWheelEventHandler(EventHandler<MouseEvent> eventHandler) {
        MouseWheelListener innerInstance = MouseEvent.createInnerMouseWheelEventHandler(this, eventHandler);
        this.getInnerInstance().addMouseWheelListener(innerInstance);
    }
    
    /**
     * このコントロールでキーをタイプした際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addKeyTypedEventHandler(EventHandler<KeyEvent> eventHandler) {
        KeyListener innerInstance = KeyEvent.createInnerKeyTypedEventHandler(this, eventHandler);
        this.getInnerInstance().addKeyListener(innerInstance);
    }
    
    /**
     * このコントロールでキーを押した際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addKeyPressedEventHandler(EventHandler<KeyEvent> eventHandler) {
        KeyListener innerInstance = KeyEvent.createInnerKeyPressedEventHandler(this, eventHandler);
        this.getInnerInstance().addKeyListener(innerInstance);
    }
    
    /**
     * このコントロールでキーを離した際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addKeyReleasedEventHandler(EventHandler<KeyEvent> eventHandler) {
        KeyListener innerInstance = KeyEvent.createInnerKeyReleasedEventHandler(this, eventHandler);
        this.getInnerInstance().addKeyListener(innerInstance);
    }

    /**
     * このコンポーネントに追加されているイベントハンドラを削除する。
     * 
     * @param eventHandler
     */
    public void removeEventHandler(EventHandler<?> eventHandler) {
        for (Object innerInstance : eventHandler.getInnerInstances(this)) {
            if (innerInstance instanceof MouseListener) {
                this.getInnerInstance().removeMouseListener((MouseListener) innerInstance);
            }
            if (innerInstance instanceof MouseMotionListener) {
                this.getInnerInstance().removeMouseMotionListener((MouseMotionListener) innerInstance);
            }
            if (innerInstance instanceof MouseWheelListener) {
                this.getInnerInstance().removeMouseWheelListener((MouseWheelListener) innerInstance);
            }
            if (innerInstance instanceof KeyListener) {
                this.getInnerInstance().removeKeyListener((KeyListener) innerInstance);
            }
        }
    }

    /**
     * このコントロールがフォーカス状態が変化した際のリスナーを追加する。
     * 
     * @param changeListener
     */
    public void addFocusChangeListener(ChangeListener<Boolean> changeListener) {
        Control control = this;
        FocusListener innerInstance = changeListener.createInnerInstance(control, new InnerInstanceCreator<>() {

            @Override
            public FocusListener create() {
                return new FocusListener() {
                    
                    @Override
                    public void focusLost(FocusEvent event) {
                        changeListener.executeWhenChanged(control, false);
                    }
                    
                    @Override
                    public void focusGained(FocusEvent event) {
                        changeListener.executeWhenChanged(control, true);
                    }
                };
            }
        });
        this.getInnerInstance().addFocusListener(innerInstance);
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
    
    @Override
    public void setVisible(boolean isVisible) {
        super.setVisible(isVisible);
        for (ChangeListener<Boolean> changeListener : this.visibleChangeListeners) {
            changeListener.executeWhenChanged(this, isVisible);
        }
    }

    @Override
    public void removeChangeListener(ChangeListener<?> changeListener) {
        super.removeChangeListener(changeListener);
        for (Object innerInstance : changeListener.getInnerInstances(this)) {
            if (innerInstance instanceof FocusListener) {
                this.getInnerInstance().removeFocusListener((FocusListener) innerInstance);
            }
        }
        this.visibleChangeListeners.remove(changeListener);
    }
}
