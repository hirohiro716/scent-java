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
     * @param amount 
     */
    public Price(RoundNumber roundNumber, Double amount) {
        this.roundNumber = roundNumber;
        if (amount != null) {
            this.amount = amount;
        }
    }

    /**
     * コンストラクタ。<br>
     * 金額の端数処理方法、金額を指定する。
     * 
     * @param roundNumber
     * @param amount 
     */
    public Price(RoundNumber roundNumber, Long amount) {
        this.roundNumber = roundNumber;
        if (amount != null) {
            this.amount = amount;
        }
    }
    
    private RoundNumber roundNumber = RoundNumber.ROUND;
    
    private double amount = 0;
    
    /**
     * このインスタンスの金額を取得する。
     * 
     * @return 結果。
     */
    public long toLong() {
        return this.roundNumber.calculate(this.amount);
    }

    /**
     * このインスタンスの金額を取得する。
     * 
     * @return 結果。
     */
    public int toInteger() {
        return (int) this.roundNumber.calculate(this.amount);
    }
    
    /**
     * このインスタンスに金額をセットする。
     * 
     * @param amount
     */
    public void setAmount(Double amount) {
        if (amount == null) {
            return;
        }
        this.amount = amount;
    }

    /**
     * このインスタンスに金額をセットする。
     * 
     * @param amount
     */
    public void setAmount(Long amount) {
        if (amount == null) {
            return;
        }
        this.amount = amount;
    }

    /**
     * このインスタンスの価格に含まれる内税額を計算する。
     * 
     * @param taxRatePercent パーセントの税率。
     * @return 結果。
     */
    public long calculateInnerTax(int taxRatePercent) {
        return this.roundNumber.calculate(this.amount / (100 + taxRatePercent) * taxRatePercent);
    }
    
    /**
     * このインスタンスの価格に対する外税額を計算する。
     * 
     * @param taxRatePercent パーセントの税率。
     * @return 結果。
     */
    public long calculateOuterTax(int taxRatePercent) {
        return this.roundNumber.calculate(this.amount * taxRatePercent / 100);
    }
    
    /**
     * このメソッドはコンストラクタの呼び出しと同じで、新しいインスタンスを作成する。
     * 
     * @param roundNumber 金額の端数処理方法の列挙型。
     * @param amount 金額。
     * @return 新しいインスタンス。
     */
    public static Price newInstance(RoundNumber roundNumber, Double amount) {
        return new Price(roundNumber, amount);
    }

    /**
     * このメソッドはコンストラクタの呼び出しと同じで、新しいインスタンスを作成する。
     * 
     * @param roundNumber 金額の端数処理方法の列挙型。
     * @param amount 金額。
     * @return 新しいインスタンス。
     */
    public static Price newInstance(RoundNumber roundNumber, Long amount) {
        return new Price(roundNumber, amount);
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