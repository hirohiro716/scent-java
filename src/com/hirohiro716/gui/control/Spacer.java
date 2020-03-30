package com.hirohiro716.gui.control;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.hirohiro716.gui.event.ChangeListener;

/**
 * スペーサーのクラス。
 * 
 * @author hiro
 *
 */
public class Spacer extends Control {
    
    /**
     * コンストラクタ。<br>
     * このコントロールがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected Spacer(JComponent innerInstance) {
        super(innerInstance);
    }
    
    /**
     * コンストラクタ。<br>
     * 指定されたサイズでインスタンスを生成する。
     * 
     * @param width
     * @param height
     */
    public Spacer(int width, int height) {
        this(new JPanel());
        this.setFixedSize(width, height);
    }

    @Override
    protected ChangeListener<Dimension> createBugFixChangeListener() {
        return null;
    }
    
    @Override
    public Color getBackgroundColor() {
        return this.getInnerInstance().getBackground();
    }

    @Override
    public void setBackgroundColor(Color color) {
        this.getInnerInstance().setBackground(color);
    }
}
