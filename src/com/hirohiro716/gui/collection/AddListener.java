package com.hirohiro716.gui.collection;

/**
 * コレクションに対するオブジェクトの追加を検知するリスナーの根底クラス。
 * 
 * @author hiro
 *
 * @param <T> 検知対象の型。
 */
public abstract class AddListener<T> {
    
    /**
     * コレクションにオブジェクトが追加されたときに呼び出される。
     * 
     * @param added
     * @param positionIndex 
     */
    protected abstract void added(T added, int positionIndex);

    /**
     * このリスナーが無効ではない場合に処理を実行する。
     * 
     * @param added
     * @param positionIndex 
     */
    public void executeWhenNotDisabled(T added, int positionIndex) {
        if (this.isDisabled == false) {
            this.added(added, positionIndex);
        }
    }
    
    private boolean isDisabled = false;
    
    /**
     * このリスナーが無効になっている場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isDisabled() {
        return this.isDisabled;
    }
    
    /**
     * このリスナーを無効に設定する。
     * 
     * @param isDisabled
     */
    public void setDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }
}
