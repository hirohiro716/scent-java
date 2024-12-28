package com.hirohiro716.scent.gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.SwingConstants;
import com.hirohiro716.scent.IdentifiableEnum;
import com.hirohiro716.scent.gui.HorizontalAlignment;
import com.hirohiro716.scent.gui.KeyCode;
import com.hirohiro716.scent.gui.VerticalAlignment;
import com.hirohiro716.scent.gui.event.ChangeListener;
import com.hirohiro716.scent.gui.event.InnerInstanceCreator;

/**
 * GUIのすべてのマーク可能なコントロールの抽象クラス。
 * 
 * @author hiro
*/
public abstract class MarkableControl extends LabeledControl {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected MarkableControl(AbstractButton innerInstance) {
        super(innerInstance, innerInstance.getText());
    }
    
    @Override
    public AbstractButton getInnerInstance() {
        return (AbstractButton) super.getInnerInstance();
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
    
    @Override
    public KeyCode getMnemonic() {
        KeyCode keyCode = IdentifiableEnum.enumOf(this.getInnerInstance().getMnemonic(), KeyCode.class);
        return keyCode;
    }
    
    @Override
    public void setMnemonic(KeyCode keyCode) {
        if (keyCode == null) {
            return;
        }
        this.getInnerInstance().setMnemonic(keyCode.getKeyCodeAWT());
    }
    
    /**
     * このコントロールがマークされている場合はtrueを返す。
     * 
     * @return
     */
    public boolean isMarked() {
        return this.getInnerInstance().isSelected();
    }
    
    /**
     * このコントロールをマークする。
     * 
     * @param isMarked
     */
    public void setMarked(boolean isMarked) {
        this.getInnerInstance().setSelected(isMarked);
        for (ChangeListener<Boolean> changeListener : this.markChangeListeners) {
            changeListener.executeWhenChanged(this, isMarked);
        }
    }
    
    private List<ChangeListener<Boolean>> markChangeListeners = new ArrayList<>();
    
    /**
     * このコントロールのマーク状態が変更された際のリスナーを追加する。
     * 
     * @param changeListener
     */
    public void addMarkChangeListener(ChangeListener<Boolean> changeListener) {
        MarkableControl control = this;
        this.markChangeListeners.add(changeListener);
        ActionListener innerInstance = changeListener.createInnerInstance(control, new InnerInstanceCreator<>() {

            @Override
            public ActionListener create() {
                return new ActionListener() {
                    
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        changeListener.executeWhenChanged(control, control.isMarked());
                    }
                };
            }
        });
        this.getInnerInstance().addActionListener(innerInstance);
    }
    
    @Override
    public void removeChangeListener(ChangeListener<?> changeListener) {
        super.removeChangeListener(changeListener);
        for (Object innerInstance : changeListener.getInnerInstances(this)) {
            if (innerInstance instanceof ActionListener) {
                this.getInnerInstance().removeActionListener((ActionListener) innerInstance);
            }
        }
        this.markChangeListeners.remove(changeListener);
    }

    /**
     * このコントロールのマーク状態が変更された際のリスナーを実行する。
     */
    protected void executeMarkChangeListener() {
        for (ChangeListener<Boolean> changeListener : this.markChangeListeners) {
            changeListener.executeWhenChanged(this, this.isMarked());
        }
    }
}
