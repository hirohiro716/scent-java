package com.hirohiro716.graphics.print;

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
    }
    
    @Override
    public String getName() {
        return this.getInnerInstance().toString();
    }
}
