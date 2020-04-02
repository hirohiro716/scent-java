package com.hirohiro716.gui.control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.UIManager;

import com.hirohiro716.StringObject;
import com.hirohiro716.datetime.Datetime;
import com.hirohiro716.datetime.Datetime.DayOfWeek;
import com.hirohiro716.gui.Border;
import com.hirohiro716.gui.Component;
import com.hirohiro716.gui.Frame;
import com.hirohiro716.gui.GUI;
import com.hirohiro716.gui.HorizontalAlignment;
import com.hirohiro716.gui.Popup;
import com.hirohiro716.gui.VerticalAlignment;
import com.hirohiro716.gui.event.ActionEvent;
import com.hirohiro716.gui.event.ChangeListener;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.KeyEvent;
import com.hirohiro716.gui.event.MouseEvent;
import com.hirohiro716.gui.event.MouseEvent.MouseButton;

/**
 * 日付の入力に特化したテキストフィールドのクラス。
 * 
 * @author hiro
 *
 */
public class DatePicker extends TextField {
    
    /**
     * コンストラクタ。<br>
     * このコントロールの初期値とフォーマットパターンを指定する。
     * 
     * @param datetime
     * @param dateFormat
     */
    public DatePicker(Datetime datetime, DateFormat dateFormat) {
        super();
        this.setTextHorizontalAlignment(HorizontalAlignment.CENTER);
        this.dateFormat = dateFormat;
        if (datetime != null) {
            this.setDatetime(datetime);
        }
        DatePicker textField = this;
        this.addSizeChangeListener(new ChangeListener<Dimension>() {
            
            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension valueBeforeChange) {
                textField.showPopup();
            }
        });
        this.addFocusChangeListener(new ChangeListener<Boolean>() {
            
            @Override
            protected void changed(Component<?> component, Boolean changedValue, Boolean valueBeforeChange) {
                if (changedValue) {
                    textField.showPopup();
                    textField.selectAll();
                } else {
                    textField.hidePopup();
                    textField.parseInputString();
                }
            }
        });
        this.addKeyPressedEventHandler(new EventHandler<KeyEvent>() {
            
            @Override
            protected void handle(KeyEvent event) {
                switch (event.getKeyCode()) {
                case ENTER:
                    textField.parseInputString();
                    break;
                default:
                    break;
                }
            }
        });
    }
    
    /**
     * コンストラクタ。<br>
     * このコントロールの初期値を指定する。
     * 
     * @param datetime
     */
    public DatePicker(Datetime datetime) {
        this(datetime, Datetime.DEFAULT_DATE_FORMAT_ONLY_DATE);
    }
    
    /**
     * コンストラクタ。<br>
     * このコントロールの初期値を指定する。
     * 
     * @param date
     */
    public DatePicker(Date date) {
        this(new Datetime(date), Datetime.DEFAULT_DATE_FORMAT_ONLY_DATE);
    }
    
    /**
     * コンストラクタ。<br>
     * このコントロールのフォーマットパターンを指定する。
     * 
     * @param dateFormat
     */
    public DatePicker(DateFormat dateFormat) {
        this((Datetime) null, dateFormat);
    }
    
    /**
     * コンストラクタ。
     */
    public DatePicker() {
        this((Datetime) null);
    }
    
    @Override
    public void setEditable(boolean isEditable) {
        super.setEditable(isEditable);
        this.setDisabledPopup(isEditable == false);
        this.hidePopup();
    }

    @Override
    public void setVisible(boolean isVisible) {
        super.setVisible(isVisible);
        this.setDisabledPopup(isVisible == false);
        this.hidePopup();
    }

    @Override
    public void setDisabled(boolean isDisabled) {
        super.setDisabled(isDisabled);
        this.setDisabledPopup(isDisabled);
        this.hidePopup();
    }
    
    private DateFormat dateFormat;
    
    /**
     * このコントロールの日付フォーマットパターンを取得する。
     * 
     * @return 結果。
     */
    public DateFormat getFormatPattern() {
        return this.dateFormat;
    }
    
    private List<EventHandler<ActionEvent>> actionEventHandlers = new ArrayList<>();
    
    /**
     * このコントロールの入力文字列がDatetimeインスタンスにパースされた際のイベントハンドラーを追加する。
     * 
     * @param eventHandler
     */
    public void addActionEventHandler(EventHandler<ActionEvent> eventHandler) {
        this.actionEventHandlers.add(eventHandler);
    }
    
    /**
     * このコントロールの入力文字列がDatetimeインスタンスにパースされた際のイベントハンドラーを削除する。
     * 
     * @param eventHandler
     */
    public void removeActionEventHandler(EventHandler<ActionEvent> eventHandler) {
        this.actionEventHandlers.remove(eventHandler);
    }
    
    private Datetime datetimeBeforeChange = null;
    
    /**
     * このコントロールの入力文字列がDatetimeインスタンスにパースされた際のイベントハンドラーを実行する。
     */
    private void executeActionEventHandler() {
        Datetime datetime = this.toDatetime();
        if (datetime == null && this.datetimeBeforeChange != null || datetime != null && datetime.equals(this.datetimeBeforeChange) == false) {
            for (EventHandler<ActionEvent> eventHandler : this.actionEventHandlers) {
                eventHandler.executeWhenControlEnabled(new ActionEvent(this, (java.awt.event.ActionEvent) null));
            }
            this.datetimeBeforeChange = datetime;
        }
    }
    
    /**
     * このコントロールの入力文字列をフォーマットパターンに従ってパースする。
     */
    private void parseInputString() {
        StringObject input = new StringObject(this.getText());
        this.clear();
        input.replace("/", "-");
        String[] values = input.split("-");
        switch (values.length) {
        case 1:
            try {
                Integer day = StringObject.newInstance(values[0]).toInteger();
                if (day >= 1 && day <= 31) {
                    Datetime datetime = new Datetime();
                    datetime.modifyDay(day);
                    this.setText(datetime.toString(this.dateFormat));
                }
            } catch (Exception exception) {
            }
            break;
        case 2:
            try {
                Datetime datetime = new Datetime();
                datetime.modifyMonth(StringObject.newInstance(values[0]).toInteger());
                datetime.modifyDay(StringObject.newInstance(values[1]).toInteger());
                this.setText(datetime.toString(this.dateFormat));
            } catch (Exception exception) {
            }
            break;
        case 3:
            try {
                Datetime datetime = new Datetime();
                StringObject thisYear = new StringObject(datetime.toYear());
                StringObject year = StringObject.newInstance(values[0]);
                switch (year.length()) {
                case 2:
                    datetime.modifyYear(StringObject.join(thisYear.extract(0, 2), year).toInteger());
                    datetime.modifyMonth(StringObject.newInstance(values[1]).toInteger());
                    datetime.modifyDay(StringObject.newInstance(values[2]).toInteger());
                    break;
                case 3:
                    datetime.modifyYear(StringObject.join(thisYear.extract(0, 1), year).toInteger());
                    datetime.modifyMonth(StringObject.newInstance(values[1]).toInteger());
                    datetime.modifyDay(StringObject.newInstance(values[2]).toInteger());
                    break;
                default:
                    datetime.set(input.toString());
                    break;
                }
                this.setText(datetime.toString(this.dateFormat));
            } catch (Exception exception) {
            }
            break;
        }
        if (this.getText().length() == 0) {
            try {
                this.setText(Datetime.newInstance(input.toString(), this.dateFormat).toString());
            } catch (ParseException exception) {
            }
        }
        this.executeActionEventHandler();
    }
    
    /**
     * このコントロールにDatetimeインスタンスから文字列をセットする。
     * 
     * @param datetime
     */
    public void setDatetime(Datetime datetime) {
        if (datetime == null) {
            this.setText(null);
        } else {
            this.setText(datetime.toString(this.dateFormat));
        }
        this.datetimeBeforeChange = this.toDatetime();
    }
    
    /**
     * このコントロールにDateインスタンスから文字列をセットする。
     * 
     * @param date
     */
    public final void setDate(Date date) {
        if (date == null) {
            this.setText(null);
        } else {
            this.setDatetime(Datetime.newInstance(date));
        }
    }
    
    /**
     * このコントロールの入力からDatetimeインスタンスを作成する。
     * 
     * @return 結果。
     */
    public Datetime toDatetime() {
        try {
            Datetime datetime = new Datetime(this.getText(), this.getFormatPattern());
            datetime.modifyHour(0);
            datetime.modifyMinute(0);
            datetime.modifySecond(0);
            datetime.modifyMillisecond(0);
            return datetime;
        } catch (ParseException exception) {
            return null;
        }
    }
    
    /**
     * このコントロールの入力からDateインスタンスを作成する。
     * 
     * @return 結果。
     */
    public Date toDate() {
        Datetime datetime = this.toDatetime();
        if (datetime == null) {
            return null;
        }
        return datetime.getDate();
    }
    
    private Popup popup = null;
    
    /**
     * このコントロールのカレンダーに使用しているポップアップを取得する。
     * 
     * @return 結果。
     */
    public Popup getPopup() {
        return this.popup;
    }
    
    private boolean isDisabledPopup = false;
    
    /**
     * このコントロールのカレンダーのポップアップを無効にする。
     * 
     * @param isDisabledPopup
     */
    public void setDisabledPopup(boolean isDisabledPopup) {
        this.isDisabledPopup = isDisabledPopup;
    }
    
    private CalendarPane calendarPane = null;
    
    /**
     * このコントロールのカレンダー用のポップアップを表示する。
     */
    public void showPopup() {
        DatePicker control = this;
        Frame<?> frame = this.getFrame();
        if (frame == null) {
            return;
        }
        if (this.popup == null || this.calendarPane == null) {
            this.popup = new Popup(frame);
            frame.addLocationChangeListener(new ChangeListener<Point>() {
                
                @Override
                protected void changed(Component<?> component, Point changedValue, Point valueBeforeChange) {
                    control.showPopup();
                }
            });
            frame.addSizeChangeListener(new ChangeListener<Dimension>() {
                
                @Override
                protected void changed(Component<?> component, Dimension changedValue, Dimension valueBeforeChange) {
                    control.showPopup();
                }
            });
            this.calendarPane = new CalendarPane();
            this.popup.getChildren().add(this.calendarPane);
        }
        if (this.getLocationOnScreen() == null || this.getWidth() < 20 || this.getHeight() == 0) {
            return;
        }
        Datetime datetime = this.toDatetime();
        if (datetime == null) {
            datetime = new Datetime();
        }
        this.calendarPane.setYearAndMonth(datetime.toYear(), datetime.toMonth());
        this.popup.setX((int) this.getLocationOnScreen().getX() + 3);
        int y = (int) this.getLocationOnScreen().getY() + this.getHeight() + 3;
        Rectangle rectangle = GUI.getMaximumWindowBounds(this.findPlacedGraphicsDevice());
        if (y + this.popup.getHeight() > rectangle.getY() + rectangle.getHeight()) {
            y = (int) this.getLocationOnScreen().getY() - this.popup.getHeight() - 3;
        }
        this.popup.setY(y);
        this.popup.setMinimumWidth(this.getFont().getSize() * 24);
        if (this.isFocused() && this.isDisabledPopup == false && this.isVisible() && this.isDisabled() == false) {
            this.calendarPane.displayCalender();
            this.popup.show();
        }
    }
    
    /**
     * このコントロールのカレンダー用のポップアップを隠す。
     */
    public void hidePopup() {
        if (this.getPopup() != null) {
            this.getPopup().hide();
        }
    }
    
    /**
     * カレンダーを表示するペインクラス。
     * 
     * @author hiro
     *
     */
    private class CalendarPane extends VerticalPane {
        
        /**
         * コンストラクタ。
         */
        public CalendarPane() {
            DatePicker control = DatePicker.this;
            Datetime defaultDatetime = control.toDatetime();
            if (defaultDatetime == null) {
                defaultDatetime = new Datetime();
            }
            // Inherit color information
            this.baseSize = control.getFont().getSize();
            this.borderColor = new Color(UIManager.getColor("controlDkShadow").getRGB());
            this.focusedBackground = new Color(control.getInnerInstance().getSelectionColor().getRGB());
            this.focusedForeground = new Color(control.getInnerInstance().getSelectedTextColor().getRGB());
            this.inactiveForeground = new Color(control.getInnerInstance().getDisabledTextColor().getRGB());
            // Pane settings
            this.setFillChildToPaneWidth(true);
            this.setBorder(Border.createLine(Color.DARK_GRAY, 1));
            this.setPadding(this.baseSize);
            HorizontalPane yearAndMonthPane = new HorizontalPane(VerticalAlignment.CENTER);
            this.yearDropDownList = this.createYearDropDownList(defaultDatetime);
            yearAndMonthPane.getChildren().add(this.yearDropDownList);
            yearAndMonthPane.getGrowableControls().add(this.yearDropDownList);
            Label yearLabel = new Label(" 年");
            yearLabel.setFont(control.getFont());
            yearAndMonthPane.getChildren().add(yearLabel);
            yearAndMonthPane.getChildren().add(new Spacer(this.baseSize, 0));
            this.monthDropDownList = this.createMonthDropDownList(defaultDatetime);
            yearAndMonthPane.getChildren().add(this.monthDropDownList);
            yearAndMonthPane.getGrowableControls().add(this.monthDropDownList);
            Label monthLabel = new Label(" 月");
            monthLabel.setFont(control.getFont());
            yearAndMonthPane.getChildren().add(monthLabel);
            this.getChildren().add(yearAndMonthPane);
            this.getChildren().add(new Spacer(0, this.baseSize));
            this.gridPane = this.createCalendarGridPane();
            this.getChildren().add(this.gridPane);
            this.displayCalender();
        }
        
        private int baseSize;
        
        private Color borderColor;
        
        private Color focusedBackground;
        
        private Color focusedForeground;
        
        private Color inactiveForeground;
        
        private Color mouseEnterdBackground = new Color(255, 255, 255, 100);
        
        private DropDownList<Integer> yearDropDownList;
        
        private DropDownList<Integer> monthDropDownList;
        
        /**
         * カレンダーの年を変更するコントロールを作成する。
         * 
         * @param defaultDatetime
         * @return 結果。
         */
        private DropDownList<Integer> createYearDropDownList(Datetime defaultDatetime) {
            DatePicker control = DatePicker.this;
            DropDownList<Integer> dropDownList = new DropDownList<>(Datetime.createYearsList(10));
            dropDownList.setFont(control.getFont());
            dropDownList.setTextHorizontalAlignment(HorizontalAlignment.RIGHT);
            dropDownList.setFocusable(false);
            dropDownList.setEnableChangeValueWithMouseWheelRotation(true);
            dropDownList.setSelectedItem(defaultDatetime.toYear());
            dropDownList.addSelectedItemChangeListener(this.monthChangeListener);
            return dropDownList;
        }
        
        /**
         * カレンダーの月を変更するコントロールを作成する。
         * 
         * @param defaultDatetime
         * @return 結果。
         */
        private DropDownList<Integer> createMonthDropDownList(Datetime defaultDatetime) {
            DatePicker control = DatePicker.this;
            DropDownList<Integer> dropDownList = new DropDownList<>(Datetime.createMonthsList());
            dropDownList.setFont(control.getFont());
            dropDownList.setTextHorizontalAlignment(HorizontalAlignment.RIGHT);
            dropDownList.setFocusable(false);
            dropDownList.setEnableChangeValueWithMouseWheelRotation(true);
            dropDownList.setSelectedItem(defaultDatetime.toMonth());
            dropDownList.addSelectedItemChangeListener(this.monthChangeListener);
            return dropDownList;
        }
        
        private ChangeListener<Integer> monthChangeListener = new ChangeListener<Integer>() {
            
            @Override
            protected void changed(Component<?> component, Integer changedValue, Integer valueBeforeChange) {
                CalendarPane pane = CalendarPane.this;
                pane.displayCalender();
            }
        };
        
        /**
         * カレンダーの年月をセットする。
         * 
         * @param year
         * @param month
         */
        public void setYearAndMonth(int year, int month) {
            this.yearDropDownList.setSelectedItem(year);
            this.monthDropDownList.setSelectedItem(month);
        }
        
        private GridPane gridPane;
        
        /**
         * カレンダー用のGridPaneを作成する。
         * 
         * @return 結果。
         */
        private GridPane createCalendarGridPane() {
            DatePicker control = DatePicker.this;
            GridPane gridPane = new GridPane();
            gridPane.setBorder(Border.createLine(this.borderColor, 1));
            int columnIndex = 0;
            for (DayOfWeek week : DayOfWeek.values()) {
                Label label = new Label(week.getName().substring(0, 1));
                label.setPadding(this.baseSize / 2);
                label.setFont(control.getFont());
                label.setForegroundColor(this.inactiveForeground);
                label.setTextHorizontalAlignment(HorizontalAlignment.CENTER);
                if (week != DayOfWeek.SUNDAY) {
                    label.setBorder(Border.createLine(this.borderColor, 0, 0, 0, 1));
                }
                gridPane.getChildren().add(label);
                gridPane.setGridLayout(label, columnIndex, 0);
                gridPane.getHorizontalGrowableControls().add(label);
                gridPane.getVerticalGrowableControls().add(label);
                columnIndex++;
            }
            return gridPane;
        }
        
        /**
         * カレンダーを現在選択されている年月に合わせて表示する。
         */
        private void displayCalender() {
            DatePicker control = DatePicker.this;
            if (this.yearDropDownList.getSelectedItem() == null || this.monthDropDownList.getSelectedItem() == null) {
                return;
            }
            while (this.gridPane.getChildren().size() > 7) {
                this.gridPane.getChildren().remove(this.gridPane.getChildren().get(7));
            }
            // Date already selected
            Datetime picked = control.toDatetime();
            // Determine the first date
            int year = this.yearDropDownList.getSelectedItem();
            int month = this.monthDropDownList.getSelectedItem();
            Datetime datetime = new Datetime();
            datetime.modifyYear(year);
            datetime.modifyMonth(month);
            datetime.modifyDay(1);
            while (datetime.toDayOfWeek() != DayOfWeek.SUNDAY) {
                datetime.addDay(-1);
            }
            // Create a 6 week date labels
            for (int rowNumber = 0; rowNumber < 6; rowNumber++) {
                for (int columnNumber = 0; columnNumber < 7; columnNumber++) {
                    Label label = new Label(String.valueOf(datetime.toDay()));
                    label.setPadding(this.baseSize / 2);
                    label.setFont(control.getFont());
                    label.setTextHorizontalAlignment(HorizontalAlignment.CENTER);
                    label.setInstanceForUseLater(datetime.clone());
                    if (datetime.toDayOfWeek() == DayOfWeek.SUNDAY) {
                        label.setBorder(Border.createLine(this.borderColor, 1, 0, 0, 0));
                    } else {
                        label.setBorder(Border.createLine(this.borderColor, 1, 0, 0, 1));
                    }
                    if (datetime.toMonth() != month) {
                        label.setForegroundColor(this.inactiveForeground);
                    }
                    if (datetime.eqaulsDate(picked)) {
                        label.setBackgroundColor(this.focusedBackground);
                        label.setForegroundColor(this.focusedForeground);
                    } else {
                        label.setBackgroundColor(this.getBackgroundColor());
                    }
                    label.addMouseWheelEventHandler(this.mouseWheelEventHandler);
                    label.addMouseEnteredEventHandler(this.mouseEnteredEventHandler);
                    label.addMouseExitedEventHandler(this.mouseExitedEventHandler);
                    label.addMouseClickedEventHandler(MouseButton.BUTTON1, this.mouseClickedEventHandler);
                    this.gridPane.getChildren().add(label);
                    this.gridPane.setGridLayout(label, columnNumber, rowNumber + 1);
                    this.gridPane.getHorizontalGrowableControls().add(label);
                    this.gridPane.getVerticalGrowableControls().add(label);
                    datetime.addDay(1);
                }
            }
        }
        
        private EventHandler<MouseEvent> mouseWheelEventHandler = new EventHandler<MouseEvent>() {
            
            @Override
            protected void handle(MouseEvent event) {
                CalendarPane pane = CalendarPane.this;
                Datetime datetime = new Datetime();
                datetime.modifyYear(pane.yearDropDownList.getSelectedItem());
                datetime.modifyMonth(pane.monthDropDownList.getSelectedItem());
                datetime.modifyDay(1);
                if (event.getWheelRotationAmount() > 0) {
                    datetime.addMonth(1);
                } else {
                    datetime.addMonth(-1);
                }
                int year = datetime.toYear();
                if (pane.yearDropDownList.getItems().contains(year) == false) {
                    pane.yearDropDownList.getItems().add(year);
                }
                pane.yearDropDownList.setSelectedItem(year);
                pane.monthDropDownList.setSelectedItem(datetime.toMonth());
            }
        };
        
        private EventHandler<MouseEvent> mouseClickedEventHandler = new EventHandler<MouseEvent>() {
            
            @Override
            protected void handle(MouseEvent event) {
                DatePicker control = DatePicker.this;
                CalendarPane pane = CalendarPane.this;
                Datetime datetime = event.getSource().getInstanceForUseLater();
                control.setText(datetime.toString(control.getFormatPattern()));
                control.executeActionEventHandler();
                pane.displayCalender();
            }
        };
        
        private EventHandler<MouseEvent> mouseEnteredEventHandler = new EventHandler<MouseEvent>() {
            
            @Override
            protected void handle(MouseEvent event) {
                CalendarPane pane = CalendarPane.this;
                if (event.getSource().getBackgroundColor() == pane.getBackgroundColor()) {
                    event.getSource().setBackgroundColor(pane.mouseEnterdBackground);
                }
            }
        };
        
        private EventHandler<MouseEvent> mouseExitedEventHandler = new EventHandler<MouseEvent>() {
            
            @Override
            protected void handle(MouseEvent event) {
                CalendarPane pane = CalendarPane.this;
                if (event.getSource().getBackgroundColor() == pane.mouseEnterdBackground) {
                    event.getSource().setBackgroundColor(pane.getBackgroundColor());
                }
            }
        };
    }
}
