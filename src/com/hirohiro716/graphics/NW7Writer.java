package com.hirohiro716.graphics;

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
            for (int barWidthRatio: getHashMapCharacterPatterns().get(this.barcode.substring(index, index + 1))) {
                allWidth += barWidthRatio;
            }
        }
        // 描画する幅を、使用するすべてのエレメントの和で除算して、ひとつの単位に割り当てる幅を計算する
        float oneWidth = width / allWidth;
        // すべてのキャラクタのエレメントとクワイエットゾーンを描画する
        float drawingX = x;
        for (int index = 0; index < this.barcode.length(); index++) {
            int[] characterPatterns = getHashMapCharacterPatterns().get(this.barcode.substring(index, index + 1));
            boolean isPause = false;
            for (int barWidthRatio: characterPatterns) {
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
    
    private static Map<String, int[]> hashMapCharacterPatterns = new HashMap<>();
    
    /**
     * それぞれの英数記号をバーコードの印字パターンの連想配列を取得する。
     * 
     * @return 結果。
     */
    private static Map<String, int[]> getHashMapCharacterPatterns() {
        if (hashMapCharacterPatterns.size() == 0) {
            hashMapCharacterPatterns.put("0", new int[] {1, 1, 1, 1, 1, 3, 3});
            hashMapCharacterPatterns.put("1", new int[] {1, 1, 1, 1, 3, 3, 1});
            hashMapCharacterPatterns.put("2", new int[] {1, 1, 1, 3, 1, 1, 3});
            hashMapCharacterPatterns.put("3", new int[] {3, 3, 1, 1, 1, 1, 1});
            hashMapCharacterPatterns.put("4", new int[] {1, 1, 3, 1, 1, 3, 1});
            hashMapCharacterPatterns.put("5", new int[] {3, 1, 1, 1, 1, 3, 1});
            hashMapCharacterPatterns.put("6", new int[] {1, 3, 1, 1, 1, 1, 3});
            hashMapCharacterPatterns.put("7", new int[] {1, 3, 1, 1, 3, 1, 1});
            hashMapCharacterPatterns.put("8", new int[] {1, 3, 3, 1, 1, 1, 1});
            hashMapCharacterPatterns.put("9", new int[] {3, 1, 1, 3, 1, 1, 1});
            hashMapCharacterPatterns.put("-", new int[] {1, 1, 1, 3, 3, 1, 1});
            hashMapCharacterPatterns.put("$", new int[] {1, 1, 3, 3, 1, 1, 1});
            hashMapCharacterPatterns.put(":", new int[] {3, 1, 1, 1, 3, 1, 3});
            hashMapCharacterPatterns.put("/", new int[] {3, 1, 3, 1, 1, 1, 3});
            hashMapCharacterPatterns.put(".", new int[] {3, 1, 3, 1, 3, 1, 1});
            hashMapCharacterPatterns.put("+", new int[] {1, 1, 3, 1, 3, 1, 3});
            hashMapCharacterPatterns.put("a", new int[] {1, 1, 3, 3, 1, 3, 1});
            hashMapCharacterPatterns.put("b", new int[] {1, 3, 1, 3, 1, 1, 3});
            hashMapCharacterPatterns.put("c", new int[] {1, 1, 1, 3, 1, 3, 3});
            hashMapCharacterPatterns.put("d", new int[] {1, 1, 1, 3, 3, 3, 1});
        }
        return hashMapCharacterPatterns;
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
        return String.valueOf(7 - Integer.parseInt(compute7DR(barcode)));
    }
}
