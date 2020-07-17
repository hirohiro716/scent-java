package com.hirohiro716.graphic.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;

/**
 * 境界線のクラス。
 * 
 * @author hiro
 *
 */
public class Border {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    private Border(javax.swing.border.Border innerInstance) {
        this.innerInstance = innerInstance;
    }

    private javax.swing.border.Border innerInstance;
    
    /**
     * 内部で使用されるGUIライブラリに依存したインスタンスを取得する。
     * 
     * @return 結果。
     */
    public javax.swing.border.Border getInnerInstance() {
       return this.innerInstance;
    }

    /**
     * 内部で使用されるGUIライブラリに依存したインスタンスを指定して境界線を作成する。
     * 
     * @param innerInstance
     * @return 結果。
     */
    public static Border create(javax.swing.border.Border innerInstance) {
        return new Border(innerInstance);
    }

    /**
     * 色と太さを指定して実線の境界線を作成する。
     * 
     * @param color
     * @param top 
     * @param right 
     * @param bottom 
     * @param left 
     * @return 結果。
     */
    public static Border createLine(Color color, int top, int right, int bottom, int left) {
        return new Border(new MatteBorder(new Insets(top, left, bottom, right), color));
    }

    /**
     * 色と太さを指定して実線の境界線を作成する。
     * 
     * @param color
     * @param topAndBottom 
     * @param rightAndLeft 
     * @return 結果。
     */
    public static Border createLine(Color color, int topAndBottom, int rightAndLeft) {
        return Border.createLine(color, topAndBottom, rightAndLeft, topAndBottom, rightAndLeft);
    }
    
    /**
     * 色と太さを指定して実線の境界線を作成する。
     * 
     * @param color
     * @param width
     * @return 結果。
     */
    public static Border createLine(Color color, int width) {
        return Border.createLine(color, width, width);
    }
    
    /**
     * 沈み彫り(エンボス)加工の境界線を作成する。
     * 
     * @return 結果。
     */
    public static Border createEmboss() {
        return new Border(new EtchedBorder(EtchedBorder.LOWERED));
    }
    
    /**
     * 浮き彫り(デボス)加工の境界線を作成する。
     * 
     * @return 結果。
     */
    public static Border createDeboss() {
        return new Border(new EtchedBorder(EtchedBorder.RAISED));
    }
    
    /**
     * 色と太さを指定して、影風の境界線を作成する。
     * 
     * @param color
     * @param width
     * @return 結果。
     */
    public static Border createShadow(Color color, int width) {
        return new Border(new ShadowBorder(color, width));
    }
    
    /**
     * 影の境界線クラス。
     * 
     * @author hiro
     *
     */
    @SuppressWarnings("serial")
    private static class ShadowBorder extends AbstractBorder {
        
        /**
         * コンストラクタ。<br>
         * 影の色と幅を指定する。
         * 
         * @param color
         * @param width
         */
        private ShadowBorder(Color color, int width) {
            this.color = color;
            this.width = width;
        }
        
        private Color color;
        
        private int width;
        
        @Override
        public Insets getBorderInsets(Component component) {
            if (component instanceof JComponent && component.isOpaque()) {
                JComponent jComponent = (JComponent) component;
                jComponent.setOpaque(false);
                jComponent.repaint();
            }
            return new Insets(this.width, this.width, this.width, this.width);
        }
        
        /**
         * 階層に合わせた色を作成する。
         * 
         * @param current
         * @param maximum
         * @return 結果。
         */
        private Color createColor(double current, double maximum) {
            double ratio = current / maximum;
            int alpha = this.color.getAlpha() - (int) (this.color.getAlpha() * ratio);
            return new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), alpha);
        }
        
        @Override
        public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
            Graphics2D graphics2d = (Graphics2D) graphics;
            double numberOfBorders = 20;
            double part = this.width / numberOfBorders;
            for (int number = 1; number <= numberOfBorders; number++) {
                graphics.setColor(this.createColor(number, numberOfBorders));
                double offset = part * number;
                double drawX = x + this.width - offset;
                double drawY = y + this.width - offset;
                double drawWidth = width - this.width * 2 + offset * 2;
                double drawHeight = height - this.width * 2 + offset * 2;
                RoundRectangle2D.Double rectangle = new RoundRectangle2D.Double(drawX, drawY, drawWidth, drawHeight, offset * 3, offset * 3);
                graphics2d.fill(rectangle);
            }
            Color backgroundColor = component.getBackground();
            graphics.setColor(backgroundColor);
            graphics.fillRect(x + this.width, y + this.width, width - this.width * 2, height - this.width * 2);
        }
    }
}
