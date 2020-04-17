package com.hirohiro716.gui.control;

import java.awt.Color;
import com.hirohiro716.gui.Border;
import com.hirohiro716.gui.GUI;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.MouseEvent;
import com.hirohiro716.gui.event.MouseEvent.MouseButton;

/**
 * クリックできるラベルのクラス。
 * 
 * @author hiro
 *
 */
public class ClickableLabel extends Button {
    
    /**
     * コンストラクタ。<br>
     * このラベルに表示するテキストを指定する。
     * 
     * @param text
     */
    public ClickableLabel(String text) {
        super(text);
        this.getInnerInstance().setContentAreaFilled(false);
        Color defaultForegroundColor = this.getForegroundColor();
        this.setStyleColor(defaultForegroundColor);
        ClickableLabel label = this;
        this.addMouseEnteredEventHandler(new EventHandler<MouseEvent>() {
            
            @Override
            protected void handle(MouseEvent event) {
                label.isMouseExited = false;
                Thread thread = new Thread(new Runnable() {
                    
                    @Override
                    public void run() {
                        for (double opacity = 1; opacity > 0.4; opacity -= 0.05) {
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException exception) {
                            }
                            if (label.isMouseExited) {
                                break;
                            }
                            final double opacityForLater = opacity;
                            GUI.executeLater(new Runnable() {
                                
                                @Override
                                public void run() {
                                    Color color = GUI.createAlphaColor(defaultForegroundColor, opacityForLater);
                                    label.setStyleColor(color);
                                }
                            });
                        }
                    }
                });
                thread.start();
            }
        });
        this.addMouseExitedEventHandler(new EventHandler<MouseEvent>() {

            @Override
            protected void handle(MouseEvent event) {
                label.isMouseExited = true;
                Thread thread = new Thread(new Runnable() {
                    
                    @Override
                    public void run() {
                        for (double opacity = 0.4; opacity < 1; opacity += 0.05) {
                            try {
                                Thread.sleep(40);
                            } catch (InterruptedException exception) {
                            }
                            if (label.isMouseExited == false) {
                                break;
                            }
                            final double opacityForLater = opacity;
                            GUI.executeLater(new Runnable() {
                                
                                @Override
                                public void run() {
                                    Color color = GUI.createAlphaColor(defaultForegroundColor, opacityForLater);
                                    label.setStyleColor(color);
                                }
                            });
                        }
                    }
                });
                thread.start();
            }
        });
        this.addMousePressedEventHandler(MouseButton.BUTTON1, new EventHandler<MouseEvent>() {

            @Override
            protected void handle(MouseEvent event) {
                Color color = GUI.createAlphaColor(defaultForegroundColor, 0.2);
                label.setStyleColor(color);
            }
        });
        this.addMouseReleasedEventHandler(MouseButton.BUTTON1, new EventHandler<MouseEvent>() {

            @Override
            protected void handle(MouseEvent event) {
                Color color = GUI.createAlphaColor(defaultForegroundColor, 1);
                label.setStyleColor(color);
            }
        });
    }
    
    private boolean isMouseExited = true;
    
    /**
     * 指定された色をテキストと下線に設定する。
     * 
     * @param color
     */
    private void setStyleColor(Color color) {
        this.setForegroundColor(color);
        this.setBorder(Border.createLine(color, 0, 0, 1, 0));
    }
}
