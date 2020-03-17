package com.hirohiro716.gui.collection;

/**
 * コレクションに対するオブジェクトの削除を検知するリスナーの根底クラス。
 * 
 * @author hiro
 *
 * @param <T> 検知対象の型。
 */
public abstract class RemoveListener<T> {
    
    /**
     * コレクションにオブジェクトが削除されたときに呼び出される。
     * 
     * @param removed
     */
    protected abstract void removed(T removed);

    /**
     * このリスナーが無効ではない場合に処理を実行する。
     * 
     * @param removed
     */
    public void executeWhenNotDisabled(T removed) {
        if (this.isDisabled == false) {
            this.removed(removed);
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
