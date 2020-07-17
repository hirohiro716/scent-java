package com.hirohiro716.graphic.ui.dialog;

import com.hirohiro716.graphic.ui.Frame;
import com.hirohiro716.graphic.ui.control.Pane;

/**
 * ダイアログのインターフェース。
 * 
 * @author hiro
 *
 */
public interface DialogInterface {

    /**
     * このダイアログのオーナーを取得する。
     * 
     * @return 結果。
     */
    public abstract Frame<?>  getOwner();
    

    /**
     * このダイアログのパネルを取得する。
     * 
     * @param <P> パネルの型。
     * @return 結果。
     */
    public abstract <P extends Pane> P getPane();
    
    /**
     * このダイアログのタイトルを取得する。
     * 
     * @return 結果。
     */
    public abstract String getTitle();
    
    /**
     * このダイアログのタイトルをセットする。
     * 
     * @param title
     */
    public abstract void setTitle(String title);
}
