package com.hirohiro716.io.json;

/**
 * JSONの解析を行った結果のクラス。
 * 
 * @author hiro
 *
 * @param <T> 解析して変換する型。
 */
public class ParseResult<T> {
    
    /**
     * コンストラクタ。
     * 
     * @param parsed 解析した結果の値。
     * @param source 解析値の元となったJSONデータ定義文。
     * @param remainder 解析した結果、残ったJSONデータ定義文。
     */
    public ParseResult(T parsed, String source, String remainder) {
        this.parsed = parsed;
        this.source = source;
        this.remainder = remainder;
    }
    
    private T parsed;
    
    /**
     * 解析した結果、変換されたオブジェクトを取得する。
     * 
     * @return 結果。
     */
    protected T getParsed() {
        return this.parsed;
    }
    
    private String source;
    
    /**
     * 解析した結果の元となったJSONデータ定義文をす得する。
     * 
     * @return 結果。
     */
    protected String getSource() {
        return this.source;
    }
    
    private String remainder;
    
    /**
     * 解析した結果、残ったJSONのデータ定義文を取得する。
     * 
     * @return 結果。
     */
    protected String getRemainder() {
        return this.remainder;
    }
}
