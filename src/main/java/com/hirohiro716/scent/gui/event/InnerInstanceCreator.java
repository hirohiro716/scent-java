package com.hirohiro716.scent.gui.event;

/**
 * GUIライブラリに依存したイベントを処理するインスタンスを作成するインターフェース。
 * 
 * @author hiro
 *
 * @param <T>
 */
public interface InnerInstanceCreator<T> {
    
    /**
     * GUIライブラリに依存したイベントを処理するインスタンスを作成する。
     * 
     * @return
     */
    public abstract T create();
}
