package com.hirohiro716.scent.gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.SwingConstants;

import com.hirohiro716.scent.IdentifiableEnum;
import com.hirohiro716.scent.gui.GUI;
import com.hirohiro716.scent.gui.HorizontalAlignment;
import com.hirohiro716.scent.gui.KeyCode;
import com.hirohiro716.scent.gui.VerticalAlignment;

/**
 * コンテキストメニューアイテムのクラス。
 */
public class ContextMenuItem extends LabeledControl {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected ContextMenuItem(JMenuItem innerInstance) {
        super(innerInstance, innerInstance.getText());
        this.setPadding(this.getFont().getSize() / 2);
    }
    
    /**
     * コンストラクタ。<br>
     * このコンテキストメニューアイテムに表示するテキストを指定する。
     * 
     * @param text
     */
    public ContextMenuItem(String text) {
        this(new JMenuItem(text));
    }
    
    @Override
    public JMenuItem getInnerInstance() {
        return (JMenuItem) super.getInnerInstance();
    }
    
    @Override
    public void setTextToInnerInstance(String text) {
        this.getInnerInstance().setText(text);
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
     * このコンテキストメニューアイテムの処理をセットする。
     * 
     * @param runnable
     */
    public void setAction(Runnable runnable) {
        this.getInnerInstance().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI.executeLater(runnable);
            }
        });
    }
}
