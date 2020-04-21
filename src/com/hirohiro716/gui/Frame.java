package com.hirohiro716.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hirohiro716.ExceptionMessenger;
import com.hirohiro716.gui.collection.AddListener;
import com.hirohiro716.gui.collection.Collection;
import com.hirohiro716.gui.collection.RemoveListener;
import com.hirohiro716.gui.control.Control;
import com.hirohiro716.gui.control.Pane;
import com.hirohiro716.gui.dialog.MessageDialog;
import com.hirohiro716.gui.dialog.ProcessAfterDialogClosing;
import com.hirohiro716.gui.dialog.MessageableDialog.ResultButton;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.FrameEvent;
import com.hirohiro716.image.Image;

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
        Frame<T> frame = this;
        this.iconImages.addListener(new AddListener<>() {
            
            @Override
            protected void added(Image added, int positionIndex) {
                try {
                    java.awt.Image image = added.createBufferedImage();
                    frame.mapIconImage.put(added, image);
                    frame.innerIconImages.add(image);
                    frame.getInnerInstance().setIconImages(frame.innerIconImages);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });
        this.iconImages.addListener(new RemoveListener<>() {
            
            @Override
            protected void removed(Image removed) {
                java.awt.Image image = frame.mapIconImage.get(removed);
                frame.mapIconImage.remove(removed);
                frame.innerIconImages.remove(image);
                frame.getInnerInstance().setIconImages(frame.innerIconImages);
            }
        });
        this.setSize(400, 300);
    }
    
    private Map<Image, java.awt.Image> mapIconImage = new HashMap<>();
    
    private List<java.awt.Image> innerIconImages = new ArrayList<>();
    
    private Collection<Image> iconImages = new Collection<>();
    
    /**
     * このフレームで使用するアイコンのコレクションを取得する。
     * 
     * @return 結果。
     */
    public Collection<Image> getIconImages() {
        return this.iconImages;
    }
    
    /**
     * このフレーム内のルートペインを取得する。
     * 
     * @return 結果。
     */
    public abstract Pane getRootPane();
    
    /**
     * このフレーム内に表示するコントロールをセットする。
     * 
     * @param control 
     */
    public abstract void setContent(Control control);
    
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
    
    private boolean isSetLocation = false;
    
    @Override
    public void setLocation(Point point) {
        super.setLocation(point);
        this.isSetLocation = (point != null);
    }

    @Override
    public void setDisabled(boolean isDisabled) {
        super.setDisabled(isDisabled);
        this.getRootPane().setDisabled(isDisabled);
    }

    @Override
    public abstract Dimension getMaximumSize();
    
    @Override
    public abstract void setMaximumSize(Dimension dimension);
    
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
        return this.getInnerInstance().isAlwaysOnTop();
    }
    
    /**
     * このフレームを常に最前面に表示する場合はtrueをセットする。初期値はfalse。
     * 
     * @param isAlwaysOnTop
     */
    public void setAlwaysOnTop(boolean isAlwaysOnTop) {
        this.getInnerInstance().setAlwaysOnTop(isAlwaysOnTop);
    }
    
    @Override
    public Color getBackgroundColor() {
        return this.getRootPane().getBackgroundColor();
    }
    
    @Override
    public void setBackgroundColor(Color color) {
        this.getRootPane().setBackgroundColor(color);
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
        if (this.isSetLocation == false) {
            if (GUI.getGraphicsDevices().length > 0) {
                Rectangle screenRectangle = GUI.getMaximumWindowBounds(GUI.getDefaultGraphicsDevice());
                this.setLocation(screenRectangle.x + screenRectangle.width / 2 - this.getWidth() / 2, screenRectangle.y + screenRectangle.height / 2 - this.getHeight() / 2);
            }
        }
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
        this.setClosable(true);
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
     * 例外が発生した場合のメッセージを表示する。
     * 
     * @param message メッセージ。
     * @param exception 発生した例外。
     * @param processAfterDialogClosing ダイアログを閉じた後の処理。
     */
    public void showException(String message, Exception exception, ProcessAfterDialogClosing<ResultButton> processAfterDialogClosing) {
        MessageDialog dialog = new MessageDialog(this);
        dialog.setTitle("例外の発生");
        dialog.setMessage(ExceptionMessenger.newInstance(exception).make(message));
        dialog.setProcessAfterClosing(processAfterDialogClosing);
        dialog.show();
    }
    
    /**
     * 例外が発生した場合のメッセージを表示する。
     * 
     * @param exception 発生した例外。
     * @param processAfterDialogClosing ダイアログを閉じた後の処理。
     */
    public final void showException(Exception exception, ProcessAfterDialogClosing<ResultButton> processAfterDialogClosing) {
        this.showException(null, exception, processAfterDialogClosing);
    }

    /**
     * 例外が発生した場合のメッセージを表示する。
     * 
     * @param message メッセージ。
     * @param exception 発生した例外。
     */
    public final void showException(String message, Exception exception) {
        this.showException(message, exception, null);
    }
    
    /**
     * 例外が発生した場合のメッセージを表示する。
     * 
     * @param exception 発生した例外。
     */
    public final void showException(Exception exception) {
        this.showException(null, exception, null);
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
