package com.hirohiro716.gui.control;

import java.awt.Dimension;

import javax.swing.JProgressBar;

import com.hirohiro716.gui.event.ChangeListener;

/**
 * プログレスバーのクラス。
 * 
 * @author hiro
 *
 */
public class ProgressBar extends Control {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected ProgressBar(JProgressBar innerInstance) {
        super(innerInstance);
    }
    
    /**
     * コンストラクタ。<br>
     * プログレスバーの最大進捗と最小進捗を指定する。
     * 
     * @param minimumValue
     * @param maximumValue
     */
    public ProgressBar(int minimumValue, int maximumValue) {
        this(new JProgressBar(minimumValue, maximumValue));
    }
    
    /**
     * コンストラクタ。
     */
    public ProgressBar() {
        this(new JProgressBar());
    }
    
    @Override
    public JProgressBar getInnerInstance() {
        return (JProgressBar) super.getInnerInstance();
    }

    @Override
    protected ChangeListener<Dimension> createBugFixChangeListener() {
        return null;
    }
    
    /**
     * このプログレスバーの最小進捗を取得する。
     * 
     * @return 結果。
     */
    public int getMinimumProgress() {
        return this.getInnerInstance().getMinimum();
    }
    
    /**
     * このプログレスバーの最小進捗をセットする。
     * 
     * @param progress
     */
    public void setMinimumProgress(int progress) {
        this.getInnerInstance().setMinimum(progress);
    }
    
    /**
     * このプログレスバーの最大進捗を取得する。
     * 
     * @return 結果。
     */
    public int getMaximumProgress() {
        return this.getInnerInstance().getMaximum();
    }
    
    /**
     * このプログレスバーの最大進捗をセットする。
     * 
     * @param progress
     */
    public void setMaximumProgress(int progress) {
        this.getInnerInstance().setMaximum(progress);
    }
    
    /**
     * このプログレスバーの進捗をセットする。
     * 
     * @param progress
     */
    public void setProgress(int progress) {
        this.getInnerInstance().setValue(progress);
        this.getInnerInstance().repaint();
    }
}
