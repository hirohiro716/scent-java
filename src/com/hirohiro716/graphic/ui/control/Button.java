package com.hirohiro716.graphic.ui.control;

import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import com.hirohiro716.IdentifiableEnum;
import com.hirohiro716.graphic.ui.HorizontalAlignment;
import com.hirohiro716.graphic.ui.KeyCode;
import com.hirohiro716.graphic.ui.VerticalAlignment;
import com.hirohiro716.graphic.ui.event.ActionEvent;
import com.hirohiro716.graphic.ui.event.EventHandler;
import com.hirohiro716.graphic.ui.event.InnerInstanceCreator;
import com.hirohiro716.image.Image;

/**
 * ボタンのクラス。
 * 
 * @author hiro
 *
 */
public class Button extends LabeledControl {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected Button(JButton innerInstance) {
        super(innerInstance);
    }
    
    /**
     * コンストラクタ。<br>
     * このボタンに表示するテキストを指定する。
     * 
     * @param text
     */
    public Button(String text) {
        this(new JButton(text));
    }
    
    /**
     * コンストラクタ。<br>
     * このボタンに表示する画像を指定する。
     * 
     * @param image
     */
    public Button(Image image) {
        this(new JButton());
        this.setImage(image);
    }

    /**
     * コンストラクタ。
     */
    public Button() {
        this(new JButton());
    }
    
    @Override
    public JButton getInnerInstance() {
        return (JButton) super.getInnerInstance();
    }
    
    @Override
    public String getText() {
        return this.getInnerInstance().getText();
    }
    
    @Override
    public void setTextToInnerInstance(String text) {
        this.getInnerInstance().setText(text);
    }
    
    /**
     * このボタンに表示する画像をセットする。
     * 
     * @param image
     */
    public void setImage(Image image) {
        if (image != null) {
            this.setText("");
            this.getInnerInstance().setIcon(new ImageIcon(image.bytes()));
        } else {
            this.getInnerInstance().setIcon(null);
        }
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
    public VerticalAlignment getTextVerticalAlignment() {
        switch (this.getInnerInstance().getVerticalAlignment()) {
        case SwingConstants.TOP:
            return VerticalAlignment.TOP;
        case SwingConstants.CENTER:
            return VerticalAlignment.CENTER;
        case SwingConstants.BOTTOM:
            return VerticalAlignment.BOTTOM;
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
    
    @Override
    public void setTextVerticalAlignment(VerticalAlignment verticalAlignment) {
        switch (verticalAlignment) {
        case TOP:
            this.getInnerInstance().setVerticalAlignment(SwingConstants.TOP);
            break;
        case CENTER:
            this.getInnerInstance().setVerticalAlignment(SwingConstants.CENTER);
            break;
        case BOTTOM:
            this.getInnerInstance().setVerticalAlignment(SwingConstants.BOTTOM);
            break;
        }
    }
    
    @Override
    public KeyCode getMnemonic() {
        KeyCode keyCode = IdentifiableEnum.enumOf(this.getInnerInstance().getMnemonic(), KeyCode.class);
        return keyCode;
    }
    
    @Override
    public void setMnemonic(KeyCode keyCode) {
        if (keyCode == null) {
            return;
        }
        this.getInnerInstance().setMnemonic(keyCode.getKeyCodeAWT());
    }
    
    /**
     * このボタンが押された際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addActionEventHandler(EventHandler<ActionEvent> eventHandler) {
        Button button = this;
        ActionListener innerInstance = eventHandler.createInnerInstance(button, new InnerInstanceCreator<>() {

            @Override
            public ActionListener create() {
                return new ActionListener() {
                    
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent event) {
                        eventHandler.executeWhenControlEnabled(new ActionEvent(button, event));
                    }
                };
            }
        });
        this.getInnerInstance().addActionListener(innerInstance);
    }
    
    @Override
    public void removeEventHandler(EventHandler<?> eventHandler) {
        super.removeEventHandler(eventHandler);
        for (Object innerInstance : eventHandler.getInnerInstances(this)) {
            if (innerInstance instanceof ActionListener) {
                this.getInnerInstance().removeActionListener((ActionListener) innerInstance);
            }
        }
    }
}
