package com.hirohiro716.graphics.print;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hirohiro716.graphics.GraphicalString;
import com.hirohiro716.graphics.NW7Writer;
import com.hirohiro716.gui.GUI;
import com.hirohiro716.graphics.GraphicalString.HorizontalPosition;
import com.hirohiro716.graphics.GraphicalString.VerticalPosition;
import com.hirohiro716.graphics.JAN13Writer;

/**
 * 印刷物の抽象クラス。
 * 
 * @author hiro
 *
 */
public abstract class Printable implements java.awt.print.Printable {
    
    private Graphics2D graphics2D = null;
    
    private PageFormat pageFormat = null;
    
    @Override
    public final int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        this.graphics2D = (Graphics2D) graphics;
        this.pageFormat = pageFormat;
        if (this.print(pageIndex)) {
            return PAGE_EXISTS;
        }
        this.graphics2D = null;
        this.pageFormat = null;
        return NO_SUCH_PAGE;
    }
    
    /**
     * 指定されたインデックスにあるページを印刷する処理。ページが存在しない場合はfalseを返す。
     * 
     * @param pageIndex
     * @return 指定されたページが存在しない場合はfalse、それ以外の場合はtrue。
     * @throws PrinterException
     */
    public abstract boolean print(int pageIndex) throws PrinterException;
    
    /**
     * 印刷対象のGraphics2Dインスタンスを取得する。印刷が開始されていない場合はnullを返す。
     * 
     * @return 結果。
     */
    protected Graphics2D getGraphics2D() {
        return this.graphics2D;
    }

    /**
     * 印刷時に指定されたページ形式を取得する。
     * 
     * @return 結果。
     */
    protected PageFormat getPageFormat() {
        return this.pageFormat;
    }

    /**
     * 印刷に使用する色を設定する。
     * 
     * @param color
     */
    protected void setColor(Color color) {
        this.graphics2D.setColor(color);
    }
    
    /**
     * 印刷に使用する色を、HTMLやCSSで使用する "#000000" や "#fff" のような形式で設定する。
     * 
     * @param webColor
     */
    protected void setWebColor(String webColor) {
        Color color = GUI.createColor(webColor);
        if (color.getAlpha() > 0) {
            this.graphics2D.setColor(color);
        }
    }
    
    /**
     * 印刷に使用するフォントを取得する。
     * 
     * @return 結果。
     */
    protected Font getFont() {
        return this.graphics2D.getFont();
    }

    /**
     * 印刷に使用するフォントを設定する。
     * 
     * @param font
     */
    protected void setFont(Font font) {
        this.graphics2D.setFont(font);
    }
    
    /**
     * 印刷に使用するフォントを設定する。
     * 
     * @param fontName
     * @param size
     * @param fontStyle
     */
    protected final void setFont(String fontName, float size, FontStyle fontStyle) {
        this.setFont(new Font(fontName, fontStyle.getValue(), (int) size));
    }
    
    /**
     * 印刷に使用するフォントを設定する。
     * 
     * @param fontName
     * @param size
     */
    protected final void setFont(String fontName, float size) {
        this.setFont(fontName, size, FontStyle.PLAIN);
    }
    
    /**
     * 印刷に使用するフォントのサイズを変更する。
     * 
     * @param size
     */
    protected final void setFontSize(float size) {
        this.setFont(GUI.createFont(this.getFont(), (int) size));
    }
    
    /**
     * 印刷に使用するフォントのスタイルを変更する。
     * 
     * @param fontStyle
     */
    protected final void setFontStyle(FontStyle fontStyle) {
        this.setFont(this.getFont().getFontName(), this.getFont().getSize(), fontStyle);
    }
    
    private Float leading = null;

    /**
     * 印刷する文字列の行と行との間隔を設定する。
     * 
     * @param millimeterLeading
     */
    protected void setLeading(float millimeterLeading) {
        this.leading = (float) MillimeterValue.newInstance(millimeterLeading).toPoint();
    }

    /**
     * 印刷する文字列の行と行との間隔をフォントに基づく初期設定にする。
     */
    protected void setDefaultLeading() {
        this.leading = null;
    }
    
    private HorizontalPosition horizontalPosition = HorizontalPosition.LEFT;
    
    /**
     * 文字列を印刷する際の水平方向の基準を設定する。
     * 
     * @param horizontalPosition
     */
    protected void setHorizontalPositionOfString(HorizontalPosition horizontalPosition) {
        this.horizontalPosition = horizontalPosition;
    }
    
    private VerticalPosition verticalPosition = VerticalPosition.BASELINE;
    
    /**
     * 文字列を印刷する際の垂直方向の基準を設定する。
     * 
     * @param verticalPosition
     */
    protected void setVerticalPositionOfString(VerticalPosition verticalPosition) {
        this.verticalPosition = verticalPosition;
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
     * 指定された文字列を印刷した場合の大きさを測定したDimensionを作成する。
     * 
     * @param string
     * @return 結果。
     */
    protected Dimension createStringDimension(String string) {
        GraphicalString graphicalString = new GraphicalString(string, this.graphics2D);
        if (this.leading != null) {
            graphicalString.setLeading(this.leading);
        }
        Dimension dimension = graphicalString.createDimension();
        this.lastAutomaticallyAdjustedFont = graphicalString.getLastAutomaticallyAdjustedFont();
        dimension.setSize(MillimeterValue.fromPoint(dimension.getWidth()).get(), MillimeterValue.fromPoint(dimension.getHeight()).get());
        return dimension;
    }
    
    /**
     * 指定された文字列を印刷した場合の大きさを測定したDimensionを作成する。
     * 
     * @param string
     * @param millimeterMaxWidth
     * @param millimeterMaxHeight
     * @return 結果。
     */
    protected Dimension createStringDimension(String string, float millimeterMaxWidth, float millimeterMaxHeight) {
        GraphicalString graphicalString = new GraphicalString(string, this.graphics2D);
        if (this.leading != null) {
            graphicalString.setLeading(this.leading);
        }
        float maxWidth = (float) MillimeterValue.newInstance(millimeterMaxWidth).toPoint();
        float maxHeight = (float) MillimeterValue.newInstance(millimeterMaxHeight).toPoint();
        graphicalString.setMaximumWidth(maxWidth);
        graphicalString.setMaximumHeight(maxHeight);
        Dimension dimension = graphicalString.createDimension();
        this.lastAutomaticallyAdjustedFont = graphicalString.getLastAutomaticallyAdjustedFont();
        dimension.setSize(MillimeterValue.fromPoint(dimension.getWidth()).get(), MillimeterValue.fromPoint(dimension.getHeight()).get());
        return dimension;
    }
    
    /**
     * 指定された文字列を指定された位置に印刷する。
     * 
     * @param string
     * @param millimeterX
     * @param millimeterY
     * @return 印刷された文字列の大きさ。
     */
    protected Dimension printString(String string, float millimeterX, float millimeterY) {
        GraphicalString graphicalString = new GraphicalString(string, this.graphics2D);
        if (this.leading != null) {
            graphicalString.setLeading(this.leading);
        }
        graphicalString.setHorizontalPosition(this.horizontalPosition);
        graphicalString.setVerticalPosition(this.verticalPosition);
        float x = (float) MillimeterValue.newInstance(millimeterX).toPoint();
        float y = (float) MillimeterValue.newInstance(millimeterY).toPoint();
        Dimension dimension = graphicalString.draw(x, y);
        this.lastAutomaticallyAdjustedFont = graphicalString.getLastAutomaticallyAdjustedFont();
        dimension.setSize(MillimeterValue.fromPoint(dimension.getWidth()).get(), MillimeterValue.fromPoint(dimension.getHeight()).get());
        return dimension;
    }
    
    /**
     * 指定された位置のボックスの中に文字列を印刷する。
     * 
     * @param string
     * @param millimeterX
     * @param millimeterY
     * @param millimeterWidth
     * @param millimeterHeight
     * @return 印刷された文字列の大きさ。
     */
    protected Dimension printStringInBox(String string, float millimeterX, float millimeterY, float millimeterWidth, float millimeterHeight) {
        GraphicalString graphicalString = new GraphicalString(string, this.graphics2D);
        if (this.leading != null) {
            graphicalString.setLeading(this.leading);
        }
        graphicalString.setHorizontalPosition(this.horizontalPosition);
        graphicalString.setVerticalPosition(this.verticalPosition);
        float width = (float) MillimeterValue.newInstance(millimeterWidth).toPoint();
        float height = (float) MillimeterValue.newInstance(millimeterHeight).toPoint();
        float x = (float) MillimeterValue.newInstance(millimeterX).toPoint();
        float y = (float) MillimeterValue.newInstance(millimeterY).toPoint();
        Dimension dimension = graphicalString.drawInBox(x, y, width, height);
        this.lastAutomaticallyAdjustedFont = graphicalString.getLastAutomaticallyAdjustedFont();
        dimension.setSize(MillimeterValue.fromPoint(dimension.getWidth()).get(), MillimeterValue.fromPoint(dimension.getHeight()).get());
        return dimension;
    }
    
    /**
     * 印刷に使用する線の幅を設定する。
     * 
     * @param millimeterWidth
     */
    protected void setStrokeWidth(float millimeterWidth) {
        BasicStroke basicStroke = (BasicStroke) this.graphics2D.getStroke();
        this.graphics2D.setStroke(new BasicStroke((float) MillimeterValue.newInstance(millimeterWidth).toPoint(), basicStroke.getEndCap(), basicStroke.getLineJoin(), basicStroke.getMiterLimit(), basicStroke.getDashArray(), basicStroke.getDashPhase()));
    }
    
    /**
     * 印刷に使用する線を破線に設定する。
     * 
     * @param millimeterDashes 破線パターンを表す配列。
     */
    protected void setStrokeDashArray(float... millimeterDashes) {
        if (millimeterDashes.length > 0) {
            float[] dashes = new float[millimeterDashes.length];
            for (int index = 0; index < millimeterDashes.length; index++) {
                dashes[index] = (float) MillimeterValue.newInstance(millimeterDashes[index]).toPoint();
            }
            BasicStroke basicStroke = (BasicStroke) this.graphics2D.getStroke();
            this.graphics2D.setStroke(new BasicStroke(basicStroke.getLineWidth(), basicStroke.getEndCap(), basicStroke.getLineJoin(), basicStroke.getMiterLimit(), dashes, basicStroke.getDashPhase()));
        }
    }
    
    /**
     * 印刷に使用する線を破線から実線に戻す。
     */
    protected void clearStrokeDashArray() {
        BasicStroke basicStroke = (BasicStroke) this.graphics2D.getStroke();
        this.graphics2D.setStroke(new BasicStroke(basicStroke.getLineWidth(), basicStroke.getEndCap(), basicStroke.getLineJoin(), basicStroke.getMiterLimit(), null, basicStroke.getDashPhase()));
    }

    /**
     * 指定された開始点から終点まで線を印刷する。
     * 
     * @param millimeterStartX
     * @param millimeterStartY
     * @param millimeterEndX
     * @param millimeterEndY
     */
    protected void printLine(float millimeterStartX, float millimeterStartY, float millimeterEndX, float millimeterEndY) {
        int startX = (int) MillimeterValue.newInstance(millimeterStartX).toPoint();
        int startY = (int) MillimeterValue.newInstance(millimeterStartY).toPoint();
        int endX = (int) MillimeterValue.newInstance(millimeterEndX).toPoint();
        int endY = (int) MillimeterValue.newInstance(millimeterEndY).toPoint();
        this.graphics2D.drawLine(startX, startY, endX, endY);
    }
    
    /**
     * 指定された開始点から水平方向の線を印刷する。
     * 
     * @param millimeterStartX
     * @param millimeterStartY
     * @param millimeterLength
     */
    protected final void printHorizontalLine(float millimeterStartX, float millimeterStartY, float millimeterLength) {
        this.printLine(millimeterStartX, millimeterStartY, millimeterStartX + millimeterLength, millimeterStartY);
    }
    
    /**
     * 指定された開始点から垂直方向の線を印刷する。
     * 
     * @param millimeterStartX
     * @param millimeterStartY
     * @param millimeterLength
     */
    protected final void printVerticalLine(float millimeterStartX, float millimeterStartY, float millimeterLength) {
        this.printLine(millimeterStartX, millimeterStartY, millimeterStartX, millimeterStartY + millimeterLength);
    }
    
    /**
     * 指定された位置に矩形の線を印刷する。
     * 
     * @param millimeterX
     * @param millimeterY
     * @param millimeterWidth
     * @param millimeterHeight
     * @param millimeterArc 角を丸くする場合の直径。
     */
    protected void printRectangleLine(float millimeterX, float millimeterY, float millimeterWidth, float millimeterHeight, float millimeterArc) {
        int x = (int) MillimeterValue.newInstance(millimeterX).toPoint();
        int y = (int) MillimeterValue.newInstance(millimeterY).toPoint();
        int width = (int) MillimeterValue.newInstance(millimeterWidth).toPoint();
        int height = (int) MillimeterValue.newInstance(millimeterHeight).toPoint();
        int arc = (int) MillimeterValue.newInstance(millimeterArc).toPoint();
        this.graphics2D.drawRoundRect(x, y, width, height, arc, arc);
    }

    /**
     * 指定された位置に矩形の線を印刷する。
     * 
     * @param millimeterX
     * @param millimeterY
     * @param millimeterWidth
     * @param millimeterHeight
     */
    protected final void printRectangleLine(float millimeterX, float millimeterY, float millimeterWidth, float millimeterHeight) {
        this.printRectangleLine(millimeterX, millimeterY, millimeterWidth, millimeterHeight, 0);
    }

    /**
     * 指定された位置に指定された大きさに矩形を塗りつぶす。
     * 
     * @param millimeterX
     * @param millimeterY
     * @param millimeterWidth
     * @param millimeterHeight
     * @param millimeterArc 角を丸くする場合の直径。
     */
    protected void printRectangleFill(float millimeterX, float millimeterY, float millimeterWidth, float millimeterHeight, float millimeterArc) {
        int x = (int) MillimeterValue.newInstance(millimeterX).toPoint();
        int y = (int) MillimeterValue.newInstance(millimeterY).toPoint();
        int width = (int) MillimeterValue.newInstance(millimeterWidth).toPoint();
        int height = (int) MillimeterValue.newInstance(millimeterHeight).toPoint();
        int arc = (int) MillimeterValue.newInstance(millimeterArc).toPoint();
        this.graphics2D.fillRoundRect(x, y, width, height, arc, arc);
    }

    /**
     * 指定された位置に指定された大きさの矩形に塗りつぶす。
     * 
     * @param millimeterX
     * @param millimeterY
     * @param millimeterWidth
     * @param millimeterHeight
     */
    protected final void printRectangleFill(float millimeterX, float millimeterY, float millimeterWidth, float millimeterHeight) {
        this.printRectangleFill(millimeterX, millimeterY, millimeterWidth, millimeterHeight, 0);
    }

    /**
     * 指定された位置に楕円形の線を印刷する。
     * 
     * @param millimeterX
     * @param millimeterY
     * @param millimeterWidth
     * @param millimeterHeight
     */
    protected void printEllipseLine(float millimeterX, float millimeterY, float millimeterWidth, float millimeterHeight) {
        int x = (int) MillimeterValue.newInstance(millimeterX).toPoint();
        int y = (int) MillimeterValue.newInstance(millimeterY).toPoint();
        int width = (int) MillimeterValue.newInstance(millimeterWidth).toPoint();
        int height = (int) MillimeterValue.newInstance(millimeterHeight).toPoint();
        this.graphics2D.drawOval(x, y, width, height);
    }

    /**
     * 指定された位置に指定された大きさの楕円形に塗りつぶす。
     * 
     * @param millimeterX
     * @param millimeterY
     * @param millimeterWidth
     * @param millimeterHeight
     */
    protected void printEllipseFill(float millimeterX, float millimeterY, float millimeterWidth, float millimeterHeight) {
        int x = (int) MillimeterValue.newInstance(millimeterX).toPoint();
        int y = (int) MillimeterValue.newInstance(millimeterY).toPoint();
        int width = (int) MillimeterValue.newInstance(millimeterWidth).toPoint();
        int height = (int) MillimeterValue.newInstance(millimeterHeight).toPoint();
        this.graphics2D.fillOval(x, y, width, height);
    }
    
    /**
     * 指定された位置に指定された大きさで画像を印刷する。
     * 
     * @param bufferedImage
     * @param millimeterX
     * @param millimeterY
     * @param millimeterWidth
     * @param millimeterHeight
     * @throws IOException 
     */
    protected void printImage(BufferedImage bufferedImage, float millimeterX, float millimeterY, float millimeterWidth, float millimeterHeight) throws IOException {
        int x = (int) MillimeterValue.newInstance(millimeterX).toPoint();
        int y = (int) MillimeterValue.newInstance(millimeterY).toPoint();
        int width = (int) MillimeterValue.newInstance(millimeterWidth).toPoint();
        int height = (int) MillimeterValue.newInstance(millimeterHeight).toPoint();
        this.graphics2D.drawImage(bufferedImage, x, y, width, height, null); // Always returns true.
    }
    
    /**
     * 指定された位置に幅だけを指定して画像を印刷する。画像の高さは自動調整される。
     * 
     * @param bufferedImage
     * @param millimeterX
     * @param millimeterY
     * @param millimeterWidth
     * @throws IOException
     */
    protected final void printImageToFitWidth(BufferedImage bufferedImage, float millimeterX, float millimeterY, float millimeterWidth) throws IOException {
        float ratio = bufferedImage.getWidth() / millimeterWidth;
        float millimeterHeight = bufferedImage.getHeight() / ratio;
        this.printImage(bufferedImage, millimeterX, millimeterY, millimeterWidth, millimeterHeight);
    }
    
    /**
     * 指定された位置に高さだけを指定して画像を印刷する。画像の幅は自動調整される。
     * 
     * @param bufferedImage
     * @param millimeterX
     * @param millimeterY
     * @param millimeterHeight
     * @throws IOException
     */
    protected final void printImageToFitHeight(BufferedImage bufferedImage, float millimeterX, float millimeterY, float millimeterHeight) throws IOException {
        float ratio = bufferedImage.getHeight() / millimeterHeight;
        float millimeterWidth = bufferedImage.getWidth() / ratio;
        this.printImage(bufferedImage, millimeterX, millimeterY, millimeterWidth, millimeterHeight);
    }

    /**
     * 指定された位置に指定された大きさでNW-7のバーコードを印刷する。
     * 
     * @param barcode
     * @param millimeterX
     * @param millimeterY
     * @param millimeterHeight
     * @param millimeterWidth
     */
    protected void printNW7(String barcode, float millimeterX, float millimeterY, float millimeterWidth, float millimeterHeight) {
        float x = (float) MillimeterValue.newInstance(millimeterX).toPoint();
        float y = (float) MillimeterValue.newInstance(millimeterY).toPoint();
        float width = (float) MillimeterValue.newInstance(millimeterWidth).toPoint();
        float height = (float) MillimeterValue.newInstance(millimeterHeight).toPoint();
        NW7Writer nw7Writer = new NW7Writer(barcode, this.graphics2D);
        nw7Writer.write(x, y, width, height);
    }
    
    /**
     * 指定された位置に指定された大きさでJAN-13のバーコードを印刷する。
     * 
     * @param barcode
     * @param millimeterX
     * @param millimeterY
     * @param millimeterWidth
     * @param millimeterHeight
     */
    protected void printJAN13(String barcode, float millimeterX, float millimeterY, float millimeterWidth, float millimeterHeight) {
        float x = (float) MillimeterValue.newInstance(millimeterX).toPoint();
        float y = (float) MillimeterValue.newInstance(millimeterY).toPoint();
        float width = (float) MillimeterValue.newInstance(millimeterWidth).toPoint();
        float height = (float) MillimeterValue.newInstance(millimeterHeight).toPoint();
        JAN13Writer jan13Writer = new JAN13Writer(barcode, this.graphics2D);
        jan13Writer.write(x, y, width, height);
    }
    
    /**
     * 角度と回転の中心を指定して以後の描画を回転して行うよう設定する。
     * 
     * @param angle
     * @param millimeterX
     * @param millimeterY 
     */
    protected void setRotate(int angle, float millimeterX, float millimeterY) {
        float x = (float) MillimeterValue.newInstance(millimeterX).toPoint();
        float y = (float) MillimeterValue.newInstance(millimeterY).toPoint();
        this.graphics2D.rotate(Math.toRadians(angle), x, y);
    }
    
    private static List<String> NEED_TO_VERITICAL_LIST = null;
    
    /**
     * 文字列を垂直書きで印刷する場合に、90度回転させる必要がある文字列のリストを取得する。
     * 
     * @return 結果。
     */
    protected static List<String> getNeedToVerticalList() {
        if (Printable.NEED_TO_VERITICAL_LIST == null) {
            List<String> list = new ArrayList<>();
            list.add("(");
            list.add(")");
            list.add("-");
            list.add("<");
            list.add("=");
            list.add(">");
            list.add("[");
            list.add("]");
            list.add("{");
            list.add("|");
            list.add("}");
            list.add("~");
            list.add("‐");
            list.add("−");
            list.add("〈");
            list.add("〉");
            list.add("《");
            list.add("》");
            list.add("「");
            list.add("」");
            list.add("『");
            list.add("』");
            list.add("【");
            list.add("】");
            list.add("〔");
            list.add("〕");
            list.add("〜");
            list.add("ー");
            list.add("（");
            list.add("）");
            list.add("＝");
            list.add("［");
            list.add("］");
            list.add("｛");
            list.add("｜");
            list.add("｝");
            Collections.unmodifiableList(list);
            Printable.NEED_TO_VERITICAL_LIST = list;
        }
        return Printable.NEED_TO_VERITICAL_LIST;
    }
    
    /**
     * フォントスタイルの列挙型。
     * 
     * @author hiro
     *
     */
    protected enum FontStyle {
        /**
         * 標準のテキスト。
         */
        PLAIN,
        /**
         * 太字のテキスト。
         */
        BOLD,
        /**
         * 斜体のテキスト。
         */
        ITALIC,
        ;
        
        /**
         * フォントのスタイルとして有効な値を取得する。
         * 
         * @return 結果。
         */
        public int getValue() {
            switch (this) {
            case BOLD:
                return Font.BOLD;
            case ITALIC:
                return Font.ITALIC;
            default:
                return Font.PLAIN;
            }
        }
    }
}
