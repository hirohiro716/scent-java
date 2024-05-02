package com.hirohiro716.scent.gui.control;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JScrollBar;

import com.hirohiro716.scent.gui.event.ChangeListener;
import com.hirohiro716.scent.gui.event.InnerInstanceCreator;

/**
 * スクロールバーのクラス。
 * 
 * @author hiro
 *
 */
public class ScrollBar extends Control {
    
    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    public ScrollBar(JScrollBar innerInstance) {
        super(innerInstance);
        this.getInnerInstance().setUnitIncrement(10);
    }
    
    @Override
    public JScrollBar getInnerInstance() {
        return (JScrollBar) super.getInnerInstance();
    }

    /**
     * このスクロールバーのスクロール位置を取得する。
     * 
     * @return 結果。
     */
    public int getScrollPosition() {
        return this.getInnerInstance().getValue();
    }
    
    /**
     * このスクロールバーのスクロール位置をセットする。
     * 
     * @param position
     */
    public void setScrollPosition(int position) {
        this.getInnerInstance().setValue(position);
    }
    
    /**
     * このスクロールバーの最小スクロール位置を取得する。
     * 
     * @return 結果。
     */
    public int getMinimumScrollPosition() {
        return this.getInnerInstance().getMinimum();
    }
    
    /**
     * このスクロールバーの最大スクロール位置を取得する。
     * 
     * @return 結果。
     */
    public int getMaximumScrollPosition() {
        return this.getInnerInstance().getMaximum();
    }
    
    /**
     * このスクロールバーの位置が変更された際のリスナーを追加する。
     * 
     * @param changeListener
     */
    public void addScrollPositionChangeListener(ChangeListener<Integer> changeListener) {
        ScrollBar scrollBar = this;
        AdjustmentListener innerInstance = changeListener.createInnerInstance(scrollBar, new InnerInstanceCreator<>() {

            @Override
            public AdjustmentListener create() {
                return new AdjustmentListener() {
                    
                    @Override
                    public void adjustmentValueChanged(AdjustmentEvent event) {
                        changeListener.executeWhenChanged(scrollBar, event.getValue());
                    }
                };
            }
        });
        this.getInnerInstance().addAdjustmentListener(innerInstance);
    }
    
    @Override
    public void removeChangeListener(ChangeListener<?> changeListener) {
        super.removeChangeListener(changeListener);
        for (Object innerInstance : changeListener.getInnerInstances(this)) {
            if (innerInstance instanceof AdjustmentListener) {
                this.getInnerInstance().removeAdjustmentListener((AdjustmentListener) innerInstance);
            }
        }
    }
}
