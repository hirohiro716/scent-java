package com.hirohiro716.graphic;

import com.hirohiro716.StringObject;
import com.hirohiro716.io.json.JSONNumber;
import com.hirohiro716.io.json.JSONObject;

/**
 * サイズのクラス。
 * 
 * @author hiro
 *
 */
public class Dimension {

    /**
     * コンストラクタ。<br>
     * 幅、高さを指定する。
     * 
     * @param width
     * @param height
     */
    public Dimension(float width, float height) {
        this.width = width;
        this.height = height;
    }
    
    /**
     * コンストラクタ。
     */
    public Dimension() {
        this(0, 0);
    }
    
    /**
     * コンストラクタ。<br>
     * 幅、高さをJSONで指定する。
     * 
     * @param json
     */
    public Dimension(JSONObject json) {
        for (String key : json.getContent().keySet()) {
            try {
                JSONNumber jsonNumber = (JSONNumber) json.getContent().get(key);
                switch (key) {
                case "width":
                    this.width = jsonNumber.getContent().floatValue();
                    break;
                case "height":
                    this.height = jsonNumber.getContent().floatValue();
                    break;
                }
            } catch (Exception exception) {
            }
        }
    }
    
    @Override
    public String toString() {
        StringObject string = new StringObject();
        string.append("width: ");
        string.append(this.width);
        string.append(", height: ");
        string.append(this.height);
        return string.toString();
    }
    
    private float width;
    
    /**
     * 幅を取得する。
     * 
     * @return 結果。
     */
    public float getWidth() {
        return this.width;
    }
    
    /**
     * 幅をセットする。
     * 
     * @param width
     */
    protected void setWidth(float width) {
        this.width = width;
    }
    
    private float height;
    
    /**
     * 高さを取得する。
     * 
     * @return 結果。
     */
    public float getHeight() {
        return this.height;
    }
    
    /**
     * 高さをセットする。
     * 
     * @param height
     */
    protected void setHeight(float height) {
        this.height = height;
    }
    
    /**
     * このインスタンスの値でJSONオブジェクトを作成する。
     * 
     * @return 結果。
     */
    public JSONObject createJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("width", this.width);
        jsonObject.put("height", this.height);
        return jsonObject;
    }
}
