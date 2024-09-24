package com.hirohiro716.scent.graphic.print;

import java.util.ArrayList;
import java.util.List;
import javax.print.attribute.standard.OrientationRequested;

/**
 * 用紙向きのクラス。
 * 
 * @author hiro
*/
public class PaperOrientation extends PrinterElement<OrientationRequested> {
    
    /**
     * コンストラクタ。<br>
     * この要素が内部で保持するライブラリに依存したインスタンスを指定する。
     * 
     * @param orientationRequested
     */
    protected PaperOrientation(OrientationRequested orientationRequested) {
        super(orientationRequested);
    }
    
    @Override
    public String getName() {
        return this.getInnerInstance().toString();
    }
    
    /**
     * すべての要素のリストを作成する。
     * 
     * @return
     */
    public static List<PaperOrientation> createList() {
        List<PaperOrientation> list = new ArrayList<>();
        list.add(PaperOrientation.PORTRAIT);
        list.add(PaperOrientation.LANDSCAPE);
        list.add(PaperOrientation.REVERSE_PORTRAIT);
        list.add(PaperOrientation.REVERSE_LANDSCAPE);
        return list;
    }

    /**
     * 指定された名前に一致する要素を取得する。見つからなかった場合はnullを返す。
     * 
     * @param name
     * @return
     */
    public static PaperOrientation paperOrientationOf(String name) {
        for (PaperOrientation paperOrientation : PaperOrientation.createList()) {
            if (paperOrientation.getName().equals(name)) {
                return paperOrientation;
            }
        }
        return null;
    }
    
    /**
     * 縦向き。
     */
    public static final PaperOrientation PORTRAIT = new PaperOrientation(OrientationRequested.PORTRAIT);
    
    /**
     * 横向き。
     */
    public static final PaperOrientation LANDSCAPE = new PaperOrientation(OrientationRequested.LANDSCAPE);
    
    /**
     * 縦向き逆。
     */
    public static final PaperOrientation REVERSE_PORTRAIT = new PaperOrientation(OrientationRequested.REVERSE_PORTRAIT);

    /**
     * 横向き逆。
     */
    public static final PaperOrientation REVERSE_LANDSCAPE = new PaperOrientation(OrientationRequested.REVERSE_LANDSCAPE);
}
