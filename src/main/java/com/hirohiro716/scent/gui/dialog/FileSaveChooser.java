package com.hirohiro716.scent.gui.dialog;

import java.awt.Component;
import javax.swing.JFileChooser;
import com.hirohiro716.scent.filesystem.File;
import com.hirohiro716.scent.gui.Frame;

/**
 * ファイルを開くダイアログのクラス。
 * 
 * @author hiro
*/
public class FileSaveChooser extends FileChooser {
    
    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner 
     */
    public FileSaveChooser(Frame<?> owner) {
        super(owner);
    }
    
    /**
     * コンストラクタ。
     */
    public FileSaveChooser() {
        this(null);
    }
    
    /**
     * ファイルの保存ダイアログを表示する。ダイアログがキャンセルされた場合はfalseを返す。
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
        if (this.getInnerInstance().showSaveDialog(component) != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        this.addChoosedFilesystemItem(new File(this.getInnerInstance().getSelectedFile()));
        return true;
    }
}
