package com.hirohiro716.scent;

import java.io.File;

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
