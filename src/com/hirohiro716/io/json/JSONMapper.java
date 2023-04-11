package com.hirohiro716.io.json;

import com.hirohiro716.property.PropertyInterface;

/**
 * JSONとオブジェクトをマップするための抽象クラス。
 * 
 * @author hiro
 * 
 * @param <P> プロパティの型。
 */
public interface JSONMapper<P extends PropertyInterface> {
    
    /**
     * JSONに含まれるすべてのプロパティを取得する。
     * 
     * @return 結果。
     */
    public abstract P[] getProperties();

    /**
     * 初期値が入力されたJSONオブジェクトを作成する。
     * 
     * @param <K> 
     * @return 結果。
     */
    public default <K extends PropertyInterface> JSONObject createDefaultJSONObject() {
        JSONObject jsonObject = new JSONObject();
        for (PropertyInterface property : this.getProperties()) {
            jsonObject.put(property, property.getDefaultValue());
        }
        return jsonObject;
    }

    /**
     * このインスタンスにマップされているすべての値が有効か検証する。
     * 
     * @throws ValidationException
     * @throws Exception
     */
    public abstract void validate() throws ValidationException, Exception;
    
    /**
     * このインスタンスにマップされているすべての値を標準化する。
     * 
     * @throws Exception
     */
    public abstract void normalize() throws Exception;
}
