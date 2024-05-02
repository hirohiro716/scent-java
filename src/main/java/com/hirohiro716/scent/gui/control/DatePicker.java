package com.hirohiro716.scent.gui.control;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hirohiro716.scent.Dimension;
import com.hirohiro716.scent.StringObject;
import com.hirohiro716.scent.datetime.Datetime;
import com.hirohiro716.scent.datetime.Datetime.DayOfWeek;
import com.hirohiro716.scent.graphic.ColorCreator;
import com.hirohiro716.scent.gui.Border;
import com.hirohiro716.scent.gui.Component;
import com.hirohiro716.scent.gui.Frame;
import com.hirohiro716.scent.gui.GUI;
import com.hirohiro716.scent.gui.HorizontalAlignment;
import com.hirohiro716.scent.gui.Popup;
import com.hirohiro716.scent.gui.VerticalAlignment;
import com.hirohiro716.scent.gui.event.ChangeListener;
import com.hirohiro716.scent.gui.event.EventHandler;
import com.hirohiro716.scent.gui.event.KeyEvent;
import com.hirohiro716.scent.gui.event.MouseEvent;
import com.hirohiro716.scent.gui.event.MouseEvent.MouseButton;

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
            protected void changed(Component<?> component, Dimension changedValue, Dimension previousValue) {
                textField.showPopup();
            }
        });
        this.addMousePressedEventHandler(MouseButton.BUTTON1, new EventHandler<MouseEvent>() {

            @Override
            protected void handle(MouseEvent event) {
                if (textField.popup != null && textField.popup.isVisible()) {
                    textField.hidePopup();
                } else {
                    textField.showPopup();
                }
            }
        });
        this.addTextChangeListener(new ChangeListener<String>() {

            @Override
            protected void changed(Component<?> component, String changedValue, String previousValue) {
                Datetime datetime = textField.toDatetime();
                textField.executeDateChangeListener(datetime);
            }
        });
        this.addFocusChangeListener(new ChangeListener<Boolean>() {
            
            @Override
            protected void changed(Component<?> component, Boolean changedValue, Boolean previousValue) {
                if (changedValue == false) {
                    Datetime datetime = textField.toDatetime();
                    textField.clear();
                    if (datetime != null) {
                        textField.setText(datetime.toString(textField.dateFormat));
                    }
                    textField.hidePopup();
                } else {
                    textField.selectAll();
                }
            }
        });
        this.addKeyPressedEventHandler(new EventHandler<KeyEvent>() {
            
            @Override
            protected void handle(KeyEvent event) {
                switch (event.getKeyCode()) {
                case ENTER:
                case TAB:
                    Datetime datetime = textField.toDatetime();
                    textField.clear();
                    if (datetime != null) {
                        textField.setText(datetime.toString(textField.dateFormat));
                    }
                    textField.hidePopup();
                    break;
                case ESCAPE:
                    textField.hidePopup();
                    break;
                default:
                    break;
                }
            }
        });
        this.setDisabledInputMethod(true);
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

    private List<ChangeListener<Date>> dateChangeListeners = new ArrayList<>();
    
    /**
     * このコントロールの入力文字列が示す日付が変更された際のリスナーを追加する。
     * 
     * @param changeListener
     */
    public void addDateChangeListener(ChangeListener<Date> changeListener) {
        this.dateChangeListeners.add(changeListener);
    }
    
    @Override
    public void removeChangeListener(ChangeListener<?> changeListener) {
        super.removeChangeListener(changeListener);
        this.dateChangeListeners.remove(changeListener);
    }
    
    private Date previousDate = null;
    
    /**
     * このコントロールの入力文字列が示す日付が変更された際のリスナーを実行する。
     */
    private void executeDateChangeListener(Datetime currentDatetime) {
        if (this.dateChangeListeners == null) {
            return;
        }
        if (this.previousDate == null && currentDatetime == null) {
            return;
        }
        if (this.previousDate == null || Datetime.newInstance(this.previousDate).eqaulsDate(currentDatetime) == false) {
            Date changed = null;
            if (currentDatetime != null) {
                changed = currentDatetime.getDate();
            }
            for (ChangeListener<Date> changeListener : this.dateChangeListeners) {
                changeListener.execute(this, changed, this.previousDate);
            }
            this.previousDate = changed;
        }
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
        Datetime datetime = new Datetime();
        datetime.modifyHour(0);
        datetime.modifyMinute(0);
        datetime.modifySecond(0);
        datetime.modifyMillisecond(0);
        StringObject input = new StringObject(this.getText());
        input.replace("/", "-");
        String[] values = input.split("-");
        switch (values.length) {
        case 1:
            try {
                Integer day = StringObject.newInstance(values[0]).toInteger();
                if (day >= 1 && day <= 31) {
                    datetime.modifyDay(day);
                    return datetime;
                }
            } catch (Exception exception) {
            }
            break;
        case 2:
            try {
                datetime.modifyMonth(StringObject.newInstance(values[0]).toInteger());
                datetime.modifyDay(StringObject.newInstance(values[1]).toInteger());
                return datetime;
            } catch (Exception exception) {
            }
            break;
        case 3:
            try {
                StringObject thisYear = new StringObject(datetime.getYear());
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
                return datetime;
            } catch (Exception exception) {
            }
            break;
        }
        try {
            return Datetime.newInstance(input.toString(), this.dateFormat);
        } catch (ParseException exception) {
        }
        return null;
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
                protected void changed(Component<?> component, Point changedValue, Point previousValue) {
                    if (control.getPopup().isVisible()) {
                        control.showPopup();
                    }
                }
            });
            frame.addSizeChangeListener(new ChangeListener<Dimension>() {
                
                @Override
                protected void changed(Component<?> component, Dimension changedValue, Dimension previousValue) {
                    if (control.getPopup().isVisible()) {
                        control.showPopup();
                    }
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
        this.calendarPane.setYearAndMonth(datetime.getYear(), datetime.getMonth());
        this.popup.setX((int) this.getLocationOnScreen().getX() + 3);
        int y = (int) this.getLocationOnScreen().getY() + this.getHeight() + 3;
        Rectangle rectangle = GUI.getMaximumWindowBounds(this.findPlacedGraphicsDevice());
        if (y + this.popup.getHeight() > rectangle.getY() + rectangle.getHeight()) {
            y = (int) this.getLocationOnScreen().getY() - this.popup.getHeight() - 3;
        }
        this.popup.setY(y);
        this.popup.setMinimumWidth(this.getFont().getSize() * 24);
        if (this.isFocused() && this.isDisabledPopup == false && this.isVisible() && this.isEditable() && this.isDisabled() == false) {
            this.popup.show();
            this.calendarPane.displayCalender();
        }
    }
    
    /**
     * このコントロールのカレンダー用のポップアップを隠す。
     */
    public void hidePopup() {
        if (this.getPopup() != null && this.getPopup().isVisible()) {
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
            this.borderColor = GUI.getBorderColor();
            this.focusedBackground = new Color(control.getInnerInstance().getSelectionColor().getRGB());
            this.focusedForeground = new Color(control.getInnerInstance().getSelectedTextColor().getRGB());
            this.todayForeground = new Color(control.getInnerInstance().getSelectionColor().getRGB());
            this.inactiveForeground = ColorCreator.createTransparent(control.getInnerInstance().getDisabledTextColor(), 0.5);
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
        
        private Color todayForeground;
        
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
            DropDownList<Integer> dropDownList = new DropDownList<>();
            dropDownList.getItems().addAll(Datetime.createYearsList(10));
            dropDownList.setFont(control.getFont());
            dropDownList.setFocusable(false);
            dropDownList.setEnableChangeValueWithMouseWheelRotation(true);
            dropDownList.setSelectedItem(defaultDatetime.getYear());
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
            DropDownList<Integer> dropDownList = new DropDownList<>();
            dropDownList.getItems().addAll(Datetime.createMonthsList());
            dropDownList.setFont(control.getFont());
            dropDownList.setFocusable(false);
            dropDownList.setEnableChangeValueWithMouseWheelRotation(true);
            dropDownList.setSelectedItem(defaultDatetime.getMonth());
            dropDownList.addSelectedItemChangeListener(this.monthChangeListener);
            return dropDownList;
        }
        
        private ChangeListener<Integer> monthChangeListener = new ChangeListener<Integer>() {
            
            @Override
            protected void changed(Component<?> component, Integer changedValue, Integer previousValue) {
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
            this.gridPane.setDisabled(false);
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
                Datetime today = new Datetime();
                for (int columnNumber = 0; columnNumber < 7; columnNumber++) {
                    Label label = new Label(String.valueOf(datetime.getDay()));
                    label.setPadding(this.baseSize / 2);
                    label.setFont(control.getFont());
                    label.setTextHorizontalAlignment(HorizontalAlignment.CENTER);
                    label.setInstanceForUseLater(datetime.clone());
                    if (datetime.toDayOfWeek() == DayOfWeek.SUNDAY) {
                        label.setBorder(Border.createLine(this.borderColor, 1, 0, 0, 0));
                    } else {
                        label.setBorder(Border.createLine(this.borderColor, 1, 0, 0, 1));
                    }
                    if (datetime.getMonth() != month) {
                        label.setForegroundColor(this.inactiveForeground);
                    } else {
                        if (datetime.eqaulsDate(picked)) {
                            label.setBackgroundColor(this.focusedBackground);
                            label.setForegroundColor(this.focusedForeground);
                        } else {
                            label.setBackgroundColor(this.getBackgroundColor());
                            if (datetime.eqaulsDate(today)) {
                                label.setForegroundColor(this.todayForeground);
                            } else {
                                label.setForegroundColor(this.getForegroundColor());
                            }
                        }
                    }
                    label.addMouseWheelEventHandler(this.mouseWheelEventHandler);
                    label.addMouseEnteredEventHandler(this.mouseEnteredEventHandler);
                    label.addMouseExitedEventHandler(this.mouseExitedEventHandler);
                    label.addMousePressedEventHandler(MouseButton.BUTTON1, this.mousePressedEventHandler);
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
                int year = datetime.getYear();
                if (pane.yearDropDownList.getItems().contains(year) == false) {
                    pane.yearDropDownList.getItems().add(year);
                }
                pane.yearDropDownList.setSelectedItem(year);
                pane.monthDropDownList.setSelectedItem(datetime.getMonth());
            }
        };
        
        private EventHandler<MouseEvent> mousePressedEventHandler = new EventHandler<MouseEvent>() {
            
            @Override
            protected void handle(MouseEvent event) {
                DatePicker control = DatePicker.this;
                CalendarPane pane = CalendarPane.this;
                pane.gridPane.setDisabled(true);
                Datetime datetime = event.getSource().getInstanceForUseLater();
                control.setText(datetime.toString(control.getFormatPattern()));
                control.hidePopup();
            }
        };

        private EventHandler<MouseEvent> mouseEnteredEventHandler = new EventHandler<MouseEvent>() {

            @Override
            protected void handle(MouseEvent event) {
                CalendarPane pane = CalendarPane.this;
                Control control = event.getSource();
                if (control.getBackgroundColor() == pane.getBackgroundColor()) {
                    control.setBackgroundColor(pane.mouseEnterdBackground);
                    control.getParent().updateDisplay();
                }
            }
        };

        private EventHandler<MouseEvent> mouseExitedEventHandler = new EventHandler<MouseEvent>() {

            @Override
            protected void handle(MouseEvent event) {
                CalendarPane pane = CalendarPane.this;
                Control control = event.getSource();
                if (control.getBackgroundColor() == pane.mouseEnterdBackground) {
                    control.setBackgroundColor(pane.getBackgroundColor());
                }
            }
        };
    }
}
