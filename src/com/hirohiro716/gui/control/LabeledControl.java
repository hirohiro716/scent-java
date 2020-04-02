package com.hirohiro716.gui.control;

import javax.swing.JComponent;

import com.hirohiro716.StringObject;
import com.hirohiro716.gui.HorizontalAlignment;
import com.hirohiro716.gui.KeyCode;
import com.hirohiro716.gui.VerticalAlignment;

/**
 * GUIのすべてのラベル付きコントロールの抽象クラス。
 * 
 * @author hiro
 *
 */
public abstract class LabeledControl extends Control {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected LabeledControl(JComponent innerInstance) {
        super(innerInstance);
        this.setDisableInputMethod(true);
    }

    /**
     * このコントロールに表示されている文字列を取得する。
     * 
     * @return 結果。
     */
    public abstract String getText();
    
    /**
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスに対して表示文字列をセットする。
     * 
     * @param text
     */
    protected abstract void setTextToInnerInstance(String text);
    
    /**
     * このコントロールに表示する文字列をセットする。
     * 
     * @param text
     */
    public void setText(String text) {
        StringObject html = new StringObject(text);
        html.replace("&", "&amp;");
        html.replace("<", "&lt;");
        html.replace(">", "&gt;");
        html.replaceCR("<br>");
        html.replaceLF("<br>");
        html.replaceCRLF("<br>");
        if (this.isWrapText) {
            html.insert("<html>", 0);
            html.append("</html>");
        }
        this.setTextToInnerInstance(html.toString());
    }
    
    private boolean isWrapText = false;
    
    /**
     * このコントロールに表示する文字列を折り返して表示する場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isWrapText() {
        return this.isWrapText;
    }
    
    /**
     * このコントロールに表示する文字列を折り返して表示する場合はtrueをセットする。初期値はfalse。
     * 
     * @param isWrapText
     */
    public void setWrapText(boolean isWrapText) {
        this.isWrapText = isWrapText;
    }
    
    /**
     * このコントロールの水平方向のテキスト表示位置を取得する。
     * 
     * @return 結果。
     */
    public abstract HorizontalAlignment getTextHorizontalAlignment();

    /**
     * このコントロールの垂直方向のテキスト表示位置を取得する。
     * 
     * @return 結果。
     */
    public abstract VerticalAlignment getTextVerticalAlignment();

    /**
     * このコントロールの水平方向のテキスト表示位置を指定する。
     * 
     * @param horizontalAlignment
     */
    public abstract void setTextHorizontalAlignment(HorizontalAlignment horizontalAlignment);

    /**
     * このコントロールの垂直方向のテキスト表示位置を指定する。
     * 
     * @param verticalAlignment
     */
    public abstract void setTextVerticalAlignment(VerticalAlignment verticalAlignment);
    
    /**
     * このコントロールのテキスト表示位置を指定する。
     * 
     * @param horizontalAlignment
     * @param verticalAlignment
     */
    public void setTextAlignment(HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) {
        this.setTextHorizontalAlignment(horizontalAlignment);
        this.setTextVerticalAlignment(verticalAlignment);
    }
    
    /**
     * このコントロールのニーモニックを取得する。
     * 
     * @return 結果。
     */
    public abstract KeyCode getMnemonic();
    
    /**
     * このコントロールにニーモニックを設定する。
     * 
     * @param keyCode
     */
    public abstract void setMnemonic(KeyCode keyCode);
}
