package com.hirohiro716.scent.gui.event;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;

import com.hirohiro716.scent.gui.Frame;

/**
 * ウィンドウの状態が変わったことを示すイベントのクラス。
 * 
 * @author hiro
*/
public class FrameEvent extends Event<java.awt.event.WindowEvent> {
    
    /**
     * コンストラクタ。<br>
     * イベントの発生元のコントロールと、GUIライブラリに依存したイベントのインスタンスを指定する。
     * 
     * @param frame
     * @param innerInstance
     */
    public FrameEvent(Frame<?> frame, java.awt.event.WindowEvent innerInstance) {
        super(frame, innerInstance);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Frame<?> getSource() {
        return super.getSource();
    }
    
    /**
     * ウィンドウを開いた際のイベントを実際に処理する、GUIライブラリに依存したイベントハンドラを作成する。
     * 
     * @param frame
     * @param eventHandler
     * @return
     */
    public static WindowListener createInnerOpenedEventHandler(Frame<?> frame, EventHandler<FrameEvent> eventHandler) {
        return eventHandler.createInnerInstance(frame, new InnerInstanceCreator<>() {

            @Override
            public WindowListener create() {
                return new WindowAdapter() {
                    
                    @Override
                    public void windowOpened(java.awt.event.WindowEvent event) {
                        eventHandler.executeWhenControlEnabled(new FrameEvent(frame, event));
                    }
                };
            }
        });
    }
    
    /**
     * ウィンドウを閉じる際のイベントを実際に処理する、GUIライブラリに依存したイベントハンドラを作成する。
     * 
     * @param frame
     * @param eventHandler
     * @return
     */
    public static WindowListener createInnerClosingEventHandler(Frame<?> frame, EventHandler<FrameEvent> eventHandler) {
        return eventHandler.createInnerInstance(frame, new InnerInstanceCreator<>() {

            @Override
            public WindowListener create() {
                return new WindowAdapter() {
                    
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent event) {
                        eventHandler.executeWhenControlEnabled(new FrameEvent(frame, event));
                    }
                };
            }
        });
    }
    
    /**
     * ウィンドウを閉じた際のイベントを実際に処理する、GUIライブラリに依存したイベントハンドラを作成する。
     * 
     * @param frame
     * @param eventHandler
     * @return
     */
    public static WindowListener createInnerClosedEventHandler(Frame<?> frame, EventHandler<FrameEvent> eventHandler) {
        return eventHandler.createInnerInstance(frame, new InnerInstanceCreator<>() {

            @Override
            public WindowListener create() {
                return new WindowAdapter() {
                    
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent event) {
                        eventHandler.executeWhenControlEnabled(new FrameEvent(frame, event));
                    }
                };
            }
        });
    }
    
    /**
     * ウィンドウが最小化された際のイベントを実際に処理する、GUIライブラリに依存したイベントハンドラを作成する。
     * 
     * @param frame
     * @param eventHandler
     * @return
     */
    public static WindowListener createInnerIconifiedEventHandler(Frame<?> frame, EventHandler<FrameEvent> eventHandler) {
        return eventHandler.createInnerInstance(frame, new InnerInstanceCreator<>() {

            @Override
            public WindowListener create() {
                return new WindowAdapter() {
                    
                    @Override
                    public void windowIconified(java.awt.event.WindowEvent event) {
                        eventHandler.executeWhenControlEnabled(new FrameEvent(frame, event));
                    }
                };
            }
        });
    }

    /**
     * ウィンドウが最小化から元に戻った際のイベントを実際に処理する、GUIライブラリに依存したイベントハンドラを作成する。
     * 
     * @param frame
     * @param eventHandler
     * @return
     */
    public static WindowListener createInnerDeiconifiedEventHandler(Frame<?> frame, EventHandler<FrameEvent> eventHandler) {
        return eventHandler.createInnerInstance(frame, new InnerInstanceCreator<>() {

            @Override
            public WindowListener create() {
                return new WindowAdapter() {
                    
                    @Override
                    public void windowDeiconified(java.awt.event.WindowEvent event) {
                        eventHandler.executeWhenControlEnabled(new FrameEvent(frame, event));
                    }
                };
            }
        });
    }
    
    /**
     * ウィンドウがアクティブになった際のイベントを実際に処理する、GUIライブラリに依存したイベントハンドラを作成する。
     * 
     * @param frame
     * @param eventHandler
     * @return
     */
    public static WindowListener createInnerActivatedEventHandler(Frame<?> frame, EventHandler<FrameEvent> eventHandler) {
        return eventHandler.createInnerInstance(frame, new InnerInstanceCreator<>() {

            @Override
            public WindowListener create() {
                return new WindowAdapter() {
                    
                    @Override
                    public void windowActivated(java.awt.event.WindowEvent event) {
                        eventHandler.executeWhenControlEnabled(new FrameEvent(frame, event));
                    }
                };
            }
        });
    }

    /**
     * ウィンドウが非アクティブになった際のイベントを実際に処理する、GUIライブラリに依存したイベントハンドラを作成する。
     * 
     * @param frame
     * @param eventHandler
     * @return
     */
    public static WindowListener createInnerDeactivatedEventHandler(Frame<?> frame, EventHandler<FrameEvent> eventHandler) {
        return eventHandler.createInnerInstance(frame, new InnerInstanceCreator<>() {

            @Override
            public WindowListener create() {
                return new WindowAdapter() {
                    
                    @Override
                    public void windowDeactivated(java.awt.event.WindowEvent event) {
                        eventHandler.executeWhenControlEnabled(new FrameEvent(frame, event));
                    }
                };
            }
        });
    }
}
