package com.hirohiro716.scent.gui.control;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import com.hirohiro716.scent.gui.KeyCode;

/**
 * コンテキストメニューのクラス。
 * 
 * @author hiro
*/
public class ContextMenu extends Control {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスと、呼び出し元のコントロールを指定する。
     * 
     * @param innerInstance
     * @param invoker
     */
    protected ContextMenu(JComponent innerInstance, Control invoker) {
        super(innerInstance);
        this.invoker = invoker;
    }
    
    /**
     * コンストラクタ。<br>
     * このコンテキストメニューの呼び出し元コントロールを指定する。
     * 
     * @param invoker
     */
    public ContextMenu(Control invoker) {
        this(new JPopupMenu(), invoker);
    }
    
    private Control invoker;
    
    @Override
    public JPopupMenu getInnerInstance() {
        return (JPopupMenu) super.getInnerInstance();
    }
    
    /**
     * 呼び出し元のコントロールを取得する。
     * 
     * @return
     */
    public Control getInvoker() {
        return this.invoker;
    }
    
    private Map<MenuElement, ContextMenuItem> itemInnerInstanceRelations = new HashMap<>();
    
    /**
     * このコンテキストメニューのアイテムを取得する。
     * 
     * @return
     */
    public ContextMenuItem[] getContextMenuItems() {
        List<ContextMenuItem> items = new ArrayList<>();
        for (MenuElement innerInstance: this.getInnerInstance().getSubElements()) {
            items.add(this.itemInnerInstanceRelations.get(innerInstance));
        }
        return items.toArray(new ContextMenuItem[] {});
    }
    
    /**
     * このコンテキストメニューにアイテムを追加する。
     * 
     * @param contextMenuItem
     */
    public void addContextMenuItem(ContextMenuItem contextMenuItem) {
        this.getInnerInstance().add(contextMenuItem.getInnerInstance());
        this.itemInnerInstanceRelations.put(contextMenuItem.getInnerInstance(), contextMenuItem);
    }
    
    /**
     * このコンテキストメニューに、表示文字列、<br>
     * ニーモニック、実行する処理を指定してアイテムを追加する。
     * 
     * @param text
     * @param mnemonic
     * @param runnable
     * @return 作成したコンテキストメニューのアイテム。
     */
    public ContextMenuItem addContextMenuItem(String text, KeyCode mnemonic, Runnable runnable) {
        ContextMenuItem item = new ContextMenuItem(text);
        item.setMnemonic(mnemonic);
        item.setAction(runnable);
        this.addContextMenuItem(item);
        return item;
    }
    
    /**
     * このコンテキストメニューに、表示文字列、実行する処理を指定してアイテムを追加する。
     * 
     * @param text
     * @param runnable
     * @return 作成したコンテキストメニューのアイテム。
     */
    public ContextMenuItem addContextMenuItem(String text, Runnable runnable) {
        return this.addContextMenuItem(text, null, runnable);
    }
    
    /**
     * このコンテキストメニューのアイテムを削除する。
     * 
     * @param contextMenuItem
     */
    public void removeContextMenuItem(ContextMenuItem contextMenuItem) {
        this.getInnerInstance().remove(contextMenuItem.getInnerInstance());
        this.itemInnerInstanceRelations.remove(contextMenuItem.getInnerInstance());
    }
    
    /**
     * このコンテキストメニューのアイテムをクリアする。
     */
    public final void clearContextMenuItem() {
        for (ContextMenuItem item: this.getContextMenuItems()) {
            this.removeContextMenuItem(item);
        }
    }
    
    /**
     * 表示する位置を指定してコンテキストメニューを表示する。
     * 
     * @param xLocationOnInvoker 呼び出し元コントロール上での水平方向位置。
     * @param yLocationOnInvoker 呼び出し元コントロール上での垂直方向位置。
     */
    public void show(int xLocationOnInvoker, int yLocationOnInvoker) {
        this.getInnerInstance().show(this.getInvoker().getInnerInstance(), xLocationOnInvoker, yLocationOnInvoker);
    }

    @Override
    public void setDisabled(boolean isDisabled) {
        throw new IllegalCallerException("This method should not be used for context menus.");
    }

    @Override
    public void setBackgroundColor(Color color) {
        throw new IllegalCallerException("This method should not be used for context menus.");
    }

    @Override
    public void setForegroundColor(Color color) {
        throw new IllegalCallerException("This method should not be used for context menus.");
    }
}
