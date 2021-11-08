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

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    /**
     * このインスタンスの文字列と、指定されたオブジェクトの文字列表現を比較する。
     * 
     * @param object
     * @return 結果。
     */
    @Override
    public boolean equals(Object object) {
        return this.toString().equals(StringObject.newInstance(object).toString());
    }

    /**
     * このインスタンスの文字列と、指定された文字列の文字列表現を比較する。
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
     * このインスタンスの文字列として、指定されたオブジェクトの文字列表現をセットする。
     * 
     * @param value
     * @return 結果。
     */
    public StringObject set(Object value) {
        this.value = new StringBuilder();
        if (value != null) {
            this.value.append(value);
        }
        return this;
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
     * @param numberOfRepeats
     * @return このインスタンス。
     */
    public StringObject repeat(int numberOfRepeats) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int number = 0; number < numberOfRepeats; number++) {
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
        this.replace("^[ 　]{1,}", "");
        return this.replace("[ 　]{1,}$", "");
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
        this.replace("\r([^\n])|\r$", replacement + "$1");
        return this.replace("\r([^\n])|\r$", replacement + "$1");
    }
    
    /**
     * LFを置き換える。CRLFのLFは置き換えられない。.
     * 
     * @param replacement
     * @return このインスタンス。
     */
    public final StringObject replaceLF(String replacement) {
        this.replace("([^\r])\n|^\n", "$1" + replacement);
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
     * タブを置き換える。
     * 
     * @param replacement
     * @return このインスタンス。
     */
    public final StringObject replaceTab(String replacement) {
        return this.replace("\t", replacement);
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
            return null;
        }
    }
    
    /**
     * 文字列をcom.hirohiro716.datetime.Datetimeに変換する。失敗した場合はnullを返す。
     * 
     * @return com.hirohiro716.datetime.Datetime、またはnull。
     */
    public Datetime toDatetime() {
        try {
            return Datetime.newInstance(this.value.toString());
        } catch (ParseException exception) {
            return null;
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
        return StringObject.joinWithSeparator(objects, null);
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
    
    private static Map<String, String> NARROW_TO_WIDE = null;
    
    private static Map<String, String> getNarrowToWideMap() {
        if (StringObject.NARROW_TO_WIDE == null) {
            Map<String, String> map = new HashMap<>();
            map.put("a", "ａ");
            map.put("b", "ｂ");
            map.put("c", "ｃ");
            map.put("d", "ｄ");
            map.put("e", "ｅ");
            map.put("f", "ｆ");
            map.put("g", "ｇ");
            map.put("h", "ｈ");
            map.put("i", "ｉ");
            map.put("j", "ｊ");
            map.put("k", "ｋ");
            map.put("l", "ｌ");
            map.put("m", "ｍ");
            map.put("n", "ｎ");
            map.put("o", "ｏ");
            map.put("p", "ｐ");
            map.put("q", "ｑ");
            map.put("r", "ｒ");
            map.put("s", "ｓ");
            map.put("t", "ｔ");
            map.put("u", "ｕ");
            map.put("v", "ｖ");
            map.put("w", "ｗ");
            map.put("x", "ｘ");
            map.put("y", "ｙ");
            map.put("z", "ｚ");
            map.put("A", "Ａ");
            map.put("B", "Ｂ");
            map.put("C", "Ｃ");
            map.put("D", "Ｄ");
            map.put("E", "Ｅ");
            map.put("F", "Ｆ");
            map.put("G", "Ｇ");
            map.put("H", "Ｈ");
            map.put("I", "Ｉ");
            map.put("J", "Ｊ");
            map.put("K", "Ｋ");
            map.put("L", "Ｌ");
            map.put("M", "Ｍ");
            map.put("N", "Ｎ");
            map.put("O", "Ｏ");
            map.put("P", "Ｐ");
            map.put("Q", "Ｑ");
            map.put("R", "Ｒ");
            map.put("S", "Ｓ");
            map.put("T", "Ｔ");
            map.put("U", "Ｕ");
            map.put("V", "Ｖ");
            map.put("W", "Ｗ");
            map.put("X", "Ｘ");
            map.put("Y", "Ｙ");
            map.put("Z", "Ｚ");
            map.put("0", "０");
            map.put("1", "１");
            map.put("2", "２");
            map.put("3", "３");
            map.put("4", "４");
            map.put("5", "５");
            map.put("6", "６");
            map.put("7", "７");
            map.put("8", "８");
            map.put("9", "９");
            map.put("ｧ", "ァ");
            map.put("ｱ", "ア");
            map.put("ｨ", "ィ");
            map.put("ｲ", "イ");
            map.put("ｩ", "ゥ");
            map.put("ｳ", "ウ");
            map.put("ｪ", "ェ");
            map.put("ｴ", "エ");
            map.put("ｫ", "ォ");
            map.put("ｵ", "オ");
            map.put("ｶ", "カ");
            map.put("ｶﾞ", "ガ");
            map.put("ｷ", "キ");
            map.put("ｷﾞ", "ギ");
            map.put("ｸ", "ク");
            map.put("ｸﾞ", "グ");
            map.put("ｹ", "ケ");
            map.put("ｹﾞ", "ゲ");
            map.put("ｺ", "コ");
            map.put("ｺﾞ", "ゴ");
            map.put("ｻ", "サ");
            map.put("ｻﾞ", "ザ");
            map.put("ｼ", "シ");
            map.put("ｼﾞ", "ジ");
            map.put("ｽ", "ス");
            map.put("ｽﾞ", "ズ");
            map.put("ｾ", "セ");
            map.put("ｾﾞ", "ゼ");
            map.put("ｿ", "ソ");
            map.put("ｿﾞ", "ゾ");
            map.put("ﾀ", "タ");
            map.put("ﾀﾞ", "ダ");
            map.put("ﾁ", "チ");
            map.put("ﾁﾞ", "ヂ");
            map.put("ｯ", "ッ");
            map.put("ﾂ", "ツ");
            map.put("ﾂﾞ", "ヅ");
            map.put("ﾃ", "テ");
            map.put("ﾃﾞ", "デ");
            map.put("ﾄ", "ト");
            map.put("ﾄﾞ", "ド");
            map.put("ﾅ", "ナ");
            map.put("ﾆ", "ニ");
            map.put("ﾇ", "ヌ");
            map.put("ﾈ", "ネ");
            map.put("ﾉ", "ノ");
            map.put("ﾊ", "ハ");
            map.put("ﾊﾞ", "バ");
            map.put("ﾊﾟ", "パ");
            map.put("ﾋ", "ヒ");
            map.put("ﾋﾞ", "ビ");
            map.put("ﾋﾟ", "ピ");
            map.put("ﾌ", "フ");
            map.put("ﾌﾞ", "ブ");
            map.put("ﾌﾟ", "プ");
            map.put("ﾍ", "ヘ");
            map.put("ﾍﾞ", "ベ");
            map.put("ﾍﾟ", "ペ");
            map.put("ﾎ", "ホ");
            map.put("ﾎﾞ", "ボ");
            map.put("ﾎﾟ", "ポ");
            map.put("ﾏ", "マ");
            map.put("ﾐ", "ミ");
            map.put("ﾑ", "ム");
            map.put("ﾒ", "メ");
            map.put("ﾓ", "モ");
            map.put("ｬ", "ャ");
            map.put("ﾔ", "ヤ");
            map.put("ｭ", "ュ");
            map.put("ﾕ", "ユ");
            map.put("ｮ", "ョ");
            map.put("ﾖ", "ヨ");
            map.put("ﾗ", "ラ");
            map.put("ﾘ", "リ");
            map.put("ﾙ", "ル");
            map.put("ﾚ", "レ");
            map.put("ﾛ", "ロ");
            map.put("ﾜ", "ワ");
            map.put("ｦ", "ヲ");
            map.put("ﾝ", "ン");
            map.put("ｳﾞ", "ヴ");
            map.put("!", "！");
            map.put("\"", "”");
            map.put("#", "＃");
            map.put("$", "＄");
            map.put("%", "％");
            map.put("&", "＆");
            map.put("'", "’");
            map.put("(", "（");
            map.put(")", "）");
            map.put("=", "＝");
            map.put("-", "－");
            map.put("‑", "－");
            map.put("ｰ", "－");
            map.put("~", "～");
            map.put("^", "＾");
            map.put("\\", "￥");
            map.put("@", "＠");
            map.put("+", "＋");
            map.put("*", "＊");
            map.put("{", "｛");
            map.put("}", "｝");
            map.put("[", "［");
            map.put("]", "］");
            map.put(";", "；");
            map.put(":", "：");
            map.put("<", "＜");
            map.put(">", "＞");
            map.put(",", "，");
            map.put(".", "．");
            map.put("?", "？");
            map.put("_", "＿");
            map.put("/", "／");
            map.put(" ", "　");
            StringObject.NARROW_TO_WIDE = map;
        }
        return StringObject.NARROW_TO_WIDE;
    }
    
    private static Map<String, String> WIDE_TO_NARROW = null;
    
    private static Map<String, String> getWideToNarrowMap() {
        if (StringObject.WIDE_TO_NARROW == null) {
            Map<String, String> map = new HashMap<>();
            map.put("ａ", "a");
            map.put("ｂ", "b");
            map.put("ｃ", "c");
            map.put("ｄ", "d");
            map.put("ｅ", "e");
            map.put("ｆ", "f");
            map.put("ｇ", "g");
            map.put("ｈ", "h");
            map.put("ｉ", "i");
            map.put("ｊ", "j");
            map.put("ｋ", "k");
            map.put("ｌ", "l");
            map.put("ｍ", "m");
            map.put("ｎ", "n");
            map.put("ｏ", "o");
            map.put("ｐ", "p");
            map.put("ｑ", "q");
            map.put("ｒ", "r");
            map.put("ｓ", "s");
            map.put("ｔ", "t");
            map.put("ｕ", "u");
            map.put("ｖ", "v");
            map.put("ｗ", "w");
            map.put("ｘ", "x");
            map.put("ｙ", "y");
            map.put("ｚ", "z");
            map.put("Ａ", "A");
            map.put("Ｂ", "B");
            map.put("Ｃ", "C");
            map.put("Ｄ", "D");
            map.put("Ｅ", "E");
            map.put("Ｆ", "F");
            map.put("Ｇ", "G");
            map.put("Ｈ", "H");
            map.put("Ｉ", "I");
            map.put("Ｊ", "J");
            map.put("Ｋ", "K");
            map.put("Ｌ", "L");
            map.put("Ｍ", "M");
            map.put("Ｎ", "N");
            map.put("Ｏ", "O");
            map.put("Ｐ", "P");
            map.put("Ｑ", "Q");
            map.put("Ｒ", "R");
            map.put("Ｓ", "S");
            map.put("Ｔ", "T");
            map.put("Ｕ", "U");
            map.put("Ｖ", "V");
            map.put("Ｗ", "W");
            map.put("Ｘ", "X");
            map.put("Ｙ", "Y");
            map.put("Ｚ", "Z");
            map.put("０", "0");
            map.put("１", "1");
            map.put("２", "2");
            map.put("３", "3");
            map.put("４", "4");
            map.put("５", "5");
            map.put("６", "6");
            map.put("７", "7");
            map.put("８", "8");
            map.put("９", "9");
            map.put("ァ", "ｧ");
            map.put("ア", "ｱ");
            map.put("ィ", "ｨ");
            map.put("イ", "ｲ");
            map.put("ゥ", "ｩ");
            map.put("ウ", "ｳ");
            map.put("ェ", "ｪ");
            map.put("エ", "ｴ");
            map.put("ォ", "ｫ");
            map.put("オ", "ｵ");
            map.put("カ", "ｶ");
            map.put("ガ", "ｶﾞ");
            map.put("キ", "ｷ");
            map.put("ギ", "ｷﾞ");
            map.put("ク", "ｸ");
            map.put("グ", "ｸﾞ");
            map.put("ケ", "ｹ");
            map.put("ゲ", "ｹﾞ");
            map.put("コ", "ｺ");
            map.put("ゴ", "ｺﾞ");
            map.put("サ", "ｻ");
            map.put("ザ", "ｻﾞ");
            map.put("シ", "ｼ");
            map.put("ジ", "ｼﾞ");
            map.put("ス", "ｽ");
            map.put("ズ", "ｽﾞ");
            map.put("セ", "ｾ");
            map.put("ゼ", "ｾﾞ");
            map.put("ソ", "ｿ");
            map.put("ゾ", "ｿﾞ");
            map.put("タ", "ﾀ");
            map.put("ダ", "ﾀﾞ");
            map.put("チ", "ﾁ");
            map.put("ヂ", "ﾁﾞ");
            map.put("ッ", "ｯ");
            map.put("ツ", "ﾂ");
            map.put("ヅ", "ﾂﾞ");
            map.put("テ", "ﾃ");
            map.put("デ", "ﾃﾞ");
            map.put("ト", "ﾄ");
            map.put("ド", "ﾄﾞ");
            map.put("ナ", "ﾅ");
            map.put("ニ", "ﾆ");
            map.put("ヌ", "ﾇ");
            map.put("ネ", "ﾈ");
            map.put("ノ", "ﾉ");
            map.put("ハ", "ﾊ");
            map.put("バ", "ﾊﾞ");
            map.put("パ", "ﾊﾟ");
            map.put("ヒ", "ﾋ");
            map.put("ビ", "ﾋﾞ");
            map.put("ピ", "ﾋﾟ");
            map.put("フ", "ﾌ");
            map.put("ブ", "ﾌﾞ");
            map.put("プ", "ﾌﾟ");
            map.put("ヘ", "ﾍ");
            map.put("ベ", "ﾍﾞ");
            map.put("ペ", "ﾍﾟ");
            map.put("ホ", "ﾎ");
            map.put("ボ", "ﾎﾞ");
            map.put("ポ", "ﾎﾟ");
            map.put("マ", "ﾏ");
            map.put("ミ", "ﾐ");
            map.put("ム", "ﾑ");
            map.put("メ", "ﾒ");
            map.put("モ", "ﾓ");
            map.put("ャ", "ｬ");
            map.put("ヤ", "ﾔ");
            map.put("ュ", "ｭ");
            map.put("ユ", "ﾕ");
            map.put("ョ", "ｮ");
            map.put("ヨ", "ﾖ");
            map.put("ラ", "ﾗ");
            map.put("リ", "ﾘ");
            map.put("ル", "ﾙ");
            map.put("レ", "ﾚ");
            map.put("ロ", "ﾛ");
            map.put("ワ", "ﾜ");
            map.put("ヲ", "ｦ");
            map.put("ン", "ﾝ");
            map.put("ヴ", "ｳﾞ");
            map.put("！", "!");
            map.put("”", "\"");
            map.put("＃", "#");
            map.put("＄", "$");
            map.put("％", "%");
            map.put("＆", "&");
            map.put("’", "'");
            map.put("（", "(");
            map.put("）", ")");
            map.put("＝", "=");
            map.put("－", "-");
            map.put("～", "~");
            map.put("＾", "^");
            map.put("￥", "\\");
            map.put("＠", "@");
            map.put("＋", "+");
            map.put("＊", "*");
            map.put("｛", "{");
            map.put("｝", "}");
            map.put("［", "[");
            map.put("］", "]");
            map.put("；", ";");
            map.put("：", ":");
            map.put("＜", "<");
            map.put("＞", ">");
            map.put("，", ",");
            map.put("．", ".");
            map.put("？", "?");
            map.put("＿", "_");
            map.put("／", "/");
            map.put("　", " ");
            StringObject.WIDE_TO_NARROW = map;
        }
        return StringObject.WIDE_TO_NARROW;
    }
    
    private static Map<String, String> HIRAGANA_TO_KATAKANA = null;
    
    private static Map<String, String> getHiraganaToKatakanaMap() {
        if (StringObject.HIRAGANA_TO_KATAKANA == null) {
            Map<String, String> map = new HashMap<>();
            map.put("ぁ", "ァ");
            map.put("あ", "ア");
            map.put("ぃ", "ィ");
            map.put("い", "イ");
            map.put("ぅ", "ゥ");
            map.put("う", "ウ");
            map.put("ぇ", "ェ");
            map.put("え", "エ");
            map.put("ぉ", "ォ");
            map.put("お", "オ");
            map.put("か", "カ");
            map.put("が", "ガ");
            map.put("き", "キ");
            map.put("ぎ", "ギ");
            map.put("く", "ク");
            map.put("ぐ", "グ");
            map.put("け", "ケ");
            map.put("げ", "ゲ");
            map.put("こ", "コ");
            map.put("ご", "ゴ");
            map.put("さ", "サ");
            map.put("ざ", "ザ");
            map.put("し", "シ");
            map.put("じ", "ジ");
            map.put("す", "ス");
            map.put("ず", "ズ");
            map.put("せ", "セ");
            map.put("ぜ", "ゼ");
            map.put("そ", "ソ");
            map.put("ぞ", "ゾ");
            map.put("た", "タ");
            map.put("だ", "ダ");
            map.put("ち", "チ");
            map.put("ぢ", "ヂ");
            map.put("っ", "ッ");
            map.put("つ", "ツ");
            map.put("づ", "ヅ");
            map.put("て", "テ");
            map.put("で", "デ");
            map.put("と", "ト");
            map.put("ど", "ド");
            map.put("な", "ナ");
            map.put("に", "ニ");
            map.put("ぬ", "ヌ");
            map.put("ね", "ネ");
            map.put("の", "ノ");
            map.put("は", "ハ");
            map.put("ば", "バ");
            map.put("ぱ", "パ");
            map.put("ひ", "ヒ");
            map.put("び", "ビ");
            map.put("ぴ", "ピ");
            map.put("ふ", "フ");
            map.put("ぶ", "ブ");
            map.put("ぷ", "プ");
            map.put("へ", "ヘ");
            map.put("べ", "ベ");
            map.put("ぺ", "ペ");
            map.put("ほ", "ホ");
            map.put("ぼ", "ボ");
            map.put("ぽ", "ポ");
            map.put("ま", "マ");
            map.put("み", "ミ");
            map.put("む", "ム");
            map.put("め", "メ");
            map.put("も", "モ");
            map.put("ゃ", "ャ");
            map.put("や", "ヤ");
            map.put("ゅ", "ュ");
            map.put("ゆ", "ユ");
            map.put("ょ", "ョ");
            map.put("よ", "ヨ");
            map.put("ら", "ラ");
            map.put("り", "リ");
            map.put("る", "ル");
            map.put("れ", "レ");
            map.put("ろ", "ロ");
            map.put("ゎ", "ヮ");
            map.put("わ", "ワ");
            map.put("ゐ", "ヰ");
            map.put("ゑ", "ヱ");
            map.put("を", "ヲ");
            map.put("ん", "ン");
            StringObject.HIRAGANA_TO_KATAKANA = map;
        }
        return StringObject.HIRAGANA_TO_KATAKANA;
    }
    
    private static Map<String, String> KATAKANA_TO_HIRAGANA = null;
    
    private static Map<String, String> getKatakanaToHiraganaMap() {
        if (StringObject.KATAKANA_TO_HIRAGANA == null) {
            Map<String, String> map = new HashMap<>();
            map.put("ァ", "ぁ");
            map.put("ア", "あ");
            map.put("ィ", "ぃ");
            map.put("イ", "い");
            map.put("ゥ", "ぅ");
            map.put("ウ", "う");
            map.put("ェ", "ぇ");
            map.put("エ", "え");
            map.put("ォ", "ぉ");
            map.put("オ", "お");
            map.put("カ", "か");
            map.put("ガ", "が");
            map.put("キ", "き");
            map.put("ギ", "ぎ");
            map.put("ク", "く");
            map.put("グ", "ぐ");
            map.put("ケ", "け");
            map.put("ゲ", "げ");
            map.put("コ", "こ");
            map.put("ゴ", "ご");
            map.put("サ", "さ");
            map.put("ザ", "ざ");
            map.put("シ", "し");
            map.put("ジ", "じ");
            map.put("ス", "す");
            map.put("ズ", "ず");
            map.put("セ", "せ");
            map.put("ゼ", "ぜ");
            map.put("ソ", "そ");
            map.put("ゾ", "ぞ");
            map.put("タ", "た");
            map.put("ダ", "だ");
            map.put("チ", "ち");
            map.put("ヂ", "ぢ");
            map.put("ッ", "っ");
            map.put("ツ", "つ");
            map.put("ヅ", "づ");
            map.put("テ", "て");
            map.put("デ", "で");
            map.put("ト", "と");
            map.put("ド", "ど");
            map.put("ナ", "な");
            map.put("ニ", "に");
            map.put("ヌ", "ぬ");
            map.put("ネ", "ね");
            map.put("ノ", "の");
            map.put("ハ", "は");
            map.put("バ", "ば");
            map.put("パ", "ぱ");
            map.put("ヒ", "ひ");
            map.put("ビ", "び");
            map.put("ピ", "ぴ");
            map.put("フ", "ふ");
            map.put("ブ", "ぶ");
            map.put("プ", "ぷ");
            map.put("ヘ", "へ");
            map.put("ベ", "べ");
            map.put("ペ", "ぺ");
            map.put("ホ", "ほ");
            map.put("ボ", "ぼ");
            map.put("ポ", "ぽ");
            map.put("マ", "ま");
            map.put("ミ", "み");
            map.put("ム", "む");
            map.put("メ", "め");
            map.put("モ", "も");
            map.put("ャ", "ゃ");
            map.put("ヤ", "や");
            map.put("ュ", "ゅ");
            map.put("ユ", "ゆ");
            map.put("ョ", "ょ");
            map.put("ヨ", "よ");
            map.put("ラ", "ら");
            map.put("リ", "り");
            map.put("ル", "る");
            map.put("レ", "れ");
            map.put("ロ", "ろ");
            map.put("ヮ", "ゎ");
            map.put("ワ", "わ");
            map.put("ヰ", "ゐ");
            map.put("ヱ", "ゑ");
            map.put("ヲ", "を");
            map.put("ン", "ん");
            StringObject.KATAKANA_TO_HIRAGANA = map;
        }
        return StringObject.KATAKANA_TO_HIRAGANA;
    }
    
    private static Map<String, String> JAPANESE_LOWER_TO_UPPER = null;
    
    private static Map<String, String> getJapaneseLowerToUpperMap() {
        if (StringObject.JAPANESE_LOWER_TO_UPPER == null) {
            Map<String, String> map = new HashMap<>();
            map.put("ぁ", "あ");
            map.put("ぃ", "い");
            map.put("ぅ", "う");
            map.put("ぇ", "え");
            map.put("ぉ", "お");
            map.put("ゃ", "や");
            map.put("ゅ", "ゆ");
            map.put("ょ", "よ");
            map.put("っ", "つ");
            map.put("ァ", "ア");
            map.put("ィ", "イ");
            map.put("ゥ", "ウ");
            map.put("ェ", "エ");
            map.put("ォ", "オ");
            map.put("ャ", "ヤ");
            map.put("ュ", "ユ");
            map.put("ョ", "ヨ");
            map.put("ッ", "ツ");
            StringObject.JAPANESE_LOWER_TO_UPPER = map;
        }
        return StringObject.JAPANESE_LOWER_TO_UPPER;
    }
    
    private static Map<String, String> JAPANESE_UPPER_TO_LOWER = null;
    
    private static Map<String, String> getJapaneseUpperToLowerMap() {
        if (StringObject.JAPANESE_UPPER_TO_LOWER == null) {
            Map<String, String> map = new HashMap<>();
            map.put("あ", "ぁ");
            map.put("い", "ぃ");
            map.put("う", "ぅ");
            map.put("え", "ぇ");
            map.put("お", "ぉ");
            map.put("や", "ゃ");
            map.put("ゆ", "ゅ");
            map.put("よ", "ょ");
            map.put("つ", "っ");
            map.put("ア", "ァ");
            map.put("イ", "ィ");
            map.put("ウ", "ゥ");
            map.put("エ", "ェ");
            map.put("オ", "ォ");
            map.put("ヤ", "ャ");
            map.put("ユ", "ュ");
            map.put("ヨ", "ョ");
            map.put("ツ", "ッ");
            StringObject.JAPANESE_UPPER_TO_LOWER = map;
        }
        return StringObject.JAPANESE_UPPER_TO_LOWER;
    }
}
