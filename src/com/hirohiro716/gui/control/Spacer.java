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
        Dimension dimension = new Dimension(width, height);
        this.getInnerInstance().setSize(dimension);
        this.getInnerInstance().setPreferredSize(dimension);
        this.getInnerInstance().setMinimumSize(dimension);
        this.getInnerInstance().setMaximumSize(dimension);
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
