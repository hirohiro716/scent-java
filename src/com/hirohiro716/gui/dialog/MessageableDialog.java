package com.hirohiro716.gui.dialog;

import java.util.ArrayList;
import java.util.List;

import com.hirohiro716.gui.Frame;
import com.hirohiro716.gui.VerticalAlignment;
import com.hirohiro716.gui.control.AnchorPane;
import com.hirohiro716.gui.control.Button;
import com.hirohiro716.gui.control.Control;
import com.hirohiro716.gui.control.HorizontalPane;
import com.hirohiro716.gui.control.Label;

/**
 * メッセージダイアログの抽象クラス。
 * 
 * @author hiro
 * 
 * @param <R> このダイアログの表示結果の型。
 */
public abstract class MessageableDialog<R> extends TitledDialog<R> {
    
    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner
     */
    public MessageableDialog(Frame<?> owner) {
        super(owner);
    }
    
    private Label labelOfMessage;
    
    /**
     * このダイアログのメッセージラベルを取得する。
     * 
     * @return 結果。
     */
    public Label getLabelOfMessage() {
        return this.labelOfMessage;
    }
    
    protected Control[] createControls() {
        List<Control> controls = new ArrayList<>();
        // Message
        this.labelOfMessage = new Label();
        this.labelOfMessage.setTextVerticalAlignment(VerticalAlignment.TOP);
        this.labelOfMessage.setWrapText(true);
        controls.add(this.labelOfMessage);
        // Input control
        Control inputControl = this.createInputControl();
        if (inputControl != null) {
            controls.add(inputControl);
        }
        // Buttons
        HorizontalPane paneButton = new HorizontalPane();
        paneButton.setSpacing(5);
        Button[] buttons = this.createButtons();
        for (Button button : buttons) {
            paneButton.getChildren().add(button);
        }
        AnchorPane anchorPaneButton = new AnchorPane();
        anchorPaneButton.getChildren().add(paneButton);
        anchorPaneButton.setAnchor(paneButton, null, 0, null, null);
        controls.add(anchorPaneButton);
        return controls.toArray(new Control[] {});
    }
    
    /**
     * このダイアログに表示する入力コントロールを作成する。<br>
     * このメソッドはスーバークラスのコンストラクタで自動的に呼び出される。
     * 
     * @return 結果。
     */
    protected abstract Control createInputControl();
    
    /**
     * このダイアログに表示するボタンを作成する。<br>
     * このメソッドはスーバークラスのコンストラクタで自動的に呼び出される。
     * 
     * @return 結果。
     */
    protected abstract Button[] createButtons();
    
    private String message;
    
    /**
     * このダイアログに表示するメッセージを取得する。
     * 
     * @return 結果。
     */
    public String getMessage() {
        return this.message;
    }
    
    /**
     * このダイアログに表示するメッセージをセットする。
     * 
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    protected void processBeforeShow() {
        super.processBeforeShow();
        this.getVerticalPaneOfControls().getGrowableControls().add(this.labelOfMessage);
        this.labelOfMessage.setText(this.getMessage());
    }
    
    /**
     * メッセージダイアログで押されたボタンの列挙型。
     * 
     * @author hiro
     *
     */
    public enum ResultButton {
        /**
         * OK。
         */
        OK,
        /**
         * キャンセル。
         */
        CANCEL,
        /**
         * はい。
         */
        YES,
        /**
         * いいえ。
         */
        NO,
    }
}
