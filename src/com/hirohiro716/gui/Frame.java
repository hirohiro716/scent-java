package com.hirohiro716.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hirohiro716.ExceptionMessenger;
import com.hirohiro716.Regex;
import com.hirohiro716.gui.collection.AddListener;
import com.hirohiro716.gui.collection.Collection;
import com.hirohiro716.gui.collection.RemoveListener;
import com.hirohiro716.gui.control.AutocompleteTextField;
import com.hirohiro716.gui.control.Button;
import com.hirohiro716.gui.control.CheckBox;
import com.hirohiro716.gui.control.Control;
import com.hirohiro716.gui.control.DatePicker;
import com.hirohiro716.gui.control.DropDownList;
import com.hirohiro716.gui.control.Label;
import com.hirohiro716.gui.control.ListView;
import com.hirohiro716.gui.control.Pane;
import com.hirohiro716.gui.control.PasswordField;
import com.hirohiro716.gui.control.RadioButton;
import com.hirohiro716.gui.control.TextArea;
import com.hirohiro716.gui.control.TextField;
import com.hirohiro716.gui.control.ToggleButton;
import com.hirohiro716.gui.dialog.MessageDialog;
import com.hirohiro716.gui.dialog.ProcessAfterDialogClosing;
import com.hirohiro716.gui.dialog.MessageableDialog.ResultButton;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.FrameEvent;
import com.hirohiro716.image.Image;
import com.hirohiro716.property.PropertyInterface;

/**
 * GUIのウィンドウやダイアログなどの抽象クラス。
 * 
 * @author hiro
 * 
 * @param <T> 内部で使用されるGUIライブラリに依存したインスタンスの型。
 */
public abstract class Frame<T extends java.awt.Window> extends Component<T> {
    
    /**
     * コンストラクタ。<br>
     * このコントロールがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param window 
     */
    protected Frame(T window) {
        super(window, window);
        Frame<T> frame = this;
        this.iconImages.addListener(new AddListener<>() {
            
            @Override
            protected void added(Image added, int positionIndex) {
                try {
                    java.awt.Image image = added.createBufferedImage();
                    frame.mapIconImage.put(added, image);
                    frame.innerIconImages.add(image);
                    frame.getInnerInstance().setIconImages(frame.innerIconImages);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });
        this.iconImages.addListener(new RemoveListener<>() {
            
            @Override
            protected void removed(Image removed) {
                java.awt.Image image = frame.mapIconImage.get(removed);
                frame.mapIconImage.remove(removed);
                frame.innerIconImages.remove(image);
                frame.getInnerInstance().setIconImages(frame.innerIconImages);
            }
        });
    }
    
    private Map<Image, java.awt.Image> mapIconImage = new HashMap<>();
    
    private List<java.awt.Image> innerIconImages = new ArrayList<>();
    
    private Collection<Image> iconImages = new Collection<>();
    
    /**
     * このフレームで使用するアイコンのコレクションを取得する。
     * 
     * @return 結果。
     */
    public Collection<Image> getIconImages() {
        return this.iconImages;
    }
    
    /**
     * このフレーム内のルートペインを取得する。
     * 
     * @return 結果。
     */
    public abstract Pane getRootPane();
    
    /**
     * このフレーム内に表示するコントロールをセットする。
     * 
     * @param control 
     */
    public abstract void setContent(Control control);
    
    /**
     * このフレームのタイトルを取得する。
     * 
     * @return 結果。
     */
    public abstract String getTitle();
    
    /**
     * このフレームにタイトルをセットする。
     * 
     * @param title
     */
    public abstract void setTitle(String title);
    
    private boolean isSetLocation = false;
    
    @Override
    public void setLocation(Point point) {
        super.setLocation(point);
        this.isSetLocation = (point != null);
    }
    
    @Override
    public void setDisabled(boolean isDisabled) {
        super.setDisabled(isDisabled);
        this.setClosable(!isDisabled);
        this.getRootPane().setDisabled(isDisabled);
    }

    @Override
    public abstract Dimension getMaximumSize();
    
    @Override
    public abstract void setMaximumSize(Dimension dimension);
    
    /**
     * このフレームがリサイズ可能な場合はtrueを返す。
     * 
     * @return 結果。
     */
    public abstract boolean isResizable();
    
    /**
     * このフレームをリサイズ可能にする場合はtrueをセットする。初期値はtrue。
     * 
     * @param isResizable
     */
    public abstract void setResizable(boolean isResizable);

    /**
     * このフレームを閉じることが可能な場合はtrueを返す。
     * 
     * @return 結果。
     */
    public abstract boolean isClosable();
    
    /**
     * このフレームを閉じることが可能にする場合はtrueをセットする。初期値はtrue。
     * 
     * @param isClosable
     */
    public abstract void setClosable(boolean isClosable);
    
    /**
     * このフレームのネイティブな装飾が無効になっている場合はtrueを返す。
     * 
     * @return 結果。
     */
    public abstract boolean isUndecorated();
    
    /**
     * このフレームのネイティブな装飾を無効にする場合はtrueをセットする。初期値はfalse。
     * 
     * @param isUndecorated
     */
    public abstract void setUndecorated(boolean isUndecorated);
    
    /**
     * このフレームが常に最前面に表示される場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isAlwaysOnTop() {
        return this.getInnerInstance().isAlwaysOnTop();
    }
    
    /**
     * このフレームを常に最前面に表示する場合はtrueをセットする。初期値はfalse。
     * 
     * @param isAlwaysOnTop
     */
    public void setAlwaysOnTop(boolean isAlwaysOnTop) {
        this.getInnerInstance().setAlwaysOnTop(isAlwaysOnTop);
    }
    
    @Override
    public Color getBackgroundColor() {
        return this.getRootPane().getBackgroundColor();
    }
    
    @Override
    public void setBackgroundColor(Color color) {
        this.getRootPane().setBackgroundColor(color);
    }
    
    @Override
    public boolean isFocusable() {
        return this.getInnerInstance().isFocusableWindow();
    }
    
    @Override
    public void setFocusable(boolean isFocusable) {
        this.getInnerInstance().setFocusableWindowState(isFocusable);
    }
    
    /**
     * このフレームをアクティブにする。
     */
    public void activate() {
        this.getInnerInstance().toFront();
    }
    
    /**
     * このフレームがアクティブになっている場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isActivated() {
        return this.getInnerInstance().isActive();
    }

    private Map<Label, PropertyInterface> mapLabelAndProperties = new HashMap<>();    
    
    /**
     * このフレームを表示する。
     * 
     * @throws Exception 
     */
    public void show() throws Exception {
        Frame<T> frame = this;
        if (this.isSetLocation == false) {
            if (GUI.getGraphicsDevices().length > 0) {
                Rectangle screenRectangle = GUI.getMaximumWindowBounds(GUI.getDefaultGraphicsDevice());
                this.setLocation(screenRectangle.x + screenRectangle.width / 2 - this.getWidth() / 2, screenRectangle.y + screenRectangle.height / 2 - this.getHeight() / 2);
            }
        }
        for (Label label : this.mapLabelAndProperties.keySet()) {
            Control control = this.getRootPane().getChildren().findControlByName(this.mapLabelAndProperties.get(label).getPhysicalName());
            if (control != null) {
                label.setLabelFor(control);
            }
        }
        GUI.executeLater(new Runnable() {
            
            @Override
            public void run() {
                frame.getInnerInstance().setVisible(true);
                frame.getInnerInstance().dispatchEvent(new WindowEvent(frame.getInnerInstance(), WindowEvent.WINDOW_OPENED));
            }
        });
    }
    
    /**
     * このフレームを非表示にする。
     */
    public void hide() {
        this.getInnerInstance().setVisible(false);
    }
    
    /**
     * このフレームを閉じる。
     */
    public void close() {
        this.setClosable(true);
        this.getInnerInstance().dispatchEvent(new WindowEvent(this.getInnerInstance(), WindowEvent.WINDOW_CLOSING));
    }
    
    private GraphicsDevice fullscreenGraphicsDevice = null;
    
    /**
     * このフレームを指定されたデバイスにフルスクリーンで表示する。
     * 
     * @param graphicsDevice
     */
    public void fullscreen(GraphicsDevice graphicsDevice) {
        this.fullscreenGraphicsDevice = graphicsDevice;
        this.fullscreenGraphicsDevice.setFullScreenWindow(this.getInnerInstance());
    }

    /**
     * このフレームが配置されているデバイスにフルスクリーンで表示する。
     */
    public void fullscreen() {
        GraphicsDevice device = this.findPlacedGraphicsDevice();
        if (device == null) {
            return;
        }
        this.fullscreen(device);
    }
    
    /**
     * このフレームのフルスクリーン表示をキャンセルする。
     */
    public void cancelFullscreen() {
        if (this.fullscreenGraphicsDevice != null && this.fullscreenGraphicsDevice.getFullScreenWindow().equals(this.getInnerInstance())) {
            this.fullscreenGraphicsDevice.setFullScreenWindow(null);
        }
    }
    
    /**
     * 例外が発生した場合のメッセージを表示する。
     * 
     * @param message メッセージ。
     * @param exception 発生した例外。
     * @param processAfterDialogClosing ダイアログを閉じた後の処理。
     */
    public void showException(String message, Exception exception, ProcessAfterDialogClosing<ResultButton> processAfterDialogClosing) {
        MessageDialog dialog = new MessageDialog(this);
        dialog.setTitle("例外の発生");
        dialog.setMessage(ExceptionMessenger.newInstance(exception).make(message));
        dialog.setProcessAfterClosing(processAfterDialogClosing);
        dialog.show();
    }
    
    /**
     * 例外が発生した場合のメッセージを表示する。
     * 
     * @param exception 発生した例外。
     * @param processAfterDialogClosing ダイアログを閉じた後の処理。
     */
    public final void showException(Exception exception, ProcessAfterDialogClosing<ResultButton> processAfterDialogClosing) {
        this.showException(null, exception, processAfterDialogClosing);
    }

    /**
     * 例外が発生した場合のメッセージを表示する。
     * 
     * @param message メッセージ。
     * @param exception 発生した例外。
     */
    public final void showException(String message, Exception exception) {
        this.showException(message, exception, null);
    }
    
    /**
     * 例外が発生した場合のメッセージを表示する。
     * 
     * @param exception 発生した例外。
     */
    public final void showException(Exception exception) {
        this.showException(null, exception, null);
    }
    
    /**
     * このフレームを開いた際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addOpenedEventHandler(EventHandler<FrameEvent> eventHandler) {
        this.getInnerInstance().addWindowListener(FrameEvent.createInnerOpenedEventHandler(this, eventHandler));
    }
    
    /**
     * このフレームを閉じる際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addClosingEventHandler(EventHandler<FrameEvent> eventHandler) {
        this.getInnerInstance().addWindowListener(FrameEvent.createInnerClosingEventHandler(this, eventHandler));
    }
    
    /**
     * このフレームを閉じた際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addClosedEventHandler(EventHandler<FrameEvent> eventHandler) {
        this.getInnerInstance().addWindowListener(FrameEvent.createInnerClosedEventHandler(this, eventHandler));
    }
    
    /**
     * このフレームを最小化した際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addIconifiedEventHandler(EventHandler<FrameEvent> eventHandler) {
        this.getInnerInstance().addWindowListener(FrameEvent.createInnerIconifiedEventHandler(this, eventHandler));
    }

    /**
     * このフレームが最小化から元に戻った際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addDeiconifiedEventHandler(EventHandler<FrameEvent> eventHandler) {
        this.getInnerInstance().addWindowListener(FrameEvent.createInnerDeiconifiedEventHandler(this, eventHandler));
    }
    
    /**
     * このフレームがアクティブになった際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addActivatedEventHandler(EventHandler<FrameEvent> eventHandler) {
        this.getInnerInstance().addWindowListener(FrameEvent.createInnerActivatedEventHandler(this, eventHandler));
    }
    
    /**
     * このフレームがアクティブになった際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addDeactivatedEventHandler(EventHandler<FrameEvent> eventHandler) {
        this.getInnerInstance().addWindowListener(FrameEvent.createInnerDeactivatedEventHandler(this, eventHandler));
    }

    /**
     * 指定されたプロパティのコントロールのためのラベルを、指定されたテキストで作成する。
     * 
     * @param property
     * @param text 
     * @return 結果。
     */
    public Label createLabelFor(PropertyInterface property, String text) {
        Label label = new Label(text);
        this.mapLabelAndProperties.put(label, property);
        return label;
    }

    /**
     * 指定されたプロパティのコントロールのためのラベルを作成する。
     * 
     * @param property
     * @return 結果。
     */
    public final Label createLabelFor(PropertyInterface property) {
        return this.createLabelFor(property, property.getLogicalName());
    }

    /**
     * 指定されたプロパティの物理名を持つラベルを作成する。
     * 
     * @param property
     * @return 結果。
     */
    public Label createLabel(PropertyInterface property) {
        Label label = new Label();
        label.setName(property.getPhysicalName());
        return label;
    }
    
    /**
     * 指定されたプロパティの物理名を持つテキストフィールドを作成する。
     * 
     * @param property
     * @return 結果。
     */
    public TextField createTextField(PropertyInterface property) {
        TextField textField = new TextField();
        textField.setName(property.getPhysicalName());
        textField.setMaximumLength(property.getMaximumLength());
        return textField;
    }
    
    /**
     * 指定されたプロパティの物理名を持つ整数値入力用テキストフィールドを作成する。
     * 
     * @param property
     * @return 結果。
     */
    public TextField createTextFieldForInteger(PropertyInterface property) {
        TextField textField = this.createTextField(property);
        textField.setTextHorizontalAlignment(HorizontalAlignment.RIGHT);
        textField.setDisableInputMethod(true);
        textField.addLimitByRegex(Regex.INTEGER_NEGATIVE.getPattern(), false);
        return textField;
    }

    /**
     * 指定されたプロパティの物理名を持つ少数値入力用テキストフィールドを作成する。
     * 
     * @param property
     * @return 結果。
     */
    public TextField createTextFieldForDecimal(PropertyInterface property) {
        TextField textField = this.createTextField(property);
        textField.setTextHorizontalAlignment(HorizontalAlignment.RIGHT);
        textField.setDisableInputMethod(true);
        textField.addLimitByRegex(Regex.DECIMAL_NEGATIVE.getPattern(), false);
        return textField;
    }
    
    /**
     * 指定されたプロパティの物理名を持つオートコンプリート機能付きのテキストフィールドを作成する。
     * 
     * @param property
     * @return 結果。
     */
    public AutocompleteTextField createAutocompleteTextField(PropertyInterface property) {
        AutocompleteTextField textField = new AutocompleteTextField();
        textField.setName(property.getPhysicalName());
        textField.setMaximumLength(property.getMaximumLength());
        return textField;
    }

    /**
     * 指定されたプロパティの物理名を持つパスワードフィールドを作成する。
     * 
     * @param property
     * @return 結果。
     */
    public PasswordField createPasswordField(PropertyInterface property) {
        PasswordField passwordField = new PasswordField();
        passwordField.setName(property.getPhysicalName());
        passwordField.setMaximumLength(property.getMaximumLength());
        return passwordField;
    }

    /**
     * 指定されたプロパティの物理名を持つ日付の入力に特化したテキストフィールドを作成する。
     * 
     * @param property
     * @return 結果。
     */
    public DatePicker createDatePicker(PropertyInterface property) {
        DatePicker datePicker = new DatePicker();
        datePicker.setName(property.getPhysicalName());
        return datePicker;
    }

    /**
     * 指定されたプロパティの物理名を持つテキストエリアを作成する。
     * 
     * @param property
     * @return 結果。
     */
    public TextArea createTextArea(PropertyInterface property) {
        TextArea textArea = new TextArea();
        textArea.setName(property.getPhysicalName());
        textArea.setMaximumLength(property.getMaximumLength());
        return textArea;
    }
    
    /**
     * 指定されたプロパティとテキストのチェックボックスを作成する。
     * 
     * @param property
     * @param text 
     * @return 結果。
     */
    public CheckBox createCheckBox(PropertyInterface property, String text) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setName(property.getPhysicalName());
        return checkBox;
    }

    /**
     * 指定されたプロパティの物理名を持つチェックボックスを作成する。
     * 
     * @param property
     * @return 結果。
     */
    public final CheckBox createCheckBox(PropertyInterface property) {
        return this.createCheckBox(property, property.getLogicalName());
    }

    /**
     * 指定されたプロパティの物理名を持つテキストのボタンを作成する。
     * 
     * @param property
     * @param text 
     * @return 結果。
     */
    public Button createButton(PropertyInterface property, String text) {
        Button button = new Button(text);
        button.setName(property.getPhysicalName());
        return button;
    }
    
    /**
     * 指定されたプロパティの物理名を持つボタンを作成する。
     * 
     * @param property
     * @return 結果。
     */
    public final Button createButton(PropertyInterface property) {
        return this.createButton(property, property.getLogicalName());
    }
    
    /**
     * 指定されたプロパティとテキストのラジオボタンを作成する。
     * 
     * @param property
     * @param text 
     * @return 結果。
     */
    public RadioButton createRadioButton(PropertyInterface property, String text) {
        RadioButton radioButton = new RadioButton(text);
        radioButton.setName(property.getPhysicalName());
        return radioButton;
    }

    /**
     * 指定されたプロパティの物理名を持つラジオボタンを作成する。
     * 
     * @param property
     * @return 結果。
     */
    public final RadioButton createRadioButton(PropertyInterface property) {
        return this.createRadioButton(property, property.getLogicalName());
    }

    /**
     * 指定されたプロパティとテキストのトグルボタンを作成する。
     * 
     * @param property
     * @param text 
     * @return 結果。
     */
    public ToggleButton createToggleButton(PropertyInterface property, String text) {
        ToggleButton toggleButton = new ToggleButton(text);
        toggleButton.setName(property.getPhysicalName());
        return toggleButton;
    }
    
    /**
     * 指定されたプロパティの物理名を持つトグルボタンを作成する。
     * 
     * @param property
     * @return 結果。
     */
    public final ToggleButton createToggleButton(PropertyInterface property) {
        return this.createToggleButton(property, property.getLogicalName());
    }
    
    /**
     * 指定されたプロパティの物理名を持つリストビューを作成する。
     * 
     * @param <I> リストアイテムの型。
     * @param property
     * @return 結果。
     */
    public <I> ListView<I> createListView(PropertyInterface property) {
        ListView<I> list = new ListView<>();
        list.setName(property.getPhysicalName());
        return list;
    }
    
    /**
     * 指定されたプロパティの物理名を持つドロップダウンリストを作成する。
     * 
     * @param <I> リストアイテムの型。
     * @param property
     * @return 結果。
     */
    public <I> DropDownList<I> createDropDownList(PropertyInterface property) {
        DropDownList<I> list = new DropDownList<>();
        list.setName(property.getPhysicalName());
        return list;
    }

    /**
     * 指定されたプロパティの物理名を持つコントロールを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param <C> コントロールの型。
     * @param property
     * @return 結果。
     */
    public <C extends Control> C findControl(PropertyInterface property) {
        return this.getRootPane().getChildren().findControlByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティの物理名を持つラベルを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param property
     * @return 結果。
     */
    public Label findLabel(PropertyInterface property) {
        return this.getRootPane().getChildren().findLabelByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティの物理名を持つテキストフィールドを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param property
     * @return 結果。
     */
    public TextField findTextField(PropertyInterface property) {
        return this.getRootPane().getChildren().findTextFieldByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティの物理名を持つオートコンプリート機能付きテキストフィールドを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param property
     * @return 結果。
     */
    public AutocompleteTextField findAutocompleteTextField(PropertyInterface property) {
        return this.getRootPane().getChildren().findAutocompleteTextFieldByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティの物理名を持つパスワードフィールドを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param property
     * @return 結果。
     */
    public PasswordField findPasswordField(PropertyInterface property) {
        return this.getRootPane().getChildren().findPasswordFieldByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティの物理名を持つ日付の入力に特化したテキストフィールドを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param property
     * @return 結果。
     */
    public DatePicker findDatePicker(PropertyInterface property) {
        return this.getRootPane().getChildren().findDatePickerByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティの物理名を持つテキストエリアを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param property
     * @return 結果。
     */
    public TextArea findTextArea(PropertyInterface property) {
        return this.getRootPane().getChildren().findTextAreaByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティの物理名を持つチェックボックスを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param property
     * @return 結果。
     */
    public CheckBox findCheckBox(PropertyInterface property) {
        return this.getRootPane().getChildren().findCheckBoxByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティの物理名を持つラジオボタンを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param property
     * @return 結果。
     */
    public RadioButton findRadioButton(PropertyInterface property) {
        return this.getRootPane().getChildren().findRadioButtonByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティの物理名を持つトグルボタンを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param property
     * @return 結果。
     */
    public ToggleButton findToggleButton(PropertyInterface property) {
        return this.getRootPane().getChildren().findToggleButtonByName(property.getPhysicalName());
    }

    /**
     * 指定されたプロパティの物理名を持つボタンを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param property
     * @return 結果。
     */
    public Button findButton(PropertyInterface property) {
        return this.getRootPane().getChildren().findButtonByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティの物理名を持つリストビューを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param <I> リストアイテムの型。
     * @param property
     * @return 結果。
     */
    public <I> ListView<I> findListView(PropertyInterface property) {
        return this.getRootPane().getChildren().findListViewByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティの物理名を持つドロップダウンリストを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param <I> リストアイテムの型。
     * @param property
     * @return 結果。
     */
    public <I> DropDownList<I> findDropDownList(PropertyInterface property) {
        return this.getRootPane().getChildren().findDropDownListByName(property.getPhysicalName());
    }
}
