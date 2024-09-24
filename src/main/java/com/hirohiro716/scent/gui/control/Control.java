package com.hirohiro716.scent.gui.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import com.hirohiro716.scent.Dimension;
import com.hirohiro716.scent.StringObject;
import com.hirohiro716.scent.gui.Border;
import com.hirohiro716.scent.gui.Component;
import com.hirohiro716.scent.gui.Frame;
import com.hirohiro716.scent.gui.GUI;
import com.hirohiro716.scent.gui.Insets;
import com.hirohiro716.scent.gui.event.ChangeListener;
import com.hirohiro716.scent.gui.event.EventHandler;
import com.hirohiro716.scent.gui.event.InnerInstanceCreator;
import com.hirohiro716.scent.gui.event.KeyEvent;
import com.hirohiro716.scent.gui.event.MouseEvent;
import com.hirohiro716.scent.gui.event.MouseEvent.MouseButton;

/**
 * GUIのすべてのコントロールの抽象クラス。
 * 
 * @author hiro
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
     * @param <T> 
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends Component<?>> T getParent() {
        return (T) this.component;
    }
    
    /**
     * このコントロールの親にあたるコンポーネントをセットする。
     * 
     * @param component
     */
    public void setParent(Component<?> component) {
        this.component = component;
    }
    
    /**
     * このコントロールが配置されているフレームを取得する。存在しない場合はnullを返す。
     * 
     * @return
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
    
    private String sizeStringOfAdjustSize = null;
    
    private int numberOfAdjustSize = 0;
    
    /**
     * このコンポーネントがGUIライブラリによって自動調整されたサイズを更に調整する。
     */
    protected void adjustSize() {
        Dimension minimumSize = this.getMinimumSize();
        Dimension maximumSize = this.getMaximumSize();
        String sizeString = StringObject.join(minimumSize, ":", maximumSize).toString();
        if (sizeString.equals(this.sizeStringOfAdjustSize)) {
            if (this.numberOfAdjustSize > 8) {
                return;
            }
        } else {
            this.sizeStringOfAdjustSize = sizeString;
            this.numberOfAdjustSize = 0;
        }
        if (this.getWidth() > 0) {
            if (this.getWidth() < minimumSize.getWidth() && minimumSize.getWidth() < maximumSize.getWidth()) {
                this.setWidthToInnerInstance(minimumSize.getIntegerWidth());
                this.numberOfAdjustSize++;
                return;
            }
        }
        if (this.getHeight() > 0) {
            if (this.getHeight() < minimumSize.getHeight() && minimumSize.getHeight() < maximumSize.getHeight()) {
                this.setHeightToInnerInstance(minimumSize.getIntegerHeight());
                this.numberOfAdjustSize++;
                return;
            }
        }
        if (this.getWidth() > maximumSize.getWidth() && minimumSize.getWidth() < maximumSize.getWidth()) {
            this.setWidthToInnerInstance(maximumSize.getIntegerWidth());
            this.numberOfAdjustSize++;
            return;
        }
        if (this.getHeight() > maximumSize.getHeight() && minimumSize.getHeight() < maximumSize.getHeight()) {
            this.setHeightToInnerInstance(maximumSize.getIntegerHeight());
            this.numberOfAdjustSize++;
            return;
        }
    }
    
    @Override
    public void setSize(Dimension dimension) {
        super.setSize(dimension);
        this.numberOfAdjustSize = 0;
        this.adjustSize();
    }
    
    @Override
    public void setMinimumSize(Dimension dimension) {
        super.setMinimumSize(dimension);
        this.adjustSize();
    }

    @Override
    public void setMaximumSize(Dimension dimension) {
        super.setMaximumSize(dimension);
        this.adjustSize();
    }

    /**
     * このコントロールで使用されているフォントを取得する。
     * 
     * @return
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
     * @return
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
    
    private javax.swing.border.Border border = this.getInnerInstanceForLayout().getBorder();
    
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
        this.getInnerInstanceForLayout().setBorder(new CompoundBorder(this.border, this.paddingBorder));
    }

    private Insets padding = new Insets();
    
    /**
     * このコントロールのパディングを取得する。
     * 
     * @return
     */
    public Insets getPadding() {
        return this.padding;
    }
    
    private EmptyBorder paddingBorder = null;
    
    /**
     * このコントロールにパディングをセットする。
     * 
     * @param insets
     */
    public void setPadding(Insets insets) {
        this.padding = insets;
        this.paddingBorder = new EmptyBorder(this.padding.getInnerInstance());
        this.getInnerInstanceForLayout().setBorder(new CompoundBorder(this.border, this.paddingBorder));
    }
    
    /**
     * このコントロールにパディングをセットする。
     * 
     * @param top 
     * @param right 
     * @param bottom 
     * @param left 
     */
    public final void setPadding(int top, int right, int bottom, int left) {
        this.setPadding(new Insets(top, right, bottom, left));
    }

    /**
     * このコントロールにパディングをセットする。
     * 
     * @param topAndBottom 
     * @param rightAndLeft 
     */
    public final void setPadding(int topAndBottom, int rightAndLeft) {
        this.setPadding(topAndBottom, rightAndLeft, topAndBottom, rightAndLeft);
    }
    
    /**
     * このコントロールにパディングをセットする。
     * 
     * @param padding
     */
    public final void setPadding(int padding) {
        this.setPadding(padding, padding);
    }
    
    /**
     * このコントロールでのインプットメソッドを無効にする場合はtrueをセットする。
     * 
     * @param isDisabledInputMethod
     */
    public void setDisableInputMethod(boolean isDisabledInputMethod) {
        this.getInnerInstance().enableInputMethods(! isDisabledInputMethod);
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

    @Override
    public void removeChangeListener(ChangeListener<?> changeListener) {
        super.removeChangeListener(changeListener);
        for (Object innerInstance : changeListener.getInnerInstances(this)) {
            if (innerInstance instanceof FocusListener) {
                this.getInnerInstance().removeFocusListener((FocusListener) innerInstance);
                this.getInnerInstanceForLayout().removeFocusListener((FocusListener) innerInstance);
            }
        }
    }
}
