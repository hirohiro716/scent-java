package com.hirohiro716.gui.control;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import com.hirohiro716.Array;
import com.hirohiro716.gui.GUI;
import com.hirohiro716.gui.collection.AddListener;
import com.hirohiro716.gui.collection.Collection;
import com.hirohiro716.gui.collection.RemoveListener;
import com.hirohiro716.gui.event.ChangeListener;

/**
 * 手動で配置するペインのクラス。
 * 
 * @author hiro
 *
 */
public class Pane extends Control {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected Pane(JPanel innerInstance) {
        super(innerInstance);
        this.getInnerInstance().setLayout(new OverlayLayout(this.getInnerInstance()));
    }
    
    /**
     * コンストラクタ。
     */
    public Pane() {
        this(new JPanel());
    }
    
    @Override
    public JPanel getInnerInstance() {
        return (JPanel) super.getInnerInstance();
    }

    @Override
    protected ChangeListener<Dimension> createBugFixChangeListener() {
        return null;
    }
    
    private Map<Control, Boolean> mapControlDisabled = new HashMap<>();
    
    @Override
    public void setDisabled(boolean isDisabled) {
        if (isDisabled == this.isDisabled()) {
            return;
        }
        if (isDisabled) {
            super.setDisabled(isDisabled);
            this.mapControlDisabled.clear();
            for (Control control : this.children) {
                this.mapControlDisabled.put(control, control.isDisabled());
                control.setDisabled(true);
            }
        } else {
            super.setDisabled(isDisabled);
            for (Control control : this.children) {
                control.setDisabled(this.mapControlDisabled.get(control));
            }
        }
    }
    
    @Override
    public void requestFocus() {
        Control control = this.getChildren().getInitialFocusControl();
        if (control != null) {
            GUI.executeLater(5, new Runnable() {
                
                @Override
                public void run() {
                    control.requestFocus();
                }
            });
        }
    }
    
    private Children children = new Children();
    
    /**
     * このペインの子要素を取得する。
     * 
     * @return 結果。
     */
    public Children getChildren() {
        return this.children;
    }
    
    /**
     * ペインの子要素のクラス。
     * 
     * @author hiro
     *
     */
    public class Children extends Collection<Control> {
        
        /**
         * コンストラクタ。
         */
        public Children() {
            this.addListener(this.addListener);
            this.addListener(this.removeListener);
        }
        
        private Control initialFocusControl = null;
        
        /**
         * ペインを表示した際に最初にフォーカスするコントロールを取得する。
         * 
         * @return 結果。
         */
        public Control getInitialFocusControl() {
            return this.initialFocusControl;
        }
        
        /**
         * ペインを表示した際に最初にフォーカスするコントロールを指定する。
         * 
         * @param control
         */
        public void setInitialFocusControl(Control control) {
            this.initialFocusControl = control;
        }

        private Map<Component, Control> hashMap = new HashMap<>();
        
        private AddListener<Control> addListener = new AddListener<Control>() {
            
            @Override
            protected void added(Control added, int positionIndex) {
                Pane pane = Pane.this;
                Children instance = Children.this;
                pane.getInnerInstance().add(added.getInnerInstanceForLayout(), positionIndex);
                added.setParent(pane);
                instance.hashMap.put(added.getInnerInstanceForLayout(), added);
                if (instance.initialFocusControl == null) {
                    instance.initialFocusControl = added;
                }
            }
        };
        
        private RemoveListener<Control> removeListener = new RemoveListener<Control>() {
            
            @Override
            protected void removed(Control removed) {
                Pane pane = Pane.this;
                Children instance = Children.this;
                pane.getInnerInstance().remove(removed.getInnerInstanceForLayout());
                removed.setParent(null);
                instance.hashMap.remove(removed.getInnerInstanceForLayout());
                if (instance.initialFocusControl == removed) {
                    instance.initialFocusControl = null;
                    if (instance.size() > 0) {
                        instance.initialFocusControl = instance.get(0);
                    }
                }
            }
        };
        
        /**
         * 指定された名前のコントロールを検索する。見つからなかった場合はnullを返す。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param <C> 検索するコントロールの型。
         * @param name
         * @return 結果。
         */
        @SuppressWarnings("unchecked")
        public <C extends Control> C findControlByName(String name) {
            try {
                return (C) this.findControlsByName(name).get(0);
            } catch (Exception exception) {
                return null;
            }
        }
        
        /**
         * 指定された名前に一致するコントロールを検索する。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param name
         * @return 結果。
         */
        public Array<Control> findControlsByName(String name) {
            return new Array<>(this.findControlsByNameAsList(name));
        }
        
        /**
         * 指定された名前に一致するコントロールのリストを作成する。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param name
         * @return 結果。
         */
        private List<Control> findControlsByNameAsList(String name) {
            List<Control> finded = new ArrayList<>();
            for (Control control : this) {
                if (name.equals(control.getName())) {
                    finded.add(control);
                }
                if (control instanceof Pane) {
                    Pane pane = (Pane) control;
                    finded.addAll(pane.getChildren().findControlsByNameAsList(name));
                }
            }
            return finded;
        }

        /**
         * 指定されたクラスのバイナリ名に完全に一致するコントロールを検索する。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param controlClass
         * @return 結果。
         */
        public <T extends Control> Array<T> findControlsByClass(Class<T> controlClass) {
            return new Array<>(this.findControlsByClassAsList(controlClass));
        }
        
        /**
         * 指定されたクラスのバイナリ名に完全に一致するコントロールのリストを作成する。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param controlClass
         * @return 結果。
         */
        @SuppressWarnings("unchecked")
        private <T extends Control> List<T> findControlsByClassAsList(Class<T> controlClass) {
            List<T> finded = new ArrayList<>();
            for (Control control : this) {
                if (control.getClass().getName().equals(controlClass.getName())) {
                    finded.add((T) control);
                }
                if (control instanceof Pane) {
                    Pane pane = (Pane) control;
                    finded.addAll(pane.getChildren().findControlsByClassAsList(controlClass));
                }
            }
            return finded;
        }
        
        /**
         * 指定されたポイントに位置するコントロールを検索する。見つからなかった場合はnullを返す。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param <C> 検索するコントロールの型。
         * @param x
         * @param y
         * @return 結果。
         */
        @SuppressWarnings("unchecked")
        public <C extends Control> C findControlByPoint(int x, int y) {
            for (Control control : this) {
                if (control.getX() <= x && control.getX() + control.getWidth() >= x && control.getY() <= y && control.getY() + control.getHeight() >= y) {
                    if (control instanceof Pane) {
                        Pane pane = (Pane) control;
                        Control finded = pane.getChildren().findControlByPoint(x - pane.getX(), y - pane.getY());
                        if (finded == null) {
                            return (C) pane;
                        }
                        return pane.getChildren().findControlByPoint(x - pane.getX(), y - pane.getY());
                    }
                    return (C) control;
                }
            }
            return null;
        }
    }
}
