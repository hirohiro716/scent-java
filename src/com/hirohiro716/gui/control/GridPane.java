package com.hirohiro716.gui.control;

import java.awt.GridLayout;

import javax.swing.JPanel;

/**
 * 行数と列数の格子状に分割して配置するペインのクラス。
 * 
 * @author hiro
 *
 */
public class GridPane extends Pane {

    /**
     * コンストラクタ。<br>
     * このコンポーネントがラップする、GUIライブラリに依存したインスタンスを指定する。
     * 
     * @param innerInstance
     */
    protected GridPane(JPanel innerInstance) {
        super(innerInstance);
        this.getInnerInstance().setLayout(this.layout);
    }
    
    /**
     * コンストラクタ。
     */
    public GridPane() {
        this(new JPanel());
    }
    
    private GridLayout layout = new GridLayout();
    
    /**
     * この配置の列数をセットする。
     * 
     * @param numberOfColumns
     */
    public void setNumberOfColumns(int numberOfColumns) {
        this.layout.setColumns(numberOfColumns);
    }
    
    /**
     * この配置の行数をセットする。
     * 
     * @param numberOfRows
     */
    public void setNumberOfRows(int numberOfRows) {
        this.layout.setRows(numberOfRows);
    }
    
    /**
     * コンポーネント間の水平方向間隔をセットする。
     * 
     * @param spacing
     */
    public void setHorizontalSpacing(int spacing) {
        this.layout.setHgap(spacing);
    }
    
    /**
     * コンポーネント間の垂直方向間隔をセットする。
     * 
     * @param spacing
     */
    public void setVerticalSpacing(int spacing) {
        this.layout.setVgap(spacing);
    }
}
