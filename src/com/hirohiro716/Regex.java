package com.hirohiro716;

import java.util.regex.Pattern;

/**
 * 汎用の正規表現パターン列挙型。
 * 
 * @author hiro
 *
 */
public enum Regex {
    /**
     * 数字のみ。
     */
    INTEGER("^[0-9]{0,}$"),
    /**
     * 正負数字のみ。
     */
    INTEGER_NEGATIVE("^[0-9\\-]{0,}$"),
    /**
     * 全角数字のみ。
     */
    INTEGER_WIDE("^[０-９]{0,}$"),
    /**
     * 小数のみ。
     */
    DECIMAL("^[0-9\\.]{0,}$"),
    /**
     * 正負小数のみ。
     */
    DECIMAL_NEGATIVE("^[0-9\\.\\-]{0,}$"),
    /**
     * 電話番号に使用される、半角数字、ハイフンのみ。
     */
    TELEPHONE("^[0-9\\-]{0,}$"),
    /**
     * 日時に使用される、半角数字、ハイフン、スラッシュ、コロンのみ。
     */
    DATETIME("^[0-9\\-\\/: ]{0,}$"),
    /**
     * 日付に使用される、半角数字、ハイフン、スラッシュのみ。
     */
    DATE("^[0-9\\-\\/]{0,}$"),
    /**
     * 時刻に使用される、半角数字、コロンのみ。
     */
    TIME("^[0-9:]{0,}$"),
    /**
     * 全角半角アルファベットのみ。
     */
    ALPHABET("^[a-zA-Zａ-ｚＡ-Ｚ]{0,}$"),
    /**
     * 半角アルファベットのみ。
     */
    ALPHABET_NARROW("^[a-zA-Z]{0,}$"),
    /**
     * 全角アルファベットのみ。
     */
    ALPHABET_WIDE("^[ａ-ｚＡ-Ｚ]{0,}$"),
    /**
     * 半角小文字アルファベットのみ。
     */
    ALPHABET_NARROW_LOWER("^[a-z]{0,}$"),
    /**
     * 半角大文字アルファベットのみ。
     */
    ALPHABET_NARROW_UPPER("^[A-Z]{0,}$"),
    /**
     * 全角小文字アルファベットのみ。
     */
    ALPHABET_WIDE_LOWER("^[ａ-ｚ]{0,}$"),
    /**
     * 全角大文字アルファベットのみ。
     */
    ALPHABET_WIDE_UPPER("^[Ａ-Ｚ]{0,}$"),
    /**
     * 半角カタカナのみ。
     */
    KATAKANA_NARROW("^[ｦ-ﾟ]{0,}$"),
    /**
     * 全角カタカナのみ。
     */
    KATAKANA_WIDE("^[ァ-ヴー]{0,}$"),
    /**
     * ひらがなのみ。
     */
    HIRAGANA("^[ぁ-んー]{0,}$"),
    /**
     * 半角文字のみ。
     */
    HALF("^[ -~｡-ﾟ]{0,}$"),
    /**
     * 全角文字のみ。
     */
    WIDE("^[^ -~｡-ﾟ]{0,}$"),
    ;

    /**
     * コンストラクタ。
     * 
     * @param regex
     */
    private Regex(String regex) {
        this.regex = regex;
    }
    
    private String regex;
    
    /**
     * 正規表現を取得する。
     * 
     * @return 結果。
     */
    public String getRegex() {
        return this.regex;
    }
    
    /**
     * コンパイル済みの正規表現を取得する。
     * 
     * @return 結果。
     */
    public Pattern getPattern() {
        return Pattern.compile(this.regex);
    }
    
    /**
     * 全角/半角の英数、大文字/小文字の英数、ひらがな/全角カタカナを区別しないコンパイル済みの正規表現を作成する。
     * 
     * @param value
     * @return Pattern
     */
    public static Pattern makeRoughComparisonPattern(String value) {
        return Pattern.compile(makeRoughComparison(value));
    }

    /**
     * 全角/半角の英数、大文字/小文字の英数、ひらがな/全角カタカナを区別しない正規表現を作成する。
     * 
     * @param value
     * @return 正規表現。
     */
    public static String makeRoughComparison(String value) {
        StringObject valueObject = new StringObject(value);
        if (valueObject.length() == 0) {
            return valueObject.toString();
        }
        StringObject result = new StringObject();
        for (int index = 0; index < valueObject.length(); index++) {
            StringObject one = valueObject.clone().extract(index, index + 1);
            convert: {
                if (Regex.HIRAGANA.getPattern().matcher(one.toString()).matches()) {
                    result.append("(");
                    result.append(one);
                    result.append("|");
                    result.append(one.katakana());
                    result.append(")");
                    break convert;
                }
                if (Regex.KATAKANA_WIDE.getPattern().matcher(one.toString()).matches()) {
                    result.append("(");
                    result.append(one);
                    result.append("|");
                    result.append(one.hiragana());
                    result.append(")");
                    break convert;
                }
                if (Regex.INTEGER.getPattern().matcher(one.toString()).matches()) {
                    result.append("(");
                    result.append(one);
                    result.append("|");
                    result.append(one.wide());
                    result.append(")");
                    break convert;
                }
                if (Regex.INTEGER_WIDE.getPattern().matcher(one.toString()).matches()) {
                    result.append("(");
                    result.append(one);
                    result.append("|");
                    result.append(one.narrow());
                    result.append(")");
                    break convert;
                }
                if (Regex.ALPHABET.getPattern().matcher(one.toString()).matches()) {
                    StringObject alphabet = one.clone().narrow().lower();
                    result.append("(");
                    result.append(alphabet);
                    result.append("|");
                    result.append(alphabet.clone().upper());
                    result.append("|");
                    result.append(alphabet.clone().wide());
                    result.append("|");
                    result.append(alphabet.clone().upper().wide());
                    result.append(")");
                    break convert;
                }
                result.append(one);
            }
        }
        return result.toString();
    }

}
