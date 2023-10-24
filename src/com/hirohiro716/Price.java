package com.hirohiro716;

/**
 * 価格のクラス。
 * 
 * @author hiro
 *
 */
public class Price {

    /**
     * コンストラクタ。<br>
     * 金額の端数処理方法を指定する。
     * 
     * @param roundNumber
     */
    public Price(RoundNumber roundNumber) {
        this.roundNumber = roundNumber;
    }

    /**
     * コンストラクタ。<br>
     * 金額の端数処理方法、金額を指定する。
     * 
     * @param roundNumber
     * @param price 
     */
    public Price(RoundNumber roundNumber, Double price) {
        this.roundNumber = roundNumber;
        if (price != null) {
            this.price = price;
        }
    }

    /**
     * コンストラクタ。<br>
     * 金額の端数処理方法、金額を指定する。
     * 
     * @param roundNumber
     * @param price 
     */
    public Price(RoundNumber roundNumber, Long price) {
        this.roundNumber = roundNumber;
        if (price != null) {
            this.price = price;
        }
    }
    
    private RoundNumber roundNumber = RoundNumber.ROUND;
    
    private double price = 0;
    
    /**
     * このインスタンスの金額を取得する。
     * 
     * @return 結果。
     */
    public long toLong() {
        return this.roundNumber.calculate(this.price);
    }

    /**
     * このインスタンスの金額を取得する。
     * 
     * @return 結果。
     */
    public int toInteger() {
        return (int) this.roundNumber.calculate(this.price);
    }
    
    /**
     * このインスタンスに金額をセットする。
     * 
     * @param price
     */
    public void setPrice(Double price) {
        if (price == null) {
            return;
        }
        this.price = price;
    }

    /**
     * このインスタンスに金額をセットする。
     * 
     * @param price
     */
    public void setPrice(Long price) {
        if (price == null) {
            return;
        }
        this.price = price;
    }

    /**
     * このインスタンスの価格に含まれる内税額を計算する。
     * 
     * @param taxRatePercent パーセントの税率。
     * @return 結果。
     */
    public long calculateInnerTax(int taxRatePercent) {
        return this.roundNumber.calculate(this.price / (100 + taxRatePercent) * taxRatePercent);
    }
    
    /**
     * このインスタンスの価格に対する外税額を計算する。
     * 
     * @param taxRatePercent パーセントの税率。
     * @return 結果。
     */
    public long calculateOuterTax(int taxRatePercent) {
        return this.roundNumber.calculate(this.price * taxRatePercent / 100);
    }
    
    /**
     * このメソッドはコンストラクタの呼び出しと同じで、新しいインスタンスを作成する。
     * 
     * @param roundNumber 金額の端数処理方法の列挙型。
     * @param price 金額。
     * @return 新しいインスタンス。
     */
    public static Price newInstance(RoundNumber roundNumber, Double price) {
        return new Price(roundNumber, price);
    }

    /**
     * このメソッドはコンストラクタの呼び出しと同じで、新しいインスタンスを作成する。
     * 
     * @param roundNumber 金額の端数処理方法の列挙型。
     * @param price 金額。
     * @return 新しいインスタンス。
     */
    public static Price newInstance(RoundNumber roundNumber, Long price) {
        return new Price(roundNumber, price);
    }

    private static final int LENGTH = 10;
    
    /**
     * 価格の最大文字数を取得する。
     * 
     * @return 結果。
     */
    public static int getLength() {
        return Price.LENGTH;
    }
    
    private static final int LONG_LENGTH = 18;
    
    /**
     * 大きな価格の最大文字数を取得する。
     * 
     * @return 結果。
     */
    public static int getLongLength() {
        return Price.LONG_LENGTH;
    }
}