package com.hirohiro716.scent.graphic;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import com.hirohiro716.scent.StringObject;

/**
 * JAN-13のバーコードを描画するクラス。
 * 
 * @author hiro
*/
public class JAN13Writer {
    
    /**
     * コンストラクタ。
     * 
     * @param barcode
     * @param graphics2D
     */
    public JAN13Writer(String barcode, Graphics2D graphics2D) {
        this.barcode = barcode;
        this.graphics2D = graphics2D;
    }
    
    private String barcode;
    
    private Graphics2D graphics2D;

    private float barScale = 1;

    /**
     * バーの拡大率をセットする。1が初期値。
     * 
     * @param barScale
     */
    public void setBarScale(float barScale) {
        this.barScale = barScale;
    }
    
    /**
     * JAN-13のバーコードを描画する。
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void write(float x, float y, float width, float height) {
        float oneModule = width / 95f;
        if (oneModule <= 0 || isValid(this.barcode) == false) {
            return;
        }
        StringObject barcode = new StringObject(this.barcode);
        float drawingX = x;
        // ノーマルガードバー
        this.graphics2D.fill(new Rectangle2D.Float(drawingX, y, oneModule * this.barScale, height));
        drawingX += oneModule * 2;
        this.graphics2D.fill(new Rectangle2D.Float(drawingX, y, oneModule * this.barScale, height));
        drawingX += oneModule;
        // リーディングディジットを取得する
        int leadingDigit = barcode.clone().extract(0, 1).toInteger();
        // センターガードバーの左側を描画する
        int[] leftParityType = JAN13Writer.LEFT_PARITY_TYPES[leadingDigit];
        for (int index = 1; index <= 6; index++) {
            int typeIndex = leftParityType[index - 1];
            int drawing = barcode.clone().extract(index, index + 1).toInteger();
            int[] parities = JAN13Writer.LEFT_PARITIES[typeIndex][drawing];
            for (int parity: parities) {
                if (parity == 1) {
                    this.graphics2D.fill(new Rectangle2D.Float(drawingX, y, oneModule * this.barScale, height));
                }
                drawingX += oneModule;
            }
        }
        // センターガードバー
        drawingX += oneModule;
        this.graphics2D.fill(new Rectangle2D.Float(drawingX, y, oneModule * this.barScale, height));
        drawingX += oneModule * 2;
        this.graphics2D.fill(new Rectangle2D.Float(drawingX, y, oneModule * this.barScale, height));
        drawingX += oneModule * 2;
        // センターガードバーの右側を描画する
        for (int index = 7; index <= 12; index++) {
            int drawing = barcode.clone().extract(index, index + 1).toInteger();
            int[] parities = JAN13Writer.RIGHT_PARITIES[drawing];
            for (int parity: parities) {
                if (parity == 1) {
                    this.graphics2D.fill(new Rectangle2D.Float(drawingX, y, oneModule * this.barScale, height));
                }
                drawingX += oneModule;
            }
        }
        // ライトガードバー
        this.graphics2D.fill(new Rectangle2D.Float(drawingX, y, oneModule * this.barScale, height));
        drawingX += oneModule * 2;
        this.graphics2D.fill(new Rectangle2D.Float(drawingX, y, oneModule * this.barScale, height));
    }
    
    /**
     * JAN-13の値として有効かどうかを確認する。有効な場合はtrueを返す。
     * 
     * @param barcode
     * @return
     */
    public static boolean isValid(String barcode) {
        StringObject stringObject = new StringObject(barcode);
        if (stringObject.length() == 13) {
            String checkDigit = JAN13Writer.computeCheckDigit(barcode);
            if (stringObject.extract(-1).equals(checkDigit)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * JAN-13の値13桁のうち、チェックディジットを含まない12桁を取得する。<br>
     * 12桁に満たない場合や、数値以外の文字列が含まれる場合はnullを返す。
     * 
     * @param barcode
     * @return 12桁の値、失敗した場合はnullを返す。
     */
    public static String exceptCheckDigit(String barcode) {
        StringObject stringObject = new StringObject(barcode);
        if (stringObject.length() >= 12 && stringObject.toLong() != null) {
            return stringObject.extract(0, 12).toString();
        }
        return null;
    }
    
    /**
     * JAN-13のチェックディジットを算出する。
     * 
     * @param barcode
     * @return 計算結果、失敗した場合はnullを返す。
     */
    public static String computeCheckDigit(String barcode) {
        StringObject stringObject = new StringObject(barcode);
        if (stringObject.length() < 12 || stringObject.toLong() == null) {
            return null;
        }
        stringObject.extract(0, 12);
        StringObject odd = new StringObject();
        StringObject even = new StringObject();
        // 文字位置が奇数か偶数かで分ける
        for (int index = 0; index < barcode.length(); index++) {
            StringObject addition = stringObject.clone().extract(index, index + 1);
            if ((index + 1) % 2 != 0) {
                odd.append(addition);
            } else {
                even.append(addition);
            }
        }
        // 偶数の数字すべての和を3倍する
        int evenSum3 = (int) even.sum() * 3;
        // 偶数x3＋奇数の数字すべての和
        StringObject evenSum3AndOddSum = new StringObject(evenSum3 + odd.sum());
        // 下一桁を取る
        StringObject last = evenSum3AndOddSum.extract(-1);
        // 10から下一桁を引く
        StringObject tenMinusLast = new StringObject(10 - last.toInteger());
        // また下一桁を取って返す
        return tenMinusLast.extract(-1).toString();
    }
    
    /**
     * 先頭の数字(リーディングディジット)から左6桁のパリティ種類を特定するための配列。<br>
     * 0の場合は奇数パリティ(A)を表し、1の場合は偶数パリティ(B)を表す。
     */
    protected static final int[][] LEFT_PARITY_TYPES = { { 0, 0, 0, 0, 0, 0 }, // 0
            { 0, 0, 1, 0, 1, 1 }, // 1
            { 0, 0, 1, 1, 0, 1 }, // 2
            { 0, 0, 1, 1, 1, 0 }, // 3
            { 0, 1, 0, 0, 1, 1 }, // 4
            { 0, 1, 1, 0, 0, 1 }, // 5
            { 0, 1, 1, 1, 0, 0 }, // 6
            { 0, 1, 0, 1, 0, 1 }, // 7
            { 0, 1, 0, 1, 1, 0 }, // 8
            { 0, 1, 1, 0, 1, 0 }, // 9
    };
    
    /**
     * 左側6桁のパリティ。<br>
     * 「0」が奇数パリティ(A)、「1」が偶数パリティ(B)を表す。
     */
    protected static final int[][][] LEFT_PARITIES = { {
            // 奇数パリティ(A)
            { 0, 0, 0, 1, 1, 0, 1 }, // 0
            { 0, 0, 1, 1, 0, 0, 1 }, // 1
            { 0, 0, 1, 0, 0, 1, 1 }, // 2
            { 0, 1, 1, 1, 1, 0, 1 }, // 3
            { 0, 1, 0, 0, 0, 1, 1 }, // 4
            { 0, 1, 1, 0, 0, 0, 1 }, // 5
            { 0, 1, 0, 1, 1, 1, 1 }, // 6
            { 0, 1, 1, 1, 0, 1, 1 }, // 7
            { 0, 1, 1, 0, 1, 1, 1 }, // 8
            { 0, 0, 0, 1, 0, 1, 1 }, // 9
    }, {
            // 偶数パリティ(B)
            { 0, 1, 0, 0, 1, 1, 1 }, // 0
            { 0, 1, 1, 0, 0, 1, 1 }, // 1
            { 0, 0, 1, 1, 0, 1, 1 }, // 2
            { 0, 1, 0, 0, 0, 0, 1 }, // 3
            { 0, 0, 1, 1, 1, 0, 1 }, // 4
            { 0, 1, 1, 1, 0, 0, 1 }, // 5
            { 0, 0, 0, 0, 1, 0, 1 }, // 6
            { 0, 0, 1, 0, 0, 0, 1 }, // 7
            { 0, 0, 0, 1, 0, 0, 1 }, // 8
            { 0, 0, 1, 0, 1, 1, 1 }, // 9
    }
    };
    
    /**
     * 右側6桁のパリティ。
     */
    protected static final int[][] RIGHT_PARITIES = { { 1, 1, 1, 0, 0, 1, 0 }, // 0
            { 1, 1, 0, 0, 1, 1, 0 }, // 1
            { 1, 1, 0, 1, 1, 0, 0 }, // 2
            { 1, 0, 0, 0, 0, 1, 0 }, // 3
            { 1, 0, 1, 1, 1, 0, 0 }, // 4
            { 1, 0, 0, 1, 1, 1, 0 }, // 5
            { 1, 0, 1, 0, 0, 0, 0 }, // 6
            { 1, 0, 0, 0, 1, 0, 0 }, // 7
            { 1, 0, 0, 1, 0, 0, 0 }, // 8
            { 1, 1, 1, 0, 1, 0, 0 }, // 9
    };
}
