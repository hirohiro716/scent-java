package com.hirohiro716.scent.gui.control;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

/**
 * キャンバスの抽象クラス。
 */
public abstract class Canvas extends Control {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected Canvas(CanvasPanel innerInstance) {
        super(innerInstance);
        this.setBackgroundColor(null);
    }
    
    /**
     * コンストラクタ。
     */
    public Canvas() {
        this(new CanvasPanel());
        this.getInnerInstance().setCanvas(this);
    }
    
    @Override
    public CanvasPanel getInnerInstance() {
        return (CanvasPanel) super.getInnerInstance();
    }
    
    /**
     * 指定されたキャンバスのグラフィクスに対して描画を行う。
     * 
     * @param graphics2D
     */
    protected abstract void paint(Graphics2D graphics2D);
    
    /**
     * キャンバスの描画内容を表示するJPanelクラス。
     */
    private static class CanvasPanel extends JPanel {
        
        private Canvas canvas;
        
        /**
         * キャンバスのインスタンスをセットする。
         * 
         * @param canvas
         */
        public void setCanvas(Canvas canvas) {
            this.canvas = canvas;
        }
        
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D graphics2D = (Graphics2D) graphics;
            graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            this.canvas.paint(graphics2D);
        }
    }
}
