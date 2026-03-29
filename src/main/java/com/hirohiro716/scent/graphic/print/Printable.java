package com.hirohiro716.scent.graphic.print;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hirohiro716.scent.Dimension;
import com.hirohiro716.scent.graphic.AWTDrawContext;
import com.hirohiro716.scent.graphic.LengthUnit;
import com.hirohiro716.scent.graphic.DrawContext.FontStyle;
import com.hirohiro716.scent.graphic.GraphicalString.HorizontalPosition;
import com.hirohiro716.scent.graphic.GraphicalString.VerticalPosition;

/**
 * 印刷物の抽象クラス。
 */
public abstract class Printable implements java.awt.print.Printable {
    
    private AWTDrawContext drawContext = null;
    
    private PageFormat pageFormat = null;
    
    private int marginTop = 0;
    
    /**
     * この印刷物の上余白を取得する。
     * 
     * @return
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
     * @return
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
     * @return
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
        this.drawContext = new AWTDrawContext((Graphics2D) graphics);
        this.drawContext.setLengthUnit(LengthUnit.MILLIMETER);
        this.pageFormat = pageFormat;
        graphics.translate(this.marginLeft, this.marginTop);
        boolean existsPage = this.print(pageIndex);
        if (existsPage) {
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
        this.drawContext = null;
        this.pageFormat = null;
        return NO_SUCH_PAGE;
    }
    
    /**
     * この印刷物で印刷したページ数を取得する。印刷部数は考慮しない。
     * 
     * @return
     */
    public int getNumberOfPrintedPages() {
        return this.listOfExistedPage.size();
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
     * 描画命令を実行するコンテキストインスタンスを取得する。印刷が開始されていない場合はnullを返す。
     * 
     * @return
     */
    protected AWTDrawContext getAWTDrawContext() {
        return this.drawContext;
    }

    /**
     * 印刷時に指定されたページ形式を取得する。
     * 
     * @return
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
        this.drawContext.setColor(color);
    }
    
    /**
     * 印刷に使用する色を、HTMLやCSSで使用する "#000000" や "#fff" のような形式で設定する。
     * 
     * @param webColor
     */
    protected void setWebColor(String webColor) {
        this.drawContext.setWebColor(webColor);
    }
    
    /**
     * 印刷に使用するフォントを取得する。
     * 
     * @return
     */
    protected Font getFont() {
        return this.drawContext.getFont();
    }

    /**
     * 印刷に使用するフォントを設定する。
     * 
     * @param font
     */
    protected void setFont(Font font) {
        this.drawContext.setFont(font);
    }
    
    /**
     * 印刷に使用するフォントを設定する。
     * 
     * @param fontName
     * @param size
     * @param fontStyle
     */
    protected void setFont(String fontName, float size, FontStyle fontStyle) {
        this.drawContext.setFont(fontName, size, fontStyle);
    }
    
    /**
     * 印刷に使用するフォントを設定する。
     * 
     * @param fontName
     * @param size
     */
    protected void setFont(String fontName, float size) {
        this.drawContext.setFont(fontName, size);
    }
    
    /**
     * 印刷に使用するフォント名を設定する。
     * 
     * @param fontName
     */
    protected void setFontName(String fontName) {
        this.drawContext.setFontName(fontName);
    }
    
    /**
     * 印刷に使用するフォントのサイズを変更する。
     * 
     * @param size
     */
    protected void setFontSize(float size) {
        this.drawContext.setFontSize(size);
    }
    
    /**
     * 印刷に使用するフォントのスタイルを変更する。
     * 
     * @param fontStyle
     */
    protected void setFontStyle(FontStyle fontStyle) {
        this.drawContext.setFontStyle(fontStyle);
    }
    
    /**
     * 印刷する文字列の行と行との間隔を設定する。
     * 
     * @param millimeterLeading
     */
    protected void setLeading(float millimeterLeading) {
        this.drawContext.setLeading(millimeterLeading);
    }

    /**
     * 印刷する文字列の行と行との間隔をフォントに基づく初期設定にする。
     */
    protected void setDefaultLeading() {
        this.drawContext.setDefaultLeading();
    }
    
    /**
     * 印刷する文字列の自動改行が無効になっている場合はtrueを返す。
     * 
     * @return
     */
    protected boolean isDisabledMultipleLine() {
        return this.drawContext.isDisabledMultipleLine();
    }
    
    /**
     * 印刷する文字列の自動改行を無効にする場合はtrueをセットする。初期値はfalse。
     * 
     * @param isDisabledMultipleLine
     */
    protected void setDisabledMultipleLine(boolean isDisabledMultipleLine) {
        this.drawContext.setDisabledMultipleLine(isDisabledMultipleLine);
    }
    
    /**
     * 文字列を印刷する際の水平方向の基準を設定する。
     * 
     * @param horizontalPosition
     */
    protected void setHorizontalPositionOfString(HorizontalPosition horizontalPosition) {
        this.drawContext.setHorizontalPositionOfString(horizontalPosition);
    }
    
    /**
     * 文字列を印刷する際の垂直方向の基準を設定する。
     * 
     * @param verticalPosition
     */
    protected void setVerticalPositionOfString(VerticalPosition verticalPosition) {
        this.drawContext.setVerticalPositionOfString(verticalPosition);
    }

    /**
     * 最後に自動調整されたフォントを取得する。
     * 
     * @return
     */
    public Font getLastAutomaticallyAdjustedFont() {
        return this.drawContext.getLastAutomaticallyAdjustedFont();
    }

    /**
     * 指定された文字列を印刷した場合の大きさを測定したDimensionを作成する。<br>
     * この処理で自動調整されたフォントはインスタンス内で保持される。
     * 
     * @param string
     * @return
     */
    protected Dimension createStringDimension(String string) {
        return this.drawContext.createStringDimension(string);
    }
    
    /**
     * 指定された文字列を印刷した場合の大きさを測定したDimensionを作成する。<br>
     * この処理で自動調整されたフォントはインスタンス内で保持される。
     * 
     * @param string
     * @param millimeterMaximumWidth
     * @param millimeterMaximumHeight
     * @return
     */
    protected Dimension createStringDimension(String string, float millimeterMaximumWidth, float millimeterMaximumHeight) {
        return this.drawContext.createStringDimension(string, millimeterMaximumWidth, millimeterMaximumHeight);
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
        return this.drawContext.drawString(string, millimeterX, millimeterY);
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
        return this.drawContext.drawStringInBox(string, millimeterX, millimeterY, millimeterWidth, millimeterHeight);
    }

    /**
     * 印刷に使用する線の幅を設定する。
     * 
     * @param millimeterWidth
     */
    protected void setStrokeWidth(float millimeterWidth) {
        this.drawContext.setStrokeWidth(millimeterWidth);
    }
    
    /**
     * 印刷に使用する線を破線に設定する。
     * 
     * @param millimeterDashes 破線パターンを表す配列。
     */
    protected void setStrokeDashArray(float... millimeterDashes) {
        this.drawContext.setStrokeDashArray(millimeterDashes);
    }
    
    /**
     * 印刷に使用する線を破線から実線に戻す。
     */
    protected void clearStrokeDashArray() {
        this.drawContext.clearStrokeDashArray();
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
        this.drawContext.drawLine(millimeterStartX, millimeterStartY, millimeterEndX, millimeterEndY);
    }
    
    /**
     * 指定された開始点から水平方向の線を印刷する。
     * 
     * @param millimeterStartX
     * @param millimeterStartY
     * @param millimeterLength
     */
    protected void printHorizontalLine(float millimeterStartX, float millimeterStartY, float millimeterLength) {
        this.drawContext.drawHorizontalLine(millimeterStartX, millimeterStartY, millimeterLength);
    }
    
    /**
     * 指定された開始点から垂直方向の線を印刷する。
     * 
     * @param millimeterStartX
     * @param millimeterStartY
     * @param millimeterLength
     */
    protected void printVerticalLine(float millimeterStartX, float millimeterStartY, float millimeterLength) {
        this.drawContext.drawVerticalLine(millimeterStartX, millimeterStartY, millimeterLength);
    }
    
    /**
     * 指定された位置に矩形の線を印刷する。
     * 
     * @param millimeterX
     * @param millimeterY
     * @param millimeterWidth
     * @param millimeterHeight
     * @param millimeterDiameter 角を丸くする場合の直径。
     */
    protected void printRectangleLine(float millimeterX, float millimeterY, float millimeterWidth, float millimeterHeight, float millimeterDiameter) {
        this.drawContext.drawRectangleLine(millimeterX, millimeterY, millimeterWidth, millimeterHeight, millimeterDiameter);
    }

    /**
     * 指定された位置に矩形の線を印刷する。
     * 
     * @param millimeterX
     * @param millimeterY
     * @param millimeterWidth
     * @param millimeterHeight
     */
    protected void printRectangleLine(float millimeterX, float millimeterY, float millimeterWidth, float millimeterHeight) {
        this.drawContext.drawRectangleLine(millimeterX, millimeterY, millimeterWidth, millimeterHeight);
    }

    /**
     * 指定された位置に指定された大きさに矩形を塗りつぶす。
     * 
     * @param millimeterX
     * @param millimeterY
     * @param millimeterWidth
     * @param millimeterHeight
     * @param millimeterDiameter 角を丸くする場合の直径。
     */
    protected void printRectangleFill(float millimeterX, float millimeterY, float millimeterWidth, float millimeterHeight, float millimeterDiameter) {
        this.drawContext.drawRectangleFill(millimeterX, millimeterY, millimeterWidth, millimeterHeight, millimeterDiameter);
    }

    /**
     * 指定された位置に指定された大きさの矩形に塗りつぶす。
     * 
     * @param millimeterX
     * @param millimeterY
     * @param millimeterWidth
     * @param millimeterHeight
     */
    protected void printRectangleFill(float millimeterX, float millimeterY, float millimeterWidth, float millimeterHeight) {
        this.drawContext.drawRectangleFill(millimeterX, millimeterY, millimeterWidth, millimeterHeight);
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
        this.drawContext.drawEllipseLine(millimeterX, millimeterY, millimeterWidth, millimeterHeight);
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
        this.drawContext.drawEllipseFill(millimeterX, millimeterY, millimeterWidth, millimeterHeight);
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
        this.drawContext.drawImage(bufferedImage, millimeterX, millimeterY, millimeterWidth, millimeterHeight);
    }
    
    /**
     * 指定された位置に幅だけを指定して画像を印刷する。画像の高さは自動調整される。
     * 
     * @param bufferedImage
     * @param millimeterX
     * @param millimeterY
     * @param millimeterWidth
     * @return 印刷された画像の大きさ。
     * @throws IOException
     */
    protected Dimension printImageToFitWidth(BufferedImage bufferedImage, float millimeterX, float millimeterY, float millimeterWidth) throws IOException {
        return this.drawContext.drawImageToFitWidth(bufferedImage, millimeterX, millimeterY, millimeterWidth);
    }
    
    /**
     * 指定された位置に高さだけを指定して画像を印刷する。画像の幅は自動調整される。
     * 
     * @param bufferedImage
     * @param millimeterX
     * @param millimeterY
     * @param millimeterHeight
     * @return 印刷された画像の大きさ。
     * @throws IOException
     */
    protected Dimension printImageToFitHeight(BufferedImage bufferedImage, float millimeterX, float millimeterY, float millimeterHeight) throws IOException {
        return this.drawContext.drawImageToFitHeight(bufferedImage, millimeterX, millimeterY, millimeterHeight);
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
        this.drawContext.drawNW7(barcode, millimeterX, millimeterY, millimeterWidth, millimeterHeight);
    }
    
    /**
     * 指定された位置に指定された大きさでJAN-13のバーコードを印刷する。
     * 
     * @param barcode
     * @param millimeterX
     * @param millimeterY
     * @param millimeterWidth
     * @param millimeterHeight
     * @param barScale バー描画の拡大率。1が初期値。
     */
    protected void printJAN13(String barcode, float millimeterX, float millimeterY, float millimeterWidth, float millimeterHeight, float barScale) {
        this.drawContext.drawJAN13(barcode, millimeterX, millimeterY, millimeterWidth, millimeterHeight, barScale);
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
        this.drawContext.drawJAN13(barcode, millimeterX, millimeterY, millimeterWidth, millimeterHeight);
    }
    
    /**
     * 角度と回転の中心を指定して以後の描画を回転して行うよう設定する。
     * 
     * @param angle
     * @param millimeterX
     * @param millimeterY 
     */
    protected void setRotate(int angle, float millimeterX, float millimeterY) {
        this.drawContext.setRotate(angle, millimeterX, millimeterY);
    }
}
