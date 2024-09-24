package com.hirohiro716.scent;

import com.hirohiro716.scent.io.json.JSONNumber;
import com.hirohiro716.scent.io.json.JSONObject;

/**
 * 位置とサイズのクラス。
 * 
 * @author hiro
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
    public Bounds(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
                    this.x = jsonNumber.getContent().floatValue();
                    break;
                case "y":
                    this.y = jsonNumber.getContent().floatValue();
                    break;
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

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Bounds) {
            Bounds bounds = (Bounds) object;
            return this.getX() == bounds.getX() && this.getY() == bounds.getY()
                    && this.getWidth() == bounds.getWidth() && this.getHeight() == bounds.getHeight();
        }
        return super.equals(object);
    }

    private float x;
    
    /**
     * 水平方向位置を取得する。
     * 
     * @return
     */
    public float getX() {
        return this.x;
    }

    /**
     * 水平方向位置を取得する。
     * 
     * @return
     */
    public int getIntegerX() {
        return (int) this.x;
    }
    
    /**
     * 水平方向位置をセットする。
     * 
     * @param x
     */
    protected void setX(float x) {
        this.x = x;
    }
    
    private float y;
    
    /**
     * 垂直方向位置を取得する。
     * 
     * @return
     */
    public float getY() {
        return this.y;
    }

    /**
     * 垂直方向位置を取得する。
     * 
     * @return
     */
    public int getIntegerY() {
        return (int) this.y;
    }
    
    /**
     * 垂直方向位置をセットする。
     * 
     * @param y
     */
    protected void setY(float y) {
        this.y = y;
    }
    
    private float width;
    
    /**
     * 幅を取得する。
     * 
     * @return
     */
    public float getWidth() {
        return this.width;
    }

    /**
     * 幅を取得する。
     * 
     * @return
     */
    public int getIntegerWidth() {
        return (int) this.width;
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
     * @return
     */
    public float getHeight() {
        return this.height;
    }

    /**
     * 高さを取得する。
     * 
     * @return
     */
    public int getIntegerHeight() {
        return (int) this.height;
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
     * @return
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
