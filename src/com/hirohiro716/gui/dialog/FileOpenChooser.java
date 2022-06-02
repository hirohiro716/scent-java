package com.hirohiro716.gui.dialog;

import java.awt.Component;
import javax.swing.JFileChooser;

import com.hirohiro716.filesystem.FilesystemItem;
import com.hirohiro716.gui.Frame;

/**
 * ファイルを開くダイアログのクラス。
 * 
 * @author hiro
 *
 */
public class FileOpenChooser extends FileSaveChooser {
    
    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner 
     */
    public FileOpenChooser(Frame<?> owner) {
        super(owner);
        this.setOpenMethod(OpenMethod.FILE);
    }
    
    /**
     * コンストラクタ。
     */
    public FileOpenChooser() {
        this(null);
    }
    
    /**
     * 複数のファイルシステムアイテムの選択が許可されている場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isAllowMultipleSelection() {
        return this.getInnerInstance().isMultiSelectionEnabled();
    }
    
    /**
     * 複数のファイルシステムアイテムの選択を許可する場合はtrueをセットする。
     * 
     * @param isAllowMultipleSelection
     */
    public void setAllowMultipleSelection(boolean isAllowMultipleSelection) {
        this.getInnerInstance().setMultiSelectionEnabled(isAllowMultipleSelection);
    }
    
    private OpenMethod openMethod = OpenMethod.FILE;
    
    /**
     * ファイルを開くダイアログのアイテム選択方法を取得する。
     * 
     * @return 結果。
     */
    public OpenMethod getOpenMethod() {
        return this.openMethod;
    }
    
    /**
     * ファイルを開くダイアログのアイテム選択方法をセットする。
     * 
     * @param openMethod
     */
    public void setOpenMethod(OpenMethod openMethod) {
        this.openMethod = openMethod;
        switch (this.openMethod) {
        case FILE:
            this.getInnerInstance().setFileSelectionMode(JFileChooser.FILES_ONLY);
            break;
        case DIRECTORY:
            this.getInnerInstance().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            break;
        }
    }
    
    /**
     * ファイルを開くダイアログを表示する。ファイルシステムアイテムが1つ以上選択された場合はtrueを返す。
     * 
     * @return 結果。
     */
    @Override
    public boolean showAndWait() {
        Component component = null;
        if (this.getOwner() != null) {
            component = this.getOwner().getInnerInstanceForLayout();
        }
        this.clearChoosedFilesystemItem();
        if (this.getInnerInstance().showOpenDialog(component) == JFileChooser.APPROVE_OPTION) {
            if (this.getInnerInstance().isMultiSelectionEnabled()) {
                for (java.io.File javaIoFile : this.getInnerInstance().getSelectedFiles()) {
                    this.addChoosedFilesystemItem(FilesystemItem.newInstance(javaIoFile));
                }
            } else {
                this.addChoosedFilesystemItem(FilesystemItem.newInstance(this.getInnerInstance().getSelectedFile()));
            }
        }
        if (this.getChoosedFilesystemItems().length == 0) {
            return false;
        }
        return true;
    }
    
    /**
     * ファイルを開くダイアログのアイテム選択方法の列挙型。
     * 
     * @author hiro
     *
     */
    public enum OpenMethod {
        /**
         * ファイル。
         */
        FILE,
        /**
         * ディレクトリ。
         */
        DIRECTORY,
    }
}
