package com.hirohiro716.graphics;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.hirohiro716.StringObject;

/**
 * 文字列を描画するクラス。
 * 
 * @author hiro
 *
 */
public class GraphicalString {
    
    /**
     * コンストラクタ。<br>
     * 描画する文字列、使用するGraphics2Dインスタンスを指定する。
     * 
     * @param string 
     * @param graphics2D
     */
    public GraphicalString(String string, Graphics2D graphics2D) {
        if (graphics2D == null) {
            if (GRAPHICS == null) {
                BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                GRAPHICS = (Graphics2D) image.getGraphics();
            }
            this.graphics2D = GRAPHICS;
        } else {
            this.graphics2D = graphics2D;
        }
        this.string = new StringObject(string);
        this.string.replaceCRLF("\n").replaceCR("\n");
    }
    
    /**
     * コンストラクタ。<br>
     * 描画する文字列を指定する。
     * 
     * @param string 
     */
    public GraphicalString(String string) {
        this(string, null);
    }

    private static Graphics2D GRAPHICS = null;
    
    private StringObject string;
    
    private Graphics2D graphics2D;

    private HorizontalPosition horizontalPosition = HorizontalPosition.LEFT;
    
    /**
     * 描画する水平方向の基準を設定する。
     * 
     * @param horizontalPosition
     */
    public void setHorizontalPosition(HorizontalPosition horizontalPosition) {
        this.horizontalPosition = horizontalPosition;
    }
    
    private VerticalPosition verticalPosition = VerticalPosition.BASELINE;
    
    /**
     * 描画する垂直方向の基準を設定する。
     * 
     * @param verticalPosition
     */
    public void setVerticalPosition(VerticalPosition verticalPosition) {
        this.verticalPosition = verticalPosition;
    }
    
    private Float maxWidth = null;
    
    /**
     * 文字列を描画する最大の幅を設定する。描画する文字列は最大の幅に応じて自動縮小される。
     * 
     * @param maxWidth
     */
    public void setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
    }
    
    /**
     * 文字列を描画する最大の幅を解除して無限にする。
     */
    public void clearMaxWidth() {
        this.maxWidth = null;
    }
    
    private Float maxHeight = null;
    
    /**
     * 文字列を描画する最大の高さを設定する。描画する文字列は最大の高さに応じて自動縮小される。
     * 
     * @param maxHeight
     */
    public void setMaxHeight(float maxHeight) {
        this.maxHeight = maxHeight;
    }
    
    /**
     * 文字列を描画する最大の高さを解除して無限に設定する。
     */
    public void clearMaxHeight() {
        this.maxHeight = null;
    }
    
    private Float leading = null;
    
    /**
     * 行と行との間隔を取得する。
     * 
     * @return 結果。
     */
    public Float getLeading() {
        float leading = this.graphics2D.getFontMetrics().getLeading();
        if (this.leading != null) {
            leading = this.leading;
        }
        return leading;
    }
    
    /**
     * 行と行との間隔を設定する。
     * 
     * @param leading
     */
    public void setLeading(float leading) {
        this.leading = leading;
    }
    
    /**
     * 行と行との間隔をフォントに基づく初期設定にする。
     */
    public void setDefaultLeading() {
        this.leading = null;
    }
    
    /**
     * 文字列を描画するレイアウトを作成する。
     * 
     * @return 結果。
     */
    private Layout createLayout() {
        List<String> lines = new ArrayList<>();
        Font font = this.graphics2D.getFont();
        Layout layout = null;
        while (layout == null) {
            FontMetrics fontMetrics = this.graphics2D.getFontMetrics();
            StringObject stringObject = new StringObject();
            for (int index = 0; index < this.string.length(); index++) {
                StringObject one = this.string.clone().extract(index, index + 1);
                Rectangle2D rectangle = fontMetrics.getStringBounds(stringObject.clone().append(one).toString(), this.graphics2D);
                if (this.maxWidth != null && this.maxWidth < rectangle.getWidth() || one.equals("\n")) {
                    lines.add(stringObject.toString());
                    stringObject = one;
                } else {
                    stringObject.append(one);
                }
            }
            lines.add(stringObject.toString());
            double width = 0;
            double height = 0;
            for (String line : lines) {
                Rectangle2D rectangle = fontMetrics.getStringBounds(line, this.graphics2D);
                if (width < rectangle.getWidth()) {
                    width = rectangle.getWidth();
                }
                if (height > 0) {
                    height += this.getLeading();
                }
                height += fontMetrics.getHeight() - fontMetrics.getLeading(); 
            }
            if (this.maxWidth == null || this.maxWidth >= width) {
                if (this.maxHeight == null || this.maxHeight >= height) {
                    layout = new Layout(lines.toArray(new String[] {}), font, width, height);
                    break;
                }
            }
            lines.clear();
            font = font.deriveFont(font.getSize2D() - 0.5f);
            this.graphics2D.setFont(font);
        }
        this.lastAutomaticallyAdjustedFont = layout.getFont();
        return layout;
    }

    private Font lastAutomaticallyAdjustedFont = null;
    
    /**
     * 最後に自動調整されたフォントを取得する。
     * 
     * @return 結果。
     */
    public Font getLastAutomaticallyAdjustedFont() {
        return this.lastAutomaticallyAdjustedFont;
    }
    
    /**
     * 文字列のサイズを取得する。
     * 
     * @return 結果。
     */
    public Dimension createDimension() {
        Font font = this.graphics2D.getFont();
        Layout layout = this.createLayout();
        Dimension dimension = new Dimension();
        dimension.setSize(layout.getWidth(), layout.getHeight());
        this.graphics2D.setFont(font);
        return dimension;
    }

    /**
     * 一行を描画する。
     * 
     * @param oneLine
     * @param x
     * @param y
     * @return 描画した文字列のサイズ。
     */
    private Dimension drawOneLine(String oneLine, float x, float y) {
        FontMetrics fontMetrics = this.graphics2D.getFontMetrics();
        Rectangle2D rectangle = fontMetrics.getStringBounds(oneLine, this.graphics2D);
        switch (this.horizontalPosition) {
        case LEFT:
            this.graphics2D.drawString(oneLine, x, y);
            break;
        case CENTER:
            this.graphics2D.drawString(oneLine, x - ((float) rectangle.getWidth() / 2), y);
            break;
        case RIGHT:
            this.graphics2D.drawString(oneLine, x - (float) rectangle.getWidth(), y);
            break;
        }
        Dimension dimension = new Dimension();
        dimension.setSize(rectangle.getWidth(), fontMetrics.getAscent() + fontMetrics.getDescent() + this.getLeading());
        return dimension;
    }
    
    /**
     * 指定された位置に文字列を描画する。
     * 
     * @param x
     * @param y
     * @return 描画した文字列のサイズ。
     */
    public Dimension draw(float x, float y) {
        Font defaultFont = this.graphics2D.getFont();
        Layout layout = this.createLayout();
        FontMetrics fontMetrics = this.graphics2D.getFontMetrics();
        float drawingY = y;
        float drawingX = x;
        switch (this.verticalPosition) {
        case TOP:
            drawingY += fontMetrics.getAscent();
            for (String line : layout.getLines()) {
                Dimension dimension = this.drawOneLine(line, drawingX, drawingY);
                drawingY += dimension.getHeight();
            }
            break;
        case CENTER:
            drawingY += fontMetrics.getAscent();
            drawingY -= layout.getHeight()  / 2;
            for (String line : layout.getLines()) {
                Dimension dimension = this.drawOneLine(line, drawingX, drawingY);
                drawingY += dimension.getHeight();
            }
            break;
        case BASELINE:
            drawingY += fontMetrics.getAscent() + fontMetrics.getDescent();
            drawingY -= layout.getHeight();
            for (String line : layout.getLines()) {
                Dimension dimension = this.drawOneLine(line, drawingX, drawingY);
                drawingY += dimension.getHeight();
            }
            break;
        case BOTTOM:
            drawingY += fontMetrics.getAscent();
            drawingY -= layout.getHeight();
            for (String line : layout.getLines()) {
                Dimension dimension = this.drawOneLine(line, drawingX, drawingY);
                drawingY += dimension.getHeight();
            }
            break;
        }
        Dimension dimension = new Dimension();
        dimension.setSize(layout.getWidth(), layout.getHeight());
        this.graphics2D.setFont(defaultFont);
        return dimension;
    }
    
    /**
     * 指定された位置のボックスの中に文字列を描画する。
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     * @return 描画した文字列のサイズ。
     */
    public Dimension drawInBox(float x, float y, float width, float height) {
        Float defaultMaxWidth = this.maxWidth;
        Float defaultMaxHeight = this.maxHeight;
        float fixX = x;
        switch (this.horizontalPosition) {
        case LEFT:
            break;
        case CENTER:
            fixX += width / 2;
            break;
        case RIGHT:
            fixX += width;
            break;
        }
        float fixY = y;
        switch (this.verticalPosition) {
        case TOP:
            break;
        case CENTER:
            fixY += height / 2;
            break;
        case BASELINE:
            fixY += height;
            break;
        case BOTTOM:
            fixY += height;
            break;
        }
        this.setMaxWidth(width);
        this.setMaxHeight(height);
        Dimension dimension = this.draw(fixX, fixY);
        this.maxWidth = defaultMaxWidth;
        this.maxHeight = defaultMaxHeight;
        return dimension;
    }

    /**
     * 文字列を描画する垂直方向基準の列挙型。
     * 
     * @author hiro
     *
     */
    public enum VerticalPosition {
        /**
         * 文字列の上端を基準とする。
         */
        TOP,
        /**
         * 文字列の上とベースラインの真ん中を基準とする。
         */
        CENTER,
        /**
         * 文字列のベースラインを基準とする。
         */
        BASELINE,
        /**
         * 文字列の下端を基準とする。
         */
        BOTTOM,
    }
    
    /**
     * 文字列を描画する水平方向基準の列挙型。
     * 
     * @author hiro
     *
     */
    public enum HorizontalPosition {
        /**
         * 文字列の左端を基準とする。
         */
        LEFT,
        /**
         * 文字列の中央を基準とする。
         */
        CENTER,
        /**
         * 文字列の右端を基準とする。
         */
        RIGHT,
    }
    
    /**
     * 文字列を描画するためのレイアウトクラス。
     * 
     * @author hiro
     *
     */
    private class Layout {
        
        /**
         * コンストラクタ。
         * 
         * @param lines 描画するすべての行。
         * @param font 描画に使用するフォント。
         * @param width 描画するすべての行の最大の幅。
         * @param height 描画するすべての行の高さ。
         */
        public Layout(String[] lines, Font font, double width, double height) {
            this.lines = lines;
            this.width = width;
            this.height = height;
            this.font = font;
        }
        
        private String[] lines;
        
        /**
         * 描画するすべての行を取得する。
         * 
         * @return 結果。
         */
        public String[] getLines() {
            return this.lines;
        }

        private Font font;
        
        /**
         * 描画に使用するフォントを取得する。
         * 
         * @return 結果。
         */
        public Font getFont() {
            return this.font;
        }
        
        private double width;
        
        /**
         * 描画するすべての行の最大の幅を取得する。
         * 
         * @return 結果。
         */
        public double getWidth() {
            return this.width;
        }
        
        private double height;
        
        /**
         * 描画するすべての行の高さを取得する。
         * 
         * @return 結果。
         */
        public double getHeight() {
            return this.height;
        }
    }
}
