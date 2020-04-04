package com.hirohiro716;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hirohiro716.datetime.Datetime;

/**
 * 文字列のクラス。
 * 
 * @author hiro
 *
 */
public class StringObject implements Cloneable, Iterable<String> {
    
    /**
     * コンストラクタ。
     */
    public StringObject() {
        this(null);
    }
    
    /**
     * コンストラクタ。<br>
     * パラメーターに指定したObjectのtoStringメソッドの結果を初期値とする。
     * 
     * @param value
     */
    public StringObject(Object value) {
        if (value != null) {
            this.value.append(value.toString());
        }
    }
    
    private StringBuilder value = new StringBuilder();
    
    @Override
    public StringObject clone() {
        return new StringObject(this.value);
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            
            private StringObject instance = StringObject.this;
            
            private int index = 0;
            
            @Override
            public boolean hasNext() {
                if (this.instance.length() > this.index) {
                    return true;
                }
                return false;
            }
            
            @Override
            public String next() {
                String part = this.instance.clone().extract(this.index, this.index + 1).toString();
                this.index++;
                return part;
            }
        };
    }
    
    /**
     * このインスタンスの文字列と、指定されたオブジェクトの文字列表現を比較する。
     * 
     * @param object
     * @return 結果。
     */
    @Override
    public boolean equals(Object object) {
        return this.toString().equals(object.toString());
    }

    /**
     * このインスタンスの文字列と、指定された文字列を比較する。
     * 
     * @param string
     * @return 結果。
     */
    public boolean equals(String string) {
        return this.toString().equals(string);
    }
    
    /**
     * 文字列の長さを返す。
     * 
     * @return length
     */
    public int length() {
        return this.value.length();
    }
    
    /**
     * 文字列を指定位置に挿入する。<br>
     * Examples:<br>
     * (new StringObject("I have money!")).insert("don't ", 2) returns "I don't
     * have money!"<br>
     * (new StringObject("Fuck!")).insert("ine l", -4) returns "Fine luck!"<br>
     * 
     * @param addition
     * @param index
     * @return このインスタンス。
     */
    public StringObject insert(Object addition, int index) {
        if (addition != null) {
            int allLength = this.length();
            int trueIndex = index;
            if (trueIndex < 0) {
                trueIndex = allLength + index;
            }
            if (trueIndex > allLength) {
                trueIndex = allLength;
            }
            this.value.insert(trueIndex, addition);
        }
        return this;
    }

    /**
     * 文字列を先頭に追加する。
     * 
     * @param addition
     * @return このインスタンス。
     */
    public StringObject prepend(Object addition) {
        if (addition != null) {
            this.insert(addition.toString(), 0);
        }
        return this;
    }
    
    /**
     * 文字列を末尾に追加する。
     * 
     * @param addition
     * @return このインスタンス。
     */
    public StringObject append(Object addition) {
        if (addition != null) {
            this.value.append(addition.toString());
        }
        return this;
    }
    
    /**
     * 指定された開始インデックスから文字列の最後までを抽出する。
     * Examples:<br>
     * (new StringObject("unhappy")).extract(2) returns "happy"<br>
     * (new StringObject("Harbison")).extract(3) returns "bison"<br>
     * (new StringObject("emptiness")).extract(9) returns ""<br>
     * (new StringObject("NoMoney")).extract(-5) returns "Money"
     * 
     * @param startIndex
     * @return このインスタンス。
     */
    public StringObject extract(int startIndex) {
        int allLength = this.length();
        int trueStartIndex = startIndex;
        if (trueStartIndex < 0) {
            trueStartIndex = allLength + startIndex;
        }
        if (trueStartIndex < 0) {
            trueStartIndex = 0;
        }
        try {
            this.value = new StringBuilder(this.toString().substring(trueStartIndex));
        } catch (Exception exception) {
            this.value = new StringBuilder();
        }
        return this;
    }
    
    /**
     * 指定された開始インデックスから終了インデックスまでを抽出する。<br>
     * Examples:<br>
     * (new StringObject("hamburger")).extract(4, 8) returns "urge"<br>
     * (new StringObject("smiles")).extract(1, 5) returns "mile"<br>
     * (new StringObject("I don't have money...")).extract(-13, -2) returns
     * "have money."
     * 
     * @param startIndex
     * @param endIndex
     * @return このインスタンス。
     */
    public StringObject extract(int startIndex, int endIndex) {
        int allLength = this.length();
        int trueStartIndex = startIndex;
        if (trueStartIndex < 0) {
            trueStartIndex = allLength + startIndex;
        }
        if (trueStartIndex < 0) {
            trueStartIndex = 0;
        }
        int trueEndIndex = endIndex;
        if (trueEndIndex < 0) {
            trueEndIndex = allLength + endIndex;
        }
        if (trueEndIndex > allLength) {
            trueEndIndex = allLength;
        }
        try {
            this.value = new StringBuilder(this.toString().substring(trueStartIndex, trueEndIndex));
        } catch (Exception exception) {
            this.value = new StringBuilder();
        }
        return this;
    }
    
    /**
     * 正規表現に一致する部分を抽出する。<br>
     * Examples:<br>
     * (new StringObject("a12bc34d")).extract("[0-9]") returns "1234"<br>
     * (new StringObject("aaabbbccc")).extract("abb") returns "abb"
     * 
     * @param regex
     * @return このインスタンス。
     */
    public StringObject extract(String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(this.value);
        this.value = new StringBuilder();
        while (matcher.find()) {
            this.value.append(matcher.group());
        }
        return this;
    }
    
    /**
     * このインスタンスの文字列を指定された回数繰り返す。
     * Examples:<br>
     * StringObject.newInstance("a").repeat(3) returns "aaa"
     * 
     * @param numberOfRepeat
     * @return このインスタンス。
     */
    public StringObject repeat(int numberOfRepeat) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int number = 0; number < numberOfRepeat; number++) {
            stringBuilder.append(this);
        }
        this.value = stringBuilder;
        return this;
    }
    
    /**
     * 先頭と末尾のスペースをすべて削除する。<br>
     * スペースとは 'U+0020' と 'U+3000' に等しいcharacter。
     * 
     * @return このインスタンス。
     */
    public StringObject trim() {
        int allLength = this.length();
        int startIndex = 0;
        FIRST: for (String one : this) {
            switch (one) {
            case " ":
            case "　":
                startIndex++;
                break;
            default:
                break FIRST;
            }
        }
        int lastIndex = allLength;
        LAST: for (String one : this) {
            switch (one) {
            case " ":
            case "　":
                lastIndex--;
                break;
            default:
                break LAST;
            }
        }
        return this.extract(startIndex, lastIndex);
    }
    
    /**
     * 指定された長さまで左側を文字で埋める。
     * 
     * @param character
     * @param length
     * @return このインスタンス。
     */
    public StringObject paddingLeft(char character, int length) {
        while (this.length() < length) {
            this.insert(character, 0);
        }
        return this;
    }
    
    /**
     * 指定された長さまで右側を文字で埋める。
     * 
     * @param character
     * @param length
     * @return このインスタンス。
     */
    public StringObject paddingRight(char character, int length) {
        while (this.length() < length) {
            this.append(character);
        }
        return this;
    }
    
    /**
     * 指定された正規表現に一致する部分を置き換える。<br>
     * このメソッドを StringObject.newInstance("test").replace(regex, repl)
     * で呼び出すと次と同じ結果を返す。<br>
     * "test".replace(regex, repl)
     * 
     * @param regex
     * @param replacement
     * @return このインスタンス。
     */
    public StringObject replace(String regex, String replacement) {
        this.value = new StringBuilder(this.toString().replaceAll(regex, replacement));
        return this;
    }
    
    /**
     * CRを置き換える。CRLFのCRは置き換えられない。
     * 
     * @param replacement
     * @return このインスタンス。
     */
    public final StringObject replaceCR(String replacement) {
        return this.replace("\r([^\n])|\r$", replacement + "$1");
    }
    
    /**
     * LFを置き換える。CRLFのLFは置き換えられない。.
     * 
     * @param replacement
     * @return このインスタンス。
     */
    public final StringObject replaceLF(String replacement) {
        return this.replace("([^\r])\n|^\n", "$1" + replacement);
    }
    
    /**
     * CRLFを置き換える。
     * 
     * @param replacement
     * @return このインスタンス。
     */
    public final StringObject replaceCRLF(String replacement) {
        return this.replace("\r\n", replacement);
    }
    
    /**
     * 意味のない小数点以下を削除する。
     * Examples:<br>
     * (new StringObject(123.000)).removeMeaninglessDecimalPoint() returns "123"
     * 
     * @return このインスタンス。
     */
    public final StringObject removeMeaninglessDecimalPoint() {
        return this.replace("\\.{1}0{1,}$", "");
    }
    
    /**
     * 日本語全角文字を半角に置き換える。
     * 
     * @return このインスタンス。
     */
    public StringObject narrow() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String one : this) {
            if (getWideToNarrowMap().containsKey(one)) {
                stringBuilder.append(getWideToNarrowMap().get(one));
            } else {
                stringBuilder.append(one);
            }
        }
        this.value = stringBuilder;
        return this;
    }
    
    /**
     * 半角を日本語全角に置き換える。
     * 
     * @return このインスタンス。
     */
    public StringObject wide() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String one : this) {
            if (getNarrowToWideMap().containsKey(one)) {
                stringBuilder.append(getNarrowToWideMap().get(one));
            } else {
                stringBuilder.append(one);
            }
        }
        this.value = stringBuilder;
        return this;
    }
    
    /**
     * 大文字を小文字に置き換える。
     * 
     * @return このインスタンス。
     */
    public StringObject lower() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String one : this) {
            if (getJapaneseUpperToLowerMap().containsKey(one)) {
                stringBuilder.append(getJapaneseUpperToLowerMap().get(one));
            } else {
                stringBuilder.append(one.toLowerCase());
            }
        }
        this.value = stringBuilder;
        return this;
    }
    
    /**
     * 小文字を大文字に置き換える。
     * 
     * @return このインスタンス。
     */
    public StringObject upper() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String one : this) {
            if (getJapaneseLowerToUpperMap().containsKey(one)) {
                stringBuilder.append(getJapaneseLowerToUpperMap().get(one));
            } else {
                stringBuilder.append(one.toUpperCase());
            }
        }
        this.value = stringBuilder;
        return this;
    }
    
    /**
     * カタカナをひらがなに置き換える。
     * 
     * @return このインスタンス。
     */
    public StringObject hiragana() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String one : this) {
            if (getKatakanaToHiraganaMap().containsKey(one)) {
                stringBuilder.append(getKatakanaToHiraganaMap().get(one));
            } else {
                stringBuilder.append(one);
            }
        }
        this.value = stringBuilder;
        return this;
    }
    
    /**
     * ひらがなをカタカナに置き換える。
     * 
     * @return このインスタンス。
     */
    public StringObject katakana() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String one : this) {
            if (getHiraganaToKatakanaMap().containsKey(one)) {
                stringBuilder.append(getHiraganaToKatakanaMap().get(one));
            } else {
                stringBuilder.append(one);
            }
        }
        this.value = stringBuilder;
        return this;
    }
    
    /**
     * 文字列表現を取得する。
     * 
     * @return 結果。
     */
    @Override
    public String toString() {
        return this.value.toString();
    }
    
    /**
     * 指定された文字セットの文字列表現を取得する。
     * 
     * @param charset
     * @return 結果。
     */
    public String toString(Charset charset) {
        return new String(this.value.toString().getBytes(charset), charset);
    }
    
    /**
     * 文字列をByteに変換する。失敗した場合はnullを返す。
     * 
     * @return Byte、またはnull。
     */
    public Byte toByte() {
        try {
            return Byte.valueOf(this.toString());
        } catch (Exception exception) {
            return null;
        }
    }
    
    /**
     * 文字列をShortに変換する。失敗した場合はnullを返す。
     * 
     * @return Short、またはnull。
     */
    public Short toShort() {
        try {
            return Double.valueOf(this.toString()).shortValue();
        } catch (Exception exception) {
            return null;
        }
    }
    
    /**
     * 文字列をIntegerに変換する。失敗した場合はnullを返す。
     * 
     * @return Integer、またはnull。
     */
    public Integer toInteger() {
        try {
            return Double.valueOf(this.toString()).intValue();
        } catch (Exception exception) {
            return null;
        }
    }
    
    /**
     * 文字列をLongに変換する。失敗した場合はnullを返す。
     * 
     * @return Long、またはnull。
     */
    public Long toLong() {
        try {
            return Double.valueOf(this.toString()).longValue();
        } catch (Exception exception) {
            return null;
        }
    }
    
    /**
     * 文字列をDoubleに変換する。失敗した場合はnullを返す。
     * 
     * @return Double、またはnull。
     */
    public Double toDouble() {
        try {
            return Double.valueOf(this.toString());
        } catch (Exception exception) {
            return null;
        }
    }
    
    /**
     * 文字列をFloatに変換する。失敗した場合はnullを返す。
     * 
     * @return Float、またはnull。
     */
    public Float toFloat() {
        try {
            return Double.valueOf(this.toString()).floatValue();
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * 文字列をbooleanに変換する。<br>
     * 大文字小文字を区別しない"true"という文字列以外はfalseを返す。
     * 
     * @return 結果。
     */
    public Boolean toBoolean() {
        try {
            return Boolean.valueOf(this.toString());
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * 文字列をjava.util.Dateに変換する。失敗した場合はnullを返す。
     * 
     * @return java.util.Date、またはnull。
     */
    public Date toDate() {
        try {
            return Datetime.newInstance(this.value.toString()).getDate();
        } catch (ParseException exception) {
            return null;
        }
    }
    
    /**
     * このインスタンスの文字列を、指定された正規表現の一致で分割する。<br>
     * このメソッドを StringObject.newInstance("aa-bb-cc--").splic("-")
     * で呼び出すと次と同じ結果を返す。<br>
     * "aa-bb-cc--".split("-", -1)
     * 
     * @param regexDelimiter
     * @return 分割した文字列の配列。
     */
    public String[] split(String regexDelimiter) {
        return this.value.toString().split(regexDelimiter, -1);
    }

    /**
     * このインスタンスの文字列中の整数一桁をすべて抽出し、その和を求める。
     * 
     * @return 結果。
     */
    public long sum() {
        long result = 0;
        StringObject numbers = this.clone().extract("[0-9]");
        for (int index = 0; index < numbers.length(); index++) {
            result += numbers.clone().extract(index, index + 1).toLong();
        }
        return result;
    }
    
    /**
     * このメソッドはコンストラクタの呼び出しと同じで、新しいインスタンスを作成する。
     * 
     * @param value
     * @return 新しいインスタンス。
     */
    public static StringObject newInstance(Object value) {
        return new StringObject(value);
    }
    
    /**
     * 引数のObject配列のtoStringメソッドを連結した新しいインスタンスを作成する。
     * 
     * @param objects
     * @param separator 
     * @return 新しいインスタンス。
     */
    public static StringObject joinWithSeparator(Object[] objects, String separator) {
        StringObject stringObject = new StringObject();
        for (Object object : objects) {
            if (stringObject.length() > 0) {
                stringObject.append(separator);
            }
            stringObject.append(object);
        }
        return stringObject;
    }
    
    /**
     * 引数のObject配列のtoStringメソッドを連結した新しいインスタンスを作成する。
     * 
     * @param objects
     * @return 新しいインスタンス。
     */
    public static StringObject join(Object... objects) {
        return joinWithSeparator(objects, null);
    }
    
    /**
     * 指定された回数分、指定文字列を繰り返した新しいインスタンスを作成する。
     * 
     * @param repeatString
     * @param numberOfRepeat
     * @return 新しいインスタンス。
     */
    public static StringObject repeat(String repeatString, int numberOfRepeat) {
        StringObject stringObject = new StringObject(repeatString);
        return stringObject.repeat(numberOfRepeat);
    }
    
    private final static Map<String, String> NARROW_TO_WIDE = new HashMap<>();
    
    private static Map<String, String> getNarrowToWideMap() {
        if (NARROW_TO_WIDE.size() == 0) {
            NARROW_TO_WIDE.put("a", "ａ");
            NARROW_TO_WIDE.put("b", "ｂ");
            NARROW_TO_WIDE.put("c", "ｃ");
            NARROW_TO_WIDE.put("d", "ｄ");
            NARROW_TO_WIDE.put("e", "ｅ");
            NARROW_TO_WIDE.put("f", "ｆ");
            NARROW_TO_WIDE.put("g", "ｇ");
            NARROW_TO_WIDE.put("h", "ｈ");
            NARROW_TO_WIDE.put("i", "ｉ");
            NARROW_TO_WIDE.put("j", "ｊ");
            NARROW_TO_WIDE.put("k", "ｋ");
            NARROW_TO_WIDE.put("l", "ｌ");
            NARROW_TO_WIDE.put("m", "ｍ");
            NARROW_TO_WIDE.put("n", "ｎ");
            NARROW_TO_WIDE.put("o", "ｏ");
            NARROW_TO_WIDE.put("p", "ｐ");
            NARROW_TO_WIDE.put("q", "ｑ");
            NARROW_TO_WIDE.put("r", "ｒ");
            NARROW_TO_WIDE.put("s", "ｓ");
            NARROW_TO_WIDE.put("t", "ｔ");
            NARROW_TO_WIDE.put("u", "ｕ");
            NARROW_TO_WIDE.put("v", "ｖ");
            NARROW_TO_WIDE.put("w", "ｗ");
            NARROW_TO_WIDE.put("x", "ｘ");
            NARROW_TO_WIDE.put("y", "ｙ");
            NARROW_TO_WIDE.put("z", "ｚ");
            NARROW_TO_WIDE.put("A", "Ａ");
            NARROW_TO_WIDE.put("B", "Ｂ");
            NARROW_TO_WIDE.put("C", "Ｃ");
            NARROW_TO_WIDE.put("D", "Ｄ");
            NARROW_TO_WIDE.put("E", "Ｅ");
            NARROW_TO_WIDE.put("F", "Ｆ");
            NARROW_TO_WIDE.put("G", "Ｇ");
            NARROW_TO_WIDE.put("H", "Ｈ");
            NARROW_TO_WIDE.put("I", "Ｉ");
            NARROW_TO_WIDE.put("J", "Ｊ");
            NARROW_TO_WIDE.put("K", "Ｋ");
            NARROW_TO_WIDE.put("L", "Ｌ");
            NARROW_TO_WIDE.put("M", "Ｍ");
            NARROW_TO_WIDE.put("N", "Ｎ");
            NARROW_TO_WIDE.put("O", "Ｏ");
            NARROW_TO_WIDE.put("P", "Ｐ");
            NARROW_TO_WIDE.put("Q", "Ｑ");
            NARROW_TO_WIDE.put("R", "Ｒ");
            NARROW_TO_WIDE.put("S", "Ｓ");
            NARROW_TO_WIDE.put("T", "Ｔ");
            NARROW_TO_WIDE.put("U", "Ｕ");
            NARROW_TO_WIDE.put("V", "Ｖ");
            NARROW_TO_WIDE.put("W", "Ｗ");
            NARROW_TO_WIDE.put("X", "Ｘ");
            NARROW_TO_WIDE.put("Y", "Ｙ");
            NARROW_TO_WIDE.put("Z", "Ｚ");
            NARROW_TO_WIDE.put("0", "０");
            NARROW_TO_WIDE.put("1", "１");
            NARROW_TO_WIDE.put("2", "２");
            NARROW_TO_WIDE.put("3", "３");
            NARROW_TO_WIDE.put("4", "４");
            NARROW_TO_WIDE.put("5", "５");
            NARROW_TO_WIDE.put("6", "６");
            NARROW_TO_WIDE.put("7", "７");
            NARROW_TO_WIDE.put("8", "８");
            NARROW_TO_WIDE.put("9", "９");
            NARROW_TO_WIDE.put("ｧ", "ァ");
            NARROW_TO_WIDE.put("ｱ", "ア");
            NARROW_TO_WIDE.put("ｨ", "ィ");
            NARROW_TO_WIDE.put("ｲ", "イ");
            NARROW_TO_WIDE.put("ｩ", "ゥ");
            NARROW_TO_WIDE.put("ｳ", "ウ");
            NARROW_TO_WIDE.put("ｪ", "ェ");
            NARROW_TO_WIDE.put("ｴ", "エ");
            NARROW_TO_WIDE.put("ｫ", "ォ");
            NARROW_TO_WIDE.put("ｵ", "オ");
            NARROW_TO_WIDE.put("ｶ", "カ");
            NARROW_TO_WIDE.put("ｶﾞ", "ガ");
            NARROW_TO_WIDE.put("ｷ", "キ");
            NARROW_TO_WIDE.put("ｷﾞ", "ギ");
            NARROW_TO_WIDE.put("ｸ", "ク");
            NARROW_TO_WIDE.put("ｸﾞ", "グ");
            NARROW_TO_WIDE.put("ｹ", "ケ");
            NARROW_TO_WIDE.put("ｹﾞ", "ゲ");
            NARROW_TO_WIDE.put("ｺ", "コ");
            NARROW_TO_WIDE.put("ｺﾞ", "ゴ");
            NARROW_TO_WIDE.put("ｻ", "サ");
            NARROW_TO_WIDE.put("ｻﾞ", "ザ");
            NARROW_TO_WIDE.put("ｼ", "シ");
            NARROW_TO_WIDE.put("ｼﾞ", "ジ");
            NARROW_TO_WIDE.put("ｽ", "ス");
            NARROW_TO_WIDE.put("ｽﾞ", "ズ");
            NARROW_TO_WIDE.put("ｾ", "セ");
            NARROW_TO_WIDE.put("ｾﾞ", "ゼ");
            NARROW_TO_WIDE.put("ｿ", "ソ");
            NARROW_TO_WIDE.put("ｿﾞ", "ゾ");
            NARROW_TO_WIDE.put("ﾀ", "タ");
            NARROW_TO_WIDE.put("ﾀﾞ", "ダ");
            NARROW_TO_WIDE.put("ﾁ", "チ");
            NARROW_TO_WIDE.put("ﾁﾞ", "ヂ");
            NARROW_TO_WIDE.put("ｯ", "ッ");
            NARROW_TO_WIDE.put("ﾂ", "ツ");
            NARROW_TO_WIDE.put("ﾂﾞ", "ヅ");
            NARROW_TO_WIDE.put("ﾃ", "テ");
            NARROW_TO_WIDE.put("ﾃﾞ", "デ");
            NARROW_TO_WIDE.put("ﾄ", "ト");
            NARROW_TO_WIDE.put("ﾄﾞ", "ド");
            NARROW_TO_WIDE.put("ﾅ", "ナ");
            NARROW_TO_WIDE.put("ﾆ", "ニ");
            NARROW_TO_WIDE.put("ﾇ", "ヌ");
            NARROW_TO_WIDE.put("ﾈ", "ネ");
            NARROW_TO_WIDE.put("ﾉ", "ノ");
            NARROW_TO_WIDE.put("ﾊ", "ハ");
            NARROW_TO_WIDE.put("ﾊﾞ", "バ");
            NARROW_TO_WIDE.put("ﾊﾟ", "パ");
            NARROW_TO_WIDE.put("ﾋ", "ヒ");
            NARROW_TO_WIDE.put("ﾋﾞ", "ビ");
            NARROW_TO_WIDE.put("ﾋﾟ", "ピ");
            NARROW_TO_WIDE.put("ﾌ", "フ");
            NARROW_TO_WIDE.put("ﾌﾞ", "ブ");
            NARROW_TO_WIDE.put("ﾌﾟ", "プ");
            NARROW_TO_WIDE.put("ﾍ", "ヘ");
            NARROW_TO_WIDE.put("ﾍﾞ", "ベ");
            NARROW_TO_WIDE.put("ﾍﾟ", "ペ");
            NARROW_TO_WIDE.put("ﾎ", "ホ");
            NARROW_TO_WIDE.put("ﾎﾞ", "ボ");
            NARROW_TO_WIDE.put("ﾎﾟ", "ポ");
            NARROW_TO_WIDE.put("ﾏ", "マ");
            NARROW_TO_WIDE.put("ﾐ", "ミ");
            NARROW_TO_WIDE.put("ﾑ", "ム");
            NARROW_TO_WIDE.put("ﾒ", "メ");
            NARROW_TO_WIDE.put("ﾓ", "モ");
            NARROW_TO_WIDE.put("ｬ", "ャ");
            NARROW_TO_WIDE.put("ﾔ", "ヤ");
            NARROW_TO_WIDE.put("ｭ", "ュ");
            NARROW_TO_WIDE.put("ﾕ", "ユ");
            NARROW_TO_WIDE.put("ｮ", "ョ");
            NARROW_TO_WIDE.put("ﾖ", "ヨ");
            NARROW_TO_WIDE.put("ﾗ", "ラ");
            NARROW_TO_WIDE.put("ﾘ", "リ");
            NARROW_TO_WIDE.put("ﾙ", "ル");
            NARROW_TO_WIDE.put("ﾚ", "レ");
            NARROW_TO_WIDE.put("ﾛ", "ロ");
            NARROW_TO_WIDE.put("ﾜ", "ワ");
            NARROW_TO_WIDE.put("ｦ", "ヲ");
            NARROW_TO_WIDE.put("ﾝ", "ン");
            NARROW_TO_WIDE.put("ｳﾞ", "ヴ");
            NARROW_TO_WIDE.put("!", "！");
            NARROW_TO_WIDE.put("\"", "”");
            NARROW_TO_WIDE.put("#", "＃");
            NARROW_TO_WIDE.put("$", "＄");
            NARROW_TO_WIDE.put("%", "％");
            NARROW_TO_WIDE.put("&", "＆");
            NARROW_TO_WIDE.put("'", "’");
            NARROW_TO_WIDE.put("(", "（");
            NARROW_TO_WIDE.put(")", "）");
            NARROW_TO_WIDE.put("=", "＝");
            NARROW_TO_WIDE.put("-", "－");
            NARROW_TO_WIDE.put("‑", "－");
            NARROW_TO_WIDE.put("ｰ", "－");
            NARROW_TO_WIDE.put("~", "～");
            NARROW_TO_WIDE.put("^", "＾");
            NARROW_TO_WIDE.put("\\", "￥");
            NARROW_TO_WIDE.put("@", "＠");
            NARROW_TO_WIDE.put("+", "＋");
            NARROW_TO_WIDE.put("*", "＊");
            NARROW_TO_WIDE.put("{", "｛");
            NARROW_TO_WIDE.put("}", "｝");
            NARROW_TO_WIDE.put("[", "［");
            NARROW_TO_WIDE.put("]", "］");
            NARROW_TO_WIDE.put(";", "；");
            NARROW_TO_WIDE.put(":", "：");
            NARROW_TO_WIDE.put("<", "＜");
            NARROW_TO_WIDE.put(">", "＞");
            NARROW_TO_WIDE.put(",", "，");
            NARROW_TO_WIDE.put(".", "．");
            NARROW_TO_WIDE.put("?", "？");
            NARROW_TO_WIDE.put("_", "＿");
            NARROW_TO_WIDE.put("/", "／");
            NARROW_TO_WIDE.put(" ", "　");
        }
        return NARROW_TO_WIDE;
    }
    
    private final static Map<String, String> WIDE_TO_NARROW = new HashMap<>();
    
    private static Map<String, String> getWideToNarrowMap() {
        if (WIDE_TO_NARROW.size() == 0) {
            WIDE_TO_NARROW.put("ａ", "a");
            WIDE_TO_NARROW.put("ｂ", "b");
            WIDE_TO_NARROW.put("ｃ", "c");
            WIDE_TO_NARROW.put("ｄ", "d");
            WIDE_TO_NARROW.put("ｅ", "e");
            WIDE_TO_NARROW.put("ｆ", "f");
            WIDE_TO_NARROW.put("ｇ", "g");
            WIDE_TO_NARROW.put("ｈ", "h");
            WIDE_TO_NARROW.put("ｉ", "i");
            WIDE_TO_NARROW.put("ｊ", "j");
            WIDE_TO_NARROW.put("ｋ", "k");
            WIDE_TO_NARROW.put("ｌ", "l");
            WIDE_TO_NARROW.put("ｍ", "m");
            WIDE_TO_NARROW.put("ｎ", "n");
            WIDE_TO_NARROW.put("ｏ", "o");
            WIDE_TO_NARROW.put("ｐ", "p");
            WIDE_TO_NARROW.put("ｑ", "q");
            WIDE_TO_NARROW.put("ｒ", "r");
            WIDE_TO_NARROW.put("ｓ", "s");
            WIDE_TO_NARROW.put("ｔ", "t");
            WIDE_TO_NARROW.put("ｕ", "u");
            WIDE_TO_NARROW.put("ｖ", "v");
            WIDE_TO_NARROW.put("ｗ", "w");
            WIDE_TO_NARROW.put("ｘ", "x");
            WIDE_TO_NARROW.put("ｙ", "y");
            WIDE_TO_NARROW.put("ｚ", "z");
            WIDE_TO_NARROW.put("Ａ", "A");
            WIDE_TO_NARROW.put("Ｂ", "B");
            WIDE_TO_NARROW.put("Ｃ", "C");
            WIDE_TO_NARROW.put("Ｄ", "D");
            WIDE_TO_NARROW.put("Ｅ", "E");
            WIDE_TO_NARROW.put("Ｆ", "F");
            WIDE_TO_NARROW.put("Ｇ", "G");
            WIDE_TO_NARROW.put("Ｈ", "H");
            WIDE_TO_NARROW.put("Ｉ", "I");
            WIDE_TO_NARROW.put("Ｊ", "J");
            WIDE_TO_NARROW.put("Ｋ", "K");
            WIDE_TO_NARROW.put("Ｌ", "L");
            WIDE_TO_NARROW.put("Ｍ", "M");
            WIDE_TO_NARROW.put("Ｎ", "N");
            WIDE_TO_NARROW.put("Ｏ", "O");
            WIDE_TO_NARROW.put("Ｐ", "P");
            WIDE_TO_NARROW.put("Ｑ", "Q");
            WIDE_TO_NARROW.put("Ｒ", "R");
            WIDE_TO_NARROW.put("Ｓ", "S");
            WIDE_TO_NARROW.put("Ｔ", "T");
            WIDE_TO_NARROW.put("Ｕ", "U");
            WIDE_TO_NARROW.put("Ｖ", "V");
            WIDE_TO_NARROW.put("Ｗ", "W");
            WIDE_TO_NARROW.put("Ｘ", "X");
            WIDE_TO_NARROW.put("Ｙ", "Y");
            WIDE_TO_NARROW.put("Ｚ", "Z");
            WIDE_TO_NARROW.put("０", "0");
            WIDE_TO_NARROW.put("１", "1");
            WIDE_TO_NARROW.put("２", "2");
            WIDE_TO_NARROW.put("３", "3");
            WIDE_TO_NARROW.put("４", "4");
            WIDE_TO_NARROW.put("５", "5");
            WIDE_TO_NARROW.put("６", "6");
            WIDE_TO_NARROW.put("７", "7");
            WIDE_TO_NARROW.put("８", "8");
            WIDE_TO_NARROW.put("９", "9");
            WIDE_TO_NARROW.put("ァ", "ｧ");
            WIDE_TO_NARROW.put("ア", "ｱ");
            WIDE_TO_NARROW.put("ィ", "ｨ");
            WIDE_TO_NARROW.put("イ", "ｲ");
            WIDE_TO_NARROW.put("ゥ", "ｩ");
            WIDE_TO_NARROW.put("ウ", "ｳ");
            WIDE_TO_NARROW.put("ェ", "ｪ");
            WIDE_TO_NARROW.put("エ", "ｴ");
            WIDE_TO_NARROW.put("ォ", "ｫ");
            WIDE_TO_NARROW.put("オ", "ｵ");
            WIDE_TO_NARROW.put("カ", "ｶ");
            WIDE_TO_NARROW.put("ガ", "ｶﾞ");
            WIDE_TO_NARROW.put("キ", "ｷ");
            WIDE_TO_NARROW.put("ギ", "ｷﾞ");
            WIDE_TO_NARROW.put("ク", "ｸ");
            WIDE_TO_NARROW.put("グ", "ｸﾞ");
            WIDE_TO_NARROW.put("ケ", "ｹ");
            WIDE_TO_NARROW.put("ゲ", "ｹﾞ");
            WIDE_TO_NARROW.put("コ", "ｺ");
            WIDE_TO_NARROW.put("ゴ", "ｺﾞ");
            WIDE_TO_NARROW.put("サ", "ｻ");
            WIDE_TO_NARROW.put("ザ", "ｻﾞ");
            WIDE_TO_NARROW.put("シ", "ｼ");
            WIDE_TO_NARROW.put("ジ", "ｼﾞ");
            WIDE_TO_NARROW.put("ス", "ｽ");
            WIDE_TO_NARROW.put("ズ", "ｽﾞ");
            WIDE_TO_NARROW.put("セ", "ｾ");
            WIDE_TO_NARROW.put("ゼ", "ｾﾞ");
            WIDE_TO_NARROW.put("ソ", "ｿ");
            WIDE_TO_NARROW.put("ゾ", "ｿﾞ");
            WIDE_TO_NARROW.put("タ", "ﾀ");
            WIDE_TO_NARROW.put("ダ", "ﾀﾞ");
            WIDE_TO_NARROW.put("チ", "ﾁ");
            WIDE_TO_NARROW.put("ヂ", "ﾁﾞ");
            WIDE_TO_NARROW.put("ッ", "ｯ");
            WIDE_TO_NARROW.put("ツ", "ﾂ");
            WIDE_TO_NARROW.put("ヅ", "ﾂﾞ");
            WIDE_TO_NARROW.put("テ", "ﾃ");
            WIDE_TO_NARROW.put("デ", "ﾃﾞ");
            WIDE_TO_NARROW.put("ト", "ﾄ");
            WIDE_TO_NARROW.put("ド", "ﾄﾞ");
            WIDE_TO_NARROW.put("ナ", "ﾅ");
            WIDE_TO_NARROW.put("ニ", "ﾆ");
            WIDE_TO_NARROW.put("ヌ", "ﾇ");
            WIDE_TO_NARROW.put("ネ", "ﾈ");
            WIDE_TO_NARROW.put("ノ", "ﾉ");
            WIDE_TO_NARROW.put("ハ", "ﾊ");
            WIDE_TO_NARROW.put("バ", "ﾊﾞ");
            WIDE_TO_NARROW.put("パ", "ﾊﾟ");
            WIDE_TO_NARROW.put("ヒ", "ﾋ");
            WIDE_TO_NARROW.put("ビ", "ﾋﾞ");
            WIDE_TO_NARROW.put("ピ", "ﾋﾟ");
            WIDE_TO_NARROW.put("フ", "ﾌ");
            WIDE_TO_NARROW.put("ブ", "ﾌﾞ");
            WIDE_TO_NARROW.put("プ", "ﾌﾟ");
            WIDE_TO_NARROW.put("ヘ", "ﾍ");
            WIDE_TO_NARROW.put("ベ", "ﾍﾞ");
            WIDE_TO_NARROW.put("ペ", "ﾍﾟ");
            WIDE_TO_NARROW.put("ホ", "ﾎ");
            WIDE_TO_NARROW.put("ボ", "ﾎﾞ");
            WIDE_TO_NARROW.put("ポ", "ﾎﾟ");
            WIDE_TO_NARROW.put("マ", "ﾏ");
            WIDE_TO_NARROW.put("ミ", "ﾐ");
            WIDE_TO_NARROW.put("ム", "ﾑ");
            WIDE_TO_NARROW.put("メ", "ﾒ");
            WIDE_TO_NARROW.put("モ", "ﾓ");
            WIDE_TO_NARROW.put("ャ", "ｬ");
            WIDE_TO_NARROW.put("ヤ", "ﾔ");
            WIDE_TO_NARROW.put("ュ", "ｭ");
            WIDE_TO_NARROW.put("ユ", "ﾕ");
            WIDE_TO_NARROW.put("ョ", "ｮ");
            WIDE_TO_NARROW.put("ヨ", "ﾖ");
            WIDE_TO_NARROW.put("ラ", "ﾗ");
            WIDE_TO_NARROW.put("リ", "ﾘ");
            WIDE_TO_NARROW.put("ル", "ﾙ");
            WIDE_TO_NARROW.put("レ", "ﾚ");
            WIDE_TO_NARROW.put("ロ", "ﾛ");
            WIDE_TO_NARROW.put("ワ", "ﾜ");
            WIDE_TO_NARROW.put("ヲ", "ｦ");
            WIDE_TO_NARROW.put("ン", "ﾝ");
            WIDE_TO_NARROW.put("ヴ", "ｳﾞ");
            WIDE_TO_NARROW.put("！", "!");
            WIDE_TO_NARROW.put("”", "\"");
            WIDE_TO_NARROW.put("＃", "#");
            WIDE_TO_NARROW.put("＄", "$");
            WIDE_TO_NARROW.put("％", "%");
            WIDE_TO_NARROW.put("＆", "&");
            WIDE_TO_NARROW.put("’", "'");
            WIDE_TO_NARROW.put("（", "(");
            WIDE_TO_NARROW.put("）", ")");
            WIDE_TO_NARROW.put("＝", "=");
            WIDE_TO_NARROW.put("－", "-");
            WIDE_TO_NARROW.put("～", "~");
            WIDE_TO_NARROW.put("＾", "^");
            WIDE_TO_NARROW.put("￥", "\\");
            WIDE_TO_NARROW.put("＠", "@");
            WIDE_TO_NARROW.put("＋", "+");
            WIDE_TO_NARROW.put("＊", "*");
            WIDE_TO_NARROW.put("｛", "{");
            WIDE_TO_NARROW.put("｝", "}");
            WIDE_TO_NARROW.put("［", "[");
            WIDE_TO_NARROW.put("］", "]");
            WIDE_TO_NARROW.put("；", ";");
            WIDE_TO_NARROW.put("：", ":");
            WIDE_TO_NARROW.put("＜", "<");
            WIDE_TO_NARROW.put("＞", ">");
            WIDE_TO_NARROW.put("，", ",");
            WIDE_TO_NARROW.put("．", ".");
            WIDE_TO_NARROW.put("？", "?");
            WIDE_TO_NARROW.put("＿", "_");
            WIDE_TO_NARROW.put("／", "/");
            WIDE_TO_NARROW.put("　", " ");
        }
        return WIDE_TO_NARROW;
    }
    
    private final static Map<String, String> HIRAGANA_TO_KATAKANA = new HashMap<>();
    
    private static Map<String, String> getHiraganaToKatakanaMap() {
        if (HIRAGANA_TO_KATAKANA.size() == 0) {
            HIRAGANA_TO_KATAKANA.put("ぁ", "ァ");
            HIRAGANA_TO_KATAKANA.put("あ", "ア");
            HIRAGANA_TO_KATAKANA.put("ぃ", "ィ");
            HIRAGANA_TO_KATAKANA.put("い", "イ");
            HIRAGANA_TO_KATAKANA.put("ぅ", "ゥ");
            HIRAGANA_TO_KATAKANA.put("う", "ウ");
            HIRAGANA_TO_KATAKANA.put("ぇ", "ェ");
            HIRAGANA_TO_KATAKANA.put("え", "エ");
            HIRAGANA_TO_KATAKANA.put("ぉ", "ォ");
            HIRAGANA_TO_KATAKANA.put("お", "オ");
            HIRAGANA_TO_KATAKANA.put("か", "カ");
            HIRAGANA_TO_KATAKANA.put("が", "ガ");
            HIRAGANA_TO_KATAKANA.put("き", "キ");
            HIRAGANA_TO_KATAKANA.put("ぎ", "ギ");
            HIRAGANA_TO_KATAKANA.put("く", "ク");
            HIRAGANA_TO_KATAKANA.put("ぐ", "グ");
            HIRAGANA_TO_KATAKANA.put("け", "ケ");
            HIRAGANA_TO_KATAKANA.put("げ", "ゲ");
            HIRAGANA_TO_KATAKANA.put("こ", "コ");
            HIRAGANA_TO_KATAKANA.put("ご", "ゴ");
            HIRAGANA_TO_KATAKANA.put("さ", "サ");
            HIRAGANA_TO_KATAKANA.put("ざ", "ザ");
            HIRAGANA_TO_KATAKANA.put("し", "シ");
            HIRAGANA_TO_KATAKANA.put("じ", "ジ");
            HIRAGANA_TO_KATAKANA.put("す", "ス");
            HIRAGANA_TO_KATAKANA.put("ず", "ズ");
            HIRAGANA_TO_KATAKANA.put("せ", "セ");
            HIRAGANA_TO_KATAKANA.put("ぜ", "ゼ");
            HIRAGANA_TO_KATAKANA.put("そ", "ソ");
            HIRAGANA_TO_KATAKANA.put("ぞ", "ゾ");
            HIRAGANA_TO_KATAKANA.put("た", "タ");
            HIRAGANA_TO_KATAKANA.put("だ", "ダ");
            HIRAGANA_TO_KATAKANA.put("ち", "チ");
            HIRAGANA_TO_KATAKANA.put("ぢ", "ヂ");
            HIRAGANA_TO_KATAKANA.put("っ", "ッ");
            HIRAGANA_TO_KATAKANA.put("つ", "ツ");
            HIRAGANA_TO_KATAKANA.put("づ", "ヅ");
            HIRAGANA_TO_KATAKANA.put("て", "テ");
            HIRAGANA_TO_KATAKANA.put("で", "デ");
            HIRAGANA_TO_KATAKANA.put("と", "ト");
            HIRAGANA_TO_KATAKANA.put("ど", "ド");
            HIRAGANA_TO_KATAKANA.put("な", "ナ");
            HIRAGANA_TO_KATAKANA.put("に", "ニ");
            HIRAGANA_TO_KATAKANA.put("ぬ", "ヌ");
            HIRAGANA_TO_KATAKANA.put("ね", "ネ");
            HIRAGANA_TO_KATAKANA.put("の", "ノ");
            HIRAGANA_TO_KATAKANA.put("は", "ハ");
            HIRAGANA_TO_KATAKANA.put("ば", "バ");
            HIRAGANA_TO_KATAKANA.put("ぱ", "パ");
            HIRAGANA_TO_KATAKANA.put("ひ", "ヒ");
            HIRAGANA_TO_KATAKANA.put("び", "ビ");
            HIRAGANA_TO_KATAKANA.put("ぴ", "ピ");
            HIRAGANA_TO_KATAKANA.put("ふ", "フ");
            HIRAGANA_TO_KATAKANA.put("ぶ", "ブ");
            HIRAGANA_TO_KATAKANA.put("ぷ", "プ");
            HIRAGANA_TO_KATAKANA.put("へ", "ヘ");
            HIRAGANA_TO_KATAKANA.put("べ", "ベ");
            HIRAGANA_TO_KATAKANA.put("ぺ", "ペ");
            HIRAGANA_TO_KATAKANA.put("ほ", "ホ");
            HIRAGANA_TO_KATAKANA.put("ぼ", "ボ");
            HIRAGANA_TO_KATAKANA.put("ぽ", "ポ");
            HIRAGANA_TO_KATAKANA.put("ま", "マ");
            HIRAGANA_TO_KATAKANA.put("み", "ミ");
            HIRAGANA_TO_KATAKANA.put("む", "ム");
            HIRAGANA_TO_KATAKANA.put("め", "メ");
            HIRAGANA_TO_KATAKANA.put("も", "モ");
            HIRAGANA_TO_KATAKANA.put("ゃ", "ャ");
            HIRAGANA_TO_KATAKANA.put("や", "ヤ");
            HIRAGANA_TO_KATAKANA.put("ゅ", "ュ");
            HIRAGANA_TO_KATAKANA.put("ゆ", "ユ");
            HIRAGANA_TO_KATAKANA.put("ょ", "ョ");
            HIRAGANA_TO_KATAKANA.put("よ", "ヨ");
            HIRAGANA_TO_KATAKANA.put("ら", "ラ");
            HIRAGANA_TO_KATAKANA.put("り", "リ");
            HIRAGANA_TO_KATAKANA.put("る", "ル");
            HIRAGANA_TO_KATAKANA.put("れ", "レ");
            HIRAGANA_TO_KATAKANA.put("ろ", "ロ");
            HIRAGANA_TO_KATAKANA.put("ゎ", "ヮ");
            HIRAGANA_TO_KATAKANA.put("わ", "ワ");
            HIRAGANA_TO_KATAKANA.put("ゐ", "ヰ");
            HIRAGANA_TO_KATAKANA.put("ゑ", "ヱ");
            HIRAGANA_TO_KATAKANA.put("を", "ヲ");
            HIRAGANA_TO_KATAKANA.put("ん", "ン");
        }
        return HIRAGANA_TO_KATAKANA;
    }
    
    private final static Map<String, String> KATAKANA_TO_HIRAGANA = new HashMap<>();
    
    private static Map<String, String> getKatakanaToHiraganaMap() {
        if (KATAKANA_TO_HIRAGANA.size() == 0) {
            KATAKANA_TO_HIRAGANA.put("ァ", "ぁ");
            KATAKANA_TO_HIRAGANA.put("ア", "あ");
            KATAKANA_TO_HIRAGANA.put("ィ", "ぃ");
            KATAKANA_TO_HIRAGANA.put("イ", "い");
            KATAKANA_TO_HIRAGANA.put("ゥ", "ぅ");
            KATAKANA_TO_HIRAGANA.put("ウ", "う");
            KATAKANA_TO_HIRAGANA.put("ェ", "ぇ");
            KATAKANA_TO_HIRAGANA.put("エ", "え");
            KATAKANA_TO_HIRAGANA.put("ォ", "ぉ");
            KATAKANA_TO_HIRAGANA.put("オ", "お");
            KATAKANA_TO_HIRAGANA.put("カ", "か");
            KATAKANA_TO_HIRAGANA.put("ガ", "が");
            KATAKANA_TO_HIRAGANA.put("キ", "き");
            KATAKANA_TO_HIRAGANA.put("ギ", "ぎ");
            KATAKANA_TO_HIRAGANA.put("ク", "く");
            KATAKANA_TO_HIRAGANA.put("グ", "ぐ");
            KATAKANA_TO_HIRAGANA.put("ケ", "け");
            KATAKANA_TO_HIRAGANA.put("ゲ", "げ");
            KATAKANA_TO_HIRAGANA.put("コ", "こ");
            KATAKANA_TO_HIRAGANA.put("ゴ", "ご");
            KATAKANA_TO_HIRAGANA.put("サ", "さ");
            KATAKANA_TO_HIRAGANA.put("ザ", "ざ");
            KATAKANA_TO_HIRAGANA.put("シ", "し");
            KATAKANA_TO_HIRAGANA.put("ジ", "じ");
            KATAKANA_TO_HIRAGANA.put("ス", "す");
            KATAKANA_TO_HIRAGANA.put("ズ", "ず");
            KATAKANA_TO_HIRAGANA.put("セ", "せ");
            KATAKANA_TO_HIRAGANA.put("ゼ", "ぜ");
            KATAKANA_TO_HIRAGANA.put("ソ", "そ");
            KATAKANA_TO_HIRAGANA.put("ゾ", "ぞ");
            KATAKANA_TO_HIRAGANA.put("タ", "た");
            KATAKANA_TO_HIRAGANA.put("ダ", "だ");
            KATAKANA_TO_HIRAGANA.put("チ", "ち");
            KATAKANA_TO_HIRAGANA.put("ヂ", "ぢ");
            KATAKANA_TO_HIRAGANA.put("ッ", "っ");
            KATAKANA_TO_HIRAGANA.put("ツ", "つ");
            KATAKANA_TO_HIRAGANA.put("ヅ", "づ");
            KATAKANA_TO_HIRAGANA.put("テ", "て");
            KATAKANA_TO_HIRAGANA.put("デ", "で");
            KATAKANA_TO_HIRAGANA.put("ト", "と");
            KATAKANA_TO_HIRAGANA.put("ド", "ど");
            KATAKANA_TO_HIRAGANA.put("ナ", "な");
            KATAKANA_TO_HIRAGANA.put("ニ", "に");
            KATAKANA_TO_HIRAGANA.put("ヌ", "ぬ");
            KATAKANA_TO_HIRAGANA.put("ネ", "ね");
            KATAKANA_TO_HIRAGANA.put("ノ", "の");
            KATAKANA_TO_HIRAGANA.put("ハ", "は");
            KATAKANA_TO_HIRAGANA.put("バ", "ば");
            KATAKANA_TO_HIRAGANA.put("パ", "ぱ");
            KATAKANA_TO_HIRAGANA.put("ヒ", "ひ");
            KATAKANA_TO_HIRAGANA.put("ビ", "び");
            KATAKANA_TO_HIRAGANA.put("ピ", "ぴ");
            KATAKANA_TO_HIRAGANA.put("フ", "ふ");
            KATAKANA_TO_HIRAGANA.put("ブ", "ぶ");
            KATAKANA_TO_HIRAGANA.put("プ", "ぷ");
            KATAKANA_TO_HIRAGANA.put("ヘ", "へ");
            KATAKANA_TO_HIRAGANA.put("ベ", "べ");
            KATAKANA_TO_HIRAGANA.put("ペ", "ぺ");
            KATAKANA_TO_HIRAGANA.put("ホ", "ほ");
            KATAKANA_TO_HIRAGANA.put("ボ", "ぼ");
            KATAKANA_TO_HIRAGANA.put("ポ", "ぽ");
            KATAKANA_TO_HIRAGANA.put("マ", "ま");
            KATAKANA_TO_HIRAGANA.put("ミ", "み");
            KATAKANA_TO_HIRAGANA.put("ム", "む");
            KATAKANA_TO_HIRAGANA.put("メ", "め");
            KATAKANA_TO_HIRAGANA.put("モ", "も");
            KATAKANA_TO_HIRAGANA.put("ャ", "ゃ");
            KATAKANA_TO_HIRAGANA.put("ヤ", "や");
            KATAKANA_TO_HIRAGANA.put("ュ", "ゅ");
            KATAKANA_TO_HIRAGANA.put("ユ", "ゆ");
            KATAKANA_TO_HIRAGANA.put("ョ", "ょ");
            KATAKANA_TO_HIRAGANA.put("ヨ", "よ");
            KATAKANA_TO_HIRAGANA.put("ラ", "ら");
            KATAKANA_TO_HIRAGANA.put("リ", "り");
            KATAKANA_TO_HIRAGANA.put("ル", "る");
            KATAKANA_TO_HIRAGANA.put("レ", "れ");
            KATAKANA_TO_HIRAGANA.put("ロ", "ろ");
            KATAKANA_TO_HIRAGANA.put("ヮ", "ゎ");
            KATAKANA_TO_HIRAGANA.put("ワ", "わ");
            KATAKANA_TO_HIRAGANA.put("ヰ", "ゐ");
            KATAKANA_TO_HIRAGANA.put("ヱ", "ゑ");
            KATAKANA_TO_HIRAGANA.put("ヲ", "を");
            KATAKANA_TO_HIRAGANA.put("ン", "ん");
        }
        return KATAKANA_TO_HIRAGANA;
    }
    
    private final static Map<String, String> JAPANESE_LOWER_TO_UPPER = new HashMap<>();
    
    private static Map<String, String> getJapaneseLowerToUpperMap() {
        if (JAPANESE_LOWER_TO_UPPER.size() == 0) {
            JAPANESE_LOWER_TO_UPPER.put("ぁ", "あ");
            JAPANESE_LOWER_TO_UPPER.put("ぃ", "い");
            JAPANESE_LOWER_TO_UPPER.put("ぅ", "う");
            JAPANESE_LOWER_TO_UPPER.put("ぇ", "え");
            JAPANESE_LOWER_TO_UPPER.put("ぉ", "お");
            JAPANESE_LOWER_TO_UPPER.put("ゃ", "や");
            JAPANESE_LOWER_TO_UPPER.put("ゅ", "ゆ");
            JAPANESE_LOWER_TO_UPPER.put("ょ", "よ");
            JAPANESE_LOWER_TO_UPPER.put("っ", "つ");
            JAPANESE_LOWER_TO_UPPER.put("ァ", "ア");
            JAPANESE_LOWER_TO_UPPER.put("ィ", "イ");
            JAPANESE_LOWER_TO_UPPER.put("ゥ", "ウ");
            JAPANESE_LOWER_TO_UPPER.put("ェ", "エ");
            JAPANESE_LOWER_TO_UPPER.put("ォ", "オ");
            JAPANESE_LOWER_TO_UPPER.put("ャ", "ヤ");
            JAPANESE_LOWER_TO_UPPER.put("ュ", "ユ");
            JAPANESE_LOWER_TO_UPPER.put("ョ", "ヨ");
            JAPANESE_LOWER_TO_UPPER.put("ッ", "ツ");
        }
        return JAPANESE_LOWER_TO_UPPER;
    }
    
    private final static Map<String, String> JAPANESE_UPPER_TO_LOWER = new HashMap<>();
    
    private static Map<String, String> getJapaneseUpperToLowerMap() {
        if (JAPANESE_UPPER_TO_LOWER.size() == 0) {
            JAPANESE_UPPER_TO_LOWER.put("あ", "ぁ");
            JAPANESE_UPPER_TO_LOWER.put("い", "ぃ");
            JAPANESE_UPPER_TO_LOWER.put("う", "ぅ");
            JAPANESE_UPPER_TO_LOWER.put("え", "ぇ");
            JAPANESE_UPPER_TO_LOWER.put("お", "ぉ");
            JAPANESE_UPPER_TO_LOWER.put("や", "ゃ");
            JAPANESE_UPPER_TO_LOWER.put("ゆ", "ゅ");
            JAPANESE_UPPER_TO_LOWER.put("よ", "ょ");
            JAPANESE_UPPER_TO_LOWER.put("つ", "っ");
            JAPANESE_UPPER_TO_LOWER.put("ア", "ァ");
            JAPANESE_UPPER_TO_LOWER.put("イ", "ィ");
            JAPANESE_UPPER_TO_LOWER.put("ウ", "ゥ");
            JAPANESE_UPPER_TO_LOWER.put("エ", "ェ");
            JAPANESE_UPPER_TO_LOWER.put("オ", "ォ");
            JAPANESE_UPPER_TO_LOWER.put("ヤ", "ャ");
            JAPANESE_UPPER_TO_LOWER.put("ユ", "ュ");
            JAPANESE_UPPER_TO_LOWER.put("ヨ", "ョ");
            JAPANESE_UPPER_TO_LOWER.put("ツ", "ッ");
        }
        return JAPANESE_UPPER_TO_LOWER;
    }
}
