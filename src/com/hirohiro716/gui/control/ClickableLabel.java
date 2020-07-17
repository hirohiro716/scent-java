package com.hirohiro716.gui.control;

import java.awt.Color;
import java.awt.Dimension;

import com.hirohiro716.gui.Border;
import com.hirohiro716.gui.Component;
import com.hirohiro716.gui.GUI;
import com.hirohiro716.gui.event.ChangeListener;
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
        ClickableLabel label = this;
        this.addSizeChangeListener(new ChangeListener<Dimension>() {
            
            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension previousValue) {
                label.setStyleColor(label.defaultForegroundColor);
            }
        });
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
                                    Color color = GUI.createAlphaColor(label.defaultForegroundColor, opacityForLater);
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
                                    Color color = GUI.createAlphaColor(label.defaultForegroundColor, opacityForLater);
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
                Color color = GUI.createAlphaColor(label.defaultForegroundColor, 0.2);
                label.setStyleColor(color);
            }
        });
        this.addMouseReleasedEventHandler(MouseButton.BUTTON1, new EventHandler<MouseEvent>() {

            @Override
            protected void handle(MouseEvent event) {
                Color color = GUI.createAlphaColor(label.defaultForegroundColor, 1);
                label.setStyleColor(color);
            }
        });
        this.setPadding(0);
    }
    
    private Color defaultForegroundColor = this.getForegroundColor();
    
    private boolean isMouseExited = true;
    
    private boolean isDisabledUnderline = false;
    
    /**
     * 指定された色をテキストと下線に設定する。
     * 
     * @param color
     */
    private void setStyleColor(Color color) {
        super.setForegroundColor(color);
        if (this.isDisabledUnderline == false) {
            this.setBorder(Border.createLine(color, 0, 0, 1, 0));
        } else {
            this.setBorder(null);
        }
    }
    
    /**
     * このラベルの下線が無効になっている場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isDisabledUnderline() {
        return this.isDisabledUnderline;
    }
    
    /**
     * このラベルの下線を無効にする場合はtrueをセットする。
     * 
     * @param isDisabledUnderline
     */
    public void setDisabledUnderline(boolean isDisabledUnderline) {
        this.isDisabledUnderline = isDisabledUnderline;
    }
    
    @Override
    public void setVisible(boolean isVisible) {
        super.setVisible(isVisible);
        this.setStyleColor(this.defaultForegroundColor);
    }

    @Override
    public void setDisabled(boolean isDisabled) {
        super.setDisabled(isDisabled);
        this.setStyleColor(this.defaultForegroundColor);
    }

    @Override
    public void setForegroundColor(Color color) {
        super.setForegroundColor(color);
        this.defaultForegroundColor = color;
    }
}
