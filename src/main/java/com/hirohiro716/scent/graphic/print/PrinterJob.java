package com.hirohiro716.scent.graphic.print;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import com.hirohiro716.scent.image.Image;
import com.hirohiro716.scent.image.Image.ImageFormat;

/**
 * プリンタージョブを作成するクラス。
 * 
 * @author hiro
*/
public class PrinterJob {
    
    /**
     * コンストラクタ。<br>
     * 使用するプリンターを指定する。
     * 
     * @param printer
     * @throws PrinterException
     */
    public PrinterJob(Printer printer) throws PrinterException {
        this.printer = printer;
        if (this.printer == null) {
            this.printer = Printer.getDefault();
        }
    }
    
    /**
     * コンストラクタ。<br>
     * デフォルトのプリンターを使用する。
     * 
     * @throws PrinterException
     */
    public PrinterJob() throws PrinterException {
        this(null);
    }
    
    private Printer printer;
    
    /**
     * このプリンタージョブで使用するプリンターを取得する。
     * 
     * @return
     */
    public Printer getPrinter() {
        return this.printer;
    }
    
    private String name;
    
    /**
     * このプリンタージョブの名前を指定する。
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * このプリンタージョブの名前を取得する。
     * 
     * @return
     */
    public String getName() {
        return this.name;
    }
    
    private int numberOfCopies = 1;
    
    /**
     * このプリンタージョブで印刷する部数を指定する。
     * 
     * @param numberOfCopies
     */
    public void setNumberOfCopies(int numberOfCopies) {
        this.numberOfCopies = numberOfCopies;
    }
    
    /**
     * このプリンタージョブで印刷する部数を取得する。
     * 
     * @return
     */
    public int getNumberOfCopies() {
        return this.numberOfCopies;
    }
    
    private PaperOrientation paperOrientation = PaperOrientation.PORTRAIT;
    
    /**
     * このプリンタージョブの印刷方向を指定する。
     * 
     * @param paperOrientation
     */
    public void setPaperOrientation(PaperOrientation paperOrientation) {
        this.paperOrientation = paperOrientation;
    }
    
    /**
     * このプリンタージョブの印刷方向を取得する。事前に指定されていない場合はnullを返す。
     * 
     * @return
     */
    public PaperOrientation getPaperOrientation() {
        return this.paperOrientation;
    }
    
    private PaperSource paperSource = null;
    
    /**
     * このプリンタージョブで使用する用紙トレイを指定する。<br>
     * ※ JDKの仕様で用紙サイズと用紙トレイはどちらか一方しかセットできない
     * 
     * @param paperSource
     */
    public void setPaperSource(PaperSource paperSource) {
        this.paperSource = paperSource;
        if (paperSource != null) {
            this.paperSize = null;
        }
    }
    
    /**
     * このプリンタージョブで使用する用紙トレイを取得する。事前に指定されていない場合はnullを返す。
     * 
     * @return
     */
    public PaperSource getPaperSource() {
        return this.paperSource;
    }
    
    private PaperSize paperSize = null;
    
    /**
     * このプリンタージョブで使用する用紙サイズを指定する。<br>
     * ※ JDKの仕様で用紙サイズと用紙トレイはどちらか一方しかセットできない
     * 
     * @param paperSize
     */
    public void setPaperSize(PaperSize paperSize) {
        this.paperSize = paperSize;
        if (paperSize != null) {
            this.paperSource = null;
        }
    }
    
    /**
     * このプリンタージョブで使用する用紙サイズを指定する。
     * 
     * @param millimeterWidth
     * @param millimeterHeight
     */
    public final void setPaperSize(float millimeterWidth, float millimeterHeight) {
        this.setPaperSize(this.printer.findPaperSize(millimeterWidth, millimeterHeight));
    }
    
    /**
     * このプリンタージョブで使用する用紙サイズを取得する。事前に指定されていない場合はnullを返す。
     * 
     * @return
     */
    public PaperSize getPaperSize() {
        return this.paperSize;
    }

    private float millimeterMarginTop = 0;
    
    /**
     * この印刷物の上余白をミリメートルで取得する。
     * 
     * @return
     */
    public float getMarginTop() {
        return this.millimeterMarginTop;
    }
    
    /**
     * この印刷物の上余白をミリメートルでセットする。
     * 
     * @param millimeterMarginTop
     */
    public void setMarginTop(float millimeterMarginTop) {
        this.millimeterMarginTop = millimeterMarginTop;
    }
    
    private float millimeterMarginLeft = 0;
    
    /**
     * この印刷物の左余白をミリメートルで取得する。
     * 
     * @return
     */
    public float getMarginLeft() {
        return this.millimeterMarginLeft;
    }
    
    /**
     * この印刷物の左余白をミリメートルでセットする。
     * 
     * @param millimeterMarginLeft
     */
    public void setMarginLeft(float millimeterMarginLeft) {
        this.millimeterMarginLeft = millimeterMarginLeft;
    }
    
    private Printable printable;
    
    /**
     * 印刷するオブジェクトを取得する。
     * 
     * @return
     */
    public Printable getPrintable() {
        return this.printable;
    }
    
    /**
     * 印刷するオブジェクトを指定する。
     * 
     * @param printable
     */
    public void setPrintable(Printable printable) {
        this.printable = printable;
    }
    
    /**
     * このジョブの設定からAWTのプリンタージョブを作成する。
     * 
     * @return
     * @throws PrinterException
     */
    private java.awt.print.PrinterJob createPrinterJobOfAWT() throws PrinterException {
        java.awt.print.PrinterJob job = java.awt.print.PrinterJob.getPrinterJob();
        job.setPrintService(this.printer.getInnerInstance());
        if (this.name != null) {
            job.setJobName(this.name);
        }
        if (this.printable != null) {
            this.printable.setMarginTop((int) MillimeterValue.newInstance(this.millimeterMarginTop).toPoint());
            this.printable.setMarginLeft((int) MillimeterValue.newInstance(this.millimeterMarginLeft).toPoint());
            this.printable.setNumberOfCopies(this.numberOfCopies);
            job.setPrintable(this.printable);
        }
        return job;
    }
    
    /**
     * このジョブの設定からAWTの属性セットを作成する。
     * 
     * @return
     */
    private PrintRequestAttributeSet createAttributeOfAWT() {
        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
        // Paper source or paper size, the one specified later has priority.
        if (this.paperSource != null) {
            // Specified by paper source
            printRequestAttributeSet.add(this.paperSource.getInnerInstance());
        }
        if (this.paperSize != null) {
            // Specified by paper size
            printRequestAttributeSet.add(this.paperSize.getInnerInstance());
            MediaSizeName mediaSizeName = this.paperSize.getInnerInstance();
            MediaSize mediaSize = MediaSize.getMediaSizeForName(mediaSizeName);
            MediaPrintableArea mediaPrintableArea = new MediaPrintableArea(0, 0, mediaSize.getX(MediaSize.MM), mediaSize.getY(MediaSize.MM), MediaPrintableArea.MM);
            printRequestAttributeSet.add(mediaPrintableArea);
        } else {
            MediaPrintableArea mediaPrintableArea = new MediaPrintableArea(0, 0, 2000, 2000, MediaPrintableArea.MM);
            printRequestAttributeSet.add(mediaPrintableArea);
        }
        if (this.paperOrientation != null) {
            printRequestAttributeSet.add(this.paperOrientation.getInnerInstance());
        }
        return printRequestAttributeSet;
    }
    
    /**
     * 印刷を実行する。
     * 
     * @throws PrinterException
     */
    public void print() throws PrinterException {
        if (this.printable == null) {
            return;
        }
        this.createPrinterJobOfAWT().print(this.createAttributeOfAWT());
    }
    
    /**
     * 新たに作成した画像に対してPrintableの印刷を行い、作成した画像の配列を取得する。
     * 
     * @param expansionRatio 画像を作成する際の拡大率。等倍は1.0。
     * @param imageFormat
     * @return
     * @throws PrinterException
     * @throws IOException
     */
    public Image[] printToImages(double expansionRatio, ImageFormat imageFormat) throws PrinterException, IOException {
        float width = 0;
        float height = 0;
        if (this.paperSize != null) {
            width = (float) (this.paperSize.getMillimeterWidth() * expansionRatio);
            height = (float) (this.paperSize.getMillimeterHeight() * expansionRatio);
        }
        MillimeterValue millimeterWidth = new MillimeterValue(width);
        MillimeterValue millimeterHeight = new MillimeterValue(height);
        int printResult = Printable.PAGE_EXISTS;
        List<Image> images = new ArrayList<>();
        for (int pageIndex = 0; printResult == Printable.PAGE_EXISTS; pageIndex++) {
            BufferedImage bufferedImage = new BufferedImage((int) millimeterWidth.toPoint(), (int) millimeterHeight.toPoint(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2d = bufferedImage.createGraphics();
            graphics2d.setColor(Color.white);
            graphics2d.fillRect(0, 0, (int) millimeterWidth.toPoint(), (int) millimeterHeight.toPoint());
            graphics2d.setColor(Color.black);
            graphics2d.transform(AffineTransform.getScaleInstance(expansionRatio, expansionRatio));
            graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            printResult = this.printable.print(graphics2d, null, pageIndex);
            if (printResult == Printable.PAGE_EXISTS) {
                images.add(new Image(imageFormat, bufferedImage));
            }
        }
        return images.toArray(new Image[] {});
    }
}
