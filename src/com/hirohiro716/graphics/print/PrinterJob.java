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
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.OrientationRequested;

import com.hirohiro716.StringObject;
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
     * 使用するPrintServiceを指定する。
     * 
     * @param printService
     * @throws PrinterException
     */
    public PrinterJob(PrintService printService) throws PrinterException {
        this.printerJob = java.awt.print.PrinterJob.getPrinterJob();
        if (printService != null) {
            this.printerJob.setPrintService(printService);
        }
    }

    /**
     * コンストラクタ。<br>
     * 使用するPrintServiceの名前を指定する。
     * 
     * @param printServiceName
     * @throws PrinterException 
     */
    public PrinterJob(String printServiceName) throws PrinterException {
        this(PrinterJob.findPrintService(printServiceName));
    }
    
    /**
     * コンストラクタ。<br>
     * デフォルトのPrintServiceを使用する。
     * 
     * @throws PrinterException
     */
    public PrinterJob() throws PrinterException {
        this((PrintService) null);
    }
    
    private java.awt.print.PrinterJob printerJob;
    
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
     * このプリンタージョブで使用するカラー属性を指定する。
     * 
     * @param chromaticity
     */
    public void setChromaticity(Chromaticity chromaticity) {
        this.printRequestAttributeSet.add(chromaticity);
    }
    
    /**
     * このプリンタージョブで使用するカラー属性を取得する。事前に指定されていない場合はnullを返す。
     * 
     * @return 結果。
     */
    public Chromaticity getChromaticity() {
        Chromaticity chromaticity = (Chromaticity) this.printRequestAttributeSet.get(Chromaticity.class);
        return chromaticity;
    }
    
    /**
     * このプリンタージョブの印刷方向を指定する。
     * 
     * @param orientationRequested
     */
    public void setOrientationRequested(OrientationRequested orientationRequested) {
        this.printRequestAttributeSet.add(orientationRequested);
    }
    
    /**
     * このプリンタージョブの印刷方向を取得する。事前に指定されていない場合はnullを返す。
     * 
     * @return 結果。
     */
    public OrientationRequested getOrientationRequested() {
        return (OrientationRequested) this.printRequestAttributeSet.get(OrientationRequested.class);
    }
    
    /**
     * このプリンタージョブで使用できる、すべてのメディアトレイを取得する。
     * 
     * @return 結果。
     */
    public MediaTray[] lookupMediaTrays() {
        List<MediaTray> mediaTrays = new ArrayList<>();
        Object values = this.printerJob.getPrintService().getSupportedAttributeValues(Media.class, null, null);
        if (values instanceof Media[]) {
            Media[] medias = (Media[]) values;
            for (Media media : medias) {
                if (media instanceof MediaTray) {
                    mediaTrays.add((MediaTray) media);
                }
            }
        }
        return mediaTrays.toArray(new MediaTray[] {});
    }
    
    /**
     * このプリンタージョブで使用するメディアトレイを指定する。
     * 
     * @param mediaTray
     */
    public void setMediaTray(MediaTray mediaTray) {
        this.printRequestAttributeSet.add(mediaTray);
    }
    
    /**
     * このプリンタージョブで使用するメディアトレイを取得する。事前に指定されていない場合はnullを返す。
     * 
     * @return 結果。
     */
    public MediaTray getMediaTray() {
        return (MediaTray) this.printRequestAttributeSet.get(MediaTray.class);
    }
    
    /**
     * このプリンタージョブで使用するメディアトレイを指定する。
     * 
     * @param mediaTrayName
     */
    public void setMediaTray(String mediaTrayName) {
        StringObject mediaTrayNameObject = new StringObject(mediaTrayName);
        for (MediaTray mediaTray : this.lookupMediaTrays()) {
            if (mediaTrayNameObject.equals(mediaTray.getName())) {
                this.setMediaTray(mediaTray);
            }
        }
    }
    
    private float requestedMediaWidth = 0;
    
    private float requestedMediaHeight = 0;
    
    /**
     * このプリンタージョブで使用するメディアサイズを指定する。プリンターに該当するサイズがない場合はデフォルトが使用される。
     * 
     * @param millimeterWidth
     * @param millimeterHeight
     */
    public void setMediaSize(float millimeterWidth, float millimeterHeight) {
        MediaSizeName mediaSizeName = MediaSize.findMedia(millimeterWidth, millimeterHeight, Size2DSyntax.MM);
        MediaSize mediaSize = MediaSize.getMediaSizeForName(mediaSizeName);
        if (mediaSizeName != null && mediaSize.getX(Size2DSyntax.MM) == millimeterWidth && mediaSize.getY(Size2DSyntax.MM) == millimeterHeight) {
            this.printRequestAttributeSet.add(mediaSize);
        }
        this.requestedMediaWidth = millimeterWidth;
        this.requestedMediaHeight = millimeterHeight;
    }
    
    /**
     * このプリンタージョブで使用するメディアサイズを指定する。
     * 
     * @param mediaSizeName
     */
    public void setMediaSize(MediaSizeName mediaSizeName) {
        this.printRequestAttributeSet.add(mediaSizeName);
        MediaSize mediaSize = MediaSize.getMediaSizeForName(mediaSizeName);
        this.requestedMediaWidth = mediaSize.getX(Size2DSyntax.MM);
        this.requestedMediaHeight = mediaSize.getY(Size2DSyntax.MM);
    }
    
    /**
     * このプリンタージョブで使用するメディアサイズを取得する。事前に指定されていない場合はnullを返す。
     * 
     * @return 結果。
     */
    public MediaSize getMediaSize() {
        return (MediaSize) this.printRequestAttributeSet.get(MediaSize.class);
    }
    
    /**
     * このプリンタージョブの印刷範囲を指定する。
     * 
     * @param millimeterX
     * @param millimeterY
     * @param millimeterWidth
     * @param millimeterHeight
     */
    public void setMediaPrintableArea(float millimeterX, float millimeterY, float millimeterWidth, float millimeterHeight) {
        MediaPrintableArea area = new MediaPrintableArea(millimeterX, millimeterY, millimeterX, millimeterY, MediaPrintableArea.MM);
        this.printRequestAttributeSet.add(area);
    }
    
    /**
     * このプリンタージョブの印刷範囲を取得する。事前に指定されていない場合はnullを返す。
     * 
     * @return 結果。
     */
    public MediaPrintableArea getMediaPrintableArea() {
        return (MediaPrintableArea) this.printRequestAttributeSet.get(MediaPrintableArea.class);
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
        if (this.getOrientationRequested() == null
                || this.getOrientationRequested() == OrientationRequested.PORTRAIT || this.getOrientationRequested() == OrientationRequested.REVERSE_PORTRAIT) {
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
    
    /**
     * すべてのPrintServiceを取得する。
     * 
     * @return 結果。
     */
    public static PrintService[] lookupPrintServices() {
        return java.awt.print.PrinterJob.lookupPrintServices();
    }
    
    /**
     * 指定された名前のPrintServiceを取得する。
     * 
     * @param printServiceName
     * @return 結果。
     */
    public static PrintService findPrintService(String printServiceName) {
        for (PrintService printService : PrinterJob.lookupPrintServices()) {
            if (printService.getName().equals(printServiceName)) {
                return printService;
            }
        }
        return null;
    }
}
