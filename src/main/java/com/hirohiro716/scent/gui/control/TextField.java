package com.hirohiro716.scent.gui.control;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.hirohiro716.scent.gui.HorizontalAlignment;
import com.hirohiro716.scent.gui.event.ActionEvent;
import com.hirohiro716.scent.gui.event.EventHandler;
import com.hirohiro716.scent.gui.event.InnerInstanceCreator;

/**
 * テキストフィールドのクラス。
 * 
 * @author hiro
 *
 */
public class TextField extends TextInputControl {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected TextField(JTextField innerInstance) {
        super(innerInstance);
    }
    
    /**
     * コンストラクタ。<br>
     * このテキストフィールドの初期値を指定する。
     * 
     * @param text
     */
    public TextField(String text) {
        this(new JTextField());
        this.setText(text);
    }
    
    /**
     * コンストラクタ。
     */
    public TextField() {
        this((String) null);
    }
    
    @Override
    public JTextField getInnerInstance() {
        return (JTextField) super.getInnerInstance();
    }
    
    @Override
    public HorizontalAlignment getTextHorizontalAlignment() {
        switch (this.getInnerInstance().getHorizontalAlignment()) {
        case SwingConstants.LEFT:
            return HorizontalAlignment.LEFT;
        case SwingConstants.CENTER:
            return HorizontalAlignment.CENTER;
        case SwingConstants.RIGHT:
            return HorizontalAlignment.RIGHT;
        }
        return null;
    }
    
    @Override
    public void setTextHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        switch (horizontalAlignment) {
        case LEFT:
            this.getInnerInstance().setHorizontalAlignment(SwingConstants.LEFT);
            break;
        case CENTER:
            this.getInnerInstance().setHorizontalAlignment(SwingConstants.CENTER);
            break;
        case RIGHT:
            this.getInnerInstance().setHorizontalAlignment(SwingConstants.RIGHT);
            break;
        }
    }
    
    /**
     * このテキストフィールドでEnterキーが押された際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addActionEventHandler(EventHandler<ActionEvent> eventHandler) {
        TextField textField = this;
        KeyListener innerInstance = eventHandler.createInnerInstance(textField, new InnerInstanceCreator<>() {

            @Override
            public KeyListener create() {
                return new KeyAdapter() {
                    
                    private boolean isPressed = false;
                    
                    @Override
                    public void keyPressed(KeyEvent event) {
                        if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                            this.isPressed = true;
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent event) {
                        if (event.getKeyCode() == KeyEvent.VK_ENTER && this.isPressed) {
                            eventHandler.executeWhenControlEnabled(new ActionEvent(textField, event));
                        }
                        this.isPressed = false;
                    }
                };
            }
        });
        this.getInnerInstance().addKeyListener(innerInstance);
    }
    
    @Override
    public void removeEventHandler(EventHandler<?> eventHandler) {
        super.removeEventHandler(eventHandler);
        for (Object innerInstance : eventHandler.getInnerInstances(this)) {
            if (innerInstance instanceof KeyListener) {
                this.getInnerInstance().removeKeyListener((KeyListener) innerInstance);
            }
        }
    }
}
