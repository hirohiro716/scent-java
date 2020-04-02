package com.hirohiro716.gui.dialog;

import com.hirohiro716.Regex;
import com.hirohiro716.StringObject;
import com.hirohiro716.datetime.Datetime;
import com.hirohiro716.gui.Component;
import com.hirohiro716.gui.Frame;
import com.hirohiro716.gui.HorizontalAlignment;
import com.hirohiro716.gui.KeyCode;
import com.hirohiro716.gui.control.Button;
import com.hirohiro716.gui.control.Control;
import com.hirohiro716.gui.control.DatePicker;
import com.hirohiro716.gui.control.HorizontalPane;
import com.hirohiro716.gui.control.Label;
import com.hirohiro716.gui.control.Spacer;
import com.hirohiro716.gui.control.TextField;
import com.hirohiro716.gui.event.ActionEvent;
import com.hirohiro716.gui.event.ChangeListener;
import com.hirohiro716.gui.event.EventHandler;

/**
 * 日付と時刻を入力するダイアログのクラス。
 * 
 * @author hiro
 *
 */
public class DatetimeInputDialog extends MessageableDialog<Datetime> {
    
    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner
     */
    public DatetimeInputDialog(Frame<?> owner) {
        super(owner);
        this.getPane().setHeight(this.getPane().getFont().getSize() * 27);
    }
    
    @Override
    public boolean isCancelableByClickBackground() {
        return this.isCancelable();
    }
    
    private DatePicker datePicker;
    
    /**
     * このダイアログの日付の入力に特化したテキストフィールドを取得する。
     * 
     * @return 結果。
     */
    public DatePicker getDatePicker() {
        return this.datePicker;
    }
    
    private TextField textFieldHour;
    
    /**
     * このダイアログの時数を入力するテキストフィールドを取得する。
     * 
     * @return 結果。
     */
    public TextField getTextFieldHour() {
        return this.textFieldHour;
    }
    
    private TextField textFieldMinute;
    
    /**
     * このダイアログの分数を入力するテキストフィールドを取得する。
     * 
     * @return 結果。
     */
    public TextField getTextFieldMinute() {
        return this.textFieldMinute;
    }
    
    /**
     * このダイアログの結果をセットして閉じる。
     */
    private void setResultValueAndClose() {
        Datetime datetime = this.datePicker.toDatetime();
        if (datetime == null) {
            return;
        }
        Integer hour = StringObject.newInstance(this.textFieldHour.getText()).toInteger();
        if (hour == null || hour < 0 || hour > 23) {
            return;
        }
        Integer minute = StringObject.newInstance(this.textFieldMinute.getText()).toInteger();
        if (minute == null || minute < 0 || minute > 59) {
            return;
        }
        this.result = new Datetime(datetime.getDate());
        this.result.modifyHour(hour);
        this.result.modifyMinute(minute);
        this.result.modifySecond(0);
        this.result.modifyMillisecond(0);
        this.close();
    }
    
    @Override
    protected Control createInputControl() {
        DatetimeInputDialog dialog = this;
        HorizontalPane pane = new HorizontalPane();
        int baseSize = pane.getFont().getSize();
        pane.setMinimumHeight(baseSize * 3);
        pane.setPadding(0, 0, 0, baseSize);
        pane.setFillChildToPaneHeight(true);
        pane.setSpacing(5);
        // Label of date
        pane.getChildren().add(new Label("日付"));
        // DatePicker
        this.datePicker = new DatePicker();
        ChangeListener<String> textChangeListener = new ChangeListener<>() {

            @Override
            protected void changed(Component<?> component, String changedValue, String valueBeforeChange) {
                dialog.buttonOK.setDisabled(true);
                Datetime datetime = dialog.datePicker.toDatetime();
                if (datetime == null) {
                    return;
                }
                Integer hour = StringObject.newInstance(dialog.textFieldHour.getText()).toInteger();
                if (hour == null || hour < 0 || hour > 23) {
                    return;
                }
                Integer minute = StringObject.newInstance(dialog.textFieldMinute.getText()).toInteger();
                if (minute == null || minute < 0 || minute > 59) {
                    return;
                }
                dialog.buttonOK.setDisabled(false);
            }
        };
        this.datePicker.addTextChangeListener(textChangeListener);
        pane.getChildren().add(this.datePicker);
        pane.getGrowableControls().add(this.datePicker);
        // Spacer
        pane.getChildren().add(new Spacer(baseSize * 1, 0));
        // Label of time
        pane.getChildren().add(new Label("時刻"));
        // TextField of hour
        this.textFieldHour = new TextField();
        this.textFieldHour.setMinimumWidth(baseSize * 4);
        this.textFieldHour.setTextHorizontalAlignment(HorizontalAlignment.CENTER);
        this.textFieldHour.addLimitByRegex(Regex.INTEGER_NARROW.getPattern(), false);
        this.textFieldHour.addTextChangeListener(textChangeListener);
        EventHandler<ActionEvent> actionEventHandler = new EventHandler<>() {

            @Override
            protected void handle(ActionEvent event) {
                dialog.setResultValueAndClose();
            }
        };
        this.textFieldHour.addActionEventHandler(actionEventHandler);
        pane.getChildren().add(this.textFieldHour);
        // Label of colon
        pane.getChildren().add(new Label(":"));
        // TextField of minute
        this.textFieldMinute = new TextField();
        this.textFieldMinute.setMinimumWidth(baseSize * 4);
        this.textFieldMinute.setTextHorizontalAlignment(HorizontalAlignment.CENTER);
        this.textFieldMinute.addLimitByRegex(Regex.INTEGER_NARROW.getPattern(), false);
        this.textFieldMinute.addTextChangeListener(textChangeListener);
        this.textFieldMinute.addActionEventHandler(actionEventHandler);
        pane.getChildren().add(this.textFieldMinute);
        return pane;
    }
    
    private Button buttonOK;
    
    private Button buttonCancel;
    
    /**
     * このダイアログがキャンセル可能な場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isCancelable() {
        return this.buttonCancel.isVisible();
    }
    
    /**
     * このダイアログをキャンセル可能にする場合はtrueをセットする。初期値はtrue。
     * 
     * @param isCancelable
     */
    public void setCancelable(boolean isCancelable) {
        this.buttonCancel.setVisible(isCancelable);
    }
    
    @Override
    protected Button[] createButtons() {
        DatetimeInputDialog dialog = this;
        this.buttonOK = new Button("OK");
        this.buttonOK.setMnemonic(KeyCode.O);
        this.buttonOK.setDisabled(true);
        this.buttonOK.addActionEventHandler(new EventHandler<ActionEvent>() {
            
            @Override
            protected void handle(ActionEvent event) {
                dialog.setResultValueAndClose();
            }
        });
        this.buttonCancel = new Button("キャンセル(C)");
        this.buttonCancel.setMnemonic(KeyCode.C);
        this.buttonCancel.addActionEventHandler(new EventHandler<ActionEvent>() {

            @Override
            protected void handle(ActionEvent event) {
                dialog.close();
            }
        });
        return new Button[] {this.buttonOK, this.buttonCancel};
    }

    @Override
    protected Control getInitialFocusControl() {
        return this.datePicker;
    }
    
    @Override
    public void setDefaultValue(Datetime defaultResultValue) {
        this.datePicker.setDatetime(defaultResultValue);
        this.textFieldHour.setText(String.valueOf(defaultResultValue.toHour()));
        this.textFieldMinute.setText(String.valueOf(defaultResultValue.toMinute()));
    }
    
    private Datetime result = null;
    
    @Override
    public Datetime getDialogResult() {
        return this.result;
    }

    @Override
    protected void setDialogResult(Datetime result) {
        this.result = result;
    }
}
