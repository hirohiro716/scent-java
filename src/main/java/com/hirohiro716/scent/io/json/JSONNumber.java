package com.hirohiro716.scent.io.json;

import com.hirohiro716.scent.StringObject;

/**
 * 数値のJSON定義文を解析するクラス。
 * (RFC 8259)(ECMA-404 2nd Edition)
 * 
 * @author hiro
*/
public class JSONNumber extends JSONValue<Double> {
    
    /**
     * コンストラクタ。
     */
    public JSONNumber() {
        super();
    }
    
    /**
     * コンストラクタ。<br>
     * JSON定義文を指定する。
     * 
     * @param json
     * @throws ParseException
     */
    public JSONNumber(String json) throws ParseException {
        super(json);
    }
    
    @Override
    protected ParseResult<Double> parse(String json) throws ParseException {
        StringObject target = new StringObject(json);
        try {
            target = new StringObject(target.split("[\\,\\}\\] ]")[0]);
            double value = target.toDouble();
            StringObject remainder = new StringObject(json);
            remainder.replace(target.toString(), "");
            return new ParseResult<Double>(value, target.toString(), remainder.toString());
        } catch (Exception exception) {
            throw new ParseException(target.toString(), exception);
        }
    }
    
    @Override
    protected String makeJSON(Double value) {
        return StringObject.newInstance(value).removeMeaninglessDecimalPoint().toString();
    }
}
