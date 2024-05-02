package com.hirohiro716.scent.gui.event;

import java.awt.AWTEvent;

import com.hirohiro716.scent.gui.Component;
import com.hirohiro716.scent.gui.control.Control;

/**
 * GUIのすべてのイベントの抽象クラス。
 * 
 * @author hiro
 * 
 * @param <T> 内部で使用されるGUIライブラリに依存したインスタンスの型。
 */
public abstract class Event<T extends AWTEvent> {
    
    /**
     * コンストラクタ。<br>
     * イベントの発生元のコンポーネントと、GUIライブラリに依存したイベントのインスタンスを指定する。
     * 
     * @param component
     * @param innerInstance
     */
    public Event(Component<?> component, T innerInstance) {
        this.component = component;
        this.innerInstance = innerInstance;
    }
    
    private Component<?> component;
    
    private T innerInstance;

    /**
     * このイベントがラップしている、GUIライブラリに依存したイベントのインスタンスを取得する。
     * 
     * @return 結果。
     */
    protected T getInnerInstance() {
        return this.innerInstance;
    }
    
    /**
     * このイベントの発生元コンポーネントを取得する。
     * 
     * @param <C> 
     * @return 結果。
     */
    @SuppressWarnings("unchecked")
    public <C extends Component<?>> C getSource() {
        return (C) this.component;
    }
    
    /**
     * このイベントを指定のコントロールで発生させる。
     * 
     * @param control
     */
    public void copy(Control control) {
        control.getInnerInstance().dispatchEvent(this.getInnerInstance());
    }
    
    @Override
    public String toString() {
        return this.getInnerInstance().toString();
    }
}
