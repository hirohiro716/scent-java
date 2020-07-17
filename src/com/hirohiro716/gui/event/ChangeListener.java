package com.hirohiro716.gui.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hirohiro716.gui.Component;

/**
 * コントロールの値やプロパティの変更を検知するリスナーの抽象クラス。
 * 
 * @author hiro
 *
 * @param <T> 検知対象の型。
 */
public abstract class ChangeListener<T> {
    
    /**
     * コントロールの値やプロパティが変更されたときに呼び出される。
     * 
     * @param component 発生元のコンポーネント。
     * @param changedValue 変更後の値。
     * @param previousValue 変更前の値。
     */
    protected abstract void changed(Component<?> component, T changedValue, T previousValue);
    
    private Map<Component<?>, List<Object>> mapInnerInstanceList = new HashMap<>();
    
    /**
     * 変更を検知するGUIライブラリに依存したインスタンスを作成する。<br>
     * 引数には、対象のコンポーネント、GUIライブラリに依存したインスタンスを作成するコールバックを指定する。
     * 
     * @param <I> 変更を検知するGUIライブラリに依存したインスタンスの型。
     * @param component
     * @param innerInstanceCreator
     * @return 結果。
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
     * コンポーネントに応じて、このリスナーで作成されたGUIライブラリに依存したリスナーのインスタンスを取得する。
     * 
     * @param component 
     * @return 結果。
     */
    public Object[] getInnerInstances(Component<?> component) {
        List<Object> list = this.mapInnerInstanceList.get(component);
        if (list == null) {
            return new Object[] {};
        }
        return list.toArray();
    }
    
    /**
     * このリスナーの処理を実行する。
     * 
     * @param component 発生元のコンポーネント。
     * @param changed 変更後の値。
     * @param previousValue 変更前の値。
     */
    public void execute(Component<?> component, T changed, T previousValue) {
        this.changed(component, changed, previousValue);
    }
    
    private Map<Component<?>, T> mapPreviousValue = new HashMap<>();
    
    /**
     * このリスナーの前回の値から変更されている場合に処理を実行する。
     * 
     * @param component 発生元のコンポーネント。
     * @param changed 変更後の値。
     */
    public void executeWhenChanged(Component<?> component, T changed) {
        T previousValue = this.mapPreviousValue.get(component);
        if (changed != null && changed.equals(previousValue) == false || changed == null && previousValue != null) {
            this.execute(component, changed, previousValue);
        }
        this.mapPreviousValue.put(component, changed);
    }
}
