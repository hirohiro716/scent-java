package com.hirohiro716.scent.gui.control;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.hirohiro716.scent.Array;
import com.hirohiro716.scent.Bounds;
import com.hirohiro716.scent.Dimension;
import com.hirohiro716.scent.Regex;
import com.hirohiro716.scent.StringObject;
import com.hirohiro716.scent.graphic.FontCreator;
import com.hirohiro716.scent.gui.Border;
import com.hirohiro716.scent.gui.Component;
import com.hirohiro716.scent.gui.Frame;
import com.hirohiro716.scent.gui.GUI;
import com.hirohiro716.scent.gui.HorizontalAlignment;
import com.hirohiro716.scent.gui.KeyCode;
import com.hirohiro716.scent.gui.Popup;
import com.hirohiro716.scent.gui.event.ActionEvent;
import com.hirohiro716.scent.gui.event.ChangeListener;
import com.hirohiro716.scent.gui.event.EventHandler;
import com.hirohiro716.scent.gui.event.InnerInstanceCreator;
import com.hirohiro716.scent.gui.event.KeyEvent;
import com.hirohiro716.scent.gui.event.MouseEvent;
import com.hirohiro716.scent.gui.MouseButton;

/**
 * オートコンプリート機能付きのテキストフィールドのクラス。
 */
public class AutocompleteTextField extends TextField {
    
    /**
     * コンストラクタ。<br>
     * このテキストフィールドの初期値を指定する。
     * 
     * @param text
     */
    public AutocompleteTextField(String text) {
        super(text);
        AutocompleteTextField textField = this;
        this.addSizeChangeListener(new ChangeListener<Dimension>() {
            
            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension previousValue) {
                textField.hidePopup();
            }
        });
        this.addFocusChangeListener(new ChangeListener<Boolean>() {
            
            @Override
            protected void changed(Component<?> component, Boolean changedValue, Boolean previousValue) {
                if (changedValue) {
                    if (textField.isDisabledPopupWhenFocusing() == false) {
                        textField.showPopup();
                    }
                } else {
                    GUI.executeLater(new Runnable() {

                        @Override
                        public void run() {
                            textField.closePopup();
                        }
                    });
                }
            }
        });
        this.addMouseClickedEventHandler(MouseButton.BUTTON1, new EventHandler<MouseEvent>() {

            @Override
            protected void handle(MouseEvent event) {
                if (textField.isEnabledPopupWhenClicked == false) {
                    return;
                }
                textField.showPopup();
            }
        });
        this.addKeyPressedEventHandler(new KeyPressedEventHandler());
        this.KeyTypedEventHandler = new KeyTypedEventHandler();
        this.addKeyTypedEventHandler(this.KeyTypedEventHandler);
        // Measures that the pop-up remains displayed for some reason
        this.listView.addMouseMovedEventHandler(new EventHandler<MouseEvent>() {

            @Override
            protected void handle(MouseEvent event) {
                if (textField.isFocused()) {
                    return;
                }
                textField.closePopup();
            }
        });
    }
    
    /**
     * コンストラクタ。
     */
    public AutocompleteTextField() {
        this((String) null);
    }
    
    /**
     * コンストラクタ。<br>
     * オートコンプリート用のリストアイテムを指定する。
     * 
     * @param listItems
     */
    public AutocompleteTextField(String[] listItems) {
        this();
        this.setListItems(listItems);
    }
    
    /**
     * コンストラクタ。<br>
     * オートコンプリート用のリストアイテムを指定する。
     * 
     * @param listItems
     */
    public AutocompleteTextField(List<String> listItems) {
        this();
        this.setListItems(listItems);
    }
    
    private KeyTypedEventHandler KeyTypedEventHandler;

    @Override
    public void setEditable(boolean isEditable) {
        super.setEditable(isEditable);
        this.setDisabledAutocomplete(isEditable == false);
        this.hidePopup();
    }

    @Override
    public void setVisible(boolean isVisible) {
        super.setVisible(isVisible);
        this.setDisabledAutocomplete(isVisible == false);
        this.hidePopup();
    }

    @Override
    public void setDisabled(boolean isDisabled) {
        super.setDisabled(isDisabled);
        this.setDisabledAutocomplete(isDisabled);
        this.hidePopup();
    }

    @Override
    public void addActionEventHandler(EventHandler<ActionEvent> eventHandler) {
        AutocompleteTextField textField = this;
        KeyListener innerInstance = eventHandler.createInnerInstance(textField, new InnerInstanceCreator<>() {

            @Override
            public KeyListener create() {
                return new KeyAdapter() {
                    
                    private boolean isPressed = false;
                    
                    @Override
                    public void keyPressed(java.awt.event.KeyEvent event) {
                        if (event.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER && textField.KeyTypedEventHandler.isDisabled() == false) {
                            this.isPressed = true;
                        }
                    }

                    @Override
                    public void keyReleased(java.awt.event.KeyEvent event) {
                        if (event.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER && textField.KeyTypedEventHandler.isDisabled() == false && this.isPressed) {
                            eventHandler.executeWhenControlEnabled(new ActionEvent(textField, event));
                        }
                        this.isPressed = false;
                    }
                };
            }
        });
        this.getInnerInstance().addKeyListener(innerInstance);
    }
    
    private boolean isDisabledAutocomplete = false;
    
    /**
     * このテキストフィールドでオートコンプリートが無効になっている場合はtrueを返す。
     * 
     * @return
     */
    public boolean isDisabledAutocomplete() {
        return this.isDisabledAutocomplete;
    }
    
    /**
     * このテキストフィールドでオートコンプリートを無効にする場合はtrueをセットする。
     * 
     * @param isDisabledAutocomplete
     */
    public void setDisabledAutocomplete(boolean isDisabledAutocomplete) {
        this.isDisabledAutocomplete = isDisabledAutocomplete;
    }
    
    private boolean isDisabledPopupWhenFocusing = false;
    
    /**
     * このテキストフィールドにフォーカスした際のポップアップ表示が無効になっている場合はtrueを返す。
     * 
     * @return
     */
    public boolean isDisabledPopupWhenFocusing() {
        return this.isDisabledPopupWhenFocusing;
    }
    
    /**
     * このテキストフィールドにフォーカスした際のポップアップ表示を無効にする場合はtrueをセットする。
     * 
     * @param isDisabledPopupWhenFocusing
     */
    public void setDisabledPopupWhenFocusing(boolean isDisabledPopupWhenFocusing) {
        this.isDisabledPopupWhenFocusing = isDisabledPopupWhenFocusing;
    }
    
    private boolean isEnabledPopupWhenClicked = false;

    /**
     * このテキストフィールドをクリックした際のポップアップ表示が有効になっている場合はtrueを返す。
     * 
     * @return
     */
    public boolean isEnabledPopupWhenClicked() {
        return this.isEnabledPopupWhenClicked;
    }
    
    /**
     * このテキストフィールドをクリックした際のポップアップ表示を有効にする場合はtrueをセットする。
     * 
     * @param isEnabledPopupWhenClicked
     */
    public void setEnabledPopupWhenClicked(boolean isEnabledPopupWhenClicked) {
        this.isEnabledPopupWhenClicked = isEnabledPopupWhenClicked;
    }
    
    private Popup popup = null;
    
    private VerticalPane paneOfPopup;
    
    /**
     * オートコンプリート用のポップアップを作成する。
     */
    private void createPopup() {
        Frame<?> frame = this.getFrame();
        this.popup = new Popup(frame);
        AutocompleteTextField control = this;
        frame.addLocationChangeListener(new ChangeListener<Point>() {
            
            @Override
            protected void changed(Component<?> component, Point changedValue, Point previousValue) {
                control.hidePopup();
            }
        });
        frame.addSizeChangeListener(new ChangeListener<Dimension>() {
            
            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension previousValue) {
                control.hidePopup();
            }
        });
        this.paneOfPopup = new VerticalPane(HorizontalAlignment.LEFT);
        this.paneOfPopup.setFillChildToPaneWidth(true);
        this.paneOfPopup.setBorder(Border.createLine(Color.DARK_GRAY, 1));
        this.paneOfPopup.setPadding(1);
        this.popup.getChildren().add(this.paneOfPopup);
    }
    
    /**
     * このテキストフィールドでオートコンプリートに使用しているポップアップを取得する。
     * 
     * @return
     */
    public Popup getPopup() {
        return this.popup;
    }
    
    /**
     * このテキストフィールドのオートコンプリート用のポップアップを表示する。
     */
    public void showPopup() {
        if (this.getFrame() == null || this.listItems.size() == 0) {
            return;
        }
        if (this.popup == null) {
            this.createPopup();
            this.configureListView();
            this.configurePaneForClearFilter();
        }
        if (this.getLocationOnScreen() == null || this.getWidth() < 20 || this.getHeight() == 0) {
            return;
        }
        this.paneForClearFilter.setVisible(this.listItems.size() > this.filteredListItems.size());
        this.popup.setSize(this.getWidth(), this.getFont().getSize() * 16);
        this.popup.setX((int) this.getLocationOnScreen().getX() + 3);
        int y = (int) this.getLocationOnScreen().getY() + this.getHeight() + 3;
        Bounds bounds = GUI.getMaximumWindowBounds(this.findPlacedGraphicsDevice());
        if (y + this.popup.getHeight() > bounds.getY() + bounds.getHeight()) {
            y = (int) this.getLocationOnScreen().getY() - this.popup.getHeight() - 3;
        }
        this.popup.setY(y);
        this.popup.setMinimumWidth(this.getWidth());
        this.listView.clearSelection();
        if (this.filteredListItems.size() > 0) {
            if (this.isFocused() && this.isVisible() && this.isEditable() && this.isDisabled() == false && this.isDisabledAutocomplete == false) {
                this.popup.show();
                this.popup.updateDisplay();
            }
        }
    }
    
    /**
     * このテキストフィールドのオートコンプリート用のポップアップを隠す。
     */
    public void hidePopup() {
        if (this.popup == null || this.popup.isVisible() == false) {
            return;
        }
        this.popup.hide();
    }
    
    /**
     * このテキストフィールドのオートコンプリート用のポップアップを閉じる。
     */
    public void closePopup() {
        if (this.popup == null) {
            return;
        }
        this.paneOfPopup.getChildren().clear();
        this.popup.getChildren().clear();
        this.popup.close();
        this.popup = null;
    }
    
    private ListView<String> listView = new ListView<>();
    
    private static final int NUMBER_OF_INITIAL_LIST_ITEM = 1000;
    
    /**
     * オートコンプリート用のリストビューを作成する。
     */
    private void configureListView() {
        AutocompleteTextField control = this;
        this.filteredListItems.addAll(this.listItems);
        List<String> initialListitems = this.filteredListItems;
        if (initialListitems.size() > AutocompleteTextField.NUMBER_OF_INITIAL_LIST_ITEM) {
            initialListitems = initialListitems.subList(0, AutocompleteTextField.NUMBER_OF_INITIAL_LIST_ITEM - 1);
        }
        synchronized (this.listView.getItems()) {
            this.listView.getItems().addAll(initialListitems);
        }
        this.listView.setFocusable(false);
        this.listView.addKeyPressedEventHandler(new EventHandler<KeyEvent>() {
            
            @Override
            protected void handle(KeyEvent event) {
                switch (event.getKeyCode()) {
                case ENTER:
                    if (control.listView.getSelectedItem() != null) {
                        control.KeyTypedEventHandler.setDisabled(true);
                        control.setText(control.listView.getSelectedItem());
                        control.listView.clearSelection();
                    }
                    control.hidePopup();
                    break;
                default:
                    break;
                }
            }
        });
        this.listView.addMouseClickedEventHandler(new EventHandler<MouseEvent>() {

            @Override
            protected void handle(MouseEvent event) {
                if (control.listView.getSelectedItem() != null) {
                    control.setText(control.listView.getSelectedItem());
                    control.listView.clearSelection();
                }
            }
        });
        this.paneOfPopup.getChildren().add(this.listView);
        this.paneOfPopup.getGrowableControls().add(this.listView);
    }
    
    /**
     * このテキストフィールドでオートコンプリートに使用しているリストビューを取得する。
     * 
     * @return
     */
    public ListView<String> getListView() {
        return this.listView;
    }
    
    private List<String> listItems = new ArrayList<>();
    
    /**
     * このテキストフィールドでオートコンプリートに使用するリストアイテム取得する。
     * 
     * @return
     */
    public String[] getListItems() {
        return this.listItems.toArray(new String[] {});
    }
    
    /**
     * このテキストフィールドでオートコンプリートに使用するリストアイテムをセットする。
     * 
     * @param listItems
     */
    public void setListItems(String[] listItems) {
        this.listItems.clear();
        for (String item: listItems) {
            if (item != null && item.length() > 0) {
                this.listItems.add(item);
            }
        }
    }

    /**
     * このテキストフィールドでオートコンプリートに使用するリストアイテムをセットする。
     * 
     * @param listItems
     */
    public void setListItems(Collection<String> listItems) {
        this.setListItems(listItems.toArray(new String[] {}));
    }
    
    /**
     * このテキストフィールドでオートコンプリートに使用するリストアイテムをクリアする。
     */
    public void clearListItems() {
        this.listItems.clear();
    }
    
    private AnchorPane paneForClearFilter = new AnchorPane();
    
    /**
     * オートコンプリートのフィルターを解除するボタンとペインを作成する。
     */
    private void configurePaneForClearFilter() {
        this.paneForClearFilter.setPadding(1, 4, 4, 1);
        this.paneForClearFilter.setVisible(false);
        this.paneOfPopup.getChildren().add(this.paneForClearFilter);
        ClickableLabel button = new ClickableLabel("すべて表示");
        button.setFocusable(false);
        button.setFont(FontCreator.create(button.getFont(), 0.8));
        AutocompleteTextField control = this;
        button.addMouseClickedEventHandler(MouseButton.BUTTON1, new EventHandler<MouseEvent>() {

            @Override
            protected void handle(MouseEvent event) {
                control.filteredListItems.addAll(control.listItems);
                synchronized (control.getListView().getItems()) {
                    control.getListView().getItems().clear();
                    control.getListView().getItems().addAll(control.filteredListItems);
                }
                control.paneForClearFilter.setVisible(false);
            }
        });
        control.paneForClearFilter.getChildren().add(button);
        control.paneForClearFilter.setAnchor(button, null, 0, null, null);
        this.paneForClearFilter.updateDisplay();
    }
    
    private List<ListItemAdder> listItemAdders = new ArrayList<>();

    private KeyCode pressedKeyCode = null;

    /**
     * このテキストフィールドで文字が押された際のイベントハンドラークラス。
     */
    private class KeyPressedEventHandler extends EventHandler<KeyEvent> {

        @Override
        protected void handle(KeyEvent event) {
            AutocompleteTextField control = AutocompleteTextField.this;
            control.pressedKeyCode = event.getKeyCode();
            control.KeyTypedEventHandler.setDisabled(false);
            if (event.isShiftDown() || event.isControlDown() || event.isAltDown()) {
                return;
            }
            switch (event.getKeyCode()) {
                case UP:
                case DOWN:
                case PAGE_UP:
                case PAGE_DOWN:
                case ENTER:
                    if (control.getPopup() != null && control.getPopup().isVisible()) {
                        event.copy(control.getListView());
                    }
                    break;
                case ESCAPE:
                    control.hidePopup();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * このテキストフィールドで文字が入力された際のイベントハンドラークラス。
     */
    private class KeyTypedEventHandler extends EventHandler<KeyEvent> {

        private boolean isDisabled = false;
        
        /**
         * このイベントハンドラーが無効になっている場合はtrueを返す。
         * 
         * @return
         */
        public boolean isDisabled() {
            return this.isDisabled;
        }
        
        /**
         * このイベントハンドラーを無効にする場合はtrueをセットする。
         * 
         * @param isDisabled
         */
        public void setDisabled(boolean isDisabled) {
            this.isDisabled = isDisabled;
        }
        
        @Override
        protected void handle(KeyEvent event) {
            AutocompleteTextField control = AutocompleteTextField.this;
            if (control.isDisabledAutocomplete() || this.isDisabled) {
                return;
            }
            if (event.getKeyCharacter().length() == 0) {
                switch (control.pressedKeyCode) {
                    case TAB:
                        return;
                    default:
                        break;
                }
            }
            Thread thread = new Thread(new Runnable() {
                
                @Override
                public void run() {
                    ListItemAdder adder = new ListItemAdder(control.getText());
                    try {
                        synchronized (control.listItemAdders) {
                            int runningAdderSize = control.listItemAdders.size();
                            while (runningAdderSize > 0) {
                                runningAdderSize = 0;
                                for (ListItemAdder activeAdder: control.listItemAdders.toArray(new ListItemAdder[] {})) {
                                    if (activeAdder.isFinished()) {
                                        control.listItemAdders.remove(activeAdder);
                                    } else {
                                        activeAdder.cancel();
                                        runningAdderSize++;
                                    }
                                }
                                Thread.sleep(100);
                            }
                            control.listItemAdders.add(adder);
                        }
                        adder.run();
                    } catch (Exception exception) {
                        adder.cancel();
                        synchronized (control.listItemAdders) {
                            control.listItemAdders.remove(adder);
                        }
                    }
                }
            });
            thread.start();
        }
    }

    private List<String> filteredListItems = new ArrayList<>();
    
    /**
     * このテキストフィールドにオートコンプリート用のアイテムを追加するクラス。
     */
    private class ListItemAdder implements Runnable {
        
        /**
         * コンストラクタ。<br>
         * 現在のテキストフィールドの値を指定する。
         * 
         * @param changedValue
         */
        public ListItemAdder(String changedValue) {
            this.changedValue = changedValue;
        }
        
        private String changedValue;
        
        private boolean isCancelRequested;
        
        /**
         * このインスタンスの処理を中止する。
         */
        public void cancel() {
            this.isCancelRequested = true;
        }
        
        @Override
        public void run() {
            AutocompleteTextField control = AutocompleteTextField.this;
            this.isCancelRequested = false;
            this.isFinished = false;
            control.filteredListItems.clear();
            for (String listItem: new Array<>(control.listItems)) {
                if (this.changedValue.contains("?")) {
                    this.isFinished = true;
                    break;
                }
                StringObject regex = StringObject.join("^", Regex.makeRoughComparison(this.changedValue), ".{0,}");
                regex.replace(" ", ".");
                regex.replace("　", ".");
                if (Pattern.compile(regex.toString()).matcher(listItem).matches()) {
                    control.filteredListItems.add(listItem);
                }
                if (this.isCancelRequested) {
                    break;
                }
            }
            Map<String, String> mapListItems = new HashMap<>();
            for (String listItem: new Array<>(control.listItems)) {
                if (this.changedValue.contains("?")) {
                    this.isFinished = true;
                    break;
                }
                if (control.filteredListItems.contains(listItem)) {
                    continue;
                }
                StringObject regex = StringObject.join(Regex.makeRoughComparison(this.changedValue));
                regex.replace(" ", ".");
                regex.replace("　", ".");
                String[] others = listItem.split(regex.toString());
                if (others.length > 1) {
                    StringObject key = new StringObject(others[0].length());
                    key.paddingLeft('0', 4);
                    key.append(listItem);
                    mapListItems.put( key.toString(), listItem);
                }
                if (this.isCancelRequested) {
                    break;
                }
            }
            List<String> keys = new ArrayList<>();
            keys.addAll(mapListItems.keySet());
            Collections.sort(keys);
            for (String key: keys) {
                control.filteredListItems.add(mapListItems.get(key));
                if (this.isCancelRequested) {
                    break;
                }
            }
            GUI.executeLater(new Runnable() {
                
                @Override
                public void run() {
                    if (control.filteredListItems.size() > 0) {
                        control.showPopup();
                    } else {
                        control.hidePopup();
                    }
                    control.getListView().getItems().clear();
                    control.getListView().getItems().addAll(control.filteredListItems);
                }
            });
            this.isFinished = true;
        }
        
        private boolean isFinished = false;
        
        /**
         * このインスタンスの処理が終わっている場合はtrueを返す。
         * 
         * @return
         */
        public boolean isFinished() {
            return this.isFinished;
        }
    }
}
