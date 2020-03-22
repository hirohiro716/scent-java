package com.hirohiro716.gui;

import com.hirohiro716.io.json.JSONNumber;
import com.hirohiro716.io.json.JSONObject;
import com.hirohiro716.io.json.ParseException;

/**
 * コンポーネントの位置とサイズのクラス。
 * 
 * @author hiro
 *
 */
public class Bounds {
    
    /**
     * コンストラクタ。<br>
     * 水平方向位置、垂直方向位置、幅、高さを指定する。
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public Bounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    /**
     * コンストラクタ。
     */
    public Bounds() {
        this(0, 0, 0, 0);
    }
    
    /**
     * コンストラクタ。<br>
     * 水平方向位置、垂直方向位置、幅、高さをJSONデータ定義文で指定する。
     * 
     * @param json
     * @throws ParseException
     */
    public Bounds(String json) throws ParseException {
        JSONObject jsonObject = new JSONObject(json);
        for (String key : jsonObject.get().keySet()) {
            try {
                JSONNumber jsonNumber = (JSONNumber) jsonObject.get().get(key);
                switch (key) {
                case "x":
                    this.x = jsonNumber.get().intValue();
                    break;
                case "y":
                    this.y = jsonNumber.get().intValue();
                    break;
                case "width":
                    this.width = jsonNumber.get().intValue();
                    break;
                case "height":
                    this.height = jsonNumber.get().intValue();
                    break;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
    
    private int x;
    
    /**
     * 水平方向位置を取得する。
     * 
     * @return 結果。
     */
    public int getX() {
        return this.x;
    }
    
    /**
     * 水平方向位置をセットする。
     * 
     * @param x
     */
    protected void setX(int x) {
        this.x = x;
    }
    
    private int y;
    
    /**
     * 垂直方向位置を取得する。
     * 
     * @return 結果。
     */
    public int getY() {
        return this.y;
    }
    
    /**
     * 垂直方向位置をセットする。
     * 
     * @param y
     */
    protected void setY(int y) {
        this.y = y;
    }
    
    private int width;
    
    /**
     * 幅を取得する。
     * 
     * @return 結果。
     */
    public int getWidth() {
        return this.width;
    }
    
    /**
     * 幅をセットする。
     * 
     * @param width
     */
    protected void setWidth(int width) {
        this.width = width;
    }
    
    private int height;
    
    /**
     * 高さを取得する。
     * 
     * @return 結果。
     */
    public int getHeight() {
        return this.height;
    }
    
    /**
     * 高さをセットする。
     * 
     * @param height
     */
    protected void setHeight(int height) {
        this.height = height;
    }
    
    /**
     * このインスタンスの値でJSONオブジェクトを作成する。
     * 
     * @return 結果。
     */
    public JSONObject createJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("x", this.x);
        jsonObject.put("y", this.y);
        jsonObject.put("width", this.width);
        jsonObject.put("height", this.height);
        return jsonObject;
    }
}
