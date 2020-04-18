package com.hirohiro716.io.json;

/**
 * 値のJSON定義文を解析する抽象クラス。
 * (RFC 8259)(ECMA-404 2nd Edition)
 * 
 * @author hiro
 *
 * @param <T> 解析後に変換する型。
 */
public abstract class JSONValue<T> {

    /**
     * コンストラクタ。
     */
    public JSONValue() {
        this.parseResult = null;
    }
    
    /**
     * コンストラクタ。<br>
     * JSON定義文を指定する。
     * 
     * @param json
     * @throws ParseException
     */
    public JSONValue(String json) throws ParseException {
        this.parseResult = this.parse(json);
    }
    
    private ParseResult<T> parseResult;
    
    /**
     * JSONの解析結果を取得する。
     * 
     * @return 結果。
     */
    protected ParseResult<T> getParseResult() {
        return this.parseResult;
    }
    
    /**
     * JSONを解析した結果、変換できた内容を取得する。
     * 
     * @return 結果。
     */
    public T getContent() {
        if (this.parseResult == null) {
            return null;
        }
        return this.parseResult.getParsed();
    }
    
    /**
     * 値のインスタンスをセットする。
     * 
     * @param value
     */
    public void setContent(T value) {
        this.parseResult = new ParseResult<T>(value, this.makeJSON(value), null);
    }

    /**
     * JSONのデータ定義文を取得する。
     * 
     * @return 結果。
     */
    public String toString() {
        if (this.parseResult == null) {
            return null;
        }
        T value = this.parseResult.getParsed();
        return this.makeJSON(value);
    }
    
    /**
     * JSONを解析して値のインスタンスに変換する。このメソッドはコンストラクタで自動的に呼び出される。
     * 
     * @param json JSONデータ定義文。nullが入ってくることもある。
     * @return 結果。
     * @throws ParseException
     */
    protected abstract ParseResult<T> parse(String json) throws ParseException;
    
    /**
     * 値のインスタンスからJSONのデータ定義文を作成する。
     * 
     * @param value
     * @return 結果。
     */
    protected abstract String makeJSON(T value);
}
