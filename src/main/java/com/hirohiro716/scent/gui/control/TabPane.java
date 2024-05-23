package com.hirohiro716.scent.gui.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;

import com.hirohiro716.scent.gui.Component;
import com.hirohiro716.scent.gui.KeyCode;
import com.hirohiro716.scent.gui.collection.AddListener;
import com.hirohiro716.scent.gui.collection.Collection;
import com.hirohiro716.scent.gui.collection.RemoveListener;
import com.hirohiro716.scent.gui.event.ChangeListener;
import com.hirohiro716.scent.gui.event.EventHandler;
import com.hirohiro716.scent.gui.event.KeyEvent;

/**
 * タブによって表示を切り替えるペインのクラス。
 * 
 * @author hiro
*/
public class TabPane extends Control {

    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected TabPane(JTabbedPane innerInstance) {
        super(innerInstance);
        TabPane instance = this;
        this.getInnerInstance().getModel().addChangeListener(new javax.swing.event.ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent event) {
                for (ChangeListener<Tab> changeListener : instance.selectedTabChangeListeners) {
                    changeListener.executeWhenChanged(instance, instance.getTabs().get(instance.getInnerInstance().getSelectedIndex()));
                }
            }
        });
        this.tabs.addListener(new AddListener<>() {
            
            @Override
            protected void added(Tab added, int positionIndex) {
                added.getPane().setParent(instance);
                instance.getInnerInstance().addTab(added.getTitle(), added.getPane().getInnerInstance());
            }
        });
        this.tabs.addListener(new RemoveListener<>() {

            @Override
            protected void removed(Tab removed) {
                removed.getPane().setParent(null);
                instance.getInnerInstance().remove(removed.getPane().getInnerInstance());
                for (ChangeListener<Tab> changeListener : instance.selectedTabChangeListeners) {
                    changeListener.executeWhenChanged(instance, instance.getTabs().get(instance.getInnerInstance().getSelectedIndex()));
                }
            }
        });
    }
    
    /**
     * コンストラクタ。
     */
    public TabPane() {
        this(new JTabbedPane(JTabbedPane.TOP));
    }

    @Override
    public JTabbedPane getInnerInstance() {
        return (JTabbedPane) super.getInnerInstance();
    }

    private Map<Pane, Boolean> mapTabPaneDisabled = new HashMap<>();
    
    @Override
    public void setDisabled(boolean isDisabled) {
        if (isDisabled == this.isDisabled()) {
            return;
        }
        if (isDisabled) {
            super.setDisabled(isDisabled);
            this.mapTabPaneDisabled.clear();
            for (Tab tab : this.tabs) {
                this.mapTabPaneDisabled.put(tab.getPane(), tab.getPane().isDisabled());
                tab.getPane().setDisabled(true);
            }
        } else {
            super.setDisabled(isDisabled);
            for (Tab tab : this.tabs) {
                tab.getPane().setDisabled(this.mapTabPaneDisabled.get(tab.getPane()));
            }
        }
    }
    
    private Collection<Tab> tabs = new Collection<>();
    
    /**
     * このタブペインのタブを格納しているコレクションを取得する。
     * 
     * @return 結果。
     */
    public Collection<Tab> getTabs() {
        return this.tabs;
    }
    
    /**
     * タイトルと表示するペインを指定して、このタブペインにタブを追加する。
     * 
     * @param title
     * @param pane
     */
    public void addTab(String title, Pane pane) {
        this.tabs.add(new Tab(title, pane));
    }
    
    /**
     * このタブペインで選択されているタブを取得する。
     * 
     * @return 結果。
     */
    public Tab getSelectedTab() {
        return this.tabs.get(this.getInnerInstance().getModel().getSelectedIndex());
    }
    
    /**
     * このタブペインのタブを選択状態にする。
     * 
     * @param tab
     */
    public void setSelectedTab(Tab tab) {
        if (this.tabs.contains(tab)) {
            this.getInnerInstance().getModel().setSelectedIndex(this.tabs.indexOf(tab));
        }
    }
    
    private List<ChangeListener<Tab>> selectedTabChangeListeners = new ArrayList<>();

    /**
     * このタブペインのタブ選択状態が変化した際のリスナーを追加する。
     * 
     * @param changeListener
     */
    public void addSelectedTabChangeListener(ChangeListener<Tab> changeListener) {
        this.selectedTabChangeListeners.add(changeListener);
    }
    
    @Override
    public void removeChangeListener(ChangeListener<?> changeListener) {
        super.removeChangeListener(changeListener);
        this.selectedTabChangeListeners.remove(changeListener);
    }
    
    /**
     * タブのクラス。
     * 
     * @author hiro
     *
     */
    public static class Tab {
        
        /**
         * コンストラクタ。<br>
         * タブのタイトルと表示するペインを指定する。
         * 
         * @param title
         * @param pane
         */
        public Tab(String title, Pane pane) {
            this.title = title;
            this.pane = pane;
            for (Control child : this.pane.getChildren().findAll()) {
                child.removeEventHandler(Tab.KEY_PRESSED_EVENT_HANDLER);
                child.addKeyPressedEventHandler(Tab.KEY_PRESSED_EVENT_HANDLER);
            }
        }
        
        private String title;
        
        /**
         * タブのタイトルを取得する。
         * 
         * @return 結果。
         */
        public String getTitle() {
            return this.title;
        }
        
        private Pane pane;
        
        /**
         * 表示するペインを取得する。
         * 
         * @param <P>
         * @return 結果。
         */
        @SuppressWarnings("unchecked")
        public <P extends Pane> P getPane() {
            return (P) this.pane;
        }

        /**
         * タブ内のコントロールに対するCtrl+PgUp、Ctrl+PgDownイベントをTabPaneに渡すイベントハンドラー。
         */
        private static EventHandler<KeyEvent> KEY_PRESSED_EVENT_HANDLER = new EventHandler<KeyEvent>() {

            @Override
            protected void handle(KeyEvent event) {
                if (event.isControlDown() && event.getKeyCode() == KeyCode.PAGE_DOWN || event.isControlDown() && event.getKeyCode() == KeyCode.PAGE_UP) {
                    Component<?> parent = event.getSource().getParent();
                    while (parent != null && parent instanceof Control) {
                        Control control = (Control) parent;
                        if (control instanceof TabPane) {
                            event.copy(control);
                            return;
                        }
                        parent = control.getParent();
                    }
                }
            }
        };
    }
}
