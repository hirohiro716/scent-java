package com.hirohiro716.database;

import java.util.LinkedHashMap;
import java.util.Map;

import com.hirohiro716.DynamicArray;
import com.hirohiro716.OS;
import com.hirohiro716.StringObject;

/**
 * レコードの検証に失敗した場合に発生する例外クラス。
 * 
 * @author hiro
 *
 */
@SuppressWarnings("serial")
public class ValidationException extends Exception {
    
    private final static String MESSAGE = "レコードの検証に失敗しました。";

    /**
     * 検証に失敗した原因のレコードを指定して、初期の例外メッセージを持つ新規例外を構築する。
     * 
     * @param <C>
     * @param causeRecord 検証に失敗した原因のレコード。
     */
    @SuppressWarnings("unchecked")
    public <C extends ColumnInterface> ValidationException(DynamicArray<C> causeRecord) {
        super(MESSAGE);
        this.causeRecord = (DynamicArray<ColumnInterface>) causeRecord;
    }
    
    /**
     * 検証に失敗した原因のレコードと例外メッセージを指定して新規例外を構築する。
     * 
     * @param <C>
     * @param message
     * @param causeRecord 検証に失敗した原因のレコード。
     */
    @SuppressWarnings("unchecked")
    public <C extends ColumnInterface> ValidationException(String message, DynamicArray<C> causeRecord) {
        super(message);
        this.causeRecord = (DynamicArray<ColumnInterface>) causeRecord;
    }
    
    private DynamicArray<ColumnInterface> causeRecord;
    
    /**
     * 検証に失敗した原因のレコードを取得する。
     * 
     * @return 結果。
     */
    public DynamicArray<ColumnInterface> getCauseRecord() {
        return this.causeRecord;
    }
    
    private Map<ColumnInterface, String> errorMessages = new LinkedHashMap<>();
    
    /**
     * 検証に失敗した原因のカラムとエラーメッセージを追加する。
     * 
     * @param column
     * @param errorMessage
     */
    public void addCauseColumn(ColumnInterface column, String errorMessage) {
        this.errorMessages.put(column, errorMessage);
    }
    
    /**
     * 検証に失敗した原因のカラムの配列を取得する。
     * 
     * @return 結果。
     */
    public ColumnInterface[] getCauseColumns() {
        return this.errorMessages.keySet().toArray(new ColumnInterface[] {});
    }

    /**
     * 検証に失敗した原因の数を取得する。
     * 
     * @return 結果。
     */
    public int getNumberOfCauseColumns() {
        return this.errorMessages.size();
    }
    
    @Override
    public String getMessage() {
        StringObject message = new StringObject(super.getMessage());
        for (ColumnInterface column : this.errorMessages.keySet()) {
            message.append(OS.thisOS().getLineSeparator());
            message.append("・");
            message.append(this.errorMessages.get(column));
        }
        return message.toString();
    }
}
