package com.hirohiro716.filesystem;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.hirohiro716.StringObject;

/**
 * ファイルシステム上のディレクトリクラス。
 * 
 * @author hiro
 *
 */
public class Directory extends FilesystemItem {

    /**
     * コンストラクタ。<br>
     * 指定されたjava.io.Fileから新しいインスタンスを作成する。
     * 
     * @param file
     * @throws IllegalArgumentException 
     */
    public Directory(java.io.File file) throws IllegalArgumentException {
        super(file);
        if (this.toJavaIoFile().exists() && this.isDirectory() == false) {
            throw new IllegalArgumentException("Argument must be directory:" + this.getPath());
        }
    }

    /**
     * コンストラクタ。<br>
     * 指定されたパスで新しいインスタンスを作成する。
     * 
     * @param location
     */
    public Directory(String location) {
        this(new java.io.File(location));
    }

    /**
     * コンストラクタ。<br>
     * 指定されたディレクトリ内、ディレクトリ名で新しいインスタンスを作成する。
     * 
     * @param parentDirectory 
     * @param directoryName 
     */
    public Directory(Directory parentDirectory, String directoryName) {
        this(new java.io.File(parentDirectory.toJavaIoFile(), directoryName));
    }
    
    /**
     * コンストラクタ。<br>
     * 指定されたディレクトリのURIで新しいインスタンスを作成する。
     * 
     * @param uri
     */
    public Directory(URI uri) {
        this(new java.io.File(uri));
    }
    
    @Override
    public boolean isExist() {
        return this.toJavaIoFile().exists() && this.isDirectory();
    }

    @Override
    public void create() throws IOException {
        Files.createDirectories(this.toJavaIoFile().toPath());
    }

    @Override
    public void delete() throws IOException {
        Files.delete(this.toJavaIoFile().toPath());
    }

    @Override
    public void move(String moveTo) throws IOException {
        Files.move(this.toJavaIoFile().toPath(), Paths.get(moveTo));
    }

    @Override
    public void copy(String copyTo) throws IOException {
        Files.copy(this.toJavaIoFile().toPath(), Paths.get(copyTo));
        String originalCanonicalPath = this.getCanonicalPath();
        String copyCanonicalPath = new Directory(copyTo).getCanonicalPath();
        for (FilesystemItem item: this.searchItems(null, null)) {
            String itemCanonicalPath = item.getCanonicalPath();
            Files.copy(Paths.get(itemCanonicalPath), Paths.get(StringObject.newInstance(itemCanonicalPath).replace(originalCanonicalPath, copyCanonicalPath).toString()));
        }
    }

    /**
     * 指定されたディレクトリ内にある、すべてのファイルシステムアイテムをサブディレクトリを含めて検索する。
     * 
     * @param directory
     * @param regexToFilterDirectoryName ディレクトリ名をフィルタするための正規表現。
     * @param regexToFilterFileName ファイル名をフィルタするための正規表現。
     * @return 結果。
     */
    private List<FilesystemItem> searchItems(java.io.File directory, String regexToFilterDirectoryName, String regexToFilterFileName) {
        List<FilesystemItem> items = new ArrayList<>();
        if (directory.exists()) {
            for (java.io.File file : directory.listFiles()) {
                if (file.isDirectory() && file.getName().matches(regexToFilterDirectoryName)) {
                    items.add(new Directory(file));
                    items.addAll(this.searchItems(file, regexToFilterDirectoryName, regexToFilterFileName));
                }
                if (file.isFile() && file.getName().matches(regexToFilterFileName)) {
                    items.add(new File(file));
                }
            }
        }
        return items;
    }

    /**
     * このディレクトリ直下にあるファイルシステムアイテムを検索する。
     * 
     * @param regexToFilterFileName ファイル名をフィルタするための正規表現。
     * @return 結果。
     */
    public File[] getFiles(String regexToFilterFileName) {
        StringObject fileRegex = new StringObject(regexToFilterFileName);
        if (fileRegex.length() == 0) {
            fileRegex.append(".*");
        }
        List<File> files = new ArrayList<>();
        for (FilesystemItem filesystemItem : this.searchItems(this.toJavaIoFile(), "", fileRegex.toString()).toArray(new FilesystemItem[] {})) {
            files.add((File) filesystemItem);
        }
        return files.toArray(new File[] {});
    }
    
    /**
     * このディレクトリ内にある、すべてのファイルシステムアイテムをサブディレクトリを含めて検索する。
     * 
     * @param regexToFilterDirectoryName ディレクトリ名をフィルタするための正規表現。
     * @param regexToFilterFileName ファイル名をフィルタするための正規表現。
     * @return 結果。
     */
    public FilesystemItem[] searchItems(String regexToFilterDirectoryName, String regexToFilterFileName) {
        StringObject directoryRegex = new StringObject(regexToFilterDirectoryName);
        if (directoryRegex.length() == 0) {
            directoryRegex.append(".*");
        }
        StringObject fileRegex = new StringObject(regexToFilterFileName);
        if (fileRegex.length() == 0) {
            fileRegex.append(".*");
        }
        return this.searchItems(this.toJavaIoFile(), directoryRegex.toString(), fileRegex.toString()).toArray(new FilesystemItem[] {});
    }
    }
