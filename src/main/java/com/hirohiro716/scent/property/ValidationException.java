package com.hirohiro716.scent.property;

import java.util.LinkedHashMap;
import java.util.Map;

import com.hirohiro716.scent.OS;
import com.hirohiro716.scent.StringObject;

/**
 * プロパティの検証に失敗した場合に発生する例外クラス。
 * 
 * @author hiro
*/
public class ValidationException extends Exception {
    
    private final static String MESSAGE = "プロパティの検証に失敗しました。";

    /**
     * 初期の例外メッセージを持つ新規例外を構築する。
     */
    public ValidationException() {
        super(ValidationException.MESSAGE);
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
    
    /**
     * 検証に失敗した原因の数を取得する。
     * 
     * @return 結果。
     */
    public int getNumberOfCauseProperties() {
        return this.errorMessages.size();
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
