package com.hirohiro716.scent.gui.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

import com.hirohiro716.scent.gui.Component;
import com.hirohiro716.scent.gui.event.ChangeListener;
import com.hirohiro716.scent.gui.event.EventHandler;
import com.hirohiro716.scent.gui.event.KeyEvent;
import com.hirohiro716.scent.gui.event.MouseEvent;

/**
 * マーク可能なコントロールをグループ化するクラス。
 * 
 * @author hiro
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
    
    private MarkableControl previousMarkedControl = null;
    
    /**
     * 登録されているコントロールマーク変更時のリスナーを実行する。
     * 
     * @param eventSourceControl
     */
    private void executeMarkedChangeListener(MarkableControl eventSourceControl) {
        MarkableControl markableControl = this.getMarkedControl();
        if (this.previousMarkedControl != null && this.previousMarkedControl.equals(markableControl) || markableControl == null) {
            return;
        }
        for (ChangeListener<MarkableControl> changeListener: this.markedControlChangeListener) {
            changeListener.execute(eventSourceControl, markableControl, this.previousMarkedControl);
        }
        this.previousMarkedControl = markableControl;
        for (MarkableControl control: this.hashMap.values()) {
            if (control != markableControl) {
                control.executeMarkChangeListener();
            }
        }
    }
    
    private EventHandler<KeyEvent> keyReleasedEventHandler = new EventHandler<KeyEvent>() {

        @Override
        protected void handle(KeyEvent event) {
            MarkableGroup group = MarkableGroup.this;
            group.executeMarkedChangeListener((MarkableControl) event.getSource());
        }
    };
    
    private EventHandler<MouseEvent> mouseReleasedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        protected void handle(MouseEvent event) {
            MarkableGroup group = MarkableGroup.this;
            group.executeMarkedChangeListener((MarkableControl) event.getSource());
        }
    };
    
    private ChangeListener<Boolean> markedChangeListener = new ChangeListener<Boolean>() {
        
        @Override
        protected void changed(Component<?> component, Boolean changedValue, Boolean previousValue) {
            MarkableGroup group = MarkableGroup.this;
            group.executeMarkedChangeListener((MarkableControl) component);
        }
    };
    
    private ButtonGroup buttonGroup = new ButtonGroup();
    
    private Map<AbstractButton, MarkableControl> hashMap = new HashMap<>();
    
    /**
     * このグループに所属するマーク可能なコントロールをすべて取得する。
     * 
     * @return
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
        markableControl.addKeyReleasedEventHandler(this.keyReleasedEventHandler);
        markableControl.addMouseReleasedEventHandler(this.mouseReleasedEventHandler);
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
        markableControl.removeEventHandler(this.keyReleasedEventHandler);
        markableControl.removeEventHandler(this.mouseReleasedEventHandler);
        markableControl.removeChangeListener(this.markedChangeListener);
    }
    
    /**
     * マークされているコントロールを取得する。存在しない場合はnullを返す。
     * 
     * @return
     */
    public MarkableControl getMarkedControl() {
        for (MarkableControl control: this.getMarkableControls()) {
            if (control.isMarked()) {
                return control;
            }
        }
        return null;
    }
}
