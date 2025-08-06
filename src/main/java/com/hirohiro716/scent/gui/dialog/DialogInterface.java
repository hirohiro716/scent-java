package com.hirohiro716.scent.gui.dialog;

import com.hirohiro716.scent.gui.Frame;
import com.hirohiro716.scent.gui.control.Pane;

/**
 * ダイアログのインターフェース。
 */
public interface DialogInterface {

    /**
     * このダイアログのオーナーを取得する。
     * 
     * @return
     */
    public abstract Frame<?>  getOwner();
    

    /**
     * このダイアログのペインを取得する。
     * 
     * @param <P> パネルの型。
     * @return
     */
    public abstract <P extends Pane> P getPane();
    
    /**
     * このダイアログのタイトルを取得する。
     * 
     * @return
     */
    public abstract String getTitle();
    
    /**
     * このダイアログのタイトルをセットする。
     * 
     * @param title
     */
    public abstract void setTitle(String title);
}
