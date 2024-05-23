package com.hirohiro716.scent.graphic;

import java.awt.Font;

/**
 * フォントを作成する静的関数のクラス。
 * 
 * @author hiro
*/
public class FontCreator {

    /**
     * 指定されたフォントをベースに新しいサイズのフォントを作成する。
     * 
     * @param baseFont
     * @param size
     * @return 結果。
     */
    public static Font create(Font baseFont, int size) {
        return new Font(baseFont.getName(), baseFont.getStyle(), size);
    }
    
    /**
     * 指定されたフォントをベースに新しい種類のフォントを作成する。
     * 
     * @param baseFont
     * @param fontName
     * @return 結果。
     */
    public static Font create(Font baseFont, String fontName) {
        return new Font(fontName, baseFont.getStyle(), baseFont.getSize());
    }
    
    /**
     * 指定されたフォントをベースに、指定された拡大率で新しいサイズのフォントを作成する。
     * 
     * @param baseFont
     * @param magnificationRatio
     * @return 結果。
     */
    public static Font create(Font baseFont, double magnificationRatio) {
        return new Font(baseFont.getName(), baseFont.getStyle(), (int) (baseFont.getSize2D() * magnificationRatio));
    }
}
