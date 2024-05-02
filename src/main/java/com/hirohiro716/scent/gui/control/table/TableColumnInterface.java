package com.hirohiro716.scent.gui.control.table;

import com.hirohiro716.scent.gui.HorizontalAlignment;
import com.hirohiro716.scent.gui.event.EventHandler;
import com.hirohiro716.scent.gui.event.MouseEvent;
import com.hirohiro716.scent.gui.event.MouseEvent.MouseButton;

/**
 * テーブルカラムのインターフェース。
 * 
 * @author hiro
 * 
 * @param <C> テーブルカラムを識別するオブジェクトの型。
 */
public interface TableColumnInterface<C> {
    
    /**
     * このテーブルカラムを識別するオブジェクトを取得する。
     * 
     * @return 結果。
     */
    public abstract C getColumnInstance();
    
    /**
     * このテーブルカラムのヘッダーテキストを取得する。
     * 
     * @return 結果。
     */
    public abstract String getHeaderText();
    
    /**
     * このテーブルカラムのヘッダーテキストをセットする。
     * 
     * @param headerText
     */
    public abstract void setHeaderText(String headerText);
    
    /**
     * このテーブルカラムのヘッダーテキストの水平方向位置を取得する。
     * 
     * @return 結果。
     */
    public abstract HorizontalAlignment getHeaderHorizontalAlignment();
    
    /**
     * このテーブルカラムのヘッダーテキストの水平方向位置をセットする。
     * 
     * @param horizontalAlignment
     */
    public abstract void setHeaderHorizontalAlignment(HorizontalAlignment horizontalAlignment);
    
    /**
     * このテーブルカラムの幅を取得する。
     * 
     * @return 結果。
     */
    public abstract Integer getWidth();
    
    /**
     * このテーブルカラムの幅をセットする。
     * 
     * @param width
     */
    public abstract void setWidth(Integer width);
    
    /**
     * このテーブルカラムの最小幅を取得する。
     * 
     * @return 結果。
     */
    public abstract Integer getMinimumWidth();
    
    /**
     * このテーブルカラムの最小幅をセットする。
     * 
     * @param width
     */
    public abstract void setMinimumWidth(Integer width);
    
    /**
     * このテーブルカラムの最大幅を取得する。
     * 
     * @return 結果。
     */
    public abstract Integer getMaximumWidth();
    
    /**
     * このテーブルカラムの最大幅をセットする。
     * 
     * @param width
     */
    public abstract void setMaximumWidth(Integer width);
    
    /**
     * このテーブルカラムがリサイズ可能な場合はtrueを返す。
     * 
     * @return 結果。
     */
    public abstract boolean isResizable();
    
    /**
     * このテーブルカラムをリサイズ可能にする場合はtrueをセットする。
     * 
     * @param isResizable
     */
    public abstract void setResizable(boolean isResizable);
    
    /**
     * このテーブルカラムのヘッダーにマウスクリックのイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public abstract void addHeaderMouseClickedEventHandler(EventHandler<MouseEvent> eventHandler);
    
    /**
     * このテーブルカラムのヘッダーにマウスの指定ボタンクリックのイベントハンドラを追加する。
     * 
     * @param mouseButton 
     * @param eventHandler
     */
    public abstract void addHeaderMouseClickedEventHandler(MouseButton mouseButton, EventHandler<MouseEvent> eventHandler);
    
    /**
     * このテーブルカラムのヘッダーにマウスのボタンを押した際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public abstract void addHeaderMousePressedEventHandler(EventHandler<MouseEvent> eventHandler);
    
    /**
     * このテーブルカラムのヘッダーにマウスの指定ボタンを押した際のイベントハンドラを追加する。
     * 
     * @param mouseButton 
     * @param eventHandler
     */
    public abstract void addHeaderMousePressedEventHandler(MouseButton mouseButton, EventHandler<MouseEvent> eventHandler);
    
    /**
     * このテーブルカラムのヘッダーにマウスのボタンを離した際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public abstract void addHeaderMouseReleasedEventHandler(EventHandler<MouseEvent> eventHandler);
    
    /**
     * このテーブルカラムのヘッダーにマウスの指定ボタンを離した際のイベントハンドラを追加する。
     * 
     * @param mouseButton 
     * @param eventHandler
     */
    public abstract void addHeaderMouseReleasedEventHandler(MouseButton mouseButton, EventHandler<MouseEvent> eventHandler);
    
    /**
     * このテーブルカラムのヘッダーの領域内でマウスホイールが回転した際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public abstract void addHeaderMouseWheelEventHandler(EventHandler<MouseEvent> eventHandler);
    
    /**
     * このテーブルカラムのヘッダーのイベントハンドラを削除する。
     * 
     * @param eventHandler
     */
    public abstract void removeEventHandler(EventHandler<?> eventHandler);
}
