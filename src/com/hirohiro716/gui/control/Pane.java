package com.hirohiro716.gui.control;

import java.awt.Component;
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
import com.hirohiro716.gui.control.TabPane.Tab;

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
    
    /**
     * このメソッドはコンストラクタの呼び出しと同じで、新しいインスタンスを作成する。
     * 
     * @param innerInstance GUIライブラリに依存したインスタンス。
     * @return 新しいインスタンス。
     */
    public static Pane newInstance(JPanel innerInstance) {
        return new Pane(innerInstance);
    }
    
    @Override
    public JPanel getInnerInstance() {
        return (JPanel) super.getInnerInstance();
    }
    
    @Override
    public void setDisabled(boolean isDisabled) {
        if (isDisabled == this.isDisabled()) {
            return;
        }
        if (isDisabled) {
            super.setDisabled(isDisabled);
            this.children.mapControlDisabled.clear();
            for (Control control : this.children) {
                this.children.mapControlDisabled.put(control, control.isDisabled());
                control.setDisabled(true);
            }
        } else {
            super.setDisabled(isDisabled);
            for (Control control : this.children) {
                control.setDisabled(this.children.mapControlDisabled.get(control));
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
        
        private Map<Component, Control> mapInnerInstance = new HashMap<>();

        private Map<Control, Boolean> mapControlDisabled = new HashMap<>();
        
        private AddListener<Control> addListener = new AddListener<Control>() {
            
            @Override
            protected void added(Control added, int positionIndex) {
                Children instance = Children.this;
                Pane pane = Pane.this;
                pane.getInnerInstance().add(added.getInnerInstanceForLayout(), positionIndex);
                if (added.getParent() != null) {
                    new IllegalArgumentException("The added child already has another parent.").printStackTrace();
                }
                added.setParent(pane);
                instance.mapInnerInstance.put(added.getInnerInstanceForLayout(), added);
                if (instance.initialFocusControl == null) {
                    instance.initialFocusControl = added;
                }
                instance.mapControlDisabled.put(added, added.isDisabled());
                if (pane.isDisabled()) {
                    added.setDisabled(true);
                }
            }
        };
        
        private RemoveListener<Control> removeListener = new RemoveListener<Control>() {
            
            @Override
            protected void removed(Control removed) {
                Children instance = Children.this;
                Pane pane = Pane.this;
                pane.getInnerInstance().remove(removed.getInnerInstanceForLayout());
                if (pane == removed.getParent()) {
                    removed.setParent(null);
                }
                instance.mapInnerInstance.remove(removed.getInnerInstanceForLayout());
                if (instance.initialFocusControl == removed) {
                    instance.initialFocusControl = null;
                    if (instance.size() > 0) {
                        instance.initialFocusControl = instance.get(0);
                    }
                }
                instance.mapControlDisabled.remove(removed);
            }
        };

        /**
         * ペインが含むすべてのコントロールを検索する。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @return 結果。
         */
        public final Array<Control> findAll() {
            return new Array<>(this.findControlsAsList());
        }
        
        /**
         * ペインが含むすべてのコントロールのリストを作成する。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @return 結果。
         */
        private List<Control> findControlsAsList() {
            List<Control> finded = new ArrayList<>();
            for (Control control : this) {
                finded.add(control);
                List<Pane> nextPanes = new ArrayList<>();
                if (control instanceof Pane) {
                    nextPanes.add((Pane) control);
                }
                if (control instanceof ScrollPane) {
                    ScrollPane scrollPane = (ScrollPane) control;
                    if (scrollPane.getContent() instanceof Pane) {
                        nextPanes.add(scrollPane.getContent());
                    }
                }
                if (control instanceof TabPane) {
                    TabPane tabPane = (TabPane) control;
                    for (Tab tab : tabPane.getTabs()) {
                        nextPanes.add(tab.getPane());
                    }
                }
                for (Pane nextPane : nextPanes) {
                    finded.addAll(nextPane.getChildren().findControlsAsList());
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
        public final <C extends Control> C findControlByPoint(int x, int y) {
            for (Control control : this) {
                if (control.getX() <= x && control.getX() + control.getWidth() >= x && control.getY() <= y && control.getY() + control.getHeight() >= y) {
                    Pane nextPane = null;
                    if (control instanceof Pane) {
                        nextPane = (Pane) control;
                    }
                    if (control instanceof ScrollPane) {
                        ScrollPane scrollPane = (ScrollPane) control;
                        if (scrollPane.getContent() instanceof Pane) {
                            nextPane = scrollPane.getContent();
                        }
                    }
                    if (control instanceof TabPane) {
                        TabPane tabPane = (TabPane) control;
                        nextPane = tabPane.getSelectedTab().getPane();
                    }
                    if (nextPane != null) {
                        Control finded = nextPane.getChildren().findControlByPoint(x - nextPane.getX(), y - nextPane.getY());
                        if (finded == null) {
                            return (C) nextPane;
                        }
                        return nextPane.getChildren().findControlByPoint(x - nextPane.getX(), y - nextPane.getY());
                        
                    }
                    return (C) control;
                }
            }
            return null;
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
                List<Pane> nextPanes = new ArrayList<>();
                if (control instanceof Pane) {
                    nextPanes.add((Pane) control);
                }
                if (control instanceof ScrollPane) {
                    ScrollPane scrollPane = (ScrollPane) control;
                    if (scrollPane.getContent() instanceof Pane) {
                        nextPanes.add(scrollPane.getContent());
                    }
                }
                if (control instanceof TabPane) {
                    TabPane tabPane = (TabPane) control;
                    for (Tab tab : tabPane.getTabs()) {
                        nextPanes.add(tab.getPane());
                    }
                }
                for (Pane nextPane : nextPanes) {
                    finded.addAll(nextPane.getChildren().findControlsByNameAsList(name));
                }
            }
            return finded;
        }
        
        /**
         * 指定された名前に一致するコントロールを検索する。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param name
         * @return 結果。
         */
        public final Array<Control> findControlsByName(String name) {
            return new Array<>(this.findControlsByNameAsList(name));
        }
        
        /**
         * 指定されたクラスのバイナリ名に完全に一致するコントロールのリストを作成する。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param controlClass
         * @return 結果。
         */
        @SuppressWarnings("unchecked")
        private <C extends Control> List<C> findControlsByClassAsList(Class<C> controlClass) {
            List<C> finded = new ArrayList<>();
            for (Control control : this) {
                if (control.getClass().getName().equals(controlClass.getName())) {
                    finded.add((C) control);
                }
                List<Pane> nextPanes = new ArrayList<>();
                if (control instanceof Pane) {
                    nextPanes.add((Pane) control);
                }
                if (control instanceof ScrollPane) {
                    ScrollPane scrollPane = (ScrollPane) control;
                    if (scrollPane.getContent() instanceof Pane) {
                        nextPanes.add(scrollPane.getContent());
                    }
                }
                if (control instanceof TabPane) {
                    TabPane tabPane = (TabPane) control;
                    for (Tab tab : tabPane.getTabs()) {
                        nextPanes.add(tab.getPane());
                    }
                }
                for (Pane nextPane : nextPanes) {
                    finded.addAll(nextPane.getChildren().findControlsByClassAsList(controlClass));
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
        public final <C extends Control> Array<C> findControlsByClass(Class<C> controlClass) {
            return new Array<>(this.findControlsByClassAsList(controlClass));
        }
        
        /**
         * 指定された名前かつ、指定されたクラスのバイナリ名に完全に一致するコントロールを検索する。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param name
         * @param controlClass
         * @return 結果。
         */
        public final <C extends Control> Array<Control> findControlsByNameAndClass(String name, Class<C> controlClass) {
            List<Control> findedByName = this.findControlsByNameAsList(name);
            for (Control control : findedByName.toArray(new Control[] {})) {
                if (control.getClass().getName().equals(controlClass.getName()) == false) {
                    findedByName.remove(control);
                }
            }
            return new Array<>(findedByName);
        }
        
        /**
         * 指定された名前のコントロールを検索する。見つからなかった場合はnullを返す。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param <C> 検索するコントロールの型。
         * @param name
         * @return 結果。
         */
        @SuppressWarnings("unchecked")
        public final <C extends Control> C findControlByName(String name) {
            for (Control control : this.findControlsByName(name)) {
                try {
                    return (C) control;
                } catch (Exception exception) {
                }
            }
            return null;
        }
        
        /**
         * 指定された名前のラベルを検索する。見つからなかった場合はnullを返す。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param name
         * @return 結果。
         */
        public final Label findLabelByName(String name) {
            return this.findControlByName(name);
        }
        
        /**
         * 指定された名前のテキストフィールドを検索する。見つからなかった場合はnullを返す。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param name
         * @return 結果。
         */
        public final TextField findTextFieldByName(String name) {
            return this.findControlByName(name);
        }
        
        /**
         * 指定された名前のオートコンプリート機能付きテキストフィールドを検索する。見つからなかった場合はnullを返す。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param name
         * @return 結果。
         */
        public final AutocompleteTextField findAutocompleteTextFieldByName(String name) {
            return this.findControlByName(name);
        }
        
        /**
         * 指定された名前のパスワードフィールドを検索する。見つからなかった場合はnullを返す。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param name
         * @return 結果。
         */
        public final PasswordField findPasswordFieldByName(String name) {
            return this.findControlByName(name);
        }
        
        /**
         * 指定された名前の日付の入力に特化したテキストフィールドを検索する。見つからなかった場合はnullを返す。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param name
         * @return 結果。
         */
        public final DatePicker findDatePickerByName(String name) {
            return this.findControlByName(name);
        }
        
        /**
         * 指定された名前のテキストエリアを検索する。見つからなかった場合はnullを返す。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param name
         * @return 結果。
         */
        public final TextArea findTextAreaByName(String name) {
            return this.findControlByName(name);
        }
        
        /**
         * 指定された名前のチェックボックスを検索する。見つからなかった場合はnullを返す。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param name
         * @return 結果。
         */
        public final CheckBox findCheckBoxByName(String name) {
            return this.findControlByName(name);
        }

        /**
         * 指定された名前のボタンを検索する。見つからなかった場合はnullを返す。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param name
         * @return 結果。
         */
        public final Button findButtonByName(String name) {
            return this.findControlByName(name);
        }
        
        /**
         * 指定された名前のラジオボタンを検索する。見つからなかった場合はnullを返す。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param name
         * @return 結果。
         */
        public final RadioButton findRadioButtonByName(String name) {
            return this.findControlByName(name);
        }
        
        /**
         * 指定された名前のトグルボタンを検索する。見つからなかった場合はnullを返す。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param name
         * @return 結果。
         */
        public final ToggleButton findToggleButtonByName(String name) {
            return this.findControlByName(name);
        }
        
        /**
         * 指定された名前のリストビューを検索する。見つからなかった場合はnullを返す。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param <T> リストアイテムの型。
         * @param name
         * @return 結果。
         */
        public final <T> ListView<T> findListViewByName(String name) {
            return this.findControlByName(name);
        }
        
        /**
         * 指定された名前のドロップダウンリストを検索する。見つからなかった場合はnullを返す。<br>
         * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
         * 
         * @param <T> リストアイテムの型。
         * @param name
         * @return 結果。
         */
        public final <T> DropDownList<T> findDropDownListByName(String name) {
            return this.findControlByName(name);
        }
    }
}
