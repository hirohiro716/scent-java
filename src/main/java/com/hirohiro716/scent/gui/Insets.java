package com.hirohiro716.scent.gui;

/**
 * コンポーネントのインセットのクラス。
 */
public class Insets {

    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    public Insets(java.awt.Insets innerInstance) {
        this.innerInstance = innerInstance;
    }
    
    /**
     * コンストラクタ。<br>
     * 上、右、下、左のインセットを指定する。
     * 
     * @param top
     * @param right
     * @param bottom
     * @param left
     */
    public Insets(int top, int right, int bottom, int left) {
        this(new java.awt.Insets(top, left, bottom, right));
    }

    /**
     * コンストラクタ。
     */
    public Insets() {
        this(0, 0, 0, 0);
    }
    
    private java.awt.Insets innerInstance;
    
    /**
     * 内部で使用されるGUIライブラリに依存したインスタンスを取得する。
     * 
     * @return
     */
    public java.awt.Insets getInnerInstance() {
       return this.innerInstance;
    }

    /**
     * 上インセットを取得する。
     * 
     * @return
     */
    public int getTop() {
        return this.innerInstance.top;
    }
    
    /**
     * 上インセットをセットする。
     * 
     * @param top
     */
    protected void setTop(int top) {
        this.innerInstance.top = top;
    }
    
    /**
     * 右インセットを取得する。
     * 
     * @return
     */
    public int getRight() {
        return this.innerInstance.right;
    }
    
    /**
     * 右インセットをセットする。
     * 
     * @param right
     */
    protected void setRight(int right) {
        this.innerInstance.right = right;
    }
    
    /**
     * 下インセットを取得する。
     * 
     * @return
     */
    public int getBottom() {
        return this.innerInstance.bottom;
    }
    
    /**
     * 下インセットをセットする。
     * 
     * @param bottom
     */
    protected void setBottom(int bottom) {
        this.innerInstance.bottom = bottom;
    }
    
    /**
     * 左インセットを取得する。
     * 
     * @return
     */
    public int getLeft() {
        return this.innerInstance.left;
    }
    
    /**
     * 左インセットをセットする。
     * 
     * @param left
     */
    protected void setLeft(int left) {
        this.innerInstance.left = left;
    }
}
