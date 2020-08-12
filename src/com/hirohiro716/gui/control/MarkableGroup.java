package com.hirohiro716.gui.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

import com.hirohiro716.gui.Component;
import com.hirohiro716.gui.event.ChangeListener;

/**
 * マーク可能なコントロールをグループ化するクラス。
 * 
 * @author hiro
 *
 */
public class MarkableGroup {
    
    private List<ChangeListener<MarkableControl>> markedControlChangeListener = new ArrayList<>();
    
    /**
     * マークされているコントロールが変更された際のリスナーを追加する。
     * 
     * @param changeListener
     */
    public void addMarkedControlChangeListener(ChangeListener<MarkableControl> changeListener) {
        this.markedControlChangeListener.add(changeListener);
    }

    /**
     * マークされているコントロールが変更された際のリスナーを削除する。
     * 
     * @param changeListener
     */
    public void removeMarkedControlChangeListener(ChangeListener<MarkableControl> changeListener) {
        this.markedControlChangeListener.remove(changeListener);
    }
    
    private ChangeListener<Boolean> markedChangeListener = new ChangeListener<Boolean>() {

        @Override
        protected void changed(Component<?> component, Boolean changedValue, Boolean previousValue) {
            MarkableGroup group = MarkableGroup.this;
            if (changedValue) {
                for (ChangeListener<MarkableControl> changeListener : group.markedControlChangeListener) {
                    changeListener.executeWhenChanged(component, (MarkableControl) component);
                }
            }
        }
    };
    
    private ButtonGroup buttonGroup = new ButtonGroup();
    
    private Map<AbstractButton, MarkableControl> hashMap = new HashMap<>();
    
    /**
     * このグループに所属するマーク可能なコントロールをすべて取得する。
     * 
     * @return 結果。
     */
    public MarkableControl[] getMarkableControls() {
        return this.hashMap.values().toArray(new MarkableControl[] {});
    }
    
    /**
     * マーク可能なコントロールをグループに追加する。
     * 
     * @param markableControl
     */
    public void add(MarkableControl markableControl) {
        this.buttonGroup.add(markableControl.getInnerInstance());
        this.hashMap.put(markableControl.getInnerInstance(), markableControl);
        markableControl.addMarkChangeListener(this.markedChangeListener);
    }
    
    /**
     * マーク可能なコントロールをグループから削除する。
     * 
     * @param markableControl
     */
    public void remove(MarkableControl markableControl) {
        this.buttonGroup.remove(markableControl.getInnerInstance());
        this.hashMap.remove(markableControl.getInnerInstance());
        markableControl.removeChangeListener(this.markedChangeListener);
    }
    
    /**
     * マークされているコントロールを取得する。存在しない場合はnullを返す。
     * 
     * @return 結果。
     */
    public MarkableControl getMarkedControl() {
        for (MarkableControl control : this.getMarkableControls()) {
            if (control.isMarked()) {
                return control;
            }
        }
        return null;
    }
}
