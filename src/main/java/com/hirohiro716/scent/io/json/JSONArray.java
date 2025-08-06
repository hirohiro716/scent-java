package com.hirohiro716.scent.io.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.hirohiro716.scent.Regex;
import com.hirohiro716.scent.StringObject;

/**
 * 配列のJSON定義文を解析するクラス。
 * (RFC 8259)(ECMA-404 2nd Edition)
 */
public class JSONArray extends JSONValue<List<JSONValue<?>>> {

    /**
     * コンストラクタ。
     */
    public JSONArray() {
        super();
        this.setContent(new ArrayList<>());
    }
    
    /**
     * コンストラクタ。<br>
     * JSON定義文を指定する。
     * 
     * @param json
     * @throws ParseException
     */
    public JSONArray(String json) throws ParseException {
        super(json);
    }

    @Override
    protected ParseResult<List<JSONValue<?>>> parse(String json) throws ParseException {
        StringObject target = new StringObject(json);
        try {
            List<JSONValue<?>> values = new ArrayList<>();
            boolean isOpen = false;
            int index;
            for (index = 0; index < target.length(); index++) {
                StringObject one = target.clone().extract(index, index + 1);
                if (isOpen == false) {
                    if (one.equals("[") && values.size() == 0 || one.equals(",") && values.size() > 0) {
                        isOpen = true;
                        continue;
                    }
                }
                if (one.equals("]")) {
                    break;
                }
                if (isOpen == false) {
                    continue;
                }
                if (one.equals("\"")) {
                    JSONString jsonString = new JSONString(target.clone().extract(index).toString());
                    values.add(jsonString);
                    index += jsonString.getParseResult().getSource().length() - 1;
                    isOpen = false;
                }
                if (one.toString().matches(Regex.DECIMAL_NEGATIVE.getRegex())) {
                    JSONNumber jsonNumber = new JSONNumber(target.clone().extract(index).toString());
                    values.add(jsonNumber);
                    index += jsonNumber.getParseResult().getSource().length() - 1;
                    isOpen = false;
                }
                if (one.equals("t") || one.equals("f")) {
                    JSONBoolean jsonBoolean = new JSONBoolean(target.clone().extract(index).toString());
                    values.add(jsonBoolean);
                    index += jsonBoolean.getParseResult().getSource().length() - 1;
                    isOpen = false;
                }
                if (one.equals("[")) {
                    JSONArray jsonArray = new JSONArray(target.clone().extract(index).toString());
                    values.add(jsonArray);
                    index += jsonArray.getParseResult().getSource().length() - 1;
                    isOpen = false;
                }
                if (one.equals("{")) {
                    JSONObject jsonObject = new JSONObject(target.clone().extract(index).toString());
                    values.add(jsonObject);
                    index += jsonObject.getParseResult().getSource().length() - 1;
                    isOpen = false;
                }
                if (one.equals("n")) {
                    values.add(null);
                    index += 3;
                    isOpen = false;
                }
            }
            target.extract(0, index + 1);
            StringObject remainder = new StringObject(json);
            remainder.extract(index + 1);
            return new ParseResult<List<JSONValue<?>>>(values, target.toString(), remainder.toString());
        } catch (Exception exception) {
            throw new ParseException(target.toString(), exception);
        }
    }
    
    @Override
    protected String makeJSON(List<JSONValue<?>> values) {
        StringObject result = new StringObject("[");
        for (JSONValue<?> jsonValue: values) {
            if (result.length() > 1) {
                result.append(", ");
            }
            if (jsonValue != null) {
                result.append(jsonValue.toString());
            } else {
                result.append("null");
            }
        }
        return result.append("]").toString();
    }
    
    /**
     * この配列の中に値を追加する。
     * 
     * @param value
     */
    public void add(Object value) {
        JSONValue<?> jsonValue = null;
        if (value instanceof JSONValue<?>) {
            jsonValue = (JSONValue<?>) value;
        }
        if (value instanceof String || value instanceof StringObject) {
            jsonValue = new JSONString();
            JSONString jsonString = (JSONString) jsonValue;
            jsonString.setContent(value.toString());
        }
        if (value instanceof Number) {
            jsonValue = new JSONNumber();
            JSONNumber jsonNumber = (JSONNumber) jsonValue;
            jsonNumber.setContent(((Number) value).doubleValue());
        }
        if (value instanceof Boolean) {
            jsonValue = new JSONBoolean();
            JSONBoolean jsonBoolean = (JSONBoolean) jsonValue;
            jsonBoolean.setContent((Boolean) value);
        }
        this.getContent().add(jsonValue);
    }
    
    /**
     * この配列の中に配列を追加する。
     * 
     * @param value
     */
    public void add(List<JSONValue<?>> value) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.setContent(value);
        this.getContent().add(jsonArray);
    }
    
    /**
     * この配列の中に連想配列を追加する。
     * 
     * @param value
     */
    public void add(LinkedHashMap<String, JSONValue<?>> value) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.setContent(value);
        this.getContent().add(jsonObject);
    }
}
