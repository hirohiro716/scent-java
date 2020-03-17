package com.hirohiro716.io.json;

import com.hirohiro716.StringObject;

/**
 * 文字列値のJSON定義文を解析するクラス。
 * (RFC 8259)(ECMA-404 2nd Edition)
 * 
 * @author hiro
 *
 */
public class JSONString extends JSONValue<String> {

    /**
     * コンストラクタ。
     */
    public JSONString() {
        super();
    }
    
    /**
     * コンストラクタ。
     * 
     * @param json
     * @throws ParseException
     */
    public JSONString(String json) throws ParseException {
        super(json);
    }

    @Override
    protected ParseResult<String> parse(String json) throws ParseException {
        StringObject target = new StringObject(json);
        try {
            StringObject value = new StringObject();
            boolean isStarted = false;
            int index;
            for (index = 0; index < target.length(); index++) {
                StringObject one = target.clone().extract(index, index + 1);
                if (one.equals("\"")) {
                    if (isStarted == false) {
                        isStarted = true;
                        continue;
                    }
                    break;
                }
                StringObject two = target.clone().extract(index, index + 2);
                switch (two.toString()) {
                case "\\\"":
                case "\\\\":
                case "\\/":
                    value.append(two.extract(1));
                    index++;
                    continue;
                case "\\b":
                    value.append("\b");
                    index++;
                    continue;
                case "\\f":
                    value.append("\f");
                    index++;
                    continue;
                case "\\n":
                    value.append("\n");
                    index++;
                    continue;
                case "\\r":
                    value.append("\r");
                    index++;
                    continue;
                case "\\t":
                    value.append("\t");
                    index++;
                    continue;
                case "\\u":
                    String code = target.clone().extract(index + 2, index + 6).toString();
                    value.append((char) Integer.parseInt(code, 16));
                    index += 5;
                    continue;
                default:
                    value.append(one);
                    continue;
                }
            }
            target.extract(0, index + 1);
            StringObject remainder = new StringObject(json);
            remainder.extract(index + 1);
            return new ParseResult<String>(value.toString(), target.toString(), remainder.toString());
        } catch (Exception exception) {
            throw new ParseException(target.toString(), exception);
        }
    }

    @Override
    protected String makeJSON(String value) {
        StringObject valueObject = new StringObject(value);
        StringObject result = new StringObject("\"");
        for (String one : valueObject) {
            switch (one.toString()) {
            case "\"":
            case "\\":
            case "/":
            case "\b":
            case "\f":
            case "\n":
            case "\r":
            case "\t":
                result.append("\\");
                break;
            }
            result.append(one);
        }
        return result.append("\"").toString();
    }
}
