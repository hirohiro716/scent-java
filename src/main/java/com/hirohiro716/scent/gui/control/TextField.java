package com.hirohiro716.scent.gui.control;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.hirohiro716.scent.RoundNumber;
import com.hirohiro716.scent.StringObject;
import com.hirohiro716.scent.gui.HorizontalAlignment;
import com.hirohiro716.scent.gui.event.ActionEvent;
import com.hirohiro716.scent.gui.event.EventHandler;
import com.hirohiro716.scent.gui.event.InnerInstanceCreator;

/**
 * テキストフィールドのクラス。
 */
public class TextField extends TextInputControl {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected TextField(JTextField innerInstance) {
        super(innerInstance);
        this.addLimitByRegex(Pattern.compile("(\n|\r)"), true);
        this.addActionEventHandler(new EventHandler<ActionEvent>() {

            @Override
            protected void handle(ActionEvent event) {
                TextField textField = TextField.this;
                if (textField.isFormulaEvaluatable() == false) {
                    return;
                }
                String formula = StringObject.newInstance(textField.getText()).toString();
                if (formula.matches("^[0-9.+\\-*/ ]{3,}$") == false) {
                    return;
                }
                StringObject operators = StringObject.newInstance(formula).extract("[+\\-*/]");
                String[] stringValues = StringObject.newInstance(formula).split("[+\\-*/]");
                if (operators.length() == 0 || operators.length() != stringValues.length - 1) {
                    return;
                }
                List<Double> values = new ArrayList<>();
                for (String stringValue: stringValues) {
                    Double value = StringObject.newInstance(stringValue).replace(" ", "").toDouble();
                    if (value == null) {
                        return;
                    }
                    values.add(value);
                }
                for (Integer index = 0; index < operators.length(); index++) {
                    String operator = operators.clone().extract(index, index + 1).toString();
                    Double result = null;
                    switch (operator) {
                        case "*":
                            result = values.get(index) * values.get(index + 1);
                            break;
                        case "/":
                            result = values.get(index) / values.get(index + 1);
                            break;
                    }
                    if (result != null) {
                        values.set(index, result);
                        values.remove(index + 1);
                        StringObject originalOperators = operators.clone();
                        operators.set(originalOperators.clone().extract(0, index));
                        operators.append(originalOperators.clone().extract(index + 1));
                        index--;
                    }
                }
                double result = values.get(0);
                for (Integer index = 0; index < operators.length(); index++) {
                    String operator = operators.clone().extract(index, index + 1).toString();
                    switch (operator) {
                        case "+":
                            result += values.get(index + 1);
                            break;
                        case "-":
                            result -= values.get(index + 1);
                            break;
                    }
                }
                textField.setText(StringObject.newInstance(textField.formulaRoundNumber.calculate(result, textField.formulaRoundingDigit)).removeMeaninglessDecimalPoint().toString());
                textField.isFormulaEvaluated = true;
            }
        });
    }

    private boolean isFormulaEvaluated = false;
    
    /**
     * コンストラクタ。<br>
     * このテキストフィールドの初期値を指定する。
     * 
     * @param text
     */
    public TextField(String text) {
        this(new JTextField());
        this.setText(text);
    }
    
    /**
     * コンストラクタ。
     */
    public TextField() {
        this((String) null);
    }
    
    @Override
    public JTextField getInnerInstance() {
        return (JTextField) super.getInnerInstance();
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

    private boolean isFormulaEvaluatable = false;

    /**
     * このコントロールで計算式の評価が可能な場合はtrueを返す。
     * 
     * @return
     */
    public boolean isFormulaEvaluatable() {
        return this.isFormulaEvaluatable;
    }
    
    /**
     * このコントロールで計算式の評価を可能にする場合はtrueをセットする。初期値はfalse。
     * 
     * @param isFormulaEvaluatable
     */
    public void setFormulaEvaluatable(boolean isFormulaEvaluatable) {
        this.isFormulaEvaluatable = isFormulaEvaluatable;
    }
    
    private RoundNumber formulaRoundNumber = RoundNumber.ROUND;

    /**
     * このコントロールで計算式の評価に使用する端数処理の列挙型を取得する。
     * 
     * @return
     */
    public RoundNumber getFormulaRoundNumber() {
        return this.formulaRoundNumber;
    }
    
    /**
     * このコントロールで計算式の評価に使用する端数処理の列挙型をセットする。初期値は四捨五入。
     * 
     * @param roundNumber
     */
    public void setFormulaRoundNumber(RoundNumber roundNumber) {
        this.formulaRoundNumber = roundNumber;
    }
    
    private int formulaRoundingDigit = 0;

    /**
     * このコントロールで計算式の評価に使用する端数処理の桁数を取得する。
     * 
     * @return
     */
    public int getFormulaRoundingDigit() {
        return this.formulaRoundingDigit;
    }

    /**
     * このコントロールで計算式の評価に使用する端数処理の桁数をセットする。初期値は0。
     * 
     * @param digit
     */
    public void setFormulaRoundingDigit(int digit) {
        this.formulaRoundingDigit = digit;
    }
    
    /**
     * このテキストフィールドでEnterキーが押された際のイベントハンドラを追加する。
     * 
     * @param eventHandler
     */
    public void addActionEventHandler(EventHandler<ActionEvent> eventHandler) {
        TextField textField = this;
        KeyListener innerInstance = eventHandler.createInnerInstance(textField, new InnerInstanceCreator<>() {

            @Override
            public KeyListener create() {
                return new KeyAdapter() {
                    
                    private boolean isPressed = false;
                    
                    @Override
                    public void keyPressed(KeyEvent event) {
                        if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                            this.isPressed = true;
                            textField.isFormulaEvaluated = false;
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent event) {
                        if (event.getKeyCode() == KeyEvent.VK_ENTER && this.isPressed) {
                            if (textField.isFormulaEvaluated == false) {
                                eventHandler.executeWhenControlEnabled(new ActionEvent(textField, event));
                            }
                        }
                        this.isPressed = false;
                    }
                };
            }
        });
        this.getInnerInstance().addKeyListener(innerInstance);
    }
    
    @Override
    public void removeEventHandler(EventHandler<?> eventHandler) {
        super.removeEventHandler(eventHandler);
        for (Object innerInstance: eventHandler.getInnerInstances(this)) {
            if (innerInstance instanceof KeyListener) {
                this.getInnerInstance().removeKeyListener((KeyListener) innerInstance);
            }
        }
    }
}
