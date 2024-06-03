package com.hirohiro716.scent.gui.dialog;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import com.hirohiro716.scent.Dimension;
import com.hirohiro716.scent.gui.Border;
import com.hirohiro716.scent.gui.Component;
import com.hirohiro716.scent.gui.Frame;
import com.hirohiro716.scent.gui.control.CenterPane;
import com.hirohiro716.scent.gui.control.Control;
import com.hirohiro716.scent.gui.control.Pane;
import com.hirohiro716.scent.gui.event.ChangeListener;
import com.hirohiro716.scent.gui.event.EventHandler;
import com.hirohiro716.scent.gui.event.MouseEvent;
import com.hirohiro716.scent.gui.event.MouseEvent.MouseButton;

/**
 * GUIのダイアログ抽象クラス。
 * 
 * @author hiro
 *
 * @param <R> このダイアログの表示結果の型。
 */
public abstract class Dialog<R> implements DialogInterface {
    
    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner
     */
    public Dialog(Frame<?> owner) {
        this.owner = owner;
        this.pane = this.createContentPane();
        this.pane.setBorder(Border.createShadow(new Color(50, 50, 50, 50), 10));
        this.backgroundPane.setControl(this.pane);
        this.backgroundPane.addMouseClickedEventHandler(MouseButton.BUTTON1, this.backgroundPaneClickEventHandler);
    }
    
    private Frame<?> owner;
    
    @Override
    public Frame<?> getOwner() {
        return this.owner;
    }
    
    private BackgroundPane backgroundPane = new BackgroundPane();
    
    /**
     * このダイアログの背景を表示しているペインを取得する。
     * 
     * @return 結果。
     */
    public CenterPane getBackgroundPane() {
        return this.backgroundPane;
    }
    
    /**
     * 親ウィンドウのサイズに同期させるリスナー。
     */
    private ChangeListener<Dimension> sizeChangeListener = new ChangeListener<Dimension>() {

        @Override
        protected void changed(Component<?> component, Dimension changedValue, Dimension previousValue) {
            Dialog<R> dialog = Dialog.this;
            dialog.getBackgroundPane().setSize(changedValue);
        }
    };
    
    /**
     * このダイアログが背景のクリックでキャンセル可能な場合はtrueを返す。
     * 
     * @return 結果。
     */
    public abstract boolean isCancelableByClickBackground();
    
    /**
     * 背景のペインをクリックした際に閉じるイベントハンドラー。
     */
    private EventHandler<MouseEvent> backgroundPaneClickEventHandler = new EventHandler<MouseEvent>() {

        @Override
        protected void handle(MouseEvent event) {
            Dialog<R> dialog = Dialog.this;
            if (dialog.isCancelableByClickBackground()) {
                Control child = dialog.backgroundPane.getChildren().findControlByPoint(event.getX(), event.getY());
                if (child == null) {
                    dialog.setCanceledDialogResult();
                    dialog.close();
                }
            }
        }
    };

    private Pane pane;
    
    @Override
    @SuppressWarnings("unchecked")
    public <P extends Pane> P getPane() {
        return (P) this.pane;
    }
    
    /**
     * このダイアログに表示する内容のペインを作成する。<br>
     * このメソッドはスーバークラスのコンストラクタで自動的に呼び出される。
     * 
     * @return 結果。
     */
    protected abstract Pane createContentPane();
    
    private String title;
    
    @Override
    public String getTitle() {
        return this.title;
    }
    
    @Override
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * このダイアログを表示する直前に実行される処理。
     */
    protected abstract void processBeforeShowing();
    
    /**
     * このダイアログを表示した後に実行される処理。
     */
    protected abstract void processAfterShowing();
    
    /**
     * このダイアログにデフォルトの値をセットする。
     * 
     * @param defaultValue
     */
    public abstract void setDefaultValue(R defaultValue);
    
    /**
     * このダイアログの表示結果を取得する。
     * 
     * @return 結果。
     */
    public abstract R getDialogResult();
    
    /**
     * このダイアログの表示結果をセットする。
     * 
     * @param result
     */
    protected abstract void setDialogResult(R result);
    
    /**
     * このダイアログにキャンセルされた場合の表示結果をセットする。
     */
    protected abstract void setCanceledDialogResult();
    
    private ProcessAfterDialogClosing<R> processAfterDialogClosing = null;
    
    /**
     * このダイアログを閉じた際に実行される処理をセットする。
     * 
     * @param processAfterDialogClosing
     */
    public void setProcessAfterClosing(ProcessAfterDialogClosing<R> processAfterDialogClosing) {
        this.processAfterDialogClosing = processAfterDialogClosing;
    }
    
    private Map<Control, Boolean> mapVisibleStatuses = new HashMap<>();

    /**
     * ダイアログを表示する前にバックアップしておいた表示状態に元に戻す。
     */
    private void restoreOwnerChildVisibleStatuses() {
        if (this.mapVisibleStatuses.size() == 0) {
            return;
        }
        for (Control control : this.owner.getRootPane().getChildren()) {
            if (control != this.backgroundPane) {
                control.setVisible(this.mapVisibleStatuses.get(control));
            }
        }
        this.mapVisibleStatuses.clear();
    }
    
    private Map<Control, Boolean> mapDisableStatuses = new HashMap<>();

    /**
     * ダイアログを表示する前にバックアップしておいた無効状態に元に戻す。
     */
    private void restoreOwnerChildDisableStatuses() {
        if (this.mapDisableStatuses.size() == 0) {
            return;
        }
        for (Control control : this.owner.getRootPane().getChildren()) {
            if (control != this.backgroundPane) {
                control.setDisabled(this.mapDisableStatuses.get(control));
            }
        }
        this.mapDisableStatuses.clear();
    }
    
    /**
     * このダイアログを表示する。
     */
    public void show() {
        if (this.owner.getRootPane().getChildren().contains(this.backgroundPane)) {
            return;
        }
        try {
            this.backgroundImage = this.owner.getRootPane().screenshot().createBufferedImage();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        this.mapVisibleStatuses.clear();
        for (Control control : this.owner.getRootPane().getChildren()) {
            this.mapVisibleStatuses.put(control, control.isVisible());
            control.setVisible(false);
            this.mapDisableStatuses.put(control, control.isDisabled());
            control.setDisabled(true);
        }
        this.owner.addSizeChangeListener(this.sizeChangeListener);
        this.backgroundPane.setSize(this.owner.getRootPane().getSize());
        this.processBeforeShowing();
        this.owner.getRootPane().getChildren().add(this.backgroundPane, 0);
        this.owner.getRootPane().updateLayout();
        this.processAfterShowing();
    }
    
    /**
     * このダイアログを閉じる。
     */
    public void close() {
        this.owner.getRootPane().getChildren().remove(this.backgroundPane);
        this.restoreOwnerChildVisibleStatuses();
        this.restoreOwnerChildDisableStatuses();
        this.owner.removeChangeListener(this.sizeChangeListener);
        this.owner.getRootPane().updateDisplay();
        if (this.processAfterDialogClosing != null) {
            this.processAfterDialogClosing.execute(this.getDialogResult());
        }
    }
    
    private BufferedImage backgroundImage = null;
    
    /**
     * ダイアログの背景を表示するパネルのクラス。
     * 
     * @author hiro
     */
    private class InnerBackgroundPanel extends JPanel {
        
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Dialog<R> dialog = Dialog.this;
            if (this.getWidth() == dialog.backgroundImage.getWidth() || this.getHeight() == dialog.backgroundImage.getHeight()) {
                graphics.drawImage(dialog.backgroundImage, 0, 0, this.getWidth(), this.getHeight(), null);
            }
            graphics.setColor(new Color(50, 50, 50, 200));
            graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
    }
    
    /**
     * ダイアログの背景を表示するペインのクラス。
     * 
     * @author hiro
     */
    private class BackgroundPane extends CenterPane {
        
        /**
         * コンストラクタ。
         */
        private BackgroundPane() {
            super(new InnerBackgroundPanel());
            this.getInnerInstance().setOpaque(false);
        }
    }
}
