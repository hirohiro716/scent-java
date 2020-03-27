package com.hirohiro716.gui.dialog;

import java.awt.Font;

import com.hirohiro716.gui.Frame;
import com.hirohiro716.gui.collection.AddListener;
import com.hirohiro716.gui.collection.Collection;
import com.hirohiro716.gui.collection.RemoveListener;
import com.hirohiro716.gui.control.Button;
import com.hirohiro716.gui.control.Control;
import com.hirohiro716.gui.control.FlowPane;
import com.hirohiro716.gui.event.ActionEvent;
import com.hirohiro716.gui.event.EventHandler;

/**
 * メニューを表示するダイアログのクラス。
 * 
 * @author hiro
 *
 */
public class MenuDialog extends TitledDialog<Button> {

    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner
     */
    public MenuDialog(Frame<?> owner) {
        super(owner);
        MenuDialog dialog = this;
        this.buttons.addListener(new AddListener<Button>() {
            
            @Override
            protected void added(Button added, int positionIndex) {
                dialog.flowPane.getChildren().add(added, positionIndex);
                added.addActionEventHandler(dialog.actionEventHandler);
            }
        });
        this.buttons.addListener(new RemoveListener<Button>() {
            
            @Override
            protected void removed(Button removed) {
                dialog.flowPane.getChildren().remove(removed);
                removed.removeEventHandler(dialog.actionEventHandler);
            }
        });
    }
    
    private EventHandler<ActionEvent> actionEventHandler = new EventHandler<>() {

        @Override
        protected void handle(ActionEvent event) {
            MenuDialog dialog = MenuDialog.this;
            dialog.result = event.getSource();
        }
    };

    private Collection<Button> buttons = new Collection<>();
    
    /**
     * このメニューダイアログに表示するボタンのコレクションを取得する。
     * 
     * @return 結果。
     */
    public Collection<Button> getButtons() {
        return this.buttons;
    }
    
    @Override
    public boolean isCancelableByClickBackground() {
        return true;
    }
    
    private FlowPane flowPane;
    
    @Override
    protected Control[] createControls() {
        this.flowPane = new FlowPane();
        return new Control[] {this.flowPane};
    }
    
    @Override
    protected Control getInitialFocusControl() {
        if (this.defaultValue == null) {
            return this.flowPane;
        }
        return this.defaultValue;
    }

    @Override
    protected void processBeforeShow() {
        super.processBeforeShow();
        this.getVerticalPaneOfControls().getGrowableControls().add(this.flowPane);
    }
    
    @Override
    protected void processAfterShow() {
        Font font = this.getPane().getFont();
        this.flowPane.setSpacing(font.getSize());
        for (Button button : this.getButtons()) {
            this.flowPane.getChildren().add(button);
        }
        this.getVerticalPaneOfControls().getGrowableControls().add(this.flowPane);
    }
    
    @Override
    public void close() {
        for (Button button : this.buttons) {
            button.removeEventHandler(this.actionEventHandler);
        }
        super.close();
    }

    private Button defaultValue = null;
    
    @Override
    public void setDefaultValue(Button defaultResultValue) {
        this.defaultValue = defaultResultValue;
    }

    private Button result = null;
    
    @Override
    public Button getDialogResult() {
        return this.result;
    }

    @Override
    protected void setDialogResult(Button result) {
        this.result = result;
    }
    
}
