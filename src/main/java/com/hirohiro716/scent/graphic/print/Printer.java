package com.hirohiro716.scent.graphic.print;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.print.PrintService;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaTray;

/**
 * プリンターのクラス。
 * 
 * @author hiro
*/
public class Printer extends PrinterElement<PrintService> {
    
    /**
     * コンストラクタ。<br>
     * この要素が内部で保持するライブラリに依存したインスタンスを指定する。
     * 
     * @param printService
     */
    protected Printer(PrintService printService) {
        super(printService);
    }
    
    @Override
    public String getName() {
        return this.getInnerInstance().getName();
    }
    
    private List<PaperSource> paperSources = new ArrayList<>();
    
    /**
     * すべての給紙トレイのリストを作成する。
     * 
     * @return
     */
    public PaperSource[] getPaperSources() {
        if (this.paperSources.size() == 0) {
            Object values = this.getInnerInstance().getSupportedAttributeValues(Media.class, null, null);
            if (values instanceof Media[]) {
                Media[] medias = (Media[]) values;
                for (Media media: medias) {
                    if (media instanceof MediaTray) {
                        this.paperSources.add(new PaperSource((MediaTray) media));
                    }
                }
            }
        }
        return this.paperSources.toArray(new PaperSource[] {});
    }
    
    /**
     * 指定された給紙トレイ名に一致する要素を取得する。見つからなかった場合はnullを返す。
     * 
     * @param nameOfPaperSource
     * @return
     */
    public PaperSource findPaperSource(String nameOfPaperSource) {
        for (PaperSource paperSource: this.getPaperSources()) {
            if (paperSource.getName().equals(nameOfPaperSource)) {
                return paperSource;
            }
        }
        return null;
    }
    
    private List<PaperSize> paperSizes = new ArrayList<>();
    
    /**
     * すべての用紙のリストを作成する。
     * 
     * @return
     */
    public PaperSize[] getPaperSizes() {
        if (this.paperSizes.size() == 0) {
            Object values = this.getInnerInstance().getSupportedAttributeValues(Media.class, null, null);
            if (values instanceof Media[]) {
                Media[] medias = (Media[]) values;
                for (Media media: medias) {
                    if (media instanceof MediaSizeName) {
                        this.paperSizes.add(new PaperSize((MediaSizeName) media));
                    }
                }
            }
        }
        return this.paperSizes.toArray(new PaperSize[] {});
    }
    
    /**
     * 指定された用紙名に一致する要素を取得する。見つからなかった場合はnullを返す。
     * 
     * @param nameOfPaperSize
     * @return
     */
    public PaperSize findPaperSize(String nameOfPaperSize) {
        for (PaperSize paperSize: this.getPaperSizes()) {
            if (paperSize.getName().equals(nameOfPaperSize)) {
                return paperSize;
            }
        }
        return null;
    }
    
    /**
     * 指定された用紙サイズに一致する要素を取得する。見つからなかった場合はnullを返す。
     * 
     * @param millimeterWidth
     * @param millimeterHeight
     * @return
     */
    public PaperSize findPaperSize(float millimeterWidth, float millimeterHeight) {
        for (PaperSize paperSize: this.getPaperSizes()) {
            if (paperSize.getMillimeterWidth() == millimeterWidth && paperSize.getMillimeterHeight() == millimeterHeight) {
                return paperSize;
            }
        }
        return null;
    }
    
    private static Map<String, Printer> PRINTERS = new LinkedHashMap<>();
    
    /**
     * すべてのプリンター定数を作成して保持する。
     */
    private static void addAllPrinters() {
        PrintService[] printServices = java.awt.print.PrinterJob.lookupPrintServices();
        if (Printer.PRINTERS.size() != printServices.length) {
            for (PrintService printService: printServices) {
                if (Printer.PRINTERS.containsKey(printService.getName()) == false) {
                    Printer printer = new Printer(printService);
                    Printer.PRINTERS.put(printer.getName(), printer);
                }
            }
        }
    }
    
    /**
     * すべてのプリンターを取得する。
     * 
     * @return
     */
    public static Printer[] getAll() {
        Printer.addAllPrinters();
        return Printer.PRINTERS.values().toArray(new Printer[] {});
    }

    /**
     * 指定された名前に一致する要素を取得する。見つからなかった場合はnullを返す。
     * 
     * @param name
     * @return
     */
    public static Printer printerOf(String name) {
        Printer.addAllPrinters();
        return Printer.PRINTERS.get(name);
    }

    /**
     * デフォルトの要素を取得する。
     * 
     * @return
     */
    public static Printer getDefault() {
        return Printer.printerOf(javax.print.PrintServiceLookup.lookupDefaultPrintService().getName());
    }
}
