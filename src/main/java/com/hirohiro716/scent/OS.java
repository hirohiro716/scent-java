package com.hirohiro716.scent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.hirohiro716.scent.filesystem.Directory;

/**
 * OSに依存する部分を吸収するための列挙型。
 */
public enum OS {
    /**
     * Windwos系統。
     */
    WINDOWS,
    /**
     * Linux系統。
     */
    LINUX,
    /**
     * macOS系統。
     */
    MACOS,
    /**
     * 不明。
     */
    UNKNOWN,
    ;
    
    private String lineSeparator = System.getProperty("line.separator");
    
    /**
     * OS標準の改行コードを取得する。
     * 
     * @return
     */
    public String getLineSeparator() {
        return this.lineSeparator;
    }
    
    private String fileSeparator = File.separator;
    
    /**
     * OSのファイルパス区切り文字を取得する。
     * 
     * @return
     */
    public String getFileSeparator() {
        return this.fileSeparator;
    }

    /**
     * フォントの保存場所を取得する。
     * 
     * @return
     */
    public Directory[] getFontDirectories() {
        List<Directory> directories = new ArrayList<>();
        switch (this) {
            case WINDOWS:
                directories.add(new Directory(System.getenv("LOCALAPPDATA") + "\\Microsoft\\Windows\\Fonts"));
                directories.add(new Directory(System.getenv("SYSTEMROOT") + "\\Fonts"));
                break;
            case MACOS:
                directories.add(new Directory(System.getProperty("user.home") + "/Library/Fonts"));
                directories.add(new Directory("/Library/Fonts"));
                directories.add(new Directory("/System/Library/Fonts"));
                break;
            case LINUX:
                directories.add(new Directory(System.getProperty("user.home") + "/.fonts"));
                directories.add(new Directory(System.getProperty("user.home") + "/.local/share/fonts"));
                directories.add(new Directory("/usr/local/share/fonts"));
                directories.add(new Directory("/usr/share/fonts"));
                break;
            case UNKNOWN:
                break;

        }
        return directories.toArray(new Directory[] {});
    }

    /**
     * このプログラムを実行しているOSを取得する。
     * 
     * @return OS
     */
    public static OS thisOS() {
        if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
            return WINDOWS;
        }
        if (System.getProperty("os.name").toLowerCase().indexOf("linux") >= 0) {
            return LINUX;
        }
        if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) {
            return MACOS;
        }
        return UNKNOWN;
    }
}
