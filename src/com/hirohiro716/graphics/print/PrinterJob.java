package com.hirohiro716.graphics.print;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.OrientationRequested;

import com.hirohiro716.image.Image;
import com.hirohiro716.image.Image.ImageFormat;

/**
 * プリンタージョブを作成するクラス。
 * 
 * @author hiro
 *
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
        this.printerJob = java.awt.print.PrinterJob.getPrinterJob();
        if (printer != null) {
            this.printerJob.setPrintService(printer.getInnerInstance());
        }
    }
    
    /**
     * コンストラクタ。<br>
     * デフォルトのプリンターを使用する。
     * 
     * @throws PrinterException
     */
    public PrinterJob() throws PrinterException {
        this(Printer.getDefault());
    }
    
    private java.awt.print.PrinterJob printerJob;
    
    /**
     * このプリンタージョブで使用するプリンターを取得する。
     * 
     * @return 結果。
     */
    public Printer getPrinter() {
        PrintService printService = this.printerJob.getPrintService();
        if (printService == null) {
            return Printer.getDefault();
        }
        return new Printer(printService);
    }
    
    /**
     * このプリンタージョブの名前を指定する。
     * 
     * @param name
     */
    public void setName(String name) {
        this.printerJob.setJobName(name);
    }
    
    /**
     * このプリンタージョブの名前を取得する。
     * 
     * @return 結果。
     */
    public String getName() {
        return this.printerJob.getJobName();
    }
    
    /**
     * このプリンタージョブで印刷する部数を指定する。
     * 
     * @param numberOfCopies
     */
    public void setCopies(int numberOfCopies) {
        this.printerJob.setCopies(numberOfCopies);
    }
    
    /**
     * このプリンタージョブで印刷する部数を取得する。
     * 
     * @return 結果。
     */
    public int getCopies() {
        return this.printerJob.getCopies();
    }
    
    private PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
    
    /**
     * このプリンタージョブの印刷方向を指定する。
     * 
     * @param paperOrientation
     */
    public void setPaperOrientation(PaperOrientation paperOrientation) {
        if (paperOrientation == null) {
            return;
        }
        this.printRequestAttributeSet.add(paperOrientation.getInnerInstance());
    }
    
    /**
     * このプリンタージョブの印刷方向を取得する。事前に指定されていない場合はnullを返す。
     * 
     * @return 結果。
     */
    public PaperOrientation getPaperOrientation() {
        OrientationRequested orientationRequested = (OrientationRequested) this.printRequestAttributeSet.get(OrientationRequested.class);
        if (orientationRequested == null) {
            return null;
        }
        return new PaperOrientation(orientationRequested);
    }
    
    /**
     * このプリンタージョブで使用する用紙トレイを指定する。
     * 
     * @param paperSource
     */
    public void setPaperSource(PaperSource paperSource) {
        if (paperSource == null) {
            return;
        }
        this.printRequestAttributeSet.add(paperSource.getInnerInstance());
    }
    
    /**
     * このプリンタージョブで使用する用紙トレイを取得する。事前に指定されていない場合はnullを返す。
     * 
     * @return 結果。
     */
    public PaperSource getPaperSource() {
        MediaTray mediaTray = (MediaTray) this.printRequestAttributeSet.get(MediaTray.class);
        if (mediaTray == null) {
            return null;
        }
        return new PaperSource(mediaTray);
    }
    
    private float requestedMediaWidth = 0;
    
    private float requestedMediaHeight = 0;
    
    /**
     * このプリンタージョブで使用する用紙サイズを指定する。プリンターに該当するサイズがない場合はデフォルトが使用される。
     * 
     * @param millimeterWidth
     * @param millimeterHeight
     */
    public void setPaperSize(float millimeterWidth, float millimeterHeight) {
        MediaSizeName mediaSizeName = MediaSize.findMedia(millimeterWidth, millimeterHeight, Size2DSyntax.MM);
        MediaSize mediaSize = MediaSize.getMediaSizeForName(mediaSizeName);
        if (mediaSizeName != null && mediaSize.getX(Size2DSyntax.MM) == millimeterWidth && mediaSize.getY(Size2DSyntax.MM) == millimeterHeight) {
            this.printRequestAttributeSet.add(mediaSize);
        }
        this.requestedMediaWidth = millimeterWidth;
        this.requestedMediaHeight = millimeterHeight;
    }
    
    /**
     * このプリンタージョブで使用する用紙サイズを指定する。
     * 
     * @param paperSize
     */
    public void setPaperSize(PaperSize paperSize) {
        if (paperSize == null) {
            return;
        }
        this.printRequestAttributeSet.add(paperSize.getInnerInstance());
        MediaSize mediaSize = MediaSize.getMediaSizeForName(paperSize.getInnerInstance());
        this.requestedMediaWidth = mediaSize.getX(Size2DSyntax.MM);
        this.requestedMediaHeight = mediaSize.getY(Size2DSyntax.MM);
    }
    
    /**
     * このプリンタージョブで使用する用紙サイズを取得する。事前に指定されていない場合はnullを返す。
     * 
     * @return 結果。
     */
    public PaperSize getPaperSize() {
        MediaSize mediaSize = (MediaSize) this.printRequestAttributeSet.get(MediaSize.class);
        if (mediaSize == null) {
            return null;
        }
        return new PaperSize(mediaSize.getMediaSizeName());
    }
    
    /**
     * このプリンタージョブの印刷範囲を指定する。
     * 
     * @param printableArea
     */
    public void setPrintableArea(PrintableArea printableArea) {
        if (printableArea == null) {
            return;
        }
        this.printRequestAttributeSet.add(printableArea.getInnerInstance());
    }
    
    /**
     * このプリンタージョブの印刷範囲を取得する。事前に指定されていない場合はnullを返す。
     * 
     * @return 結果。
     */
    public PrintableArea getPrintableArea() {
        MediaPrintableArea mediaPrintableArea = (MediaPrintableArea) this.printRequestAttributeSet.get(MediaPrintableArea.class);
        if (mediaPrintableArea == null) {
            return null;
        }
        return new PrintableArea(mediaPrintableArea);
    }
    
    private java.awt.print.Printable printable;
    
    /**
     * 印刷するオブジェクトを指定する。
     * 
     * @param printable
     */
    public void setPrintable(Printable printable) {
        this.printable = printable;
    }
    
    /**
     * 印刷するオブジェクトを指定する。
     * 
     * @param printable
     */
    public void setPrintable(java.awt.print.Printable printable) {
        this.printable = printable;
    }
    
    /**
     * 印刷設定を行うダイアログを表示する。ダイアログがキャンセルされた場合はfalseを返す。
     * 
     * @return 結果。
     */
    public boolean showPrintDialog() {
        return this.printerJob.printDialog(this.printRequestAttributeSet);
    }
    
    /**
     * 印刷を実行する。
     * 
     * @throws PrinterException
     */
    public void print() throws PrinterException {
        this.printerJob.setPrintable(this.printable);
        this.printerJob.print(this.printRequestAttributeSet);
    }
    
    /**
     * 新たに作成した画像に対してPrintableの印刷を行い、作成した画像の配列を取得する。
     * 
     * @param expansionRatio 画像を作成する際の拡大率。等倍は1.0。
     * @param imageFormat
     * @return 結果。
     * @throws PrinterException
     * @throws IOException
     */
    public Image[] printToImages(double expansionRatio, ImageFormat imageFormat) throws PrinterException, IOException {
        MillimeterValue width;
        MillimeterValue height;
        if (this.getPaperOrientation() == null
                || this.getPaperOrientation() == PaperOrientation.PORTRAIT || this.getPaperOrientation() == PaperOrientation.REVERSE_PORTRAIT) {
            width = new MillimeterValue(this.requestedMediaWidth * expansionRatio);
            height = new MillimeterValue(this.requestedMediaHeight * expansionRatio);
        } else {
            width = new MillimeterValue(this.requestedMediaHeight * expansionRatio);
            height = new MillimeterValue(this.requestedMediaWidth * expansionRatio);
        }
        int printResult = Printable.PAGE_EXISTS;
        List<Image> images = new ArrayList<>();
        for (int pageIndex = 0; printResult == Printable.PAGE_EXISTS; pageIndex++) {
            BufferedImage bufferedImage = new BufferedImage((int) width.toPoint(), (int) height.toPoint(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2d = bufferedImage.createGraphics();
            graphics2d.setColor(Color.white);
            graphics2d.fillRect(0, 0, (int) width.toPoint(), (int) height.toPoint());
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
