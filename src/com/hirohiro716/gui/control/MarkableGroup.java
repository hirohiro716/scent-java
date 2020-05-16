package com.hirohiro716.gui.control;

import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

/**
 * マーク可能なコントロールをグループ化するクラス。
 * 
 * @author hiro
 *
 */
public class MarkableGroup {
    
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
    }
    
    /**
     * マーク可能なコントロールをグループから削除する。
     * 
     * @param markableControl
     */
    public void remove(MarkableControl markableControl) {
        this.buttonGroup.remove(markableControl.getInnerInstance());
        this.hashMap.remove(markableControl.getInnerInstance());
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
