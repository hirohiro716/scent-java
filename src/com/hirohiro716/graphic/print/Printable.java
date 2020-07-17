package com.hirohiro716.graphic.print;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hirohiro716.graphic.ColorCreator;
import com.hirohiro716.graphic.Dimension;
import com.hirohiro716.graphic.FontCreator;
import com.hirohiro716.graphic.GraphicalString;
import com.hirohiro716.graphic.JAN13Writer;
import com.hirohiro716.graphic.NW7Writer;
import com.hirohiro716.graphic.GraphicalString.HorizontalPosition;
import com.hirohiro716.graphic.GraphicalString.VerticalPosition;

/**
 * 印刷物の抽象クラス。
 * 
 * @author hiro
 *
 */
public abstract class Printable implements java.awt.print.Printable {
    
    private Graphics2D graphics2D = null;
    
    private PageFormat pageFormat = null;
    
    private int marginTop = 0;
    
    /**
     * この印刷物の上余白を取得する。
     * 
     * @return 結果。
     */
    public int getMarginTop() {
        return this.marginTop;
    }
    
    /**
     * この印刷物の上余白をセットする。
     * 
     * @param margin
     */
    public void setMarginTop(int margin) {
        this.marginTop = margin;
    }
    
    private int marginLeft = 0;
    
    /**
     * この印刷物の左余白を取得する。
     * 
     * @return 結果。
     */
    public int getMarginLeft() {
        return this.marginLeft;
    }
    
    /**
     * この印刷物の左余白をセットする。
     * 
     * @param margin
     */
    public void setMarginLeft(int margin) {
        this.marginLeft = margin;
    }
    
    private int numberOfCopies = 1;
    
    /**
     * この印刷物を印刷する部数を取得する。
     * 
     * @return 結果。
     */
    public int getNumberOfCopies() {
        return this.numberOfCopies;
    }
    
    /**
     * この印刷物を印刷する部数をセットする。初期値は1部。
     * 
     * @param numberOfCopies
     */
    public void setNumberOfCopies(int numberOfCopies) {
        this.numberOfCopies = numberOfCopies;
    }
    
    private List<Integer> listOfExistedPage = new ArrayList<>();
    
    @Override
    public final int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        this.graphics2D = (Graphics2D) graphics;
        this.pageFormat = pageFormat;
        this.graphics2D.translate(this.marginLeft, this.marginTop);
        this.setVerticalPositionOfString(VerticalPosition.TOP);
        this.setHorizontalPositionOfString(HorizontalPosition.LEFT);
        boolean isExistPage = this.print(pageIndex);
        if (isExistPage) {
            if (this.listOfExistedPage.contains(pageIndex) == false) {
                this.listOfExistedPage.add(pageIndex);
            }
            return PAGE_EXISTS;
        }
        int copyNumber = pageIndex / this.listOfExistedPage.size();
        if (copyNumber < this.numberOfCopies) {
            int copyIndex = pageIndex % this.listOfExistedPage.size();
            this.print(copyIndex);
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
        Color color = ColorCreator.create(webColor);
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
     * 印刷に使用するフォント名を設定する。
     * 
     * @param fontName
     */
    protected final void setFontName(String fontName) {
        this.setFont(FontCreator.create(this.getFont(), fontName));
    }
    
    /**
     * 印刷に使用するフォントのサイズを変更する。
     * 
     * @param size
     */
    protected final void setFontSize(float size) {
        this.setFont(FontCreator.create(this.getFont(), (int) size));
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
        this.leading = MillimeterValue.newInstance(millimeterLeading).toPoint();
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
     * 指定された文字列を印刷した場合の大きさを測定したDimensionを作成する。<br>
     * この処理で自動調整されたフォントはインスタンス内で保持される。
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
        dimension = new Dimension(MillimeterValue.fromPoint(dimension.getWidth()).get(), MillimeterValue.fromPoint(dimension.getHeight()).get());
        return dimension;
    }
    
    /**
     * 指定された文字列を印刷した場合の大きさを測定したDimensionを作成する。<br>
     * この処理で自動調整されたフォントはインスタンス内で保持される。
     * 
     * @param string
     * @param millimeterMaximumWidth
     * @param millimeterMaximumHeight
     * @return 結果。
     */
    protected Dimension createStringDimension(String string, float millimeterMaximumWidth, float millimeterMaximumHeight) {
        GraphicalString graphicalString = new GraphicalString(string, this.graphics2D);
        if (this.leading != null) {
            graphicalString.setLeading(this.leading);
        }
        float maximumWidth = MillimeterValue.newInstance(millimeterMaximumWidth).toPoint();
        float maximumHeight = MillimeterValue.newInstance(millimeterMaximumHeight).toPoint();
        graphicalString.setMaximumWidth(maximumWidth);
        graphicalString.setMaximumHeight(maximumHeight);
        Dimension dimension = graphicalString.createDimension();
        this.lastAutomaticallyAdjustedFont = graphicalString.getLastAutomaticallyAdjustedFont();
        dimension = new Dimension(MillimeterValue.fromPoint(dimension.getWidth()).get(), MillimeterValue.fromPoint(dimension.getHeight()).get());
        return dimension;
    }
    
    /**
     * 指定された文字列を指定された位置に印刷する。<br>
     * この処理で自動調整されたフォントはインスタンス内で保持される。
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
        float x = MillimeterValue.newInstance(millimeterX).toPoint();
        float y = MillimeterValue.newInstance(millimeterY).toPoint();
        Dimension dimension = graphicalString.draw(x, y);
        this.lastAutomaticallyAdjustedFont = graphicalString.getLastAutomaticallyAdjustedFont();
        dimension = new Dimension(MillimeterValue.fromPoint(dimension.getWidth()).get(), MillimeterValue.fromPoint(dimension.getHeight()).get());
        return dimension;
    }
    
    /**
     * 指定された位置のボックスの中に文字列を印刷する。<br>
     * この処理で自動調整されたフォントはインスタンス内で保持される。
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
        float width = MillimeterValue.newInstance(millimeterWidth).toPoint();
        float height = MillimeterValue.newInstance(millimeterHeight).toPoint();
        float x = MillimeterValue.newInstance(millimeterX).toPoint();
        float y = MillimeterValue.newInstance(millimeterY).toPoint();
        Dimension dimension = graphicalString.drawInBox(x, y, width, height);
        this.lastAutomaticallyAdjustedFont = graphicalString.getLastAutomaticallyAdjustedFont();
        dimension = new Dimension(MillimeterValue.fromPoint(dimension.getWidth()).get(), MillimeterValue.fromPoint(dimension.getHeight()).get());
        return dimension;
    }
    
    /**
     * 印刷に使用する線の幅を設定する。
     * 
     * @param millimeterWidth
     */
    protected void setStrokeWidth(float millimeterWidth) {
        BasicStroke basicStroke = (BasicStroke) this.graphics2D.getStroke();
        this.graphics2D.setStroke(new BasicStroke(MillimeterValue.newInstance(millimeterWidth).toPoint(), basicStroke.getEndCap(), basicStroke.getLineJoin(), basicStroke.getMiterLimit(), basicStroke.getDashArray(), basicStroke.getDashPhase()));
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
                dashes[index] = MillimeterValue.newInstance(millimeterDashes[index]).toPoint();
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
        double startX = MillimeterValue.newInstance(millimeterStartX).toPoint();
        double startY = MillimeterValue.newInstance(millimeterStartY).toPoint();
        double endX = MillimeterValue.newInstance(millimeterEndX).toPoint();
        double endY = MillimeterValue.newInstance(millimeterEndY).toPoint();
        this.graphics2D.draw(new Line2D.Double(startX, startY, endX, endY));
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
        double x = MillimeterValue.newInstance(millimeterX).toPoint();
        double y = MillimeterValue.newInstance(millimeterY).toPoint();
        double width = MillimeterValue.newInstance(millimeterWidth).toPoint();
        double height = MillimeterValue.newInstance(millimeterHeight).toPoint();
        double arc = MillimeterValue.newInstance(millimeterArc).toPoint();
        this.graphics2D.draw(new RoundRectangle2D.Double(x, y, width, height, arc, arc));
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
        double x = MillimeterValue.newInstance(millimeterX).toPoint();
        double y = MillimeterValue.newInstance(millimeterY).toPoint();
        double width = MillimeterValue.newInstance(millimeterWidth).toPoint();
        double height = MillimeterValue.newInstance(millimeterHeight).toPoint();
        double arc = MillimeterValue.newInstance(millimeterArc).toPoint();
        this.graphics2D.fill(new RoundRectangle2D.Double(x, y, width, height, arc, arc));
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
        double x = MillimeterValue.newInstance(millimeterX).toPoint();
        double y = MillimeterValue.newInstance(millimeterY).toPoint();
        double width = MillimeterValue.newInstance(millimeterWidth).toPoint();
        double height = MillimeterValue.newInstance(millimeterHeight).toPoint();
        this.graphics2D.draw(new Ellipse2D.Double(x, y, width, height));
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
        double x = MillimeterValue.newInstance(millimeterX).toPoint();
        double y = MillimeterValue.newInstance(millimeterY).toPoint();
        double width = MillimeterValue.newInstance(millimeterWidth).toPoint();
        double height = MillimeterValue.newInstance(millimeterHeight).toPoint();
        this.graphics2D.fill(new Ellipse2D.Double(x, y, width, height));
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
        AffineTransform transform = this.graphics2D.getTransform();
        double translateX = MillimeterValue.newInstance(millimeterX).toPoint();
        double translateY = MillimeterValue.newInstance(millimeterY).toPoint();
        double scaleX = MillimeterValue.newInstance(millimeterWidth).toPoint() / bufferedImage.getWidth();
        double scaleY = MillimeterValue.newInstance(millimeterHeight).toPoint() / bufferedImage.getHeight();
        this.graphics2D.translate(translateX, translateY);
        this.graphics2D.scale(scaleX, scaleY);
        this.graphics2D.drawImage(bufferedImage, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
        this.graphics2D.setTransform(transform);
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
        float x = MillimeterValue.newInstance(millimeterX).toPoint();
        float y = MillimeterValue.newInstance(millimeterY).toPoint();
        float width = MillimeterValue.newInstance(millimeterWidth).toPoint();
        float height = MillimeterValue.newInstance(millimeterHeight).toPoint();
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
        float x = MillimeterValue.newInstance(millimeterX).toPoint();
        float y = MillimeterValue.newInstance(millimeterY).toPoint();
        float width = MillimeterValue.newInstance(millimeterWidth).toPoint();
        float height = MillimeterValue.newInstance(millimeterHeight).toPoint();
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
        float x = MillimeterValue.newInstance(millimeterX).toPoint();
        float y = MillimeterValue.newInstance(millimeterY).toPoint();
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
