package com.hirohiro716.scent.gui.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

import com.hirohiro716.scent.graphic.ColorCreator;
import com.hirohiro716.scent.gui.GUI;
import com.hirohiro716.scent.gui.HorizontalAlignment;

/**
 * チェックボックスのクラス。
 * 
 * @author hiro
*/
public class CheckBox extends MarkableControl {

    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected CheckBox(JCheckBox innerInstance) {
        super(innerInstance);
        this.setTextHorizontalAlignment(HorizontalAlignment.CENTER);
    }
    
    /**
     * このメソッドはコンストラクタの呼び出しと同じで、新しいインスタンスを作成する。
     * 
     * @param innerInstance GUIライブラリに依存したインスタンス。
     * @return 新しいインスタンス。
     */
    public static CheckBox newInstance(JCheckBox innerInstance) {
        return new CheckBox(innerInstance);
    }
    
    /**
     * コンストラクタ。<br>
     * このチェックボックスに表示するテキストを指定する。
     * 
     * @param text
     */
    public CheckBox(String text) {
        this(new JCheckBox(text));
    }
    
    /**
     * コンストラクタ。
     */
    public CheckBox() {
        this(new JCheckBox((String) null));
    }
    
    @Override
    public JCheckBox getInnerInstance() {
        return (JCheckBox) super.getInnerInstance();
    }

    /**
     * フォントサイズによってチェックマークのサイズ伸縮を有効にする場合はtrueをセットする。
     * 
     * @param isScalableMarkEnabled
     */
    public void setScalableMarkEnabled(boolean isScalableMarkEnabled) {
        if (isScalableMarkEnabled) {
            this.getInnerInstance().setIcon(new CheckBoxIcon(this));
        } else {
            this.getInnerInstance().setIcon(null);
        }
    }

    /**
     * フォントサイズによるチェックマークのサイズ伸縮が有効になっている場合はtrueを返す。
     * 
     * @return
     */
    public boolean isScalableMarkEnabled() {
        return this.getInnerInstance().getIcon() != null;
    }

    /**
     * 状態毎の色を取得するため。
     */
    private static JTextField textField = new JTextField();

    /**
     * チェックボックスアイコンのクラス。
     */
    private class CheckBoxIcon implements Icon {

        /**
         * コンストラクタ。<br>
         * チェックボックスのインスタンスを指定する。
         * 
         * @param checkBox
         */
        public CheckBoxIcon(CheckBox checkBox){
            this.checkBox = checkBox;
            this.size = (int) (checkBox.getFont().getSize() * 1.1);
        }

        private CheckBox checkBox;

        private int size;

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D graphics2d = (Graphics2D) graphics;
            graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2d.setFont(this.checkBox.getFont());
            ButtonModel buttonModel = ((AbstractButton) component).getModel();
            FontMetrics fontMetrics = graphics2d.getFontMetrics();
            Rectangle2D textRectangle = fontMetrics.getStringBounds(this.checkBox.getText(), graphics2d);
            int maximumLineWidth = 3;
            int offsetX = 0;
            switch (this.checkBox.getTextHorizontalAlignment()) {
                case LEFT:
                    offsetX = maximumLineWidth;
                    break;
                case CENTER:
                    offsetX = this.checkBox.getWidth() / 2;
                    offsetX -= textRectangle.getWidth() / 2;
                    offsetX -= maximumLineWidth / 2;
                    offsetX -= this.size / 2;
                    break;
                case RIGHT:
                    offsetX = this.checkBox.getWidth();
                    offsetX -= textRectangle.getWidth();
                    offsetX -= maximumLineWidth * 1.5;
                    offsetX -= this.size;
                    break;
            }
            int offsetY = 0;
            switch (this.checkBox.getTextVerticalAlignment()) {
                case TOP:
                    offsetY = fontMetrics.getLeading();
                    offsetY += maximumLineWidth;
                    break;
                case CENTER:
                    offsetY = this.checkBox.getHeight() / 2;
                    offsetY -= fontMetrics.getLeading() / 2;
                    offsetY -= fontMetrics.getAscent() / 2;
                    break;
                case BOTTOM:
                    offsetY = this.checkBox.getHeight();
                    offsetY -= fontMetrics.getHeight();
                    offsetY += fontMetrics.getLeading();
                    break;
            }
            graphics2d.setColor(CheckBox.textField.getBackground());
            if (buttonModel.isEnabled()) {
                if (buttonModel.isPressed()) {
                    graphics2d.setColor(ColorCreator.createTransparent(CheckBox.textField.getSelectionColor(), 0.1));
                }
            }
            graphics2d.fillRect(offsetX, offsetY, this.size, this.size);
            if (buttonModel.isRollover()) {
                graphics2d.setColor(ColorCreator.createTransparent(Color.LIGHT_GRAY, 0.08));
                graphics2d.fillRect(offsetX, offsetY, this.size, this.size);
            }
            if (checkBox.isFocused()) {
                graphics2d.setColor(ColorCreator.createTransparent(CheckBox.textField.getSelectionColor(), 0.45));
                graphics2d.setStroke(new BasicStroke(maximumLineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                graphics2d.drawRect(offsetX, offsetY, this.size, this.size);
            } else {
                graphics2d.setColor(ColorCreator.createTransparent(GUI.getBorderColor(), 0.6));
                graphics2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                graphics2d.drawRect(offsetX, offsetY, this.size, this.size);
            }
            if (buttonModel.isSelected()) {
                graphics2d.setStroke(new BasicStroke((int) (this.size * 0.15), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                if (buttonModel.isEnabled()) {
                    graphics2d.setColor(CheckBox.textField.getSelectionColor());
                } else {
                    graphics2d.setColor(CheckBox.textField.getDisabledTextColor());
                }
                graphics2d.drawLine((int) (offsetX + this.size * 0.25), (int) (offsetY + this.size * 0.5), (int) (offsetX + this.size * 0.35), (int) (offsetY + this.size * 0.8));
                graphics2d.drawLine((int) (offsetX + this.size * 0.35), (int) (offsetY + this.size * 0.8), (int) (offsetX + this.size * 0.8), (int) (offsetY + this.size * 0.3));
            }
        }

        @Override
        public int getIconWidth() {
            return this.size;
        }

        @Override
        public int getIconHeight() {
            return this.size;
        }
    }
}
