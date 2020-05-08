package com.hirohiro716.gui;

import java.util.HashMap;
import java.util.Map;

import com.hirohiro716.Regex;
import com.hirohiro716.gui.Window.CloseOperation;
import com.hirohiro716.gui.control.AutocompleteTextField;
import com.hirohiro716.gui.control.Button;
import com.hirohiro716.gui.control.CheckBox;
import com.hirohiro716.gui.control.Control;
import com.hirohiro716.gui.control.DatePicker;
import com.hirohiro716.gui.control.DropDownList;
import com.hirohiro716.gui.control.Label;
import com.hirohiro716.gui.control.ListView;
import com.hirohiro716.gui.control.PasswordField;
import com.hirohiro716.gui.control.RadioButton;
import com.hirohiro716.gui.control.TextArea;
import com.hirohiro716.gui.control.TextField;
import com.hirohiro716.gui.control.ToggleButton;
import com.hirohiro716.gui.dialog.ConfirmationDialog;
import com.hirohiro716.gui.dialog.MessageDialog;
import com.hirohiro716.gui.dialog.ProcessAfterDialogClosing;
import com.hirohiro716.gui.dialog.MessageableDialog.ResultButton;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.FrameEvent;
import com.hirohiro716.property.PropertyInterface;

/**
 * GUIエディターの抽象クラス。
 * 
 * @author hiro
 * 
 * @param <T> 編集するターゲットの型。
 */
public abstract class Editor<T> {
    
    /**
     * コンストラクタ。<br>
     * 表示するウィンドウのタイトル、幅、高さを指定する。
     * 
     * @param title 
     * @param width 
     * @param height 
     */
    public Editor(String title, int width, int height) {
        this.window = new Window();
        this.window.setTitle(title);
        this.window.setSize(width, height);
        this.window.addClosingEventHandler(new CloseEventHandler());
    }
    
    /**
     * ターゲットのインスタンス編集処理を行う。<br>
     * このメソッドはスーバークラスで自動的に呼び出される。
     * 
     * @return 編集処理済みのターゲットインスタンス。
     * @throws Exception
     */
    protected abstract T editTarget() throws Exception;
    
    private T target;
    
    /**
     * 編集中のターゲットインスタンスを取得する。エディター表示前はnullを返す。
     * 
     * @return 結果。
     */
    public T getTarget() {
        return this.target;
    }
    
    /**
     * 編集中のターゲットとしてインスタンスをセットする。
     * 
     * @param target
     */
    protected void setTarget(T target) {
        this.target = target;
    }
    
    private Window window;
    
    /**
     * このエディターのウィンドウを取得する。
     * 
     * @return 結果。
     */
    public Window getWindow() {
        return this.window;
    }
    
    /**
     * このエディターを表示する直前に実行される処理。<br>
     * このメソッドはスーバークラスで自動的に呼び出される。
     * 
     * @throws Exception 
     */
    protected abstract void processBeforeShowing() throws Exception;

    /**
     * このエディターを表示した直後に実行される処理。<br>
     * このメソッドはスーバークラスで自動的に呼び出される。
     * 
     * @throws Exception 
     */
    protected abstract void processAfterShowing() throws Exception;

    private Map<PropertyInterface, Label> mapLabel = new HashMap<>();    
    
    /**
     * このエディターを表示する。
     * 
     * @throws Exception
     */
    public void show() throws Exception {
        // Edit target
        this.target = this.editTarget();
        // Create content
        Control content = this.createContent();
        this.window.setContent(content);
        // Label for
        for (PropertyInterface property : this.mapLabel.keySet()) {
            Control control = this.window.getRootPane().getChildren().findControlByName(property.getPhysicalName());
            if (control != null) {
                this.mapLabel.get(property).setLabelFor(control);
            }
        }
        // Show
        this.processBeforeShowing();
        this.window.show();
        this.processAfterShowing();
    }
    
    private boolean isShowConfirmationBeforeClosing = true;
    
    /**
     * このエディターを閉じる前に確認を表示する場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isShowConfirmationBeforeClosing() {
        return this.isShowConfirmationBeforeClosing;
    }
    
    /**
     * このエディターを閉じる前に確認を表示する場合はtrueをセットする。
     * 
     * @param isShowConfirmationBeforeClosing
     */
    public void setShowConfirmationBeforeClosing(boolean isShowConfirmationBeforeClosing) {
        this.isShowConfirmationBeforeClosing = isShowConfirmationBeforeClosing;
    }

    /**
     * このエディターを閉じる直前に実行される処理。<br>
     * このメソッドはスーバークラスで自動的に呼び出される。
     * 
     * @throws Exception 
     */
    protected abstract void processBeforeClosing() throws Exception;
    
    /**
     * 編集中のターゲットの情報をエディターに出力する。<br>
     * このメソッドはスーバークラスで自動的に実行されない。
     */
    public abstract void outputToEditor();
    
    /**
     * 編集中のターゲットにエディターの情報を入力する。<br>
     * このメソッドはスーバークラスで自動的に実行されない。
     */
    public abstract void inputFromEditor();
    
    /**
     * バリデーションに失敗した場合のメッセージを表示する。
     * 
     * @param exception 発生した例外。
     * @param processAfterDialogClosing ダイアログを閉じた後の処理。
     */
    protected void showValidationException(Exception exception, ProcessAfterDialogClosing<ResultButton> processAfterDialogClosing) {
        MessageDialog dialog = new MessageDialog(this.window);
        dialog.setTitle("バリデーションに失敗");
        dialog.setMessage(exception.getMessage());
        dialog.setProcessAfterClosing(processAfterDialogClosing);
        dialog.show();
    }

    /**
     * バリデーションに失敗した場合のメッセージを表示する。
     * 
     * @param exception 発生した例外。
     */
    protected final void showValidationException(Exception exception) {
        this.showValidationException(exception, null);
    }

    /**
     * 例外が発生した場合のメッセージを表示する。
     * 
     * @param message メッセージ。
     * @param exception 発生した例外。
     * @param processAfterDialogClosing ダイアログを閉じた後の処理。
     */
    protected void showException(String message, Exception exception, ProcessAfterDialogClosing<ResultButton> processAfterDialogClosing) {
        this.window.showException(message, exception, processAfterDialogClosing);
    }
    
    /**
     * 例外が発生した場合のメッセージを表示する。
     * 
     * @param exception 発生した例外。
     * @param processAfterDialogClosing ダイアログを閉じた後の処理。
     */
    protected final void showException(Exception exception, ProcessAfterDialogClosing<ResultButton> processAfterDialogClosing) {
        this.showException(null, exception, processAfterDialogClosing);
    }

    /**
     * 例外が発生した場合のメッセージを表示する。
     * 
     * @param message メッセージ。
     * @param exception 発生した例外。
     */
    protected final void showException(String message, Exception exception) {
        this.showException(message, exception, null);
    }
    
    /**
     * 例外が発生した場合のメッセージを表示する。
     * 
     * @param exception 発生した例外。
     */
    protected final void showException(Exception exception) {
        this.showException(null, exception, null);
    }
    
    /**
     * このエディターを閉じる。
     */
    public void close() {
        this.isShowConfirmationBeforeClosing = false;
        this.window.close();
    }

    /**
     * このエディターを閉じる際のイベントハンドラー。
     * 
     * @author hiro
     *
     */
    private class CloseEventHandler extends EventHandler<FrameEvent> {
        
        private boolean isAgree = false;
        
        private ConfirmationDialog dialog = null;
        
        private final static String DIALOG_TITLE = "閉じる確認";
        
        private final static String DIALOG_MESSAGE = "この画面を閉じようとしています。";
        
        @Override
        protected void handle(FrameEvent event) {
            Editor<T> editor = Editor.this;
            if (editor.isShowConfirmationBeforeClosing && this.isAgree == false) {
                if (this.dialog != null) {
                    return;
                }
                editor.window.setCloseOperation(CloseOperation.DO_NOT_CLOSE);
                this.dialog = new ConfirmationDialog(editor.window);
                this.dialog.setTitle(CloseEventHandler.DIALOG_TITLE);
                this.dialog.setMessage(CloseEventHandler.DIALOG_MESSAGE);
                this.dialog.setDefaultValue(ResultButton.OK);
                this.dialog.setProcessAfterClosing(new ProcessAfterDialogClosing<>() {
                    
                    @Override
                    public void execute(ResultButton dialogResult) {
                        CloseEventHandler handler = CloseEventHandler.this;
                        if (dialogResult == ResultButton.OK) {
                            try {
                                editor.processBeforeClosing();
                                handler.isAgree = true;
                                editor.window.close();
                            } catch (Exception exception) {
                            }
                        }
                        handler.dialog = null;
                    }
                });
                this.dialog.show();
            } else {
                editor.window.setCloseOperation(CloseOperation.DISPOSE);
            }
        }
    };
    
    /**
     * このエディターに表示するコンテンツを作成する。<br>
     * このメソッドはスーバークラスで自動的に呼び出される。
     * 
     * @return 結果。
     * @throws Exception 
     */
    protected abstract Control createContent() throws Exception;
    
    /**
     * 指定されたプロパティとテキストのラベルを作成する。
     * 
     * @param property
     * @param text 
     * @return 結果。
     */
    protected Label createLabel(PropertyInterface property, String text) {
        Label label = new Label(text);
        this.mapLabel.put(property, label);
        return label;
    }

    /**
     * 指定されたプロパティのラベルを作成する。
     * 
     * @param property
     * @return 結果。
     */
    protected final Label createLabel(PropertyInterface property) {
        return this.createLabel(property, property.getLogicalName());
    }
    
    /**
     * 指定されたプロパティのテキストフィールドを作成する。
     * 
     * @param property
     * @return 結果。
     */
    protected TextField createTextField(PropertyInterface property) {
        TextField textField = new TextField();
        textField.setName(property.getPhysicalName());
        textField.setMaximumLength(property.getMaximumLength());
        return textField;
    }
    
    /**
     * 指定されたプロパティの整数値入力用テキストフィールドを作成する。
     * 
     * @param property
     * @return 結果。
     */
    protected TextField createTextFieldForInteger(PropertyInterface property) {
        TextField textField = this.createTextField(property);
        textField.setTextHorizontalAlignment(HorizontalAlignment.RIGHT);
        textField.setDisableInputMethod(true);
        textField.addLimitByRegex(Regex.INTEGER_NEGATIVE.getPattern(), false);
        return textField;
    }

    /**
     * 指定されたプロパティの少数値入力用テキストフィールドを作成する。
     * 
     * @param property
     * @return 結果。
     */
    protected TextField createTextFieldForDecimal(PropertyInterface property) {
        TextField textField = this.createTextField(property);
        textField.setTextHorizontalAlignment(HorizontalAlignment.RIGHT);
        textField.setDisableInputMethod(true);
        textField.addLimitByRegex(Regex.DECIMAL_NEGATIVE.getPattern(), false);
        return textField;
    }
    
    /**
     * 指定されたプロパティのオートコンプリート機能付きのテキストフィールドを作成する。
     * 
     * @param property
     * @return 結果。
     */
    protected AutocompleteTextField createAutocompleteTextField(PropertyInterface property) {
        AutocompleteTextField textField = new AutocompleteTextField();
        textField.setName(property.getPhysicalName());
        textField.setMaximumLength(property.getMaximumLength());
        return textField;
    }

    /**
     * 指定されたプロパティのパスワードフィールドを作成する。
     * 
     * @param property
     * @return 結果。
     */
    protected PasswordField createPasswordField(PropertyInterface property) {
        PasswordField passwordField = new PasswordField();
        passwordField.setName(property.getPhysicalName());
        passwordField.setMaximumLength(property.getMaximumLength());
        return passwordField;
    }

    /**
     * 指定されたプロパティの日付の入力に特化したテキストフィールドを作成する。
     * 
     * @param property
     * @return 結果。
     */
    protected DatePicker createDatePicker(PropertyInterface property) {
        DatePicker datePicker = new DatePicker();
        datePicker.setName(property.getPhysicalName());
        return datePicker;
    }

    /**
     * 指定されたプロパティのテキストエリアを作成する。
     * 
     * @param property
     * @return 結果。
     */
    protected TextArea createTextArea(PropertyInterface property) {
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
    protected CheckBox createCheckBox(PropertyInterface property, String text) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setName(property.getPhysicalName());
        return checkBox;
    }

    /**
     * 指定されたプロパティのチェックボックスを作成する。
     * 
     * @param property
     * @return 結果。
     */
    protected final CheckBox createCheckBox(PropertyInterface property) {
        return this.createCheckBox(property, property.getLogicalName());
    }

    /**
     * 指定されたプロパティとテキストのボタンを作成する。
     * 
     * @param property
     * @param text 
     * @return 結果。
     */
    protected Button createButton(PropertyInterface property, String text) {
        Button button = new Button(text);
        button.setName(property.getPhysicalName());
        return button;
    }
    
    /**
     * 指定されたプロパティのボタンを作成する。
     * 
     * @param property
     * @return 結果。
     */
    protected final Button createButton(PropertyInterface property) {
        return this.createButton(property, property.getLogicalName());
    }
    
    /**
     * 指定されたプロパティとテキストのラジオボタンを作成する。
     * 
     * @param property
     * @param text 
     * @return 結果。
     */
    protected RadioButton createRadioButton(PropertyInterface property, String text) {
        RadioButton radioButton = new RadioButton(text);
        radioButton.setName(property.getPhysicalName());
        return radioButton;
    }

    /**
     * 指定されたプロパティのラジオボタンを作成する。
     * 
     * @param property
     * @return 結果。
     */
    protected final RadioButton createRadioButton(PropertyInterface property) {
        return this.createRadioButton(property, property.getLogicalName());
    }

    /**
     * 指定されたプロパティとテキストのトグルボタンを作成する。
     * 
     * @param property
     * @param text 
     * @return 結果。
     */
    protected ToggleButton createToggleButton(PropertyInterface property, String text) {
        ToggleButton toggleButton = new ToggleButton(text);
        toggleButton.setName(property.getPhysicalName());
        return toggleButton;
    }
    
    /**
     * 指定されたプロパティのトグルボタンを作成する。
     * 
     * @param property
     * @return 結果。
     */
    protected final ToggleButton createToggleButton(PropertyInterface property) {
        return this.createToggleButton(property, property.getLogicalName());
    }
    
    /**
     * 指定されたプロパティのリストビューを作成する。
     * 
     * @param <I> リストアイテムの型。
     * @param property
     * @return 結果。
     */
    protected <I> ListView<I> createListView(PropertyInterface property) {
        ListView<I> list = new ListView<>();
        list.setName(property.getPhysicalName());
        return list;
    }
    
    /**
     * 指定されたプロパティのドロップダウンリストを作成する。
     * 
     * @param <I> リストアイテムの型。
     * @param property
     * @return 結果。
     */
    protected <I> DropDownList<I> createDropDownList(PropertyInterface property) {
        DropDownList<I> list = new DropDownList<>();
        list.setName(property.getPhysicalName());
        return list;
    }

    /**
     * 指定されたプロパティのコントロールを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param <C> コントロールの型。
     * @param property
     * @return 結果。
     */
    public <C extends Control> C findControl(PropertyInterface property) {
        return this.getWindow().getRootPane().getChildren().findControlByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティのラベルを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param property
     * @return 結果。
     */
    public Label findLabel(PropertyInterface property) {
        return this.getWindow().getRootPane().getChildren().findLabelByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティのテキストフィールドを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param property
     * @return 結果。
     */
    public TextField findTextField(PropertyInterface property) {
        return this.getWindow().getRootPane().getChildren().findTextFieldByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティのオートコンプリート機能付きテキストフィールドを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param property
     * @return 結果。
     */
    public AutocompleteTextField findAutocompleteTextField(PropertyInterface property) {
        return this.getWindow().getRootPane().getChildren().findAutocompleteTextFieldByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティのパスワードフィールドを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param property
     * @return 結果。
     */
    public PasswordField findPasswordField(PropertyInterface property) {
        return this.getWindow().getRootPane().getChildren().findPasswordFieldByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティの日付の入力に特化したテキストフィールドを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param property
     * @return 結果。
     */
    public DatePicker findDatePicker(PropertyInterface property) {
        return this.getWindow().getRootPane().getChildren().findDatePickerByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティのテキストエリアを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param property
     * @return 結果。
     */
    public TextArea findTextArea(PropertyInterface property) {
        return this.getWindow().getRootPane().getChildren().findTextAreaByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティのチェックボックスを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param property
     * @return 結果。
     */
    public CheckBox findCheckBox(PropertyInterface property) {
        return this.getWindow().getRootPane().getChildren().findCheckBoxByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティのラジオボタンを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param property
     * @return 結果。
     */
    public RadioButton findRadioButton(PropertyInterface property) {
        return this.getWindow().getRootPane().getChildren().findRadioButtonByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティのトグルボタンを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param property
     * @return 結果。
     */
    public ToggleButton findToggleButton(PropertyInterface property) {
        return this.getWindow().getRootPane().getChildren().findToggleButtonByName(property.getPhysicalName());
    }

    /**
     * 指定されたプロパティのボタンを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param property
     * @return 結果。
     */
    public Button findButton(PropertyInterface property) {
        return this.getWindow().getRootPane().getChildren().findButtonByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティのリストビューを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param <I> リストアイテムの型。
     * @param property
     * @return 結果。
     */
    public <I> ListView<I> findListView(PropertyInterface property) {
        return this.getWindow().getRootPane().getChildren().findListViewByName(property.getPhysicalName());
    }
    
    /**
     * 指定されたプロパティのドロップダウンリストを検索する。見つからなかった場合はnullを返す。<br>
     * このメソッドはペインに追加されているすべての子要素を再帰的に検索する。
     * 
     * @param <I> リストアイテムの型。
     * @param property
     * @return 結果。
     */
    public <I> DropDownList<I> findDropDownList(PropertyInterface property) {
        return this.getWindow().getRootPane().getChildren().findDropDownListByName(property.getPhysicalName());
    }
}
