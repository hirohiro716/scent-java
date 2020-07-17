package com.hirohiro716.graphic.print;

import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;

/**
 * 用紙サイズのクラス。
 * 
 * @author hiro
 *
 */
public class PaperSize extends PrinterElement<MediaSizeName> {
    
    /**
     * コンストラクタ。<br>
     * この要素が内部で保持するライブラリに依存したインスタンスを指定する。
     * 
     * @param mediaSizeName
     */
    protected PaperSize(MediaSizeName mediaSizeName) {
        super(mediaSizeName);
        MediaSize mediaSize = MediaSize.getMediaSizeForName(mediaSizeName);
        if (mediaSize != null) {
            this.millimeterWidth = mediaSize.getX(MediaSize.MM);
            this.millimeterHeight = mediaSize.getY(MediaSize.MM);
        }
    }
    
    private float millimeterWidth = -1;
    
    private float millimeterHeight = -1;
    
    /**
     * この用紙サイズの幅を取得する。
     * 
     * @return 結果。
     */
    public float getMillimeterWidth() {
        return this.millimeterWidth;
    }
    
    /**
     * この用紙サイズの高さを取得する。
     * 
     * @return 結果。
     */
    public float getMillimeterHeight() {
        return this.millimeterHeight;
    }
    
    @Override
    public String getName() {
        return this.getInnerInstance().toString();
    }
}
