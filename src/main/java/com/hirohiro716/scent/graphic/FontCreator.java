package com.hirohiro716.scent.graphic;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import com.hirohiro716.scent.OS;
import com.hirohiro716.scent.filesystem.Directory;
import com.hirohiro716.scent.filesystem.File;
import com.hirohiro716.scent.filesystem.FilesystemItem;

/**
 * フォントを作成する静的関数のクラス。
 */
public class FontCreator {

    /**
     * 指定されたフォントをベースに新しいサイズのフォントを作成する。
     * 
     * @param baseFont
     * @param size
     * @return
     */
    public static Font create(Font baseFont, int size) {
        return new Font(baseFont.getName(), baseFont.getStyle(), size);
    }
    
    /**
     * 指定されたフォントをベースに新しい種類のフォントを作成する。
     * 
     * @param baseFont
     * @param fontName
     * @return
     */
    public static Font create(Font baseFont, String fontName) {
        return new Font(fontName, baseFont.getStyle(), baseFont.getSize());
    }
    
    /**
     * 指定されたフォントをベースに、指定された拡大率で新しいサイズのフォントを作成する。
     * 
     * @param baseFont
     * @param magnificationRatio
     * @return
     */
    public static Font create(Font baseFont, double magnificationRatio) {
        return new Font(baseFont.getName(), baseFont.getStyle(), (int) (baseFont.getSize2D() * magnificationRatio));
    }

    private static Map<String, File> fontNameAndFontFile = new HashMap<>();

    /**
     * 指定されたフォント名を含むフォントファイルを取得する。見つからなかった場合はnullを返す。
     * 
     * @param fontName
     * @return
     */
    public static File findFontFile(String fontName) {
        if (FontCreator.fontNameAndFontFile.containsKey(fontName)) {
            return FontCreator.fontNameAndFontFile.get(fontName);
        }
        for (Directory directory: OS.thisOS().getFontDirectories()) {
            for (FilesystemItem filesystemItem: directory.searchItems("", ".*")) {
                try {
                    File file = (File) filesystemItem;
                    for (Font font: Font.createFonts(file.toJavaIoFile())) {
                        if (font.getFontName().equals(fontName)) {
                            FontCreator.fontNameAndFontFile.put(fontName, file);
                            return file;
                        }
                    }
                } catch (Exception exception) {
                }
            }
        }
        return null;
    }
}
