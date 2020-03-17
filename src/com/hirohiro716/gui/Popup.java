package com.hirohiro716.gui;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JPanel;
import javax.swing.JWindow;

import com.hirohiro716.gui.collection.Collection;
import com.hirohiro716.gui.control.Control;
import com.hirohiro716.gui.control.Pane;

/**
 * GUIでポップアップを表示するクラス。
 * 
 * @author hiro
 *
 */
public class Popup extends Component<JWindow> {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected Popup(JWindow innerInstance) {
        super(innerInstance, innerInstance);
        this.pane = new Pane();
        this.pane.setInnerInstance((JPanel) this.getInnerInstance().getContentPane());
        this.getInnerInstance().addWindowListener(this.windowListener);
    }
    
    /**
     * コンストラクタ。<br>
     * このポップアップのオーナーを指定する。
     * 
     * @param owner
     */
    public Popup(Frame<?> owner) {
        this(new JWindow(owner.getInnerInstance()));
        this.owner = owner;
    }
    
    private Frame<?> owner;
    
    /**
     * このダイアログのオーナーを取得する。
     * 
     * @return 結果。
     */
    public Frame<?>  getOwner() {
        return this.owner;
    }
    
    private Pane pane;
    
    /**
     * このポップアップの子要素を取得する。
     * 
     * @return 結果。
     */
    public Collection<Control> getChildren() {
        return this.pane.getChildren();
    }
    
    /**
     * このポップアップを表示する。
     */
    public void show() {
        this.getInnerInstance().setVisible(true);
    }
    
    /**
     * このポップアップを非表示にする。
     */
    public void hide() {
        this.getInnerInstance().setVisible(false);
    }
    
    /**
     * このポップアップを閉じる。
     */
    public void close() {
        this.getInnerInstance().dispose();
    }
    
    @Override
    public Color getBackgroundColor() {
        return this.getInnerInstance().getBackground();
    }
    
    @Override
    public void setBackgroundColor(Color color) {
        this.getInnerInstance().setBackground(color);
    }
    
    private WindowListener windowListener = new WindowListener() {
        
        @Override
        public void windowOpened(WindowEvent event) {
        }
        
        @Override
        public void windowIconified(WindowEvent event) {
        }
        
        @Override
        public void windowDeiconified(WindowEvent event) {
        }
        
        @Override
        public void windowDeactivated(WindowEvent event) {
        }
        
        @Override
        public void windowClosing(WindowEvent event) {
        }
        
        @Override
        public void windowClosed(WindowEvent event) {
            Popup.this.getInnerInstance().dispose();
        }
        
        @Override
        public void windowActivated(WindowEvent event) {
        }
    };
}
