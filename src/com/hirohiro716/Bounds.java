package com.hirohiro716;

import com.hirohiro716.io.json.JSONNumber;
import com.hirohiro716.io.json.JSONObject;

/**
 * 位置とサイズのクラス。
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
     * 水平方向位置、垂直方向位置、幅、高さをJSONで指定する。
     * 
     * @param json
     */
    public Bounds(JSONObject json) {
        for (String key : json.getContent().keySet()) {
            try {
                JSONNumber jsonNumber = (JSONNumber) json.getContent().get(key);
                switch (key) {
                case "x":
                    this.x = jsonNumber.getContent().intValue();
                    break;
                case "y":
                    this.y = jsonNumber.getContent().intValue();
                    break;
                case "width":
                    this.width = jsonNumber.getContent().intValue();
                    break;
                case "height":
                    this.height = jsonNumber.getContent().intValue();
                    break;
                }
            } catch (Exception exception) {
            }
        }
    }
    
    @Override
    public String toString() {
        StringObject string = new StringObject();
        string.append("x: ");
        string.append(this.x);
        string.append(", y: ");
        string.append(this.y);
        string.append(", width: ");
        string.append(this.width);
        string.append(", height: ");
        string.append(this.height);
        return string.toString();
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
    public void setX(int x) {
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
    public void setY(int y) {
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
    public void setWidth(int width) {
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
    public void setHeight(int height) {
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
