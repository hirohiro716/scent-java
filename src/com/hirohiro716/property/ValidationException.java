package com.hirohiro716.property;

import java.util.LinkedHashMap;
import java.util.Map;

import com.hirohiro716.OS;
import com.hirohiro716.StringObject;

/**
 * プロパティの検証に失敗した場合に発生する例外クラス。
 * 
 * @author hiro
 *
 */
@SuppressWarnings("serial")
public class ValidationException extends Exception {
    
    private final static String MESSAGE = "値の検証に失敗しました。";

    /**
     * 初期の例外メッセージを持つ新規例外を構築する。
     */
    public ValidationException() {
        super(MESSAGE);
    }
    
    /**
     * 例外メッセージを指定して新規例外を構築する。
     * 
     * @param message
     */
    public ValidationException(String message) {
        super(message);
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
     * @return 結果。
     */
    public PropertyInterface[] getCauseProperties() {
        return this.errorMessages.keySet().toArray(new PropertyInterface[] {});
    }
    
    @Override
    public String getMessage() {
        StringObject message = new StringObject(super.getMessage());
        for (PropertyInterface property : this.errorMessages.keySet()) {
            message.append(OS.thisOS().getLineSeparator());
            message.append("・");
            message.append(this.errorMessages.get(property));
        }
        return message.toString();
    }
}
