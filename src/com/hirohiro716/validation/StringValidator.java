package com.hirohiro716.validation;

import java.util.Date;

import com.hirohiro716.DynamicArray;
import com.hirohiro716.StringObject;
import com.hirohiro716.datetime.Datetime;

/**
 * 文字列の検証を行うクラス。
 * 
 * @author hiro
 *
 */
public class StringValidator {
    
    /**
     * コンストラクタ。
     */
    public StringValidator() {
        super();
    }
    
    /**
     * コンストラクタ。<br>
     * 検証する対象の名前を指定する。指定された名前はエラーメッセージの作成に使用される。
     * 
     * @param targetName
     */
    public StringValidator(String targetName) {
        this.targetName = targetName;
    }
    
    private String targetName = null;
    
    /**
     * 検証する対象の名前をセットする。指定された名前はエラーメッセージの作成に使用される。
     * 
     * @param targetName
     */
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
    
    private DynamicArray<Pattern> parameters = new DynamicArray<>();
    
    /**
     * 検証対象が長さゼロの文字列、またはnullの場合に例外をスローする検証を予約する。
     */
    public void addBlankCheck() {
        this.parameters.put(Pattern.BLANK, null);
    }
    
    /**
     * 検証対象に整数以外の文字列が含まれる場合に例外をスローする検証を予約する。
     */
    public void addIntegerCheck() {
        this.parameters.put(Pattern.INTEGER, null);
    }
    
    /**
     * 検証対象に整数または小数点以外の文字列が含まれる場合に例外をスローする検証を予約する。
     */
    public void addDecimalCheck() {
        this.parameters.put(Pattern.DECIMAL, null);
    }
    
    /**
     * 検証対象が指定された文字数ではない場合に例外をスローする検証を予約する。<br>
     * 引数に負数が指定された場合は検証を実行しない。
     * 
     * @param length
     */
    public void addLengthCheck(int length) {
        this.parameters.put(Pattern.LENGTH, length);
    }
    
    /**
     * 検証対象が指定された文字数を超える場合に例外をスローする検証を予約する。<br>
     * 引数に負数が指定された場合は検証を実行しない。
     * 
     * @param length
     */
    public void addMaximumLengthCheck(int length) {
        this.parameters.put(Pattern.MAXIMUM_LENGTH, length);
    }
    
    /**
     * 検証対象が指定された文字数未満の場合に例外をスローする検証を予約する。<br>
     * 引数に負数が指定された場合は検証を実行しない。
     * 
     * @param length
     */
    public void addMinimumLengthCheck(int length) {
        this.parameters.put(Pattern.MINIMUM_LENGTH, length);
    }
    
    /**
     * 検証対象がゼロと等しい場合に例外をスローする検証を予約する。
     */
    public void addZeroCheck() {
        this.parameters.put(Pattern.ZERO, null);
    }
    
    /**
     * 検証対象が指定された値を超える場合に例外をスローする検証を予約する。
     * 
     * @param maximumValue
     */
    public void addMaximumValueCheck(double maximumValue) {
        this.parameters.put(Pattern.MAXIMUM_VALUE, maximumValue);
    }
    
    /**
     * 検証対象が指定された値未満の場合に例外をスローする検証を予約する。
     * 
     * @param minimumValue
     */
    public void addMinimumValueCheck(double minimumValue) {
        this.parameters.put(Pattern.MINIMUM_VALUE, minimumValue);
    }
    
    /**
     * 検証対象の文字列が日付、または時刻として有効ではない場合に例外をスローする検証を予約する。
     */
    public void addDatetimeCheck() {
        this.parameters.put(Pattern.DATETIME, null);
    }
    
    /**
     * 検証対象の文字列が電話番号として有効ではない場合に例外をスローする検証を予約する。
     */
    public void addTelephoneNumberCheck() {
        this.parameters.put(Pattern.TELEPHONE_NUMBER, null);
    }
    
    /**
     * 検証対象の文字列が正規表現とマッチしない場合に例外をスローする検証を予約する。
     * 
     * @param regex
     */
    public void addRegexCheck(String regex) {
        this.parameters.put(Pattern.REGEX, regex);
    }
    
    /**
     * 検証対象の文字列が正規表現とマッチする場合に例外をスローする検証を予約する。
     * 
     * @param regex
     */
    public void addReverseRegexCheck(String regex) {
        this.parameters.put(Pattern.REVERSE_REGEX, regex);
    }
    
    /**
     * すべての検証の予約をクリアする。
     */
    public void clear() {
        this.parameters.clear();
    }
    
    /**
     * 予約されている検証をすべて実行する。
     * 
     * @param target 対象の文字列。
     * @throws ValidationException
     */
    public void validate(Object target) throws ValidationException {
        StringObject value = new StringObject(target);
        for (Pattern pattern : this.parameters.getKeys()) {
            switch (pattern) {
            case BLANK:
                if (value.length() == 0) {
                    throw createValidationException(pattern);
                }
                break;
            case INTEGER:
                if (value.length() > 0 && value.toLong() == null) {
                    throw createValidationException(pattern);
                }
                break;
            case DECIMAL:
                if (value.length() > 0 && value.toDouble() == null) {
                    throw createValidationException(pattern);
                }
                break;
            case LENGTH:
                int length = this.parameters.get(pattern);
                if (value.length() > 0 && length > -1 && value.length() != length) {
                    throw createValidationException(pattern);
                }
                break;
            case MAXIMUM_LENGTH:
                int maximumLength = this.parameters.get(pattern);
                if (value.length() > 0 && maximumLength > -1 && value.length() > maximumLength) {
                    throw createValidationException(pattern);
                }
                break;
            case MINIMUM_LENGTH:
                int minimumLength = this.parameters.get(pattern);
                if (value.length() > 0 && minimumLength > -1 && value.length() < minimumLength) {
                    throw createValidationException(pattern);
                }
                break;
            case ZERO:
                if (value.toDouble() != null && value.toDouble() == 0) {
                    throw createValidationException(pattern);
                }
                break;
            case MAXIMUM_VALUE:
                double maximumValue = this.parameters.get(pattern);
                if (value.toDouble() != null && value.toDouble() > maximumValue) {
                    throw createValidationException(pattern);
                }
                break;
            case MINIMUM_VALUE:
                double minimumValue = this.parameters.get(pattern);
                if (value.toDouble() != null && value.toDouble() < minimumValue) {
                    throw createValidationException(pattern);
                }
                break;
            case DATETIME:
                if (target instanceof Date == false && target instanceof Datetime == false && value.length() > 0 && value.toDate() == null) {
                    throw createValidationException(pattern);
                }
                break;
            case TELEPHONE_NUMBER:
                DynamicArray<Integer> telephoneNumberParts = new DynamicArray<>(value.split("-"));
                if (value.length() > 0 && value.toString().matches("^[\\+\\-0-9]{10,}$") == false || telephoneNumberParts.size() < 3 || telephoneNumberParts.containsValue("")) {
                    throw createValidationException(pattern);
                }
                break;
            case REGEX:
                String regex = this.parameters.get(pattern);
                if (value.length() > 0 && value.toString().matches(regex) == false) {
                    throw createValidationException(pattern);
                }
                break;
            case REVERSE_REGEX:
                String regexReverse = this.parameters.get(pattern);
                if (value.length() > 0 && value.toString().matches(regexReverse)) {
                    throw createValidationException(pattern);
                }
                break;
            }
        }
    }
    
    /**
     * 検証パターン毎のエラーメッセージを作成する。
     * 
     * @param pattern
     * @return 結果。
     */
    private ValidationException createValidationException(Pattern pattern) {
        StringObject message = new StringObject();
        if (this.targetName == null) {
            message.append("対象");
        } else {
            message.append(this.targetName);
        }
        if (this.parameters.containsKey(pattern)) {
            switch (pattern) {
            case LENGTH:
            case MAXIMUM_LENGTH:
            case MINIMUM_LENGTH:
            case MAXIMUM_VALUE:
            case MINIMUM_VALUE:
                StringObject number = new StringObject(this.parameters.get(pattern));
                message.append(pattern.getErrorMessage().replace(StringValidator.ERROR_MESSAGE_ARGUMENT, number.removeMeaninglessDecimalPoint().toString()));
                break;
            case BLANK:
            case INTEGER:
            case DECIMAL:
            case ZERO:
            case DATETIME:
            case TELEPHONE_NUMBER:
            case REGEX:
            case REVERSE_REGEX:
                message.append(pattern.getErrorMessage());
                break;
            }
        }
        return new ValidationException(message.toString());
    }
    
    private static final String ERROR_MESSAGE_ARGUMENT = "{n}";
    
    /**
     * 検証するパターンの列挙型。
     * 
     * @author hiro
     *
     */
    private enum Pattern {
        BLANK("が空欄です。"),
        INTEGER("に数字以外の文字列が含まれています。"),
        DECIMAL("に数字以外の文字列が含まれています。"),
        LENGTH(StringObject.join("は", StringValidator.ERROR_MESSAGE_ARGUMENT, "桁である必要があります。").toString()),
        MAXIMUM_LENGTH(StringObject.join("の文字数がオーバーしています。", StringValidator.ERROR_MESSAGE_ARGUMENT, "文字まで入力できます。").toString()),
        MINIMUM_LENGTH(StringObject.join("の文字数が足りません。", StringValidator.ERROR_MESSAGE_ARGUMENT, "文字必要です。").toString()),
        ZERO("にゼロは入力できません。"),
        MAXIMUM_VALUE(StringObject.join("は最大で「", StringValidator.ERROR_MESSAGE_ARGUMENT, "」まで入力できます。").toString()),
        MINIMUM_VALUE(StringObject.join("は「", StringValidator.ERROR_MESSAGE_ARGUMENT, "」以上である必要があります。").toString()),
        DATETIME("が正しくありません。"),
        TELEPHONE_NUMBER("が正しくありません。"),
        REGEX("が正しくありません。"),
        REVERSE_REGEX("が正しくありません。"),
        ;
        
        /**
         * コンストラクタ。
         * 
         * @param errorMessage 検証に失敗した場合のメッセージ。
         */
        private Pattern(String errorMessage) {
            this.errorMessage = errorMessage;
        }
        
        private String errorMessage;
        
        /**
         * 検証に失敗した場合のメッセージを取得する。
         * 
         * @return 結果。
         */
        public String getErrorMessage() {
            return this.errorMessage;
        }
    }
}
