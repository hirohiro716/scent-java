package com.hirohiro716.io.json;

/**
 * JSONの解析に失敗した場合に発生する例外クラス。
 * 
 * @author hiro
 * 
 */
@SuppressWarnings("serial")
public class ParseException extends Exception {
    
    /**
     * 指定されたJSONデータ定義文を元に新規例外を構築する。原因は初期化されないが、Throwable.initCause(java.lang.Throwable)を呼び出すことにで初期化することができる。
     * 
     * @param json
     */
    public ParseException(String json) {
        super(json);
    }
    
    /**
     * 指定されたJSONデータ定義文および、原因を使用して新規例外を構築する。causeのメッセージがこの例外のメッセージに自動的に統合されることはない。
     * 
     * @param json
     * @param cause
     */
    public ParseException(String json, Throwable cause) {
        super(json, cause);
    }
}
