package com.hirohiro716.gui.dialog;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

import com.hirohiro716.filesystem.Directory;
import com.hirohiro716.filesystem.File;
import com.hirohiro716.filesystem.FilesystemItem;
import com.hirohiro716.gui.Component;
import com.hirohiro716.gui.Frame;
import com.hirohiro716.gui.collection.AddListener;
import com.hirohiro716.gui.collection.Collection;
import com.hirohiro716.gui.collection.RemoveListener;
import com.hirohiro716.gui.control.Control;
import com.hirohiro716.gui.control.Pane;
import com.hirohiro716.gui.event.ChangeListener;

/**
 * ファイル選択ダイアログの抽象クラス。
 * 
 * @author hiro
 *
 */
public abstract class FileChooser extends Control implements DialogInterface {
    
    /**
     * コンストラクタ。<br>
     * このダイアログのオーナーを指定する。
     * 
     * @param owner 
     */
    public FileChooser(Frame<?> owner) {
        super(new JFileChooser());
        this.owner = owner;
        this.pane = new Pane();
        this.pane.setInnerInstance(this.getInnerInstance().getRootPane());
        this.setListenerOfFileFilter();
    }
    
    /**
     * このコンポーネントがラップしている、GUIライブラリに依存したインスタンスを取得する。
     * 
     * @return 結果。
     */
    public JFileChooser getInnerInstance() {
        return (JFileChooser) super.getInnerInstance();
    }

    @Override
    protected ChangeListener<Dimension> createBugFixChangeListener() {
        return new ChangeListener<Dimension>() {

            @Override
            protected void changed(Component<?> component, Dimension changedValue, Dimension valueBeforeChange) {
                // nop
            }
        };
    }
    
    private Frame<?> owner;
    
    @Override
    public Frame<?> getOwner() {
        return this.owner;
    }
    
    private Pane pane;
    
    @Override
    @SuppressWarnings("unchecked")
    public Pane getPane() {
        return this.pane;
    }
    
    @Override
    public String getTitle() {
        return this.getInnerInstance().getDialogTitle();
    }
    
    @Override
    public void setTitle(String title) {
        this.getInnerInstance().setDialogTitle(title);
    }
    
    /**
     * このダイアログで表示するカレントディレクトリを指定する。
     * 
     * @param directory
     */
    public void setCurrentDirectory(Directory directory) {
        this.getInnerInstance().setCurrentDirectory(directory.toJavaIoFile());
    }
    
    private Collection<FileFilter> fileFilters = new Collection<>();
    
    private Map<FileFilter, javax.swing.filechooser.FileFilter> mapFileFilters = new HashMap<>();
    
    /**
     * ファイルフィルターのコレクションを編集した際のリスナーを設定する。
     */
    private void setListenerOfFileFilter() {
        // All file filter
        javax.swing.filechooser.FileFilter innerInstance = this.getInnerInstance().getAcceptAllFileFilter();
        FileFilter fileFilter = new FileFilter() {
            
            @Override
            public String getText() {
                return innerInstance.getDescription();
            }
            
            @Override
            public boolean accept(File file) {
                return innerInstance.accept(file.toJavaIoFile());
            }
        };
        this.fileFilters.add(fileFilter);
        this.mapFileFilters.put(fileFilter, innerInstance);
        // Set listener
        FileChooser fileChooser = this;
        this.fileFilters.addListener(new AddListener<FileFilter>() {
            
            @Override
            protected void added(FileFilter added, int positionIndex) {
                javax.swing.filechooser.FileFilter innerInstance = new javax.swing.filechooser.FileFilter() {

                    @Override
                    public boolean accept(java.io.File file) {
                        FilesystemItem filesystemItem = FilesystemItem.newInstance(file);
                        if (filesystemItem.isDirectory()) {
                            return true;
                        }
                        return added.accept((File) filesystemItem);
                    }

                    @Override
                    public String getDescription() {
                        return added.getText();
                    }
                };
                fileChooser.getInnerInstance().addChoosableFileFilter(innerInstance);
                fileChooser.mapFileFilters.put(added, innerInstance);
            }
        });
        this.fileFilters.addListener(new RemoveListener<FileFilter>() {
            
            @Override
            protected void removed(FileFilter removed) {
                javax.swing.filechooser.FileFilter innerInstance = fileChooser.mapFileFilters.get(removed);
                fileChooser.getInnerInstance().removeChoosableFileFilter(innerInstance);
                fileChooser.mapFileFilters.remove(removed);
            }
        });
    }
    
    /**
     * このダイアログで表示するファイルを制限するファイルフィルターのコレクションを取得する。
     * 
     * @return 結果。
     */
    public Collection<FileFilter> getFileFilters() {
        return this.fileFilters;
    }
    
    private List<FilesystemItem> choosedFilesystemItems = new ArrayList<>();
    
    /**
     * このダイアログによって選択されたものとしてファイルシステムアイテムとして追加する。
     * 
     * @param filesystemItem 
     */
    protected void addChoosedFilesystemItem(FilesystemItem filesystemItem) {
        if (filesystemItem == null) {
            return;
        }
        this.choosedFilesystemItems.add(filesystemItem);
    }
    
    /**
     * このダイアログによって選択されたファイルシステムアイテムを取得する。
     * 
     * @return 結果。
     */
    public FilesystemItem[] getChoosedFilesystemItems() {
        return this.choosedFilesystemItems.toArray(new FilesystemItem[] {});
    }
    
    /**
     * このダイアログによって選択されたファイルシステムアイテムを取得する。未選択の場合はnullを返す。
     * 
     * @return 結果。
     */
    public FilesystemItem getChoosedFilesystemItem() {
        if (this.choosedFilesystemItems.size() == 0) {
            return null;
        }
        return this.choosedFilesystemItems.get(0);
    }
    
    /**
     * このダイアログによって選択されているファイルシステムアイテムをクリアする。
     */
    protected void clearChoosedFilesystemItem() {
        this.choosedFilesystemItems.clear();
    }
    
    /**
     * このダイアログによって選択されたものとしてファイルシステムアイテムをセットする。
     * 
     * @param choosedFilesystemItems
     */
    public void setChoosedFilesystemItems(FilesystemItem... choosedFilesystemItems) {
        this.clearChoosedFilesystemItem();
        for (FilesystemItem filesystemItem : choosedFilesystemItems) {
            this.addChoosedFilesystemItem(filesystemItem);
        }
        List<java.io.File> files = new ArrayList<>();
        for (FilesystemItem filesystemItem : choosedFilesystemItems) {
            files.add(filesystemItem.toJavaIoFile());
        }
        this.getInnerInstance().setSelectedFiles(files.toArray(new java.io.File[] {}));
    }
    
    /**
     * ファイルの選択ダイアログを表示する。ダイアログがキャンセルされた場合はfalseを返す。
     * 
     * @return 結果。
     */
    public abstract boolean showAndWait();
    
    /**
     * ファイルフィルターのクラス。
     * 
     * @author hiro
     *
     */
    public static abstract class FileFilter {
        
        /**
         * このファイルフィルターの表示テキストを取得する。
         * 
         * @return 結果。
         */
        public abstract String getText();
        
        /**
         * このファイルフィルターで指定されたファイルを許可する場合はtrueを返す。
         * 
         * @param file
         * @return 結果。
         */
        public abstract boolean accept(File file);
    }
}
