package com.hirohiro716.scent.io.json;

import java.util.LinkedHashMap;
import java.util.Map;

import com.hirohiro716.scent.OS;
import com.hirohiro716.scent.StringObject;
import com.hirohiro716.scent.property.PropertyInterface;

/**
 * JSONオブジェクトの検証に失敗した場合に発生する例外クラス。
 * 
 * @author hiro
*/
public class ValidationException extends Exception {
    
    private final static String MESSAGE = "オブジェクトの検証に失敗しました。";

    /**
     * 検証に失敗した原因のJSONオブジェクトを指定して、初期の例外メッセージを持つ新規例外を構築する。
     * 
     * @param causeJSONObject 検証に失敗した原因のJSONオブジェクト。
     */
    public ValidationException(JSONObject causeJSONObject) {
        super(ValidationException.MESSAGE);
        this.causeJSONObject = causeJSONObject;
    }
    
    /**
     * 検証に失敗した原因のJSONオブジェクトと例外メッセージを指定して新規例外を構築する。
     * 
     * @param message
     * @param causeJSONObject 検証に失敗した原因のJSONオブジェクト。
     */
    public ValidationException(String message, JSONObject causeJSONObject) {
        super(message);
        this.causeJSONObject = causeJSONObject;
    }
    
    private JSONObject causeJSONObject;
    
    /**
     * 検証に失敗した原因のJSONオブジェクトを取得する。
     * 
     * @return
     */
    public JSONObject getCauseJSONObject() {
        return this.causeJSONObject;
    }
    
    private Map<PropertyInterface, String> errorMessages = new LinkedHashMap<>();
    
    /**
     * 検証に失敗した原因のプロパティとエラーメッセージを追加する。
     * 
     * @param property
     * @param errorMessage
     */
    public void addCauseProperty(PropertyInterface property, String errorMessage) {
        this.errorMessages.put(property, errorMessage);
    }
    
    /**
     * 検証に失敗した原因のプロパティの配列を取得する。
     * 
     * @param <P> 
     * @return
     */
    @SuppressWarnings("unchecked")
    public  <P extends PropertyInterface> P[] getCauseProperties() {
        return (P[]) this.errorMessages.keySet().toArray(new PropertyInterface[] {});
    }

    /**
     * 検証に失敗した原因の数を取得する。
     * 
     * @return
     */
    public int getNumberOfCauseProperties() {
        return this.errorMessages.size();
    }
    
    @Override
    public String getMessage() {
        StringObject message = new StringObject(super.getMessage());
        for (PropertyInterface property: this.errorMessages.keySet()) {
            message.append(OS.thisOS().getLineSeparator());
            message.append("・");
            message.append(this.errorMessages.get(property));
        }
        return message.toString();
    }
}
