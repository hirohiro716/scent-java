package com.hirohiro716.graphic.ui.control;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.hirohiro716.IdentifiableEnum;
import com.hirohiro716.graphic.ui.HorizontalAlignment;
import com.hirohiro716.graphic.ui.KeyCode;
import com.hirohiro716.graphic.ui.VerticalAlignment;
import com.hirohiro716.graphic.ui.event.EventHandler;
import com.hirohiro716.graphic.ui.event.MouseEvent;
import com.hirohiro716.graphic.ui.event.MouseEvent.MouseButton;

/**
 * ラベルのクラス。
 * 
 * @author hiro
 *
 */
public class Label extends LabeledControl {

    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected Label(JLabel innerInstance) {
        super(innerInstance);
        innerInstance.setFocusable(false);
    }

    /**
     * このメソッドはコンストラクタの呼び出しと同じで、新しいインスタンスを作成する。
     * 
     * @param innerInstance GUIライブラリに依存したインスタンス。
     * @return 新しいインスタンス。
     */
    public static Label newInstance(JLabel innerInstance) {
        return new Label(innerInstance);
    }
    
    /**
     * コンストラクタ。<br>
     * このラベルに表示するテキストを指定する。
     * 
     * @param text
     */
    public Label(String text) {
        this(new JLabel(text));
    }
    
    /**
     * コンストラクタ。
     */
    public Label() {
        this(new JLabel());
    }
    
    @Override
    public JLabel getInnerInstance() {
        return (JLabel) super.getInnerInstance();
    }
    
    @Override
    public String getText() {
        return this.getInnerInstance().getText();
    }
    
    @Override
    public void setTextToInnerInstance(String text) {
        this.getInnerInstance().setText(text);
    }
    
    @Override
    public HorizontalAlignment getTextHorizontalAlignment() {
        switch (this.getInnerInstance().getHorizontalAlignment()) {
        case SwingConstants.LEFT:
            return HorizontalAlignment.LEFT;
        case SwingConstants.CENTER:
            return HorizontalAlignment.CENTER;
        case SwingConstants.RIGHT:
            return HorizontalAlignment.RIGHT;
        }
        return null;
    }

    @Override
    public VerticalAlignment getTextVerticalAlignment() {
        switch (this.getInnerInstance().getVerticalAlignment()) {
        case SwingConstants.TOP:
            return VerticalAlignment.TOP;
        case SwingConstants.CENTER:
            return VerticalAlignment.CENTER;
        case SwingConstants.BOTTOM:
            return VerticalAlignment.BOTTOM;
        }
        return null;
    }

    @Override
    public void setTextHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        switch (horizontalAlignment) {
        case LEFT:
            this.getInnerInstance().setHorizontalAlignment(SwingConstants.LEFT);
            break;
        case CENTER:
            this.getInnerInstance().setHorizontalAlignment(SwingConstants.CENTER);
            break;
        case RIGHT:
            this.getInnerInstance().setHorizontalAlignment(SwingConstants.RIGHT);
            break;
        }
    }

    @Override
    public void setTextVerticalAlignment(VerticalAlignment verticalAlignment) {
        switch (verticalAlignment) {
        case TOP:
            this.getInnerInstance().setVerticalAlignment(SwingConstants.TOP);
            break;
        case CENTER:
            this.getInnerInstance().setVerticalAlignment(SwingConstants.CENTER);
            break;
        case BOTTOM:
            this.getInnerInstance().setVerticalAlignment(SwingConstants.BOTTOM);
            break;
        }
    }
    
    private Control labelFor = null;
    
    /**
     * このラベルに関連付けるコントロールを取得する。
     * 
     * @return 結果。
     */
    public Control getLabelFor() {
        return this.labelFor;
    }
    
    private EventHandler<MouseEvent> labelForEventHandler = null;
    
    /**
     * このラベルに関連付けるコントロールを指定する。<br>
     * 次の条件を満たした場合、関連付けたコントロールにフォーカスをリクエストする。<br>
     * ・ラベルをマウスのボタン1でクリックした場合<br>
     * ・Altキーとニーモニック文字のキーを押した場合
     * 
     * @param control
     */
    public void setLabelFor(Control control) {
        this.labelFor = control;
        if (this.labelForEventHandler != null) {
            this.removeEventHandler(this.labelForEventHandler);
        }
        if (control == null) {
            return;
        }
        this.getInnerInstance().setLabelFor(control.getInnerInstance());
        this.labelForEventHandler = new EventHandler<MouseEvent>() {
            @Override
            protected void handle(MouseEvent event) {
                control.requestFocus();
            }
        };
        this.addMouseClickedEventHandler(MouseButton.BUTTON1, this.labelForEventHandler);
    }

    @Override
    public KeyCode getMnemonic() {
        KeyCode keyCode = IdentifiableEnum.enumOf(this.getInnerInstance().getDisplayedMnemonic(), KeyCode.class);
        return keyCode;
    }
    
    @Override
    public void setMnemonic(KeyCode keyCode) {
        if (keyCode == null) {
            return;
        }
        this.getInnerInstance().setDisplayedMnemonic(keyCode.getKeyCodeAWT());
    }
}
