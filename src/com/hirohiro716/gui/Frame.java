package com.hirohiro716.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.event.WindowEvent;

import com.hirohiro716.gui.control.Pane;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.FrameEvent;

/**
 * GUIのウィンドウやダイアログなどの抽象クラス。
 * 
 * @author hiro
 * 
 * @param <T> 内部で使用されるGUIライブラリに依存したインスタンスの型。
 */
public abstract class Frame<T extends java.awt.Window> extends Component<T> {
    
    /**
     * コンストラクタ。<br>
     * このコントロールがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param window 
     */
    protected Frame(T window) {
        super(window, window);
    }
    
    /**
     * このフレーム内のペインを取得する。
     * 
     * @return 結果。
     */
    public abstract Pane getPane();
    
    /**
     * このフレームのタイトルを取得する。
     * 
     * @return 結果。
     */
    public abstract String getTitle();
    
    /**
     * このフレームにタイトルをセットする。
     * 
     * @param title
     */
    public abstract void setTitle(String title);
    
    @Override
    public abstract Dimension getMaximumSize();
    
    @Override
    public abstract void setMaximumSize(int width, int height);
    
    /**
     * このフレームがリサイズ可能な場合はtrueを返す。
     * 
     * @return 結果。
     */
    public abstract boolean isResizable();
    
    /**
     * このフレームをリサイズ可能にする場合はtrueをセットする。初期値はtrue。
     * 
     * @param isResizable
     */
    public abstract void setResizable(boolean isResizable);

    /**
     * このフレームを閉じることが可能な場合はtrueを返す。
     * 
     * @return 結果。
     */
    public abstract boolean isClosable();
    
    /**
     * このフレームを閉じることが可能にする場合はtrueをセットする。初期値はtrue。
     * 
     * @param isClosable
     */
    public abstract void setClosable(boolean isClosable);
    
    /**
     * このフレームのネイティブな装飾が無効になっている場合はtrueを返す。
     * 
     * @return 結果。
     */
    public abstract boolean isUndecorated();
    
    /**
     * このフレームのネイティブな装飾を無効にする場合はtrueをセットする。初期値はfalse。
     * 
     * @param isUndecorated
     */
    public abstract void setUndecorated(boolean isUndecorated);
    
    /**
     * このフレームが常に最前面に表示される場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isAlwaysOnTop() {
        return this.isAlwaysOnTop();
    }
    
    /**
     * このフレームを常に最前面に表示する場合はtrueをセットする。初期値はfalse。
     * 
     * @param isAlwaysOnTop
     */
    public void setAlwaysOnTop(boolean isAlwaysOnTop) {
        this.setAlwaysOnTop(isAlwaysOnTop);
    }
    
    @Override
    public Color getBackgroundColor() {
        return this.getPane().getBackgroundColor();
    }
    
    @Override
    public void setBackgroundColor(Color color) {
        this.getPane().setBackgroundColor(color);
    }
    
    @Override
    public boolean isFocusable() {
        return this.getInnerInstance().isFocusableWindow();
    }
    
    @Override
    public void setFocusable(boolean isFocusable) {
        this.getInnerInstance().setFocusableWindowState(isFocusable);
    }
    
    /**
     * このフレームをアクティブにする。
     */
    public void activate() {
        this.getInnerInstance().toFront();
    }
    
    /**
     * このフレームがアクティブになっている場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isActivated() {
        return this.getInnerInstance().isActive();
    }
    
    /**
     * このフレームを表示する。
     */
    public void show() {
        this.getInnerInstance().setVisible(true);
    }
    
    /**
     * このフレームを非表示にする。
     */
    public void hide() {
        this.getInnerInstance().setVisible(false);
    }
    
    /**
     * このフレームを閉じる。
     */
    public void close() {
        this.getInnerInstance().dispatchEvent(new WindowEvent(this.getInnerInstance(), WindowEvent.WINDOW_CLOSING));
    }
    
    private GraphicsDevice fullscreenGraphicsDevice = null;
    
    /**
     * このフレームを指定されたデバイスにフルスクリーンで表示する。
     * 
     * @param graphicsDevice
     */
    public void fullscreen(GraphicsDevice graphicsDevice) {
        this.fullscreenGraphicsDevice = graphicsDevice;
        this.fullscreenGraphicsDevice.setFullScreenWindow(this.getInnerInstance());
    }

    /**
     * このフレームが配置されているデバイスにフルスクリーンで表示する。
     */
    public void fullscreen() {
        GraphicsDevice device = this.findPlacedGraphicsDevice();
        if (device == null) {
            return;
        }
        this.fullscreen(device);
    }
    
    /**
     * このフレームのフルスクリーン表示をキャンセルする。
     */
    public void cancelFullscreen() {
        if (this.fullscreenGraphicsDevice != null && this.fullscreenGraphicsDevice.getFullScreenWindow().equals(this.getInnerInstance())) {
            this.fullscreenGraphicsDevice.setFullScreenWindow(null);
        }
    }
    
    /**
     * このフレームを開いた際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addOpenedEventHandler(EventHandler<FrameEvent> eventHandler) {
        this.getInnerInstance().addWindowListener(FrameEvent.createInnerOpenedEventHandler(this, eventHandler));
    }
    
    /**
     * このフレームを閉じる際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addClosingEventHandler(EventHandler<FrameEvent> eventHandler) {
        this.getInnerInstance().addWindowListener(FrameEvent.createInnerClosingEventHandler(this, eventHandler));
    }
    
    /**
     * このフレームを閉じた際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addClosedEventHandler(EventHandler<FrameEvent> eventHandler) {
        this.getInnerInstance().addWindowListener(FrameEvent.createInnerClosedEventHandler(this, eventHandler));
    }
    
    /**
     * このフレームを最小化した際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addIconifiedEventHandler(EventHandler<FrameEvent> eventHandler) {
        this.getInnerInstance().addWindowListener(FrameEvent.createInnerIconifiedEventHandler(this, eventHandler));
    }

    /**
     * このフレームが最小化から元に戻った際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addDeiconifiedEventHandler(EventHandler<FrameEvent> eventHandler) {
        this.getInnerInstance().addWindowListener(FrameEvent.createInnerDeiconifiedEventHandler(this, eventHandler));
    }
    
    /**
     * このフレームがアクティブになった際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addActivatedEventHandler(EventHandler<FrameEvent> eventHandler) {
        this.getInnerInstance().addWindowListener(FrameEvent.createInnerActivatedEventHandler(this, eventHandler));
    }
    
    /**
     * このフレームがアクティブになった際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addDeactivatedEventHandler(EventHandler<FrameEvent> eventHandler) {
        this.getInnerInstance().addWindowListener(FrameEvent.createInnerDeactivatedEventHandler(this, eventHandler));
    }
}
