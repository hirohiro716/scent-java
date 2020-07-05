package com.hirohiro716.graphics.print;

import javax.print.attribute.standard.MediaPrintableArea;

/**
 * 印刷可能範囲のクラス。
 * 
 * @author hiro
 *
 */
public class PrintableArea extends PrinterElement<MediaPrintableArea> {
    
    /**
     * コンストラクタ。<br>
     * この要素が内部で保持するライブラリに依存したインスタンスを指定する。
     * 
     * @param mediaPrintableArea
     */
    protected PrintableArea(MediaPrintableArea mediaPrintableArea) {
        super(mediaPrintableArea);
    }

    /**
     * コンストラクタ。<br>
     * 印刷可能な範囲を指定する。
     * 
     * @param millimeterX
     * @param millimeterY
     * @param millimeterWidth
     * @param millimeterHeight
     */
    protected PrintableArea(float millimeterX, float millimeterY, float millimeterWidth, float millimeterHeight) {
        super(new MediaPrintableArea(millimeterX, millimeterY, millimeterX, millimeterY, MediaPrintableArea.MM));
    }
    
    @Override
    public String getName() {
        return this.getInnerInstance().getName();
    }
}
