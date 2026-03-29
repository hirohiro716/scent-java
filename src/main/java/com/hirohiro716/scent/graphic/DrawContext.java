package com.hirohiro716.scent.graphic;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hirohiro716.scent.Dimension;
import com.hirohiro716.scent.graphic.GraphicalString.HorizontalPosition;
import com.hirohiro716.scent.graphic.GraphicalString.VerticalPosition;

/**
 * 描画命令を実行するコンテキストの抽象クラス。
 * 
 * @param <I> 内部で描画に使用するインスタンスの型。
 */
public abstract class DrawContext<I> {

    /**
     * コンストラクタ。
     * 
     * @param innerInstance 描画するインスタンスを指定する。インスタンスは長さの単位にポイントを使用する必要がある。
     */
    protected DrawContext(I innerInstance) {
        this.innerInstance = innerInstance;
    }

    private I innerInstance;

    /**
     * コンストラクタで指定した描画するインスタンスを取得する。
     * 
     * @return
     */
    protected I getInnerInstance() {
        return this.innerInstance;
    }

    private LengthUnit unit = LengthUnit.POINT;

    /**
     * 描画に使用する単位を取得する。
     * 
     * @return
     */
    public LengthUnit getLengthUnit() {
        return this.unit;
    }

    /**
     * 描画に使用する単位を設定する。
     * 
     * @param lengthUnit
     */
    public void setLengthUnit(LengthUnit lengthUnit) {
        this.unit = lengthUnit;
    }

    /**
     * 描画に使用する色を設定する。
     * 
     * @param innerInstance コンストラクタで指定した描画インスタンス。
     * @param color
     */
    protected abstract void setColor(I innerInstance, Color color);
    
    /**
     * 描画に使用する色を設定する。
     * 
     * @param color
     */
    public final void setColor(Color color) {
        this.setColor(this.innerInstance, color);
    }
    
    /**
     * 描画に使用する色を、HTMLやCSSで使用する "#000000" や "#fff" のような形式で設定する。
     * 
     * @param webColor
     */
    public final void setWebColor(String webColor) {
        Color color = ColorCreator.create(webColor);
        this.setColor(this.innerInstance, color);
    }
    
    /**
     * 描画に使用するフォントを取得する。
     * 
     * @param innerInstance コンストラクタで指定した描画インスタンス。
     * @return
     */
    protected abstract Font getFont(I innerInstance);

    /**
     * 描画に使用するフォントを取得する。
     * 
     * @return
     */
    public final Font getFont() {
        return this.getFont(this.innerInstance);
    }

    /**
     * 描画に使用するフォントを設定する。
     * 
     * @param innerInstance コンストラクタで指定した描画インスタンス。
     * @param font
     */
    protected abstract void setFont(I innerInstance, Font font);
    
    /**
     * 描画に使用するフォントを設定する。
     * 
     * @param font
     */
    public final void setFont(Font font) {
        this.setFont(this.innerInstance, font);
    }
    
    /**
     * 描画に使用するフォントを設定する。
     * 
     * @param fontName
     * @param size
     * @param fontStyle
     */
    public final void setFont(String fontName, float size, FontStyle fontStyle) {
        this.setFont(new Font(fontName, fontStyle.getValue(), (int) size));
    }
    
    /**
     * 描画に使用するフォントを設定する。
     * 
     * @param fontName
     * @param size
     */
    public final void setFont(String fontName, float size) {
        this.setFont(fontName, size, FontStyle.PLAIN);
    }
    
    /**
     * 描画に使用するフォント名を設定する。
     * 
     * @param fontName
     */
    public final void setFontName(String fontName) {
        this.setFont(FontCreator.create(this.getFont(), fontName));
    }
    
    /**
     * 描画に使用するフォントのサイズを変更する。
     * 
     * @param size
     */
    public final void setFontSize(float size) {
        this.setFont(FontCreator.create(this.getFont(), (int) size));
    }
    
    /**
     * 描画に使用するフォントのスタイルを変更する。
     * 
     * @param fontStyle
     */
    public final void setFontStyle(FontStyle fontStyle) {
        this.setFont(this.getFont().getFontName(), this.getFont().getSize(), fontStyle);
    }
    
    private Float leading = null;

    /**
     * 描画する文字列の行と行との間隔を取得する。
     * 
     * @return
     */
    public Float getLeading() {
        if (this.leading == null) {
            return null;
        }
        return this.unit.fromPoint(this.leading);
    }
    
    /**
     * 描画する文字列の行と行との間隔を設定する。
     * 
     * @param leading
     */
    public void setLeading(float leading) {
        this.leading = this.unit.toPoint(leading);
    }

    /**
     * 描画する文字列の行と行との間隔をフォントに基づく初期設定にする。
     */
    public void setDefaultLeading() {
        this.leading = null;
    }
    
    private boolean isDisabledMultipleLine = false;
    
    /**
     * 描画する文字列の自動改行が無効になっている場合はtrueを返す。
     * 
     * @return
     */
    public boolean isDisabledMultipleLine() {
        return this.isDisabledMultipleLine;
    }
    
    /**
     * 描画する文字列の自動改行を無効にする場合はtrueをセットする。初期値はfalse。
     * 
     * @param isDisabledMultipleLine
     */
    public void setDisabledMultipleLine(boolean isDisabledMultipleLine) {
        this.isDisabledMultipleLine = isDisabledMultipleLine;
    }
    
    private HorizontalPosition horizontalPosition = HorizontalPosition.LEFT;
    
    /**
     * 文字列を描画する際の水平方向の基準を設定する。
     * 
     * @param horizontalPosition
     */
    public void setHorizontalPositionOfString(HorizontalPosition horizontalPosition) {
        this.horizontalPosition = horizontalPosition;
    }
    
    private VerticalPosition verticalPosition = VerticalPosition.BASELINE;
    
    /**
     * 文字列を描画する際の垂直方向の基準を設定する。
     * 
     * @param verticalPosition
     */
    public void setVerticalPositionOfString(VerticalPosition verticalPosition) {
        this.verticalPosition = verticalPosition;
    }
    
    private Font lastAutomaticallyAdjustedFont = null;

    /**
     * 最後に自動調整されたフォントを取得する。
     * 
     * @return
     */
    public Font getLastAutomaticallyAdjustedFont() {
        return this.lastAutomaticallyAdjustedFont;
    }

    /**
     * 描画に使用するフォントサイズの情報を作成する。
     * 
     * @param innerInstance コンストラクタで指定した描画インスタンス。
     * @return
     */
    public abstract FontMetrics createFontMetrics(I innerInstance);

    /**
     * 描画に使用するフォントサイズの情報を作成する。
     * 
     * @return
     */
    public FontMetrics createFontMetrics() {
        return this.createFontMetrics(this.innerInstance);
    }

    /**
     * 指定された文字列を描画した場合の幅を測定する。
     * 
     * @param innerInstance コンストラクタで指定した描画インスタンス。
     * @param string
     * @return
     */
    protected abstract float measureStringWidth(I innerInstance, String string);

    /**
     * 指定された文字列を描画した場合の幅を測定する。
     * 
     * @param string
     * @return
     */
    public final float measureStringWidth(String string) {
        return this.measureStringWidth(this.innerInstance, string);
    }

    /**
     * 指定された1行の文字列を指定された位置に描画する。
     * 
     * @param innerInstance コンストラクタで指定した描画インスタンス。
     * @param string
     * @param x
     * @param y
     */
    protected abstract void drawSingleLineString(I innerInstance, String string, float x, float y);

    /**
     * 指定された1行の文字列を指定された位置に描画する。
     * 
     * @param string
     * @param x
     * @param y
     */
    public final void drawSingleLineString(String string, float x, float y) {
        this.drawSingleLineString(this.innerInstance, string, x, y);
    }

    /**
     * 指定された文字列を描画した場合の大きさを測定したDimensionを作成する。<br>
     * この処理で自動調整されたフォントはインスタンス内で保持される。
     * 
     * @param string
     * @return
     */
    public Dimension createStringDimension(String string) {
        GraphicalString graphicalString = new GraphicalString(string, this);
        if (this.leading != null) {
            graphicalString.setLeading(this.leading);
        }
        graphicalString.setDisabledMultipleLine(this.isDisabledMultipleLine());
        Dimension dimension = graphicalString.createDimension();
        this.lastAutomaticallyAdjustedFont = graphicalString.getLastAutomaticallyAdjustedFont();
        return new Dimension(this.unit.fromPoint(dimension.getWidth()), this.unit.fromPoint(dimension.getHeight()));
    }

    /**
     * 指定された文字列を描画した場合の大きさを測定したDimensionを作成する。<br>
     * この処理で自動調整されたフォントはインスタンス内で保持される。
     * 
     * @param string
     * @param maximumWidth
     * @param maximumHeight
     * @return
     */
    public Dimension createStringDimension(String string, float maximumWidth, float maximumHeight) {
        GraphicalString graphicalString = new GraphicalString(string, this);
        if (this.leading != null) {
            graphicalString.setLeading(leading);
        }
        graphicalString.setDisabledMultipleLine(this.isDisabledMultipleLine());
        graphicalString.setMaximumWidth(this.unit.toPoint(maximumWidth));
        graphicalString.setMaximumHeight(this.unit.toPoint(maximumHeight));
        Dimension dimension = graphicalString.createDimension();
        this.lastAutomaticallyAdjustedFont = graphicalString.getLastAutomaticallyAdjustedFont();
        return new Dimension(this.unit.fromPoint(dimension.getWidth()), this.unit.fromPoint(dimension.getHeight()));
    }
    
    /**
     * 指定された文字列を指定された位置に描画する。<br>
     * この処理で自動調整されたフォントはインスタンス内で保持される。
     * 
     * @param string
     * @param x
     * @param y
     * @return 描画された文字列の大きさ。
     */
    public Dimension drawString(String string, float x, float y) {
        GraphicalString graphicalString = new GraphicalString(string, this);
        if (this.leading != null) {
            graphicalString.setLeading(this.leading);
        }
        graphicalString.setDisabledMultipleLine(this.isDisabledMultipleLine);
        graphicalString.setHorizontalPosition(this.horizontalPosition);
        graphicalString.setVerticalPosition(this.verticalPosition);
        Dimension dimension = graphicalString.draw(this.unit.toPoint(x), this.unit.toPoint(y));
        this.lastAutomaticallyAdjustedFont = graphicalString.getLastAutomaticallyAdjustedFont();
        return new Dimension(this.unit.fromPoint(dimension.getWidth()), this.unit.fromPoint(dimension.getHeight()));
    }
    
    /**
     * 指定された位置のボックスの中に文字列を描画する。<br>
     * この処理で自動調整されたフォントはインスタンス内で保持される。
     * 
     * @param string
     * @param x
     * @param y
     * @param width
     * @param height
     * @return 描画された文字列の大きさ。
     */
    public Dimension drawStringInBox(String string, float x, float y, float width, float height) {
        GraphicalString graphicalString = new GraphicalString(string, this);
        if (this.leading != null) {
            graphicalString.setLeading(this.leading);
        }
        graphicalString.setDisabledMultipleLine(this.isDisabledMultipleLine);
        graphicalString.setHorizontalPosition(this.horizontalPosition);
        graphicalString.setVerticalPosition(this.verticalPosition);
        Dimension dimension = graphicalString.drawInBox(this.unit.toPoint(x), this.unit.toPoint(y), this.unit.toPoint(width), this.unit.toPoint(height));
        this.lastAutomaticallyAdjustedFont = graphicalString.getLastAutomaticallyAdjustedFont();
        return new Dimension(this.unit.fromPoint(dimension.getWidth()), this.unit.fromPoint(dimension.getHeight()));
    }

    /**
     * 描画に使用する線の幅を設定する。
     * 
     * @param innerInstance コンストラクタで指定した描画インスタンス。
     * @param width
     */
    protected abstract void setStrokeWidth(I innerInstance, float width);
    
    /**
     * 描画に使用する線の幅を設定する。
     * 
     * @param width
     */
    public void setStrokeWidth(float width) {
        this.setStrokeWidth(this.innerInstance, this.unit.toPoint(width));
    }


    /**
     * 描画に使用する線を破線に設定する。
     * 
     * @param innerInstance コンストラクタで指定した描画インスタンス。
     * @param dashes 破線パターンを表す配列。
     */
    protected abstract void setStrokeDashArray(I innerInstance, float... dashes);
    
    /**
     * 描画に使用する線を破線に設定する。
     * 
     * @param dashes 破線パターンを表す配列。
     */
    public void setStrokeDashArray(float... dashes) {
        float[] innerDashes = new float[dashes.length];
        for (int index = 0; index < dashes.length; index++) {
            innerDashes[index] = this.unit.toPoint(dashes[index]);
        }
        this.setStrokeDashArray(this.innerInstance, innerDashes);
    }
    
    /**
     * 描画に使用する線を破線から実線に戻す。
     * 
     * @param innerInstance コンストラクタで指定した描画インスタンス。
     */
    protected abstract void clearStrokeDashArray(I innerInstance);

    /**
     * 描画に使用する線を破線から実線に戻す。
     */
    public void clearStrokeDashArray() {
        this.clearStrokeDashArray(this.innerInstance);
    }

    /**
     * 指定された開始点から終点まで線を描画する。
     * 
     * @param innerInstance コンストラクタで指定した描画インスタンス。
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    protected abstract void drawLine(I innerInstance, float startX, float startY, float endX, float endY);

    /**
     * 指定された開始点から終点まで線を描画する。
     * 
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    public final void drawLine(float startX, float startY, float endX, float endY) {
        this.drawLine(this.innerInstance, this.unit.toPoint(startX), this.unit.toPoint(startY), this.unit.toPoint(endX), this.unit.toPoint(endY));
    }
    
    /**
     * 指定された開始点から水平方向の線を描画する。
     * 
     * @param startX
     * @param startY
     * @param length
     */
    public final void drawHorizontalLine(float startX, float startY, float length) {
        this.drawLine(startX, startY, startX + length, startY);
    }
    
    /**
     * 指定された開始点から垂直方向の線を描画する。
     * 
     * @param startX
     * @param startY
     * @param length
     */
    public final void drawVerticalLine(float startX, float startY, float length) {
        this.drawLine(startX, startY, startX, startY + length);
    }
    
    /**
     * 指定された位置に矩形の線を描画する。
     * 
     * @param innerInstance コンストラクタで指定した描画インスタンス。
     * @param x
     * @param y
     * @param width
     * @param height
     * @param diameter 角を丸くする場合の直径。
     */
    protected abstract void drawRectangleLine(I innerInstance, float x, float y, float width, float height, float diameter);

    /**
     * 指定された位置に矩形の線を描画する。
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     * @param diameter 角を丸くする場合の直径。
     */
    public final void drawRectangleLine(float x, float y, float width, float height, float diameter) {
        this.drawRectangleLine(this.innerInstance, this.unit.toPoint(x), this.unit.toPoint(y), this.unit.toPoint(width), this.unit.toPoint(height), this.unit.toPoint(diameter));
    }

    /**
     * 指定された位置に矩形の線を描画する。
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public final void drawRectangleLine(float x, float y, float width, float height) {
        this.drawRectangleLine(x, y, width, height, 0);
    }

    /**
     * 指定された位置に指定された大きさに矩形を塗りつぶす。
     * 
     * @param innerInstance コンストラクタで指定した描画インスタンス。
     * @param x
     * @param y
     * @param width
     * @param height
     * @param diameter 角を丸くする場合の直径。
     */
    protected abstract void drawRectangleFill(I innerInstance, float x, float y, float width, float height, float diameter);

    /**
     * 指定された位置に指定された大きさに矩形を塗りつぶす。
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     * @param diameter 角を丸くする場合の直径。
     */
    public final void drawRectangleFill(float x, float y, float width, float height, float diameter) {
        this.drawRectangleFill(this.innerInstance, this.unit.toPoint(x), this.unit.toPoint(y), this.unit.toPoint(width), this.unit.toPoint(height), this.unit.toPoint(diameter));
    }

    /**
     * 指定された位置に指定された大きさの矩形に塗りつぶす。
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public final void drawRectangleFill(float x, float y, float width, float height) {
        this.drawRectangleFill(x, y, width, height, 0);
    }

    /**
     * 指定された位置に楕円形の線を描画する。
     * 
     * @param innerInstance コンストラクタで指定した描画インスタンス。
     * @param x
     * @param y
     * @param width
     * @param height
     */
    protected abstract void drawEllipseLine(I innerInstance, float x, float y, float width, float height);

    /**
     * 指定された位置に楕円形の線を描画する。
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public final void drawEllipseLine(float x, float y, float width, float height) {
        this.drawEllipseLine(this.innerInstance, this.unit.toPoint(x), this.unit.toPoint(y), this.unit.toPoint(width), this.unit.toPoint(height));
    }

    /**
     * 指定された位置に指定された大きさの楕円形に塗りつぶす。
     * 
     * @param innerInstance コンストラクタで指定した描画インスタンス。
     * @param x
     * @param y
     * @param width
     * @param height
     */
    protected abstract void drawEllipseFill(I innerInstance, float x, float y, float width, float height);
    
    /**
     * 指定された位置に指定された大きさの楕円形に塗りつぶす。
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public final void drawEllipseFill(float x, float y, float width, float height) {
        this.drawEllipseFill(this.innerInstance, this.unit.toPoint(x), this.unit.toPoint(y), this.unit.toPoint(width), this.unit.toPoint(height));
    }
    
    /**
     * 指定された位置に指定された大きさで画像を描画する。
     * 
     * @param innerInstance コンストラクタで指定した描画インスタンス。
     * @param bufferedImage
     * @param x
     * @param y
     * @param width
     * @param height
     * @throws IOException 
     */
    protected abstract void drawImage(I innerInstance, BufferedImage bufferedImage, float x, float y, float width, float height) throws IOException;
    
    /**
     * 指定された位置に指定された大きさで画像を描画する。
     * 
     * @param bufferedImage
     * @param x
     * @param y
     * @param width
     * @param height
     * @throws IOException 
     */
    public final void drawImage(BufferedImage bufferedImage, float x, float y, float width, float height) throws IOException {
        this.drawImage(this.innerInstance, bufferedImage, this.unit.toPoint(x), this.unit.toPoint(y), this.unit.toPoint(width), this.unit.toPoint(height));
    }
    
    /**
     * 指定された位置に幅だけを指定して画像を描画する。画像の高さは自動調整される。
     * 
     * @param bufferedImage
     * @param x
     * @param y
     * @param width
     * @return 描画された画像の大きさ。
     * @throws IOException
     */
    public final Dimension drawImageToFitWidth(BufferedImage bufferedImage, float x, float y, float width) throws IOException {
        float ratio = bufferedImage.getWidth() / width;
        float height = bufferedImage.getHeight() / ratio;
        this.drawImage(bufferedImage, x, y, width, height);
        return new Dimension(width, height);
    }
    
    /**
     * 指定された位置に高さだけを指定して画像を描画する。画像の幅は自動調整される。
     * 
     * @param bufferedImage
     * @param x
     * @param y
     * @param height
     * @return 描画された画像の大きさ。
     * @throws IOException
     */
    public final Dimension drawImageToFitHeight(BufferedImage bufferedImage, float x, float y, float height) throws IOException {
        float ratio = bufferedImage.getHeight() / height;
        float width = bufferedImage.getWidth() / ratio;
        this.drawImage(bufferedImage, x, y, width, height);
        return new Dimension(width, height);
    }

    /**
     * 指定された位置に指定された大きさでNW-7のバーコードを描画する。
     * 
     * @param barcode
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public final void drawNW7(String barcode, float x, float y, float width, float height) {
        NW7Writer nw7Writer = new NW7Writer(barcode, this);
        nw7Writer.write(x, y, width, height);
    }
    
    /**
     * 指定された位置に指定された大きさでJAN-13のバーコードを描画する。
     * 
     * @param barcode
     * @param x
     * @param y
     * @param width
     * @param height
     * @param barScale バー描画の拡大率。1が初期値。
     */
    public final void drawJAN13(String barcode, float x, float y, float width, float height, float barScale) {
        JAN13Writer jan13Writer = new JAN13Writer(barcode, this);
        jan13Writer.setBarScale(barScale);
        jan13Writer.write(x, y, width, height);
    }
    
    /**
     * 指定された位置に指定された大きさでJAN-13のバーコードを描画する。
     * 
     * @param barcode
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public final void drawJAN13(String barcode, float x, float y, float width, float height) {
        this.drawJAN13(barcode, x, y, width, height, 1);
    }
    
    /**
     * 横方向と縦方向の倍率を指定して以後の描画をスケーリング(拡大縮小)して行うよう設定する。
     * 
     * @param innerInstance コンストラクタで指定した描画インスタンス。
     * @param scaleX
     * @param scaleY
     */
    protected abstract void setScale(I innerInstance, float scaleX, float scaleY);
    
    /**
     * 角度と回転の中心を指定して以後の描画を回転して行うよう設定する。
     * 
     * @param scaleX
     * @param scaleY
     */
    public final void setScale(float scaleX, float scaleY) {
        this.setScale(this.innerInstance, scaleX, scaleY);
    }
    
    /**
     * 角度と回転の中心を指定して以後の描画を回転して行うよう設定する。
     * 
     * @param innerInstance コンストラクタで指定した描画インスタンス。
     * @param angle
     * @param x
     * @param y 
     */
    protected abstract void setRotate(I innerInstance, int angle, float x, float y);
    
    /**
     * 角度と回転の中心を指定して以後の描画を回転して行うよう設定する。
     * 
     * @param angle
     * @param x
     * @param y 
     */
    public final void setRotate(int angle, float x, float y) {
        this.setRotate(this.innerInstance, angle, this.unit.toPoint(x), this.unit.toPoint(y));
    }
    
    private static List<String> NEED_TO_VERITICAL_LIST = null;
    
    /**
     * 文字列を垂直書きで描画する場合に、90度回転させる必要がある文字列のリストを取得する。
     * 
     * @return
     */
    public static List<String> getNeedToVerticalList() {
        if (DrawContext.NEED_TO_VERITICAL_LIST == null) {
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
            DrawContext.NEED_TO_VERITICAL_LIST = list;
        }
        return DrawContext.NEED_TO_VERITICAL_LIST;
    }

    /**
     * フォントスタイルの列挙型。
     */
    public enum FontStyle {
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
         * @return
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

    /**
     * フォントサイズに関する情報のクラス。
     */
    public static class FontMetrics {

        /**
         * コンストラクタ。
         * 
         * @param ascent ベースラインから上方線までの距離を指定する。
         * @param descent ベースラインから下方線までの距離を指定する。
         * @param leading 下方線の下部から次の行の最上部までの間の推奨される距離を指定する。
         */
        public FontMetrics(float ascent, float descent, float leading) {
            this.ascent = ascent;
            this.descent = descent;
            this.leading = leading;
            this.height = ascent + descent + leading;
        }

        private float ascent;

        /**
         * ベースラインから上方線までの距離を取得する。
         * 
         * @return
         */
        public float getAscent() {
            return this.ascent;
        }

        /**
         * ベースラインから上方線までの距離を取得する。
         * 
         * @return
         */
        public int getIntegerAscent() {
            return (int) this.ascent;
        }
        
        private float descent;

        /**
         * ベースラインから下方線までの距離を取得する。
         * 
         * @return
         */
        public float getDescent() {
            return this.descent;
        }

        /**
         * ベースラインから下方線までの距離を取得する。
         * 
         * @return
         */
        public int getIntegerDescent() {
            return (int) this.descent;
        }
        
        private float leading;

        /**
         * 下方線の下部から次の行の最上部までの間の推奨される距離を取得する。
         * 
         * @return
         */
        public float getLeading() {
            return this.leading;
        }

        /**
         * 下方線の下部から次の行の最上部までの間の推奨される距離を取得する。
         * 
         * @return
         */
        public int getIntegerLeading() {
            return (int) this.leading;
        }

        private float height;

        /**
         * 全体の高さを取得する。
         * 
         * @return
         */
        public float getHeight() {
            return this.height;
        }

        /**
         * 全体の高さを取得する。
         * 
         * @return
         */
        public int getIntegerHeight() {
            return (int) this.height;
        }
    }
}
