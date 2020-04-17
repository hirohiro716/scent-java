package com.hirohiro716.io.json;

import com.hirohiro716.StringObject;

/**
 * 真偽値のJSON定義文を解析するクラス。
 * (RFC 8259)(ECMA-404 2nd Edition)
 * 
 * @author hiro
 *
 */
public class JSONBoolean extends JSONValue<Boolean> {

    /**
     * コンストラクタ。
     */
    public JSONBoolean() {
        super();
    }
    
    /**
     * コンストラクタ。<br>
     * JSON定義文を指定する。
     * 
     * @param json
     * @throws ParseException
     */
    public JSONBoolean(String json) throws ParseException {
        super(json);
    }

    @Override
    protected ParseResult<Boolean> parse(String json) throws ParseException {
        StringObject target = new StringObject(json);
        try {
            target = new StringObject(target.split("[\\,\\}\\] ]")[0]);
            boolean value = target.toBoolean();
            StringObject remainder = new StringObject(json);
            remainder.replace(target.toString(), "");
            return new ParseResult<Boolean>(value, target.toString(), remainder.toString());
        } catch (Exception exception) {
            throw new ParseException(target.toString(), exception);
        }
    }

    @Override
    protected String makeJSON(Boolean value) {
        if (value == null) {
            return "false";
        }
        return value.toString();
    }
}
