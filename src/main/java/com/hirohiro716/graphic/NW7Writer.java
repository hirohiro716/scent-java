package com.hirohiro716.graphic;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import com.hirohiro716.StringObject;

/**
 * NW-7のバーコードを描画するクラス。
 * 
 * @author hiro
 *
 */
public class NW7Writer {
    
    /**
     * コンストラクタ。
     * 
     * @param barcode 
     * @param graphics2D
     */
    public NW7Writer(String barcode, Graphics2D graphics2D) {
        this.barcode = barcode;
        this.graphics2D = graphics2D;
    }
    
    private String barcode;
    
    private Graphics2D graphics2D;
    
    /**
     * NW-7のバーコードを描画する。
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void write(float x, float y, float width, float height) {
        // キャラクタ間のギャップの比率
        int gapWidthRatio = 4;
        // すべてのエレメントの和を計算する
        float allWidth = 0;
        for (int index = 0; index < this.barcode.length(); index++) {
            if (index > 0) {
                allWidth += gapWidthRatio;
            }
            for (float barWidthRatio: NW7Writer.getHashMapCharacterPatterns().get(this.barcode.substring(index, index + 1))) {
                allWidth += barWidthRatio;
            }
        }
        // 描画する幅を、使用するすべてのエレメントの和で除算して、ひとつの単位に割り当てる幅を計算する
        float oneWidth = width / allWidth;
        // すべてのキャラクタのエレメントとクワイエットゾーンを描画する
        float drawingX = x;
        for (int index = 0; index < this.barcode.length(); index++) {
            float[] characterPatterns = NW7Writer.getHashMapCharacterPatterns().get(this.barcode.substring(index, index + 1));
            boolean isPause = false;
            for (float barWidthRatio: characterPatterns) {
                float barWidth = oneWidth * barWidthRatio;
                if (isPause == false) {
                    Rectangle2D.Float rectangle = new Rectangle2D.Float(drawingX, y, barWidth, height);
                    this.graphics2D.fill(rectangle);
                    isPause = true;
                } else {
                    isPause = false;
                }
                drawingX += barWidth;
            }
            drawingX += oneWidth * gapWidthRatio;
        }
    }
    
    private static Map<String, float[]> MAP_CHARACTER_PATTERNS = null;
    
    /**
     * それぞれの英数記号をバーコードの印字パターンの連想配列を取得する。
     * 
     * @return 結果。
     */
    private static Map<String, float[]> getHashMapCharacterPatterns() {
        if (NW7Writer.MAP_CHARACTER_PATTERNS == null) {
            Map<String, float[]> map = new HashMap<>();
            map.put("0", new float[] {1, 1, 1, 1, 1, 2.5f, 2.5f});
            map.put("1", new float[] {1, 1, 1, 1, 2.5f, 2.5f, 1});
            map.put("2", new float[] {1, 1, 1, 2.5f, 1, 1, 2.5f});
            map.put("3", new float[] {2.5f, 2.5f, 1, 1, 1, 1, 1});
            map.put("4", new float[] {1, 1, 2.5f, 1, 1, 2.5f, 1});
            map.put("5", new float[] {2.5f, 1, 1, 1, 1, 2.5f, 1});
            map.put("6", new float[] {1, 2.5f, 1, 1, 1, 1, 2.5f});
            map.put("7", new float[] {1, 2.5f, 1, 1, 2.5f, 1, 1});
            map.put("8", new float[] {1, 2.5f, 2.5f, 1, 1, 1, 1});
            map.put("9", new float[] {2.5f, 1, 1, 2.5f, 1, 1, 1});
            map.put("-", new float[] {1, 1, 1, 2.5f, 2.5f, 1, 1});
            map.put("$", new float[] {1, 1, 2.5f, 2.5f, 1, 1, 1});
            map.put(":", new float[] {2.5f, 1, 1, 1, 2.5f, 1, 2.5f});
            map.put("/", new float[] {2.5f, 1, 2.5f, 1, 1, 1, 2.5f});
            map.put(".", new float[] {2.5f, 1, 2.5f, 1, 2.5f, 1, 1});
            map.put("+", new float[] {1, 1, 2.5f, 1, 2.5f, 1, 2.5f});
            map.put("a", new float[] {1, 1, 2.5f, 2.5f, 1, 2.5f, 1});
            map.put("b", new float[] {1, 2.5f, 1, 2.5f, 1, 1, 2.5f});
            map.put("c", new float[] {1, 1, 1, 2.5f, 1, 2.5f, 2.5f});
            map.put("d", new float[] {1, 1, 1, 2.5f, 2.5f, 2.5f, 1});
            NW7Writer.MAP_CHARACTER_PATTERNS = map;
        }
        return NW7Writer.MAP_CHARACTER_PATTERNS;
    }
    
    /**
     * 7DRでチェックディジットを算出する。
     * 
     * @param barcode
     * @return 結果。
     */
    public static String compute7DR(String barcode) {
        long barcodeLong = StringObject.newInstance(barcode).toLong();
        return String.valueOf(barcodeLong % 7);
    }
    
    /**
     * 7DSRでチェックディジットを算出する。
     * 
     * @param barcode
     * @return 結果。
     */
    public static String compute7DSR(String barcode) {
        return String.valueOf(7 - Integer.parseInt(NW7Writer.compute7DR(barcode)));
    }
}
