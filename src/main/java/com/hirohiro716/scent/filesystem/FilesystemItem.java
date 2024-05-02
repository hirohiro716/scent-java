package com.hirohiro716.scent.filesystem;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * ファイルシステムのアイテムを示す、ファイルやディレクトリの抽象クラス。
 * 
 * @author hiro
 *
 */
public abstract class FilesystemItem {

    /**
     * コンストラクタ。<br>
     * 指定されたjava.io.Fileから新しいインスタンスを作成する。
     * 
     * @param file
     */
    public FilesystemItem(File file) {
        this.file = file;
    }

    /**
     * コンストラクタ。<br>
     * 指定されたパスで新しいインスタンスを作成する。
     * 
     * @param location
     */
    public FilesystemItem(String location) {
        this.file = new File(location);
    }
    
    /**
     * コンストラクタ。<br>
     * 指定されたURIで新しいインスタンスを作成する。
     * 
     * @param uri
     */
    public FilesystemItem(URI uri) {
        this.file = new File(uri);
    }
    
    private File file;
    
    /**
     * 内部で保持しているjava.io.Fileインスタンスを取得する。
     * 
     * @return java.io.File
     */
    public File toJavaIoFile() {
        return this.file;
    }
    
    /**
     * 内部で保持しているjava.io.FileインスタンスのURIを構築する。
     * 
     * @return 結果。
     */
    public URI toURI() {
        return this.file.toURI();
    }
    
    /**
     * 内部で保持しているjava.io.FileインスタンスのURIからURLを構築する。
     * 
     * @return 結果。
     * @throws MalformedURLException
     */
    public URL toURL() throws MalformedURLException {
        return this.file.toURI().toURL();
    }
    
    @Override
    public String toString() {
        return this.file.toString();
    }

    /**
     * ファイルシステムアイテムの名前を取得する。
     * 
     * @return 結果。
     */
    public String getName() {
        return this.file.getName();
    }
    
    /**
     * ファイルシステムアイテムの抽象パスを取得する。<br>
     * コンストラクタに相対パスを渡している場合は、戻り値も相対パスになる。
     * 
     * @return 結果。
     */
    public String getPath() {
        return this.file.getPath();
    }
    
    /**
     * このファイルシステムアイテムの絶対パスを取得する。<br>
     * 「 . 」や「 .. 」などの省略表現の解決は行われない。
     * 
     * @return 結果。
     */
    public String getAbsolutePath() {
        return this.file.getAbsolutePath();
    }
    
    /**
     * このファイルシステムアイテムの正規のパスを取得する。<br>
     * 「 . 」や「 .. 」などの短縮形または冗長な名前の解決を行う。
     * 
     * @return 結果。
     * @throws IOException ファイルシステムクエリに失敗した場合。
     */
    public String getCanonicalPath() throws IOException {
        return this.file.getCanonicalPath();
    }
    
    /**
     * このファイルシステムアイテムが通常のファイルかどうかをテストする。<br>
     * アイテムがディレクトリではなく、さらにシステムに依存する他の基準を満たしている場合、trueを返す。
     * 
     * @return 結果。
     */
    public boolean isFile() {
        return this.file.isFile();
    }
    
    /**
     * このファイルシステムアイテムがディレクトリの場合、trueを返す。
     * 
     * @return 結果。
     */
    public boolean isDirectory() {
        return this.file.isDirectory();
    }
    
    /**
     * このファイルシステムアイテムの親ディレクトリを取得する。失敗した場合はnullを返す。
     * 
     * @return 結果。
     */
    public Directory getParentDirectory() {
        File parent = this.file.getParentFile();
        if (parent != null) {
            return new Directory(parent);
        }
        return null;
    }
    
    /**
     * この抽象パスのアイテムがファイルシステムに存在する場合、trueを返す。
     * 
     * @return 結果。
     */
    public abstract boolean exists();
    
    
    /**
     * この抽象パスのアイテムをファイルシステムに作成する。
     * 
     * @throws IOException
     */
    public abstract void create() throws IOException;

    /**
     * この抽象パスのアイテムをファイルシステムから削除する。
     * 
     * @throws IOException
     */
    public abstract void delete() throws IOException;
    
    /**
     * この抽象パスのファイルシステムアイテムを移動する。
     * 
     * @param moveTo 移動先。
     * @throws IOException
     */
    public abstract void move(String moveTo) throws IOException;
    
    /**
     * この抽象パスのファイルシステムアイテムをコピーする。
     * 
     * @param copyTo コピー先。
     * @throws IOException
     */
    public abstract void copy(String copyTo) throws IOException;
    
    /**
     * このコンピューターファイルシステムで使用される区切り文字を取得する。
     * 
     * @return 結果。
     */
    public static String getFileSeparator() {
        return System.getProperty("file.separator");
    }
    
    /**
     * このメソッドはコンストラクタの呼び出しと同じで、指定されたjava.io.Fileから新しいインスタンスを作成する。
     * 
     * @param file
     * @return 結果。
     */
    public static FilesystemItem newInstance(File file) {
        if (file.isDirectory()) {
            return new Directory(file);
        }
        return new com.hirohiro716.scent.filesystem.File(file);
    }
    
    /**
     * このメソッドはコンストラクタの呼び出しと同じで、指定されたパスから新しいインスタンスを作成する。
     * 
     * @param location
     * @return 結果。
     */
    public static FilesystemItem newInstance(String location) {
        return FilesystemItem.newInstance(new File(location));
    }

    /**
     * このメソッドはコンストラクタの呼び出しと同じで、指定されたURIから新しいインスタンスを作成する。
     * 
     * @param uri
     * @return 結果。
     */
    public static FilesystemItem newInstance(URI uri) {
        return FilesystemItem.newInstance(new File(uri));
    }
}
