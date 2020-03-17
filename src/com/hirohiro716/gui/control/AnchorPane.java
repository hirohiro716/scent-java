package com.hirohiro716.gui.control;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.hirohiro716.gui.collection.AddListener;
import com.hirohiro716.gui.collection.RemoveListener;

/**
 * アンカーポイントにコントロールを配置するペインのクラス。
 * 
 * @author hiro
 *
 */
public class AnchorPane extends Pane {

    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected AnchorPane(JPanel innerInstance) {
        super(innerInstance);
        this.getChildren().addListener(this.addListener);
        this.getChildren().addListener(this.removeListener);
        this.getInnerInstance().setLayout(this.gridBagLayout);
    }
    
    /**
     * コンストラクタ。
     */
    public AnchorPane() {
        this(new JPanel());
    }
    
    private GridBagLayout gridBagLayout = new GridBagLayout();
    
    private ArrayList<Control> layoutedControls = new ArrayList<>();
    
    /**
     * このペインに配置したコントロールのアンカーポイントを設定する。
     * 
     * @param control
     * @param top
     * @param right
     * @param bottom
     * @param left
     */
    public void setAnchor(Control control, Integer top, Integer right, Integer bottom, Integer left) {
        this.gridBagLayout.removeLayoutComponent(control.getInnerInstanceForLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        if (top != null && right != null && bottom != null && left != null) {
            constraints.fill = GridBagConstraints.BOTH;
            constraints.insets = new Insets(top, left, bottom, right);
        } else {
            if (top != null && bottom != null) {
                constraints.fill = GridBagConstraints.VERTICAL;
                if (right != null && left == null) {
                    constraints.anchor = GridBagConstraints.EAST;
                } else if (right == null && left != null) {
                    constraints.anchor = GridBagConstraints.WEST;
                }
            } else if (right != null && left != null) {
                constraints.fill = GridBagConstraints.HORIZONTAL;
                if (top != null && bottom == null) {
                    constraints.anchor = GridBagConstraints.NORTH;
                } else if (top == null && bottom != null) {
                    constraints.anchor = GridBagConstraints.SOUTH;
                }
            } else {
                if (top != null && right == null && bottom == null && left == null) {
                    constraints.anchor = GridBagConstraints.NORTH;
                } else if (top != null && right != null && bottom == null && left == null) {
                    constraints.anchor = GridBagConstraints.NORTHEAST;
                } else if (top == null && right != null && bottom == null && left == null) {
                    constraints.anchor = GridBagConstraints.EAST;
                } else if (top == null && right != null && bottom != null && left == null) {
                    constraints.anchor = GridBagConstraints.SOUTHEAST;
                } else if (top == null && right == null && bottom != null && left == null) {
                    constraints.anchor = GridBagConstraints.SOUTH;
                } else if (top == null && right == null && bottom != null && left != null) {
                    constraints.anchor = GridBagConstraints.SOUTHWEST;
                } else if (top == null && right == null && bottom == null && left != null) {
                    constraints.anchor = GridBagConstraints.WEST;
                } else if (top != null && right == null && bottom == null && left != null) {
                    constraints.anchor = GridBagConstraints.NORTHWEST;
                }
            }
            int intTop = (top == null) ? 0 : top;
            int intRight = (right == null) ? 0 : right;
            int intBottom = (bottom == null) ? 0 : bottom;
            int intLeft = (left == null) ? 0 : left;
            constraints.insets = new Insets(intTop, intLeft, intBottom, intRight);
        }
        this.gridBagLayout.setConstraints(control.getInnerInstance(), constraints);
        if (this.layoutedControls.contains(control) == false) {
            control.setVisible(true);
            this.layoutedControls.add(control);
        }
    }

    /**
     * このペインに配置したコントロールのアンカーポイントを設定する。
     * 
     * @param control
     * @param top
     * @param right
     * @param bottom
     * @param left
     */
    public void setAnchor(Control control, int top, int right, int bottom, int left) {
        this.setAnchor(control, (Integer) top, (Integer) right, (Integer) bottom, (Integer) left);
    }
    
    private AddListener<Control> addListener = new AddListener<Control>() {
        
        @Override
        protected void added(Control added, int positionIndex) {
            AnchorPane pane = AnchorPane.this;
            if (pane.layoutedControls.contains(added) == false) {
                added.setVisible(false);
            }
        }
    };
    
    private RemoveListener<Control> removeListener = new RemoveListener<Control>() {

        @Override
        protected void removed(Control removed) {
            AnchorPane pane = AnchorPane.this;
            pane.layoutedControls.remove(removed);
        }
    };
}
