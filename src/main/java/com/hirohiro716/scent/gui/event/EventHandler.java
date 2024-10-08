package com.hirohiro716.scent.gui.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hirohiro716.scent.gui.Component;
import com.hirohiro716.scent.gui.control.Control;

/**
 * コントロールのイベントを処理するハンドラーの抽象クラス。
 * 
 * @author hiro
 *
 * @param <E> 扱うイベントの型。
 */
public abstract class EventHandler<E extends Event<?>> {
    
    /**
     * このハンドラーが登録されているイベントが発生した場合に呼び出される。
     * 
     * @param event
     */
    protected abstract void handle(E event);
    
    private Map<Component<?>, List<Object>> mapInnerInstanceList = new HashMap<>();
    
    /**
     * イベントを処理するGUIライブラリに依存したインスタンスを作成する。<br>
     * 引数には、イベント発生元のコンポーネント、GUIライブラリに依存したインスタンスを作成するコールバックを指定する。
     * 
     * @param <I> イベントを処理するGUIライブラリに依存したインスタンスの型。
     * @param component
     * @param innerInstanceCreator
     * @return
     */
    public <I> I createInnerInstance(Component<?> component, InnerInstanceCreator<I> innerInstanceCreator) {
        List<Object> innerInstanceList = this.mapInnerInstanceList.get(component);
        if (innerInstanceList == null) {
            innerInstanceList = new ArrayList<>();
            this.mapInnerInstanceList.put(component, innerInstanceList);
        }
        I innerInstance = innerInstanceCreator.create();
        innerInstanceList.add(innerInstance);
        return innerInstance;
    }
    
    /**
     * コンポーネントに応じて、このハンドラーで作成されたGUIライブラリに依存したハンドラーのインスタンスを取得する。
     * 
     * @param component 
     * @return
     */
    public Object[] getInnerInstances(Component<?> component) {
        if (this.mapInnerInstanceList.containsKey(component)) {
            return this.mapInnerInstanceList.get(component).toArray();
        }
        return new Object[] {};
    }
    
    /**
     * このハンドラーが登録されているコントロールが無効ではない場合に<br>
     * 作成したイベントインスタンスの処理を実行する。
     * 
     * @param event
     */
    public void executeWhenControlEnabled(E event) {
        if (event.getSource().isDisabled() == false) {
            this.handle(event);
        } else {
            if (event.getSource() instanceof Control) {
                Control control = (Control) event.getSource();
                event.copy(control.getParent());
            }
        }
    }
}
