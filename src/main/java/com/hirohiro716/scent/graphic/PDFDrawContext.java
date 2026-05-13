package com.hirohiro716.scent.graphic;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.Cleaner.Cleanable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hirohiro716.scent.IdentifiableEnum;
import com.hirohiro716.scent.filesystem.Directory;
import com.hirohiro716.scent.filesystem.File;
import com.hirohiro716.scent.graphic.print.Printable;
import com.hirohiro716.scent.reflection.DynamicClass;

/**
 * PDFファイルへの描画命令を実行するコンテキストのクラス。
 */
public class PDFDrawContext extends DrawContext<PDFDrawContext.PDFCreator> {

    /**
     * コンストラクタ。
     * 
     * @param pdfCreator PDFを作成するインスタンスを指定する。
     */
    public PDFDrawContext(PDFCreator pdfCreator) {
        super(pdfCreator);
        this.setLengthUnit(LengthUnit.MILLIMETER);
    }

    /**
     * コンストラクタ。
     * 
     * @param pdfboxLibraryDirectory PDFBoxライブラリの各jarファイルが入ったディレクトリを指定する
     * @param pageSize ページサイズを指定する。
     * @throws ClassNotFoundException
     * @throws Exception
     */
    public PDFDrawContext(Directory pdfboxLibraryDirectory, PageSize pageSize) throws ClassNotFoundException, Exception {
        this(new PDFCreator(pdfboxLibraryDirectory, pageSize));
    }

    @Override
    protected void setColor(PDFCreator pdfCreator, Color color) {
        pdfCreator.setColor(color);
    }

    @Override
    protected Font getFont(PDFCreator pdfCreator) {
        return pdfCreator.getFont();
    }

    @Override
    protected void setFont(PDFCreator pdfCreator, Font font) {
        pdfCreator.setFont(font);
    }

    // NOTE: 出力結果を揃えるため、算出結果が大きめに丸められるAWTの値を使用する。
    //       また、中央揃えの描画時にAWTの計測値の方が、枠線に対して的確に中央に描画できる。
    private AWTDrawContext awtDrawContextForMeasurement = new AWTDrawContext(AWTDrawContext.createGraphics2D());

    @Override
    public FontMetrics createFontMetrics(PDFCreator pdfCreator) {
        this.awtDrawContextForMeasurement.setFont(this.getFont());
        return this.awtDrawContextForMeasurement.createFontMetrics();
    }

    @Override
    protected float measureStringWidth(PDFCreator pdfCreator, String string) {
        this.awtDrawContextForMeasurement.setFont(this.getFont());
        return this.awtDrawContextForMeasurement.measureStringWidth(string);
    }

    @Override
    protected void drawSingleLineString(PDFCreator pdfCreator, String string, float x, float y) {
        pdfCreator.drawSingleLineString(string, x, y);
    }

    @Override
    protected void setStrokeWidth(PDFCreator pdfCreator, float width) {
        pdfCreator.setStrokeWidth(width);
    }

    @Override
    protected void setStrokeDashArray(PDFCreator pdfCreator, float... dashes) {
        pdfCreator.setStrokeDashArray(dashes);
    }

    @Override
    protected void clearStrokeDashArray(PDFCreator pdfCreator) {
        pdfCreator.clearStrokeDashArray(pdfCreator);
    }

    @Override
    protected void drawLine(PDFCreator pdfCreator, float startX, float startY, float endX, float endY) {
        pdfCreator.drawLine(startX, startY, endX, endY);
    }

    @Override
    protected void drawRectangleLine(PDFCreator pdfCreator, float x, float y, float width, float height, float diameter) {
        pdfCreator.drawRectangleLine(x, y, width, height, diameter);
    }

    @Override
    protected void drawRectangleFill(PDFCreator pdfCreator, float x, float y, float width, float height, float diameter) {
        pdfCreator.drawRectangleFill(x, y, width, height, diameter);
    }

    @Override
    protected void drawEllipseLine(PDFCreator pdfCreator, float x, float y, float width, float height) {
        pdfCreator.drawEllipseLine(x, y, width, height);
    }

    @Override
    protected void drawEllipseFill(PDFCreator pdfCreator, float x, float y, float width, float height) {
        pdfCreator.drawEllipseFill(x, y, width, height);
    }

    @Override
    protected void drawImage(PDFCreator pdfCreator, BufferedImage bufferedImage, float x, float y, float width, float height) throws IOException {
        pdfCreator.drawImage(bufferedImage, x, y, width, height);
    }

    /**
     * 指定されたPrintableを描画する。
     * 
     * @param printable
     * @throws PrinterException 
     */
    public void drawPrintable(Printable printable) throws PrinterException {
        int maximumPageNumber = 0;
        while (printable.print(this, maximumPageNumber)) {
            maximumPageNumber++;
        }
        for (int pageIndex = 0; pageIndex < maximumPageNumber; pageIndex++) {
            if (pageIndex > 0) {
                this.getInnerInstance().addPage();
            }
            printable.print(this, pageIndex);
        }
    }

    @Override
    public void setScale(PDFCreator pdfCreator, float scaleX, float scaleY) {
        pdfCreator.setScale(scaleX, scaleY);
    }

    @Override
    public void setRotate(PDFCreator pdfCreator, int angle, float x, float y) {
        pdfCreator.setRotate(angle, x, y);
    }

    /**
     * 横方向と縦方向の位置を指定して以後の描画を移動して行うよう設定する。
     * 
     * @param x
     * @param y 
     */
    public void setTranslate(float x, float y) {
        this.getInnerInstance().setTranslate(this.getLengthUnit().toPoint(x), this.getLengthUnit().toPoint(y));
    }

    /**
     * このインスタンスの内容をPDFファイルに保存する。
     * 
     * @param file
     * @throws IOException
     */
    public final void saveToFile(File file) throws IOException {
        this.getInnerInstance().saveToFile(file);
    }

    /**
     * ページサイズの列挙型。
     */
    public enum PageSize implements IdentifiableEnum<String> {
        /**
         * A3。
         */
        A3("A3", 297, 420),
        /**
         * A4。
         */
        A4("A4", 210, 297),
        /**
         * A5。
         */
        A5("A5", 148, 210),
        /**
         * B4。
         */
        B4("B4", 257, 364),
        /**
         * B5。
         */
        B5("B5", 182, 257),
        /**
         * B6。
         */
        B6("B6", 128, 182),
        ;
        
        /**
         * コンストラクタ。
         * 
         * @param id
         */
        private PageSize(String id, float width, float height) {
            this.id = id;
            this.width = width;
            this.height = height;
        }
        
        private String id;

        @Override
        public String getID() {
            return this.id;
        }
        
        @Override
        public String getName() {
            return this.id;
        }

        private float width;

        /**
         * 幅を取得する。
         * 
         * @return
         */
        public float getWidth() {
            return this.width;
        }

        /**
         * 幅を取得する。
         * 
         * @return
         */
        public int getIntegerWidth() {
            return (int) this.width;
        }
        
        private float height;

        /**
         * 高さを取得する。
         * 
         * @return
         */
        public float getHeight() {
            return this.height;
        }

        /**
         * 高さを取得する。
         * 
         * @return
         */
        public int getIntegerHeight() {
            return (int) this.height;
        }
        
        /**
         * 指定されたIDから、該当する列挙子を取得する。該当するものがない場合はnullを返す。
         * 
         * @param id
         * @return
         */
        public static PageSize enumOf(String id) {
            return IdentifiableEnum.enumOf(id, PageSize.class);
        }

        /**
         * すべての列挙子で、キーがID、値が名前の連想配列を作成する。
         * 
         * @return
         */
        public static List<String> createList() {
            HashMap<String, String> hashMap = IdentifiableEnum.createLinkedHashMap(PageSize.class);
            return new ArrayList<>(hashMap.keySet());
        }
    }

    /**
     * PDFを作成するクラス。<br>
     * ・PDFBox - <a href="https://mvnrepository.com/artifact/org.apache.pdfbox/pdfbox/">https://mvnrepository.com/artifact/org.apache.pdfbox/pdfbox/</a><br>
     * ・FontBox - <a href="https://mvnrepository.com/artifact/org.apache.pdfbox/fontbox/">https://mvnrepository.com/artifact/org.apache.pdfbox/fontbox/</a><br>
     * ・Commons Logging - <a href="https://mvnrepository.com/artifact/commons-logging/commons-logging/">https://mvnrepository.com/artifact/commons-logging/commons-logging/</a><br>
     */
    public static class PDFCreator extends DynamicClass implements Cleanable {

        /**
         * コンストラクタ。
         * 
         * @param pdfboxLibraryDirectory PDFBoxライブラリの各jarファイルが入ったディレクトリを指定する
         * @param pageSize ページサイズを指定する。
         * @throws ClassNotFoundException
         * @throws Exception
         */
        public PDFCreator(Directory pdfboxLibraryDirectory, PageSize pageSize) throws ClassNotFoundException, Exception {
            super(pdfboxLibraryDirectory);
            this.pageSize = pageSize;
            this.pdDocument = this.classPDDocument.getConstructor().newInstance();
            this.addPage();
        }

        private PageSize pageSize;

        private float pageHeight;

        private Class<?> classPDDocument = this.loadClass("org.apache.pdfbox.pdmodel.PDDocument");

        private Class<?> classPDRectangle = this.loadClass("org.apache.pdfbox.pdmodel.common.PDRectangle");

        private Class<?> classPDPage = this.loadClass("org.apache.pdfbox.pdmodel.PDPage");

        private Class<?> classPDPageContentStream = this.loadClass("org.apache.pdfbox.pdmodel.PDPageContentStream");

        private Object pdDocument = null;

        private Object pdPage = null;

        private Object pdPageContentStream = null;

        /**
         * ページを追加する。
         */
        public void addPage() {
            try {
                if (pdPageContentStream != null) {
                    this.classPDPageContentStream.getMethod("close").invoke(this.pdPageContentStream);
                }
                float pageWidth = LengthUnit.MILLIMETER.toPoint(this.pageSize.getWidth());
                this.pageHeight = LengthUnit.MILLIMETER.toPoint(this.pageSize.getHeight());;
                Object pdRectangle = this.classPDRectangle.getConstructor(float.class, float.class).newInstance(pageWidth, this.pageHeight);
                this.pdPage = this.classPDPage.getConstructor(classPDRectangle).newInstance(pdRectangle);
                this.pdPageContentStream = this.classPDPageContentStream.getConstructor(this.classPDDocument, this.classPDPage).newInstance(this.pdDocument, this.pdPage);
                this.classPDDocument.getMethod("addPage", this.classPDPage).invoke(this.pdDocument, this.pdPage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        /**
         * 描画に使用する色を設定する。
         * 
         * @param color
         */
        public void setColor(Color color) {
            try {
                this.classPDPageContentStream.getMethod("setStrokingColor", Color.class).invoke(this.pdPageContentStream, color);
                this.classPDPageContentStream.getMethod("setNonStrokingColor", Color.class).invoke(this.pdPageContentStream, color);
            } catch (Exception exception) {
            }
        }

        private Class<?> classPDFont = this.loadClass("org.apache.pdfbox.pdmodel.font.PDFont");

        private Class<?> classPDType0Font = this.loadClass("org.apache.pdfbox.pdmodel.font.PDType0Font");

        private Class<?> classTrueTypeCollection = this.loadClass("org.apache.fontbox.ttf.TrueTypeCollection");

        private Class<?> classTrueTypeFont = this.loadClass("org.apache.fontbox.ttf.TrueTypeFont");

        private Font awtFont = null;

        private Object pdFont = null;

        /**
         * 描画に使用するフォントを取得する。まだ設定されていない場合はnullを返す。
         * 
         * @return
         */
        public Font getFont() {
            return this.awtFont;
        }

        private Map<String, Object> loadedFonts = new HashMap<>();

        /**
         * 描画に使用するフォントを設定する。
         * 
         * @param font
         */
        public void setFont(Font font) {
            try {
                String fontName = font.getPSName();
                this.pdFont = this.loadedFonts.get(fontName);
                if (this.pdFont == null) {
                    File fontFile = FontCreator.findFontFile(font.getFontName());
                    if (fontFile == null) {
                        throw new Exception("The font file could not be found.");
                    }
                    if (fontFile.getAbsolutePath().endsWith(".ttc")) {
                        try (Closeable trueTypeCollection = (Closeable) this.classTrueTypeCollection.getConstructor(java.io.File.class).newInstance(fontFile.toJavaIoFile())) {
                            Object trueTypeFont = this.classTrueTypeCollection.getMethod("getFontByName", String.class).invoke(trueTypeCollection, fontName);
                            this.pdFont = this.classPDType0Font.getMethod("load", this.classPDDocument, this.classTrueTypeFont, boolean.class).invoke(null, this.pdDocument, trueTypeFont, true);
                        }
                    } else {
                        this.pdFont = this.classPDType0Font.getMethod("load", this.classPDDocument, java.io.File.class).invoke(null, this.pdDocument, fontFile.toJavaIoFile());
                    }
                    this.loadedFonts.put(fontName, this.pdFont);
                }
                this.awtFont = font;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        private Class<?> classPDFontDescriptor = this.loadClass("org.apache.pdfbox.pdmodel.font.PDFontDescriptor");

        /**
         * 描画に使用するフォントサイズの情報を作成する。
         * 
         * @return
         */
        public FontMetrics createFontMetrics() {
            if (this.pdFont != null) {
                try {
                    float fontSize = this.awtFont.getSize2D();
                    Object fontDescriptor = this.classPDFont.getMethod("getFontDescriptor").invoke(this.pdFont);
                    float ascent = (float) this.classPDFontDescriptor.getMethod("getAscent").invoke(fontDescriptor);
                    float descent = (float) this.classPDFontDescriptor.getMethod("getDescent").invoke(fontDescriptor);
                    float leading = (float) this.classPDFontDescriptor.getMethod("getLeading").invoke(fontDescriptor);
                    return new FontMetrics(ascent * fontSize / 1000, descent * fontSize / 1000 * -1, leading * fontSize / 1000);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            return null;
        }

        /**
         * 指定された1行の文字列を描画した場合の大きさを測定したDimensionを作成する。
         * 
         * @param pdfCreator
         * @param string
         * @return
         */
        public float measureStringWidth(String string) {
            if (this.pdFont != null) {
                try {
                    float fontSize = this.awtFont.getSize2D();
                    float width = (float) this.classPDFont.getMethod("getStringWidth", String.class).invoke(this.pdFont, string);
                    return width * fontSize / 1000;
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            return 0;
        }

        /**
         * 指定された1行の文字列を指定された位置に描画する。
         * 
         * @param string
         * @param x
         * @param y
         */
        public void drawSingleLineString(String string, float x, float y) {
            if (this.pdFont != null) {
                try {
                    float fontSize = this.awtFont.getSize2D();
                    this.classPDPageContentStream.getMethod("beginText").invoke(this.pdPageContentStream);
                    this.classPDPageContentStream.getMethod("setFont", this.classPDFont, float.class).invoke(this.pdPageContentStream, this.pdFont, fontSize);
                    this.classPDPageContentStream.getMethod("newLineAtOffset", float.class, float.class).invoke(this.pdPageContentStream, x, this.pageHeight - y);
                    this.classPDPageContentStream.getMethod("showText", String.class).invoke(this.pdPageContentStream, string);
                    this.classPDPageContentStream.getMethod("endText").invoke(this.pdPageContentStream);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

        /**
         * 描画に使用する線の幅を設定する。
         * 
         * @param width
         */
        public void setStrokeWidth(float width) {
            try {
                this.classPDPageContentStream.getMethod("setLineWidth", float.class).invoke(this.pdPageContentStream, width);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        /**
         * 描画に使用する線を破線に設定する。
         * 
         * @param dashes
         */
        public void setStrokeDashArray(float... dashes) {
            try {
                this.classPDPageContentStream.getMethod("setLineDashPattern", float[].class, float.class).invoke(this.pdPageContentStream, dashes, 0);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        /**
         * 描画に使用する線を破線から実線に戻す。
         * 
         * @param pdfCreator
         */
        public void clearStrokeDashArray(PDFCreator pdfCreator) {
            try {
                this.classPDPageContentStream.getMethod("setLineDashPattern", float[].class, float.class).invoke(this.pdPageContentStream, null, 0);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        /**
         * 指定された開始点から終点まで線を描画する。
         * 
         * @param startX
         * @param startY
         * @param endX
         * @param endY
         */
        public void drawLine(float startX, float startY, float endX, float endY) {
            try {
                this.classPDPageContentStream.getMethod("moveTo", float.class, float.class).invoke(this.pdPageContentStream, startX, this.pageHeight - startY);
                this.classPDPageContentStream.getMethod("lineTo", float.class, float.class).invoke(this.pdPageContentStream, endX, this.pageHeight - endY);
                this.classPDPageContentStream.getMethod("stroke").invoke(this.pdPageContentStream);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        /**
         * 右上の角に曲線を描く。
         * 
         * @param startX
         * @param startY
         * @param radiusX
         * @param radiusY
         */
        private void curveAtUpperRightCorner(float startX, float startY, float radiusX, float radiusY) {
            try {
                float handleLengthX = 0.55228475f * radiusX;
                float handleLengthY = 0.55228475f * radiusY;
                this.classPDPageContentStream.getMethod("curveTo", float.class, float.class, float.class, float.class, float.class, float.class).invoke(this.pdPageContentStream,
                    startX + handleLengthX,
                    startY,
                    startX + radiusX,
                    startY - radiusY + handleLengthY,
                    startX + radiusX,
                    startY - radiusY
                );
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        /**
         * 右下の角に曲線を描く。
         * 
         * @param startX
         * @param startY
         * @param radiusX
         * @param radiusY
         */
        private void curveAtLowerRightCorner(float startX, float startY, float radiusX, float radiusY) {
            try {
                float handleLengthX = 0.55228475f * radiusX;
                float handleLengthY = 0.55228475f * radiusY;
                this.classPDPageContentStream.getMethod("curveTo", float.class, float.class, float.class, float.class, float.class, float.class).invoke(this.pdPageContentStream,
                    startX,
                    startY - handleLengthY,
                    startX - radiusX + handleLengthX,
                    startY - radiusY,
                    startX - radiusX,
                    startY - radiusY
                );
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        /**
         * 左下の角に曲線を描く。
         * 
         * @param startX
         * @param startY
         * @param radiusX
         * @param radiusY
         */
        private void curveAtLowerLeftCorner(float startX, float startY, float radiusX, float radiusY) {
            try {
                float handleLengthX = 0.55228475f * radiusX;
                float handleLengthY = 0.55228475f * radiusY;
                this.classPDPageContentStream.getMethod("curveTo", float.class, float.class, float.class, float.class, float.class, float.class).invoke(this.pdPageContentStream,
                    startX - handleLengthX,
                    startY,
                    startX - radiusX,
                    startY + radiusY - handleLengthY,
                    startX - radiusX,
                    startY + radiusY
                );
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        /**
         * 左上の角に曲線を描く。
         * 
         * @param startX
         * @param startY
         * @param radiusX
         * @param radiusY
         */
        private void curveAtUpperLeftCorner(float startX, float startY, float radiusX, float radiusY) {
            try {
                float handleLengthX = 0.55228475f * radiusX;
                float handleLengthY = 0.55228475f * radiusY;
                this.classPDPageContentStream.getMethod("curveTo", float.class, float.class, float.class, float.class, float.class, float.class).invoke(this.pdPageContentStream,
                    startX,
                    startY + handleLengthY,
                    startX + radiusX - handleLengthX,
                    startY + radiusY,
                    startX + radiusX,
                    startY + radiusY
                );
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        /**
         * 指定された位置に矩形の線を描画する。
         * 
         * @param x
         * @param y
         * @param width
         * @param height
         * @param diameter
         */
        public void drawRectangleLine(float x, float y, float width, float height, float diameter) {
            try {
                float radius = diameter / 2;
                this.classPDPageContentStream.getMethod("moveTo", float.class, float.class).invoke(this.pdPageContentStream, x + radius, this.pageHeight - y);
                this.classPDPageContentStream.getMethod("lineTo", float.class, float.class).invoke(this.pdPageContentStream, x + width - radius, this.pageHeight - y);
                this.curveAtUpperRightCorner(x + width - radius, this.pageHeight - y, radius, radius);
                this.classPDPageContentStream.getMethod("lineTo", float.class, float.class).invoke(this.pdPageContentStream, x + width, this.pageHeight - y - height + radius);
                this.curveAtLowerRightCorner(x + width, this.pageHeight - y - height + radius, radius, radius);
                this.classPDPageContentStream.getMethod("lineTo", float.class, float.class).invoke(this.pdPageContentStream, x + radius, this.pageHeight - y - height);
                this.curveAtLowerLeftCorner(x + radius, this.pageHeight - y - height, radius, radius);
                this.classPDPageContentStream.getMethod("lineTo", float.class, float.class).invoke(this.pdPageContentStream, x, this.pageHeight - y - radius);
                this.curveAtUpperLeftCorner(x, this.pageHeight - y - radius, radius, radius);
                this.classPDPageContentStream.getMethod("stroke").invoke(this.pdPageContentStream);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        /**
         * 指定された位置に指定された大きさに矩形を塗りつぶす。
         * 
         * @param x
         * @param y
         * @param width
         * @param height
         * @param diameter
         */
        public void drawRectangleFill(float x, float y, float width, float height, float diameter) {
            try {
                float radius = diameter / 2;
                this.classPDPageContentStream.getMethod("moveTo", float.class, float.class).invoke(this.pdPageContentStream, x + radius, this.pageHeight - y);
                this.classPDPageContentStream.getMethod("lineTo", float.class, float.class).invoke(this.pdPageContentStream, x + width - radius, this.pageHeight - y);
                this.curveAtUpperRightCorner(x + width - radius, this.pageHeight - y, radius, radius);
                this.classPDPageContentStream.getMethod("lineTo", float.class, float.class).invoke(this.pdPageContentStream, x + width, this.pageHeight - y - height + radius);
                this.curveAtLowerRightCorner(x + width, this.pageHeight - y - height + radius, radius, radius);
                this.classPDPageContentStream.getMethod("lineTo", float.class, float.class).invoke(this.pdPageContentStream, x + radius, this.pageHeight - y - height);
                this.curveAtLowerLeftCorner(x + radius, this.pageHeight - y - height, radius, radius);
                this.classPDPageContentStream.getMethod("lineTo", float.class, float.class).invoke(this.pdPageContentStream, x, this.pageHeight - y - radius);
                this.curveAtUpperLeftCorner(x, this.pageHeight - y - radius, radius, radius);
                this.classPDPageContentStream.getMethod("fill").invoke(this.pdPageContentStream);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        /**
         * 指定された位置に楕円形の線を描画する。
         * 
         * @param x
         * @param y
         * @param width
         * @param height
         */
        public void drawEllipseLine(float x, float y, float width, float height) {
            try {
                float radiusX = width / 2;
                float radiusY = height / 2;
                this.classPDPageContentStream.getMethod("moveTo", float.class, float.class).invoke(this.pdPageContentStream, x + radiusX, this.pageHeight - y);
                this.curveAtUpperRightCorner(x + radiusX, this.pageHeight - y, radiusX, radiusY);
                this.curveAtLowerRightCorner(x + width, this.pageHeight - y - height + radiusY, radiusX, radiusY);
                this.curveAtLowerLeftCorner(x + radiusX, this.pageHeight - y - height, radiusX, radiusY);
                this.curveAtUpperLeftCorner(x, this.pageHeight - y - radiusY, radiusX, radiusY);
                this.classPDPageContentStream.getMethod("stroke").invoke(this.pdPageContentStream);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        /**
         * 指定された位置に指定された大きさの楕円形に塗りつぶす。
         * 
         * @param x
         * @param y
         * @param width
         * @param height
         */
        public void drawEllipseFill(float x, float y, float width, float height) {
            try {
                float radiusX = width / 2;
                float radiusY = height / 2;
                this.classPDPageContentStream.getMethod("moveTo", float.class, float.class).invoke(this.pdPageContentStream, x + radiusX, this.pageHeight - y);
                this.curveAtUpperRightCorner(x + radiusX, this.pageHeight - y, radiusX, radiusY);
                this.curveAtLowerRightCorner(x + width, this.pageHeight - y - height + radiusY, radiusX, radiusY);
                this.curveAtLowerLeftCorner(x + radiusX, this.pageHeight - y - height, radiusX, radiusY);
                this.curveAtUpperLeftCorner(x, this.pageHeight - y - radiusY, radiusX, radiusY);
                this.classPDPageContentStream.getMethod("fill").invoke(this.pdPageContentStream);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        private Class<?> classLosslessFactory = this.loadClass("org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory");

        private Class<?> classPDImageXObject = this.loadClass("org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject");

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
        public void drawImage(BufferedImage bufferedImage, float x, float y, float width, float height) throws IOException {
            try {
                Object imageXObject = this.classLosslessFactory.getMethod("createFromImage", this.classPDDocument, BufferedImage.class).invoke(null, this.pdDocument, bufferedImage);
                this.classPDPageContentStream.getMethod("drawImage", this.classPDImageXObject, float.class, float.class, float.class, float.class).invoke(this.pdPageContentStream, imageXObject, x, this.pageHeight - y - height, width, height);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        private Class<?> classMatrix = this.loadClass("org.apache.pdfbox.util.Matrix");

        /**
         * 横方向と縦方向の倍率を指定して以後の描画をスケーリング(拡大縮小)して行うよう設定する。
         * 
         * @param scaleX
         * @param scaleY
         */
        public void setScale(float scaleX, float scaleY) {
            try {
                Object matrix = this.classMatrix.getMethod("getScaleInstance", float.class, float.class).invoke(null, scaleX, scaleY);
                this.classPDPageContentStream.getMethod("transform", this.classMatrix).invoke(this.pdPageContentStream, matrix);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        /**
         * 角度と回転の中心を指定して以後の描画を回転して行うよう設定する。
         * 
         * @param angle
         * @param x
         * @param y
         */
        protected void setRotate(int angle, float x, float y) {
            try {
                Object rotateMatrix = this.classMatrix.getMethod("getRotateInstance", double.class, float.class, float.class).invoke(null, Math.toRadians(angle * -1), x, this.pageHeight - y);
                this.classPDPageContentStream.getMethod("transform", this.classMatrix).invoke(this.pdPageContentStream, rotateMatrix);
                Object translateMatrix = this.classMatrix.getMethod("getTranslateInstance", float.class, float.class).invoke(null, 0, this.pageHeight * -1);
                this.classPDPageContentStream.getMethod("transform", this.classMatrix).invoke(this.pdPageContentStream, translateMatrix);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        /**
         * 横方向と縦方向の位置を指定して以後の描画を移動して行うよう設定する。
         * 
         * @param x
         * @param y
         */
        public void setTranslate(float x, float y) {
            try {
                Object matrix = this.classMatrix.getMethod("getTranslateInstance", float.class, float.class).invoke(null, x, y * -1);
                this.classPDPageContentStream.getMethod("transform", this.classMatrix).invoke(this.pdPageContentStream, matrix);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        /**
         * このインスタンスの内容をPDFファイルに保存する。
         * 
         * @param file
         * @throws IOException
         */
        public final void saveToFile(File file) throws IOException {
            try {
                if (file.exists()) {
                    file.delete();
                }
                this.classPDPageContentStream.getMethod("close").invoke(this.pdPageContentStream);
                this.classPDDocument.getMethod("save", java.io.File.class).invoke(this.pdDocument, file.toJavaIoFile());
                this.classPDDocument.getMethod("close").invoke((this.pdDocument));
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException exception) {
                exception.printStackTrace();
            }
        }

        @Override
        public void clean() {
            try {
                this.classPDPageContentStream.getMethod("close").invoke(this.pdPageContentStream);
            } catch (Exception exception) {
            }
            try {
                this.classPDDocument.getMethod("close").invoke((this.pdDocument));
            } catch (Exception exception) {
            }
        }
    }
}
