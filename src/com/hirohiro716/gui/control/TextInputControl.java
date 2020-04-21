package com.hirohiro716.gui.control;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import com.hirohiro716.StringObject;
import com.hirohiro716.gui.Component;
import com.hirohiro716.gui.HorizontalAlignment;
import com.hirohiro716.gui.Insets;
import com.hirohiro716.gui.KeyCode;
import com.hirohiro716.gui.event.ChangeListener;
import com.hirohiro716.gui.event.EventHandler;
import com.hirohiro716.gui.event.InnerInstanceCreator;
import com.hirohiro716.gui.event.KeyEvent;
import com.hirohiro716.gui.event.MouseEvent;
import com.hirohiro716.gui.event.MouseEvent.MouseButton;

/**
 * GUIのすべてのテキスト入力コントロールの抽象クラス。
 * 
 * @author hiro
 *
 */
public abstract class TextInputControl extends Control {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスと、<br>
     * コンポーネントのレイアウトに使用する、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     * @param innerInstanceForLayout 
     */
    protected TextInputControl(JTextComponent innerInstance, JComponent innerInstanceForLayout) {
        super(innerInstance, innerInstanceForLayout);
        TextInputControl control = this;
        this.contextMenu = this.createContextMenu();
        this.addMouseClickedEventHandler(MouseButton.BUTTON3, new EventHandler<MouseEvent>() {
            
            @Override
            protected void handle(MouseEvent event) {
                control.contextMenu.show(event.getX(), event.getY());
            }
        });
        this.addKeyReleasedEventHandler(new EventHandler<KeyEvent>() {
            
            @Override
            protected void handle(KeyEvent event) {
                if (event.getKeyCode() == KeyCode.F10 && event.isShiftDown()) {
                    control.contextMenu.show(control.getWidth() - control.getFont().getSize() / 2, control.getHeight() / 2);
                }
            }
        });
        this.addFocusChangeListener(new ChangeListener<Boolean>() {

            @Override
            protected void changed(Component<?> component, Boolean changedValue, Boolean previousValue) {
                if (control.isEnableSelectAllWhenFocused) {
                    control.selectAll();
                }
            }
        });
        this.getInnerInstance().setDocument(new RestrictedDocument());
    }

    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected TextInputControl(JTextComponent innerInstance) {
        this(innerInstance, innerInstance);
    }
    
    @Override
    public JTextComponent getInnerInstance() {
        return (JTextComponent) super.getInnerInstance();
    }
    
    private Insets padding = new Insets();
    
    @Override
    public Insets getPadding() {
        return this.padding;
    }

    @Override
    public void setPadding(Insets insets) {
        this.padding = insets;
        this.getInnerInstance().setMargin(this.padding.getInnerInstance());
    }
    
    /**
     * このテキスト入力コントロールに入力されている文字列を取得する。
     * 
     * @return 結果。
     */
    public String getText() {
        return this.getInnerInstance().getText();
    }
    
    /**
     * このテキスト入力コントロールに文字列をセットする。
     * 
     * @param text
     */
    public void setText(String text) {
        this.getInnerInstance().setText(text);
    }
    
    /**
     * このテキスト入力コントロールの文字列をクリアする。
     */
    public void clear() {
        this.getInnerInstance().setText(null);
    }
    
    /**
     * このテキスト入力コントロールが編集可能な場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isEditable() {
        return this.getInnerInstance().isEditable();
    }
    
    /**
     * このテキスト入力コントロールを編集可能にする場合はtrueをセットする。
     * 
     * @param isEditable
     */
    public void setEditable(boolean isEditable) {
        this.getInnerInstance().setEditable(isEditable);
    }
    
    /**
     * このテキスト入力コントロールの水平方向のテキスト表示位置を取得する。
     * 
     * @return 結果。
     */
    public abstract HorizontalAlignment getTextHorizontalAlignment();
    
    /**
     * このテキスト入力コントロールの水平方向のテキスト表示位置を指定する。
     * 
     * @param horizontalAlignment
     */
    public abstract void setTextHorizontalAlignment(HorizontalAlignment horizontalAlignment);
    
    /**
     * このテキスト入力コントロールの選択されている文字列を取得する。
     * 
     * @return 結果。
     */
    public String getSelectedText() {
        return this.getInnerInstance().getSelectedText();
    }
    
    /**
     * このテキスト入力コントロールの選択文字列の開始位置を取得する。
     * 
     * @return 結果。
     */
    public int getSelectionStart() {
        return this.getInnerInstance().getSelectionStart();
    }
    
    /**
     * このテキスト入力コントロールの選択文字列の終了位置を取得する。
     * 
     * @return 結果。
     */
    public int getSelectionEnd() {
        return this.getInnerInstance().getSelectionEnd();
    }
    
    /**
     * このテキスト入力コントロールの入力文字列の指定された範囲を選択状態にする。
     * 
     * @param selectionStart
     * @param selectionEnd
     */
    public void select(int selectionStart, int selectionEnd) {
        this.getInnerInstance().select(selectionStart, selectionEnd);
    }
    
    /**
     * このテキスト入力コントロールの入力文字列をすべて選択状態にする。
     */
    public void selectAll() {
        this.getInnerInstance().selectAll();
    }
    
    private boolean isEnableSelectAllWhenFocused = false;
    
    /**
     * このテキスト入力コントロールがフォーカスされた際に、すべての文字列を選択状態にする場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isEnableSelectAllWhenFocused() {
        return this.isEnableSelectAllWhenFocused;
    }
    
    /**
     * このテキスト入力コントロールがフォーカスされた際に、すべての文字列を選択状態にする場合はtrueをセットする。
     * 
     * @param isEnableSelectAllWhenFocused
     */
    public void setEnableSelectAllWhenFocused(boolean isEnableSelectAllWhenFocused) {
        this.isEnableSelectAllWhenFocused = isEnableSelectAllWhenFocused;
    }
    
    /**
     * 選択範囲を切り取ってクリップボードに格納する。
     */
    public void cutSelection() {
        this.getInnerInstance().cut();
    }
    
    /**
     * 選択範囲をコピーしてクリップボードに格納する。
     */
    public void copySelection() {
        this.getInnerInstance().copy();
    }
    
    /**
     * 選択範囲にクリップボードの値を貼り付ける。
     */
    public void pasteToSelection() {
        this.getInnerInstance().paste();
    }

    /**
     * このコントロールの入力文字列が変更された際のリスナーを追加する。
     * 
     * @param changeListener
     */
    public void addTextChangeListener(ChangeListener<String> changeListener) {
        TextInputControl control = this;
        DocumentListener innerInstance = changeListener.createInnerInstance(control, new InnerInstanceCreator<>() {

            @Override
            public DocumentListener create() {
                return new DocumentListener() {
                    
                    @Override
                    public void removeUpdate(DocumentEvent event) {
                        changeListener.executeWhenChanged(control, control.getText());
                    }
                    
                    @Override
                    public void insertUpdate(DocumentEvent event) {
                        changeListener.executeWhenChanged(control, control.getText());
                    }
                    
                    @Override
                    public void changedUpdate(DocumentEvent event) {
                        changeListener.executeWhenChanged(control, control.getText());
                    }
                };
            }
        });
        this.getInnerInstance().getDocument().addDocumentListener(innerInstance);
    }

    @Override
    public void removeChangeListener(ChangeListener<?> changeListener) {
        super.removeChangeListener(changeListener);
        for (Object innerInstance : changeListener.getInnerInstances(this)) {
            if (innerInstance instanceof DocumentListener) {
                this.getInnerInstance().getDocument().removeDocumentListener((DocumentListener) innerInstance);
            }
        }
    }

    private int maximumLength = -1;
    
    /**
     * このテキスト入力コントロールに入力できる最大文字数を取得する。
     * 
     * @return 結果。
     */
    public int getMaxLength() {
        return this.maximumLength;
    }
    
    /**
     * このテキスト入力コントロールに入力できる文字数を制限する。初期値は-1の無制限。
     * 
     * @param maximumLength
     */
    public void setMaxLength(int maximumLength) {
        this.maximumLength = maximumLength;
    }
    
    private Map<Pattern, Boolean> inputLimitRegexPatterns = new HashMap<>();
    
    /**
     * 文字列の入力を制限している正規表現のパターンをすべて取得する。
     * 
     * @return 結果。
     */
    public Pattern[] getLimitByRegexPatterns() {
        return this.inputLimitRegexPatterns.keySet().toArray(new Pattern[] {});
    }
    
    /**
     * 文字列の入力を制限している正規表現のパターンが、逆に評価するようセットされている場合はtrueを返す。<br>
     * 指定されたパターンが存在しない場合はnullを返す。
     * 
     * @param pattern
     * @return 結果。
     */
    public Boolean isInverseLimitByRegex(Pattern pattern) {
        return this.inputLimitRegexPatterns.get(pattern);
    }
    
    /**
     * 文字列の入力を、追加されている正規表現すべてにマッチする文字列に制限する。<br>
     * 反転するように指定した場合、その正規表現のマッチは逆に評価される。
     * 
     * @param pattern
     * @param isInverse
     */
    public void addLimitByRegex(Pattern pattern, boolean isInverse) {
        this.inputLimitRegexPatterns.put(pattern, isInverse);
    }
    
    /**
     * 文字列の入力を制限している正規表現を取り除く。
     * 
     * @param pattern
     */
    public void removeLimitByRegex(Pattern pattern) {
        this.inputLimitRegexPatterns.remove(pattern);
    }
    
    /**
     * 文字列の入力を制限している正規表現をすべて削除する。
     */
    public void clearLimitByRegex() {
        this.inputLimitRegexPatterns.clear();
    }

    private ContextMenu contextMenu;
    
    /**
     * このテキスト入力コントロールのコンテキストメニューを取得する。
     * 
     * @return 結果。
     */
    public ContextMenu getContextMenu() {
        return this.contextMenu;
    }
    
    /**
     * このテキスト入力コントロールの入力値を制限するクラス。
     * 
     * @author hiro
     *
     */
    @SuppressWarnings("serial")
    private class RestrictedDocument extends PlainDocument {
        
        @Override
        public void insertString(int offset, String string, AttributeSet attributeSet) throws BadLocationException {
            TextInputControl control = TextInputControl.this;
            StringObject input = new StringObject(string);
            StringObject pass = new StringObject();
            for (String one : input) {
                boolean isPass = true;
                for (Pattern pattern : control.getLimitByRegexPatterns()) {
                    if (pattern.matcher(one).find() == control.isInverseLimitByRegex(pattern)) {
                        isPass = false;
                        break;
                    }
                }
                if (isPass) {
                    pass.append(one);
                }
            }
            if (control.getMaxLength() > -1) {
                pass.extract(0, control.getMaxLength() - control.getText().length());
            }
            super.insertString(offset, pass.toString(), attributeSet);
        }
    }
    
    /**
     * テキスト入力コントロール用のコンテキストメニューを作成する。
     * 
     * @return 結果。
     */
    private ContextMenu createContextMenu() {
        TextInputControl control = this;
        ContextMenuItem cut = new ContextMenuItem("切り取り(X)");
        cut.setMnemonic(KeyCode.X);
        cut.setAction(new Runnable() {
            
            @Override
            public void run() {
                control.cutSelection();
            }
        });
        ContextMenuItem copy = new ContextMenuItem("コピー(C)");
        copy.setMnemonic(KeyCode.C);
        copy.setAction(new Runnable() {
            
            @Override
            public void run() {
                control.copySelection();
            }
        });
        ContextMenuItem paste = new ContextMenuItem("貼り付け(P)");
        paste.setMnemonic(KeyCode.P);
        paste.setAction(new Runnable() {
            
            @Override
            public void run() {
                control.pasteToSelection();
            }
        });
        ContextMenu menu = new ContextMenu(control) {
            
            @Override
            public void show(int xLocationOnInvoker, int yLocationOnInvoker) {
                super.show(xLocationOnInvoker, yLocationOnInvoker);
                StringObject selectedText = new StringObject(control.getSelectedText());
                cut.setDisabled(!control.isEditable() || selectedText.length() == 0);
                copy.setDisabled(selectedText.length() == 0);
                paste.setDisabled(!control.isEditable());
            }
        };
        menu.addContextMenuItem(cut);
        menu.addContextMenuItem(copy);
        menu.addContextMenuItem(paste);
        return menu;
    }
}
