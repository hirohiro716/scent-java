package com.hirohiro716.gui.control;

import javax.swing.JPasswordField;

/**
 * パスワードフィールドのクラス。
 * 
 * @author hiro
 *
 */
public class PasswordField extends TextField {

    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected PasswordField(JPasswordField innerInstance) {
        super(innerInstance);
        this.setDisableInputMethod(true);
        for (ContextMenuItem item : this.getContextMenu().getContextMenuItems()) {
            switch (item.getMnemonic()) {
            case C:
            case X:
                this.getContextMenu().removeContextMenuItem(item);
                break;
            default:
                break;
            }
        }
    }
    
    /**
     * コンストラクタ。
     */
    public PasswordField() {
        this(new JPasswordField());
    }
    
    /**
     * コンストラクタ。<br>
     * このパスワードフィールドの初期値を指定する。
     * 
     * @param text
     */
    public PasswordField(String text) {
        this(new JPasswordField());
        this.setText(text);
    }
    
    @Override
    public JPasswordField getInnerInstance() {
        return (JPasswordField) super.getInnerInstance();
    }
}
