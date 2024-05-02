package com.hirohiro716.scent.graphic;

import java.awt.Color;

import com.hirohiro716.scent.StringObject;

/**
 * 色を作成する静的関数のクラス。
 * 
 * @author hiro
 *
 */
public class ColorCreator {
    
    /**
     * 指定されたHTMLやCSSで使用する "#000000" や "#fff" のような形式の色を作成する。
     * 
     * @param webColor
     * @return 結果。
     */
    public static Color create(String webColor) {
        StringObject colorString = new StringObject(webColor);
        if (colorString.toString().indexOf("#") == 0) {
            try {
                int red;
                int green;
                int blue;
                switch (colorString.length()) {
                case 4:
                    red = Integer.parseInt(colorString.clone().extract(1, 2).repeat(2).toString(), 16);
                    green = Integer.parseInt(colorString.clone().extract(2, 3).repeat(2).toString(), 16);
                    blue = Integer.parseInt(colorString.clone().extract(3, 4).repeat(2).toString(), 16);
                    return new Color(red, green, blue);
                case 7:
                    red = Integer.parseInt(colorString.clone().extract(1, 3).toString(), 16);
                    green = Integer.parseInt(colorString.clone().extract(3, 5).toString(), 16);
                    blue = Integer.parseInt(colorString.clone().extract(5, 7).toString(), 16);
                    return new Color(red, green, blue);
                }
            } catch (Exception exception) {
            }
        }
        return createTransparent(Color.BLACK, 0);
    }
    
    /**
     * 指定された色をベースに、指定された不透明度で新しい色を作成する。
     * 
     * @param baseColor
     * @param opacity
     * @return 結果。
     */
    public static Color createTransparent(Color baseColor, double opacity) {
        int alpha = (int) (baseColor.getAlpha() * opacity);
        return new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha);
    }
}
