package com.hirohiro716.io.json;

import java.util.LinkedHashMap;
import java.util.List;

import com.hirohiro716.Regex;
import com.hirohiro716.StringObject;

/**
 * 連想配列(JSONオブジェクト)のJSON定義文を解析するクラス。
 * (RFC 8259)(ECMA-404 2nd Edition)
 * 
 * @author hiro
 *
 */
public class JSONObject extends JSONValue<LinkedHashMap<String, JSONValue<?>>> {

    /**
     * コンストラクタ。
     */
    public JSONObject() {
        super();
        this.setContent(new LinkedHashMap<>());
    }
    
    /**
     * コンストラクタ。<br>
     * JSON定義文を指定する。
     * 
     * @param json
     * @throws ParseException
     */
    public JSONObject(String json) throws ParseException {
        super(json);
    }

    @Override
    protected ParseResult<LinkedHashMap<String, JSONValue<?>>> parse(String json) throws ParseException {
        StringObject target = new StringObject(json);
        try {
            LinkedHashMap<String, JSONValue<?>> values = new LinkedHashMap<>();
            boolean isOpen = false;
            String key = null;
            int index;
            for (index = 0; index < target.length(); index++) {
                StringObject one = target.clone().extract(index, index + 1);
                if (one.equals("{") && key == null && values.size() == 0 || one.equals(",") && values.size() > 0) {
                    isOpen = true;
                    key = null;
                    continue;
                }
                if (one.equals(":")) {
                    isOpen = true;
                    continue;
                }
                if (one.equals("}")) {
                    break;
                }
                if (isOpen == false) {
                    continue;
                }
                if (one.equals("\"")) {
                    JSONString jsonString = new JSONString(target.clone().extract(index).toString());
                    if (key == null) {
                        key = jsonString.getContent();
                    } else {
                        values.put(key, jsonString);
                    }
                    index += jsonString.getParseResult().getSource().length() - 1;
                    isOpen = false;
                }
                if (key != null && one.toString().matches(Regex.DECIMAL_NEGATIVE.getRegex())) {
                    JSONNumber jsonNumber = new JSONNumber(target.clone().extract(index).toString());
                    values.put(key, jsonNumber);
                    index += jsonNumber.getParseResult().getSource().length() - 1;
                    isOpen = false;
                }
                if (key != null && one.equals("t") || one.equals("f")) {
                    JSONBoolean jsonBoolean = new JSONBoolean(target.clone().extract(index).toString());
                    values.put(key, jsonBoolean);
                    index += jsonBoolean.getParseResult().getSource().length() - 1;
                    isOpen = false;
                }
                if (key != null && one.equals("[")) {
                    JSONArray jsonArray = new JSONArray(target.clone().extract(index).toString());
                    values.put(key, jsonArray);
                    index += jsonArray.getParseResult().getSource().length() - 1;
                    isOpen = false;
                }
                if (key != null && one.equals("{")) {
                    JSONObject jsonObject = new JSONObject(target.clone().extract(index).toString());
                    values.put(key, jsonObject);
                    index += jsonObject.getParseResult().getSource().length() - 1;
                    isOpen = false;
                }
                if (key != null && one.equals("n")) {
                    values.put(key, null);
                    index += 3;
                    isOpen = false;
                }
            }
            target.extract(0, index + 1);
            StringObject remainder = new StringObject(json);
            remainder.extract(index + 1);
            return new ParseResult<LinkedHashMap<String, JSONValue<?>>>(values, target.toString(), remainder.toString());
        } catch (Exception exception) {
            throw new ParseException(target.toString(), exception);
        }
    }

    @Override
    protected String makeJSON(LinkedHashMap<String, JSONValue<?>> hashMap) {
        StringObject result = new StringObject("{");
        for (String key : hashMap.keySet()) {
            JSONValue<?> jsonValue = hashMap.get(key);
            if (result.length() > 1) {
                result.append(", ");
            }
            JSONString keyJsonString = new JSONString();
            keyJsonString.setContent(key);
            result.append(keyJsonString.toString());
            result.append(": ");
            if (jsonValue != null) {
                result.append(jsonValue.toString());
            } else {
                result.append("null");
            }
        }
        return result.append("}").toString();
    }
    
    /**
     * この連想配列の中の値を取得する。
     * 
     * @param key
     * @return 結果。
     */
    public JSONValue<?> get(String key) {
        return this.getContent().get(key);
    }

    /**
     * この連想配列の中に値をセットする。
     * 
     * @param key 
     * @param value
     */
    public void put(String key, Object value) {
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
        this.getContent().put(key, jsonValue);
    }
    
    /**
     * この連想配列の中に配列を追加する。
     * 
     * @param key 
     * @param value
     */
    public void put(String key, List<JSONValue<?>> value) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.setContent(value);
        this.getContent().put(key, jsonArray);
    }

    /**
     * この連想配列の中に連想配列を追加する。
     * 
     * @param key 
     * @param value
     */
    public void put(String key, LinkedHashMap<String, JSONValue<?>> value) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.setContent(value);
        this.getContent().put(key, jsonObject);
    }
}
