package com.hirohiro716.scent.gui.event;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.hirohiro716.scent.gui.Component;
import com.hirohiro716.scent.gui.control.Control;

/**
 * マウス操作イベントのクラス。
 * 
 * @author hiro
*/
public class MouseEvent extends InputEvent<java.awt.event.MouseEvent> {
    
    /**
     * コンストラクタ。<br>
     * イベントの発生元のコントロールと、GUIライブラリに依存したイベントのインスタンスを指定する。
     * 
     * @param control
     * @param innerInstance
     */
    public MouseEvent(Control control, java.awt.event.MouseEvent innerInstance) {
        super(control, innerInstance);
    }

    @Override
    public void consume() {
        this.getInnerInstance().consume();
    }
    
    @Override
    public boolean isConsumed() {
        return this.getInnerInstance().isConsumed();
    }
    
    /**
     * このイベントの原因となったマウスボタンを取得する。
     * 
     * @return 結果。
     */
    public MouseButton getMouseButton() {
        switch (this.getInnerInstance().getButton()) {
        case java.awt.event.MouseEvent.BUTTON1:
            return MouseButton.BUTTON1;
        case java.awt.event.MouseEvent.BUTTON2:
            return MouseButton.BUTTON2;
        case java.awt.event.MouseEvent.BUTTON3:
            return MouseButton.BUTTON3;
        }
        return null;
    }
    
    /**
     * このイベントのマウスクリック回数を取得する。
     * 
     * @return 結果。
     */
    public int getClickCount() {
        return this.getInnerInstance().getClickCount();
    }
    
    /**
     * このイベントの水平方向絶対座標を取得する。
     * 
     * @return 結果。
     */
    public int getScreenX() {
        return this.getInnerInstance().getXOnScreen();
    }
    
    /**
     * このイベントの垂直方向絶対座標を取得する。
     * 
     * @return 結果。
     */
    public int getScreenY() {
        return this.getInnerInstance().getYOnScreen();
    }
    
    /**
     * このイベントの発生元コンポーネントを基準とした、水平方向相対座標を取得する。
     * 
     * @return 結果。
     */
    public int getX() {
        return this.getInnerInstance().getX();
    }
    
    /**
     * このイベントの発生元コンポーネントを基準とした、垂直方向相対座標を取得する。
     * 
     * @return 結果。
     */
    public int getY() {
        return this.getInnerInstance().getY();
    }
    
    /**
     * このイベントの発生元コンポーネントが配置されているウィンドウを基準とした、水平方向相対座標を取得する。
     * 
     * @return 結果。
     */
    public int getRootX() {
        int x = this.getInnerInstance().getX();
        x += this.getSource().getX();
        Control parent = this.getSource().getParent();
        while (parent != null) {
            x += parent.getX();
            Component<?> parentComponent = parent.getParent();
            if (parentComponent instanceof Control) {
                parent = (Control) parentComponent;
            } else {
                parent = null;
            }
        }
        return x;
    }
    
    /**
     * このイベントの発生元コンポーネントが配置されているウィンドウを基準とした、垂直方向相対座標を取得する。
     * 
     * @return 結果。
     */
    public int getRootY() {
        int y = this.getInnerInstance().getY();
        y += this.getSource().getY();
        Control parent = this.getSource().getParent();
        while (parent != null) {
            y += parent.getY();
            Component<?> parentComponent = parent.getParent();
            if (parentComponent instanceof Control) {
                parent = (Control) parentComponent;
            } else {
                parent = null;
            }
        }
        return y;
    }
    
    /**
     * このイベントのマウスホイール回転量を取得する。手前に回転させた場合は正数、奥に回転させた場合は負数を返す。
     * 
     * @return 結果。
     */
    public int getWheelRotationAmount() {
        if (this.getInnerInstance() instanceof MouseWheelEvent) {
            MouseWheelEvent mouseWheelEvent = (MouseWheelEvent) this.getInnerInstance();
            return mouseWheelEvent.getWheelRotation();
        }
        return 0;
    }

    /**
     * このイベントでマウスホイールが回転したことによるスクロール量を取得する。<br>
     * 手前に回転させた場合は正数、奥に回転させた場合は負数を返す。
     * 
     * @return 結果。
     */
    public int getScrollAmount() {
        if (this.getInnerInstance() instanceof MouseWheelEvent) {
            MouseWheelEvent mouseWheelEvent = (MouseWheelEvent) this.getInnerInstance();
            return mouseWheelEvent.getUnitsToScroll();
        }
        return 0;
    }
        
    /**
     * マウスのボタンを表す列挙型。
     * 
     * @author hiro
     */
    public enum MouseButton {
        /**
         * ボタン1、通常のマウスボタン配置で左側。
         */
        BUTTON1,
        /**
         * ボタン2、通常のマウスボタン配置で中央。
         */
        BUTTON2,
        /**
         * ボタン3、通常のマウスボタン配置で右側。
         */
        BUTTON3,
    }
    
    /**
     * マウスクリックのイベントを実際に処理する、GUIライブラリに依存したイベントハンドラを作成する。
     * 
     * @param control
     * @param eventHandler
     * @return 結果。
     */
    public static MouseListener createInnerMouseClickedEventHandler(Control control, EventHandler<MouseEvent> eventHandler) {
        return eventHandler.createInnerInstance(control, new InnerInstanceCreator<>() {

            @Override
            public MouseListener create() {
                return new MouseAdapter() {
                    
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent event) {
                        eventHandler.executeWhenControlEnabled(new MouseEvent(control, event));
                    }
                };
            }
        });
    }
    
    /**
     * マウスの指定ボタンクリックのイベントを実際に処理する、GUIライブラリに依存したイベントハンドラを作成する。
     * 
     * @param control
     * @param mouseButton 
     * @param eventHandler
     * @return 結果。
     */
    public static MouseListener createInnerMouseClickedEventHandler(Control control, MouseButton mouseButton, EventHandler<MouseEvent> eventHandler) {
        return eventHandler.createInnerInstance(control, new InnerInstanceCreator<>() {

            @Override
            public MouseListener create() {
                return new MouseAdapter() {
                    
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent event) {
                        MouseEvent mouseEvent = new MouseEvent(control, event);
                        if (mouseEvent.getMouseButton() == mouseButton) {
                            eventHandler.executeWhenControlEnabled(new MouseEvent(control, event));
                        }
                    }
                };
            }
        });
    }
    
    /**
     * マウスのボタンを押した際のイベントを実際に処理する、GUIライブラリに依存したイベントハンドラを作成する。
     * 
     * @param control
     * @param eventHandler
     * @return 結果。
     */
    public static MouseListener createInnerMousePressedEventHandler(Control control, EventHandler<MouseEvent> eventHandler) {
        return eventHandler.createInnerInstance(control, new InnerInstanceCreator<>() {

            @Override
            public MouseListener create() {
                return new MouseAdapter() {
                    
                    @Override
                    public void mousePressed(java.awt.event.MouseEvent event) {
                        eventHandler.executeWhenControlEnabled(new MouseEvent(control, event));
                    }
                };
            }
        });
    }

    /**
     * マウスの指定ボタンを押した際のイベントを実際に処理する、GUIライブラリに依存したイベントハンドラを作成する。
     * 
     * @param control
     * @param mouseButton 
     * @param eventHandler
     * @return 結果。
     */
    public static MouseListener createInnerMousePressedEventHandler(Control control, MouseButton mouseButton, EventHandler<MouseEvent> eventHandler) {
        return eventHandler.createInnerInstance(control, new InnerInstanceCreator<>() {

            @Override
            public MouseListener create() {
                return new MouseAdapter() {
                    
                    @Override
                    public void mousePressed(java.awt.event.MouseEvent event) {
                        MouseEvent mouseEvent = new MouseEvent(control, event);
                        if (mouseEvent.getMouseButton() == mouseButton) {
                            eventHandler.executeWhenControlEnabled(new MouseEvent(control, event));
                        }
                    }
                };
            }
        });
    }
    
    /**
     * マウスのボタンを離した際のイベントを実際に処理する、GUIライブラリに依存したイベントハンドラを作成する。
     * 
     * @param control
     * @param eventHandler
     * @return 結果。
     */
    public static MouseListener createInnerMouseReleasedEventHandler(Control control, EventHandler<MouseEvent> eventHandler) {
        return eventHandler.createInnerInstance(control, new InnerInstanceCreator<>() {

            @Override
            public MouseListener create() {
                return new MouseAdapter() {
                    
                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent event) {
                        eventHandler.executeWhenControlEnabled(new MouseEvent(control, event));
                    }
                };
            }
        });
    }

    /**
     * マウスの指定ボタンを離した際のイベントを実際に処理する、GUIライブラリに依存したイベントハンドラを作成する。
     * 
     * @param control
     * @param mouseButton 
     * @param eventHandler
     * @return 結果。
     */
    public static MouseListener createInnerMouseReleasedEventHandler(Control control, MouseButton mouseButton, EventHandler<MouseEvent> eventHandler) {
        return eventHandler.createInnerInstance(control, new InnerInstanceCreator<>() {

            @Override
            public MouseListener create() {
                return new MouseAdapter() {
                    
                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent event) {
                        MouseEvent mouseEvent = new MouseEvent(control, event);
                        if (mouseEvent.getMouseButton() == mouseButton) {
                            eventHandler.executeWhenControlEnabled(new MouseEvent(control, event));
                        }
                    }
                };
            }
        });
    }
    
    /**
     * マウスのポインターがコントロールの領域に入った際のイベントを実際に処理する、GUIライブラリに依存したイベントハンドラを作成する。
     * 
     * @param control
     * @param eventHandler
     * @return 結果。
     */
    public static MouseListener createInnerMouseEnteredEventHandler(Control control, EventHandler<MouseEvent> eventHandler) {
        return eventHandler.createInnerInstance(control, new InnerInstanceCreator<>() {

            @Override
            public MouseListener create() {
                return new MouseAdapter() {
                    
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent event) {
                        eventHandler.executeWhenControlEnabled(new MouseEvent(control, event));
                    }
                };
            }
        });
    }
    
    /**
     * マウスのポインターがコントロールの領域から出た際のイベントを実際に処理する、GUIライブラリに依存したイベントハンドラを作成する。
     * 
     * @param control
     * @param eventHandler
     * @return 結果。
     */
    public static MouseListener createInnerMouseExitedEventHandler(Control control, EventHandler<MouseEvent> eventHandler) {
        return eventHandler.createInnerInstance(control, new InnerInstanceCreator<>() {

            @Override
            public MouseListener create() {
                return new MouseAdapter() {
                    
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent event) {
                        eventHandler.executeWhenControlEnabled(new MouseEvent(control, event));
                    }
                };
            }
        });
    }
    
    /**
     * マウスのポインターがコントロールの領域内で動いた際のイベントを実際に処理する、GUIライブラリに依存したイベントハンドラを作成する。
     * 
     * @param control
     * @param eventHandler
     * @return 結果。
     */
    public static MouseMotionListener createInnerMouseMovedEventHandler(Control control, EventHandler<MouseEvent> eventHandler) {
        return eventHandler.createInnerInstance(control, new InnerInstanceCreator<>() {

            @Override
            public MouseMotionListener create() {
                return new MouseMotionAdapter() {
                    
                    @Override
                    public void mouseMoved(java.awt.event.MouseEvent event) {
                        eventHandler.executeWhenControlEnabled(new MouseEvent(control, event));
                    }
                };
            }
        });
    }
    
    /**
     * マウスでコントロールの領域内からドラッグした際のイベントを実際に処理する、GUIライブラリに依存したイベントハンドラを作成する。
     * 
     * @param control
     * @param eventHandler
     * @return 結果。
     */
    public static MouseMotionListener createInnerMouseDraggedEventHandler(Control control, EventHandler<MouseEvent> eventHandler) {
        return eventHandler.createInnerInstance(control, new InnerInstanceCreator<>() {

            @Override
            public MouseMotionListener create() {
                return new MouseMotionAdapter() {
                    
                    @Override
                    public void mouseDragged(java.awt.event.MouseEvent event) {
                        eventHandler.executeWhenControlEnabled(new MouseEvent(control, event));
                    }
                };
            }
        });
    }

    /**
     * コントロールの領域内でマウスホイールが回転した際のイベントを実際に処理する、GUIライブラリに依存したイベントハンドラを作成する。
     * 
     * @param control
     * @param eventHandler
     * @return 結果。
     */
    public static MouseWheelListener createInnerMouseWheelEventHandler(Control control, EventHandler<MouseEvent> eventHandler) {
        return eventHandler.createInnerInstance(control, new InnerInstanceCreator<>() {

            @Override
            public MouseWheelListener create() {
                return new MouseWheelListener() {
                    
                    @Override
                    public void mouseWheelMoved(MouseWheelEvent event) {
                        eventHandler.executeWhenControlEnabled(new MouseEvent(control, event));
                    }
                };
            }
        });
    }
}
