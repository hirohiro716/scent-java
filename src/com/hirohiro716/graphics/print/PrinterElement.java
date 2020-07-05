package com.hirohiro716.graphics.print;

/**
 * プリンターの要素の抽象クラス。
 * 
 * @author hiro
 * 
 * @param <T> この要素が内部で保持するライブラリに依存したインスタンスの型。
 */
public abstract class PrinterElement<T> {
    
    /**
     * コンストラクタ。<br>
     * この要素が内部で保持するライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected PrinterElement(T innerInstance) {
        this.innerInstance = innerInstance;
    }
    
    private T innerInstance;
    
    /**
     * この要素が内部で保持しているライブラリに依存したインスタンスを取得する。
     * 
     * @return 結果。
     */
    public T getInnerInstance() {
        return this.innerInstance;
    }
    
    /**
     * この要素が内部で保持しているライブラリに依存したインスタンスとして指定された値をセットする。
     * 
     * @param innerInstance
     */
    protected void setInnerInstance(T innerInstance) {
        this.innerInstance = innerInstance;
    }
    
    /**
     * この要素の名前を取得する。
     * 
     * @return 結果。
     */
    public abstract String getName();

    @Override
    public String toString() {
        return this.getName();
    }
}
