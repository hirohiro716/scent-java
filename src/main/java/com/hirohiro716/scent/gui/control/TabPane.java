package com.hirohiro716.scent.gui.control;

import java.awt.Font;
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
                for (ChangeListener<Tab> changeListener: instance.selectedTabChangeListeners) {
                    changeListener.executeWhenChanged(instance, instance.getTabs().get(instance.getInnerInstance().getSelectedIndex()));
                }
            }
        });
        this.tabs.addListener(new AddListener<>() {
            
            @Override
            protected void added(Tab added, int positionIndex) {
                added.getTitleLabel().setFont(instance.getFont());
                added.getPane().setParent(instance);
                instance.getInnerInstance().addTab(added.getTitle(), added.getPane().getInnerInstance());
            }
        });
        this.tabs.addListener(new RemoveListener<>() {

            @Override
            protected void removed(Tab removed) {
                removed.getPane().setParent(null);
                instance.getInnerInstance().remove(removed.getPane().getInnerInstance());
                for (ChangeListener<Tab> changeListener: instance.selectedTabChangeListeners) {
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

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (this.tabs != null) {
            for (Tab tab: this.tabs) {
                tab.getTitleLabel().setFont(font);
            }
        }
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
            for (Tab tab: this.tabs) {
                this.mapTabPaneDisabled.put(tab.getPane(), tab.getPane().isDisabled());
                tab.getPane().setDisabled(true);
            }
        } else {
            super.setDisabled(isDisabled);
            for (Tab tab: this.tabs) {
                tab.getPane().setDisabled(this.mapTabPaneDisabled.get(tab.getPane()));
            }
        }
    }
    
    private Collection<Tab> tabs = new Collection<>();
    
    /**
     * このタブペインのタブを格納しているコレクションを取得する。
     * 
     * @return
     */
    public Collection<Tab> getTabs() {
        return this.tabs;
    }
    
    /**
     * タイトルと表示するペインを指定して、このタブペインにタブを追加する。
     * 
     * @param title
     * @param pane
     * @return 追加したタブ。
     */
    public Tab addTab(String title, Pane pane) {
        int numberOfTabs = this.tabs.size();
        Tab tab = new Tab(title, pane);
        this.tabs.add(tab);
        this.getInnerInstance().setTabComponentAt(numberOfTabs, tab.getTitleLabel().getInnerInstance());
        return tab;
    }
    
    /**
     * このタブペインで選択されているタブを取得する。
     * 
     * @return
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
            this.titleLabel = new Label(title);
            this.pane = pane;
            for (Control child: this.pane.getChildren().findAll()) {
                child.removeEventHandler(Tab.KEY_PRESSED_EVENT_HANDLER);
                child.addKeyPressedEventHandler(Tab.KEY_PRESSED_EVENT_HANDLER);
            }
        }
        
        private String title;

        /**
         * タブのタイトルを取得する。
         * 
         * @return
         */
        public String getTitle() {
            return this.title;
        }
        
        private Label titleLabel;

        /**
         * タブのタイトルラベルを取得する。
         * 
         * @return
         */
        public Label getTitleLabel() {
            return this.titleLabel;
        }
        
        private Pane pane;
        
        /**
         * 表示するペインを取得する。
         * 
         * @param <P>
         * @return
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
