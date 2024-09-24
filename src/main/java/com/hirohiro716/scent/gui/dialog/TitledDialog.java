package com.hirohiro716.scent.gui.dialog;

import java.awt.Color;
import java.awt.Font;

import javax.swing.UIManager;

import com.hirohiro716.scent.Dimension;
import com.hirohiro716.scent.graphic.ColorCreator;
import com.hirohiro716.scent.graphic.FontCreator;
import com.hirohiro716.scent.gui.Border;
import com.hirohiro716.scent.gui.Component;
import com.hirohiro716.scent.gui.Frame;
import com.hirohiro716.scent.gui.control.AnchorPane;
import com.hirohiro716.scent.gui.control.ClickableLabel;
import com.hirohiro716.scent.gui.control.Control;
import com.hirohiro716.scent.gui.control.Label;
import com.hirohiro716.scent.gui.control.Pane;
import com.hirohiro716.scent.gui.control.VerticalPane;
import com.hirohiro716.scent.gui.event.ActionEvent;
import com.hirohiro716.scent.gui.event.ChangeListener;
import com.hirohiro716.scent.gui.event.EventHandler;

/**
 * タイトル付きダイアログの抽象クラス。
 * 
 * @author hiro
 * 
 * @param <R> このダイアログの表示結果の型。
 */
public abstract class TitledDialog<R> extends Dialog<R> {

    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner
     */
    public TitledDialog(Frame<?> owner) {
        super(owner);
    }

    private Label labelOfTitle;
    
    /**
     * このダイアログのタイトルラベルを取得する。
     * 
     * @return
     */
    public Label getLabelOfTitle() {
        return this.labelOfTitle;
    }
    
    /**
     * このダイアログに閉じる記号を表示する場合はtrueを返す。
     * 
     * @return
     */
    protected abstract boolean isShowCloseSymbol();
    
    private VerticalPane verticalPane;
    
    /**
     * このダイアログのコントロールを表示するペインを取得する。
     * 
     * @return
     */
    public VerticalPane getVerticalPaneOfControls() {
        return this.verticalPane;
    }
    
    @Override
    protected Pane createContentPane() {
        TitledDialog<?> dialog = this;
        // AnchorPane
        AnchorPane pane = new AnchorPane();
        Font font = pane.getFont();
        int padding = font.getSize();
        pane.setSize(padding * 34, padding * 24);
        // Title
        Color borderColor = new Color(UIManager.getColor("controlShadow").getRGB());
        this.labelOfTitle = new Label();
        this.labelOfTitle.setBorder(Border.createLine(borderColor, 0, 0, 1, 0));
        int padding2 = padding * 2;
        Font fontTitle = FontCreator.create(font, 1.2);
        this.labelOfTitle.setFont(fontTitle);
        this.labelOfTitle.setPadding(0, 0, padding2, 0);
        pane.getChildren().add(this.labelOfTitle);
        if (this.isShowCloseSymbol() == false) {
            pane.setAnchor(this.labelOfTitle, padding2, padding2, null, padding2);
        } else {
            pane.setAnchor(this.labelOfTitle, padding2, padding2 + padding2, null, padding2);
            // Close label
            ClickableLabel clickableLabel = new ClickableLabel("×");
            clickableLabel.setForegroundColor(ColorCreator.createTransparent(clickableLabel.getForegroundColor(), 0.8));
            clickableLabel.setDisabledUnderline(true);
            clickableLabel.addActionEventHandler(new EventHandler<ActionEvent>() {
                
                @Override
                protected void handle(ActionEvent event) {
                    dialog.close();
                }
            });
            pane.getChildren().add(clickableLabel);
            pane.setAnchor(clickableLabel, padding2, padding2, null, null);
        }
        // Controls
        this.verticalPane = new VerticalPane();
        this.verticalPane.setSpacing(padding2);
        this.verticalPane.setFillChildToPaneWidth(true);
        Control[] controls = this.createControls();
        for (Control control : controls) {
            if (control != null) {
                this.verticalPane.getChildren().add(control);
            }
        }
        pane.getChildren().add(this.verticalPane);
        pane.addSizeChangeListener(new ChangeListener<Dimension>() {

            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension previousValue) {
                AnchorPane pane = dialog.getPane();
                int padding = pane.getFont().getSize() * 4;
                dialog.verticalPane.setWidth(changedValue.getIntegerWidth() - padding);
            }
        });
        // Change anchor when other control resizing
        ChangeListener<Dimension> sizeChangeListener = new ChangeListener<Dimension>() {
            
            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension previousValue) {
                AnchorPane pane = dialog.getPane();
                int padding = pane.getFont().getSize();
                int padding2 = padding * 2;
                int padding4 = padding * 4;
                pane.setAnchor(dialog.verticalPane, dialog.labelOfTitle.getHeight() + padding4, padding2, padding2, padding2);
                pane.updateLayout();
            }
        };
        this.labelOfTitle.addSizeChangeListener(sizeChangeListener);
        return pane;
    }

    /**
     * このダイアログに表示するコントロールを作成する。<br>
     * このメソッドはスーバークラスのコンストラクタで自動的に呼び出される。
     * 
     * @return
     */
    protected abstract Control[] createControls();

    /**
     * このダイアログを表示した際にフォーカスするコントロールを取得する。
     * 
     * @return
     */
    protected abstract Control getInitialFocusControl();
    
    @Override
    protected void processBeforeShowing() {
        this.labelOfTitle.setText(this.getTitle());
    }

    @Override
    protected void processAfterShowing() {
        if (this.getInitialFocusControl() != null) {
            this.verticalPane.getChildren().setInitialFocusControl(this.getInitialFocusControl());
            this.verticalPane.requestFocus();
        }
    }
}
