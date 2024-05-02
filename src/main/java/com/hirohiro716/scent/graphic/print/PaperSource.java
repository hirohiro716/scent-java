package com.hirohiro716.scent.graphic.print;

import javax.print.attribute.standard.MediaTray;

/**
 * 給紙トレイのクラス。
 * 
 * @author hiro
 *
 */
public class PaperSource extends PrinterElement<MediaTray> {
    
    /**
     * コンストラクタ。<br>
     * この要素が内部で保持するライブラリに依存したインスタンスを指定する。
     * 
     * @param mediaTray
     */
    protected PaperSource(MediaTray mediaTray) {
        super(mediaTray);
    }
    
    @Override
    public String getName() {
        return this.getInnerInstance().toString();
    }
}
