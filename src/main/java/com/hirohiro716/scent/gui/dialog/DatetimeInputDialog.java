package com.hirohiro716.scent.gui.dialog;

import com.hirohiro716.scent.Regex;
import com.hirohiro716.scent.StringObject;
import com.hirohiro716.scent.datetime.Datetime;
import com.hirohiro716.scent.gui.Frame;
import com.hirohiro716.scent.gui.HorizontalAlignment;
import com.hirohiro716.scent.gui.KeyCode;
import com.hirohiro716.scent.gui.control.Button;
import com.hirohiro716.scent.gui.control.Control;
import com.hirohiro716.scent.gui.control.DatePicker;
import com.hirohiro716.scent.gui.control.HorizontalPane;
import com.hirohiro716.scent.gui.control.Label;
import com.hirohiro716.scent.gui.control.Spacer;
import com.hirohiro716.scent.gui.control.TextField;
import com.hirohiro716.scent.gui.event.ActionEvent;
import com.hirohiro716.scent.gui.event.EventHandler;

/**
 * 日付と時刻を入力するダイアログのクラス。
 * 
 * @author hiro
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
     * @return
     */
    public DatePicker getDatePicker() {
        return this.datePicker;
    }
    
    private TextField textFieldHour;
    
    /**
     * このダイアログの時数を入力するテキストフィールドを取得する。
     * 
     * @return
     */
    public TextField getTextFieldHour() {
        return this.textFieldHour;
    }
    
    private TextField textFieldMinute;
    
    /**
     * このダイアログの分数を入力するテキストフィールドを取得する。
     * 
     * @return
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
    
    private Label labelDate;
    
    private Label labelTime;
    
    private Label labelColon;
    
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
        this.labelDate = new Label("日付");
        pane.getChildren().add(this.labelDate);
        // DatePicker
        this.datePicker = new DatePicker();
        EventHandler<ActionEvent> actionEventHandler = new EventHandler<>() {

            @Override
            protected void handle(ActionEvent event) {
                dialog.setResultValueAndClose();
            }
        };
        this.datePicker.addActionEventHandler(actionEventHandler);
        pane.getChildren().add(this.datePicker);
        pane.getGrowableControls().add(this.datePicker);
        // Spacer
        pane.getChildren().add(new Spacer(baseSize * 1, 0));
        // Label of time
        this.labelTime = new Label("時刻");
        pane.getChildren().add(this.labelTime);
        // TextField of hour
        this.textFieldHour = new TextField();
        this.textFieldHour.setWidth(baseSize * 4);
        this.textFieldHour.setMinimumWidth(baseSize * 4);
        this.textFieldHour.setTextHorizontalAlignment(HorizontalAlignment.CENTER);
        this.textFieldHour.addLimitByRegex(Regex.INTEGER.getPattern(), false);
        this.textFieldHour.setDisableInputMethod(true);
        this.textFieldHour.addActionEventHandler(actionEventHandler);
        pane.getChildren().add(this.textFieldHour);
        // Label of colon
        this.labelColon = new Label(":");
        pane.getChildren().add(this.labelColon);
        // TextField of minute
        this.textFieldMinute = new TextField();
        this.textFieldMinute.setWidth(baseSize * 4);
        this.textFieldMinute.setMinimumWidth(baseSize * 4);
        this.textFieldMinute.setTextHorizontalAlignment(HorizontalAlignment.CENTER);
        this.textFieldMinute.addLimitByRegex(Regex.INTEGER.getPattern(), false);
        this.textFieldMinute.setDisableInputMethod(true);
        this.textFieldMinute.addActionEventHandler(actionEventHandler);
        pane.getChildren().add(this.textFieldMinute);
        return pane;
    }
    
    private Button buttonOK;
    
    private Button buttonCancel;
    
    /**
     * このダイアログがキャンセル可能な場合はtrueを返す。
     * 
     * @return
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
    
    private boolean isTimeInput = true;
    
    /**
     * このダイアログで時刻が入力できる場合はtrueを返す。
     * 
     * @return
     */
    public boolean isTimeInput() {
        return this.isTimeInput;
    }
    
    /**
     * このダイアログで時刻を入力する場合はtrueをセットする。初期値はtrue。
     * 
     * @param isTimeInput
     */
    public void setTimeInput(boolean isTimeInput) {
        this.isTimeInput = isTimeInput;
    }
    
    @Override
    protected void processBeforeShowing() {
        super.processBeforeShowing();
        if (this.isTimeInput == false) {
            this.labelDate.setVisible(false);
            this.labelTime.setVisible(false);
            this.textFieldHour.setVisible(false);
            String zero = "0";
            if (this.textFieldHour.getText().length() == 0) {
                this.textFieldHour.setText(zero);
            }
            this.labelColon.setVisible(false);
            this.textFieldMinute.setVisible(false);
            if (this.textFieldMinute.getText().length() == 0) {
                this.textFieldMinute.setText(zero);
            }
        }
    }

    @Override
    protected Control getInitialFocusControl() {
        return this.datePicker;
    }
    
    @Override
    public void setDefaultValue(Datetime defaultValue) {
        if (defaultValue == null) {
            return;
        }
        this.datePicker.setDatetime(defaultValue);
        this.textFieldHour.setText(String.valueOf(defaultValue.getHour()));
        this.textFieldMinute.setText(String.valueOf(defaultValue.getMinute()));
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

    @Override
    protected void setCanceledDialogResult() {
        this.result = null;
    }
}
