package com.hirohiro716.scent;

/**
 * 個人番号のクラス。
 */
public class IndividualNumber {

    /**
     * コンストラクタ。<br>
     * 個人番号を指定する。
     * 
     * @param number
     */
    public IndividualNumber(String number) {
        this.number = StringObject.newInstance(number).extract("[0-9]").toString();
    }

    private String number;

    /**
     * 個人番号のチェックディジットを算出する。算出できない場合はnullを返す。
     * 
     * @return
     */
    public String computeCheckDigit() {
        StringObject individualNumber = new StringObject(this.number);
        individualNumber.extract(0, 11);
        if (individualNumber.length() < 11) {
            return null;
        }
        int sumQn = 0;
        int[] qn = new int[] {6, 5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
        for (int index = 0; index < individualNumber.length(); index++) {
            sumQn += individualNumber.clone().extract(index, index + 1).toInteger() * qn[index];
        }
        int remainder = sumQn % 11;
        int checkDigit = 0;
        if (remainder > 1) {
            checkDigit = 11 - remainder;
        }
        return String.valueOf(checkDigit);
    }

    /**
     * 個人番号の値として有効かどうかを確認する。有効な場合はtrueを返す。
     * 
     * @return
     */
    public boolean isValid() {
        StringObject individualNumber = new StringObject(this.number);
        if (individualNumber.length() != 12) {
            return false;
        }
        individualNumber.extract(11, 12);
        return individualNumber.equals(this.computeCheckDigit());
    }

    /**
     * 個人番号のチェックディジットを算出して12桁目にセットする。
     */
    public void computeAndSetCheckDigit() {
        String checkDigit = this.computeCheckDigit();
        if (checkDigit == null) {
            return;
        }
        StringObject individualNumber = new StringObject(this.number);
        individualNumber.extract(0, 11);
        individualNumber.append(checkDigit);
        this.number = individualNumber.toString();
    }

    @Override
    public String toString() {
        return this.number;
    }
}
