package com.hirohiro716.gui.control;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import com.hirohiro716.Array;
import com.hirohiro716.Dimension;
import com.hirohiro716.Regex;
import com.hirohiro716.StringObject;
import com.hirohiro716.graphic.FontCreator;
import com.hirohiro716.gui.Border;
import com.hirohiro716.gui.Component;
import com.hirohiro716.gui.Frame;
import com.hirohiro716.gui.GUI;
import com.hirohiro716.gui.HorizontalAlignment;
import com.hirohiro716.gui.Popup;
import com.hirohiro716.gui.event.ChangeListener;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.KeyEvent;
import com.hirohiro716.gui.event.MouseEvent;
import com.hirohiro716.gui.event.MouseEvent.MouseButton;

/**
 * オートコンプリート機能付きのテキストフィールドのクラス。
 * 
 * @author hiro
 *
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
                textField.showPopup();
            }
        });
        this.addFocusChangeListener(new ChangeListener<Boolean>() {
            
            @Override
            protected void changed(Component<?> component, Boolean changedValue, Boolean previousValue) {
                if (changedValue) {
                    if (textField.isDisableAutocomplete() == false) {
                        textField.showPopup();
                    }
                } else {
                    textField.hidePopup();
                }
            }
        });
        this.addKeyPressedEventHandler(new EventHandler<KeyEvent>() {
            
            @Override
            protected void handle(KeyEvent event) {
                switch (event.getKeyCode()) {
                case UP:
                case DOWN:
                case PAGE_UP:
                case PAGE_DOWN:
                case ENTER:
                    if (textField.getPopup().isVisible()) {
                        event.copy(textField.getListView());
                    }
                    break;
                case ESCAPE:
                    textField.hidePopup();
                    break;
                default:
                    break;
                }
            }
        });
        this.addTextChangeListener(new TextChangeListener());
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

    @Override
    public void setEditable(boolean isEditable) {
        super.setEditable(isEditable);
        this.setDisableAutocomplete(isEditable == false);
        this.hidePopup();
    }

    @Override
    public void setVisible(boolean isVisible) {
        super.setVisible(isVisible);
        this.setDisableAutocomplete(isVisible == false);
        this.hidePopup();
    }

    @Override
    public void setDisabled(boolean isDisabled) {
        super.setDisabled(isDisabled);
        this.setDisableAutocomplete(isDisabled);
        this.hidePopup();
    }
    
    private boolean isDisableAutocomplete = false;
    
    /**
     * このテキストフィールドでオートコンプリートが無効になっている場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isDisableAutocomplete() {
        return this.isDisableAutocomplete;
    }
    
    /**
     * このテキストフィールドでオートコンプリートを無効にする場合はtrueをセットする。
     * 
     * @param isDisableAutocomplete
     */
    public void setDisableAutocomplete(boolean isDisableAutocomplete) {
        this.isDisableAutocomplete = isDisableAutocomplete;
    }
    
    private Popup popup = null;
    
    private VerticalPane paneOfPopup;
    
    /**
     * オートコンプリート用のポップアップを作成する。
     */
    private void createPopup() {
        Frame<?> frame = this.getFrame();
        this.popup = new Popup(frame);
        this.popup.setSize(this.getWidth(), this.getFont().getSize() * 16);
        AutocompleteTextField control = this;
        frame.addLocationChangeListener(new ChangeListener<Point>() {
            
            @Override
            protected void changed(Component<?> component, Point changedValue, Point previousValue) {
                control.showPopup();
            }
        });
        frame.addSizeChangeListener(new ChangeListener<Dimension>() {
            
            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension previousValue) {
                control.showPopup();
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
     * @return 結果。
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
        this.popup.setX((int) this.getLocationOnScreen().getX() + 3);
        int y = (int) this.getLocationOnScreen().getY() + this.getHeight() + 3;
        Rectangle rectangle = GUI.getMaximumWindowBounds(this.findPlacedGraphicsDevice());
        if (y + this.popup.getHeight() > rectangle.getY() + rectangle.getHeight()) {
            y = (int) this.getLocationOnScreen().getY() - this.popup.getHeight() - 3;
        }
        this.popup.setY(y);
        this.popup.setMinimumWidth(this.getWidth());
        this.listView.clearSelection();
        if (this.isFocused() && this.isVisible() && this.isEditable() && this.isDisabled() == false) {
            if (this.filteredListItems.size() > 0 && this.filteredListItems.contains(this.getText()) == false) {
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
     * @return 結果。
     */
    public ListView<String> getListView() {
        return this.listView;
    }
    
    private List<String> listItems = new ArrayList<>();
    
    /**
     * このテキストフィールドでオートコンプリートに使用するリストアイテム取得する。
     * 
     * @return 結果。
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
        for (String item : listItems) {
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
    
    /**
     * このテキストフィールドの値が変更された際のリスナークラス。
     * 
     * @author hiro
     *
     */
    private class TextChangeListener extends ChangeListener<String> {
        
        @Override
        protected void changed(Component<?> component, String changedValue, String previousValue) {
            AutocompleteTextField control = AutocompleteTextField.this;
            if (control.isDisableAutocomplete()) {
                return;
            }
            Thread thread = new Thread(new Runnable() {
                
                @Override
                public void run() {
                    try {
                        ListItemAdder adder = new ListItemAdder(changedValue);
                        int runningAdderSize = control.listItemAdders.size();
                        while (runningAdderSize > 0) {
                            runningAdderSize = 0;
                            for (ListItemAdder activeAdder : control.listItemAdders.toArray(new ListItemAdder[] {})) {
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
                        adder.run();
                    } catch (Exception exception) {
                    }
                }
            });
            thread.start();
        }
    }

    private List<String> filteredListItems = new ArrayList<>();
    
    /**
     * このテキストフィールドにオートコンプリート用のアイテムを追加するクラス。
     * 
     * @author hiro
     *
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
            for (String listItem : new Array<>(control.listItems)) {
                StringObject regex = StringObject.join(".{0,}", Regex.makeRoughComparison(this.changedValue), ".{0,}");
                regex.replace(" ", ".{0,}");
                regex.replace("　", ".{0,}");
                if (Pattern.compile(regex.toString()).matcher(listItem).matches()) {
                    control.filteredListItems.add(listItem);
                }
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
         * @return 結果。
         */
        public boolean isFinished() {
            return this.isFinished;
        }
    }
}
