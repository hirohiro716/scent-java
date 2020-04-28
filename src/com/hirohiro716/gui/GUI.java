package com.hirohiro716.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.hirohiro716.StringObject;

/**
 * GUIのグローバルな静的関数のクラス。
 * 
 * @author hiro
 *
 */
public class GUI {

    /**
     * GUIライブラリに依存した処理を、専用スレッドで後で安全に実行するようスケジュールする。
     * 
     * @param runnable
     */
    public static void executeLater(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }
    
    /**
     * GUIライブラリに依存した処理を、指定された世代に専用スレッドで安全に実行するようスケジュールする。
     * 
     * @param generationToExecute
     * @param runnable
     */
    public static void executeLater(int generationToExecute, Runnable runnable) {
        if (generationToExecute <= 0) {
            GUI.executeLater(runnable);
        } else {
            GUI.executeLater(new Runnable() {
                
                @Override
                public void run() {
                    GUI.executeLater(generationToExecute - 1, runnable);
                }
            });
        }
    }
    
    /**
     * クリップボードの文字列を取得する。
     * 
     * @return 結果。
     */
    public static String getClipboardString() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        try {
            return (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (Exception exception) {
            return null;
        }
    }
    
    /**
     * クリップボードに文字列をセットする。
     * 
     * @param string
     */
    public static void setClipboardString(String string) {
        if (string == null || string.length() == 0) {
            return;
        }
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        StringSelection stringSelection = new StringSelection(string);
        clipboard.setContents(stringSelection, stringSelection);
    }
    
    /**
     * 利用できるフォントの配列を取得する。
     * 
     * @return 結果。
     */
    public static Font[] getAvailableFonts() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    }
    
    /**
     * 利用できるフォント名の配列を取得する。
     * 
     * @return 結果。
     */
    public static String[] getAvailableFontNames() {
        List<String> list = new ArrayList<>();
        for (Font font : getAvailableFonts()) {
            list.add(font.getFontName());
        }
        return list.toArray(new String[] {});
    }
    
    /**
     * 指定されたフォントをベースに新しいサイズのフォントを作成する。
     * 
     * @param baseFont
     * @param size
     * @return 結果。
     */
    public static Font createFont(Font baseFont, int size) {
        return new Font(baseFont.getName(), baseFont.getStyle(), size);
    }
    
    /**
     * 指定されたフォントをベースに新しい種類のフォントを作成する。
     * 
     * @param baseFont
     * @param fontName
     * @return 結果。
     */
    public static Font createFont(Font baseFont, String fontName) {
        return new Font(fontName, baseFont.getStyle(), baseFont.getSize());
    }

    /**
     * 指定されたフォントをベースに、指定された拡大率で新しいサイズのフォントを作成する。
     * 
     * @param baseFont
     * @param magnificationRatio
     * @return 結果。
     */
    public static Font createFont(Font baseFont, double magnificationRatio) {
        return new Font(baseFont.getName(), baseFont.getStyle(), (int) (baseFont.getSize2D() * magnificationRatio));
    }
    
    private static String FONT_NAME = null;
    
    /**
     * GUIのコントロール共通のフォント名を取得する。
     * 
     * @return 結果。
     */
    public static String getFontName() {
        return GUI.FONT_NAME;
    }
    
    /**
     * GUIのコントロール共通のフォント名をセットする。
     * 
     * @param fontName
     */
    public static void setFontName(String fontName) {
        GUI.FONT_NAME = fontName;
    }
    
    private static int FONT_SIZE_TO_ADD = 0;
    
    /**
     * GUIすべてのコントロールのフォントに対して加算するサイズを取得する。
     * 
     * @return 結果。
     */
    public static int getFontSizeToAdd() {
        return GUI.FONT_SIZE_TO_ADD;
    }
    
    /**
     * GUIすべてのコントロールのフォントに対して加算するサイズをセットする。
     * 
     * @param fontSizeToAdd
     */
    public static void setFontSizeToAdd(int fontSizeToAdd) {
        GUI.FONT_SIZE_TO_ADD = fontSizeToAdd;
    }

    /**
     * 指定されたHTMLやCSSで使用する "#000000" や "#fff" のような形式の色を作成する。
     * 
     * @param webColor
     * @return 結果。
     */
    public static Color createColor(String webColor) {
        StringObject colorString = new StringObject(webColor);
        if (colorString.toString().indexOf("#") == 0) {
            try {
                int red;
                int green;
                int blue;
                switch (colorString.length()) {
                case 4:
                    red = Integer.parseInt(colorString.clone().extract(1, 2).repeat(2).toString(), 16);
                    green = Integer.parseInt(colorString.clone().extract(2, 3).repeat(2).toString(), 16);
                    blue = Integer.parseInt(colorString.clone().extract(3, 4).repeat(2).toString(), 16);
                    return new Color(red, green, blue);
                case 7:
                    red = Integer.parseInt(colorString.clone().extract(1, 3).toString(), 16);
                    green = Integer.parseInt(colorString.clone().extract(3, 5).toString(), 16);
                    blue = Integer.parseInt(colorString.clone().extract(5, 7).toString(), 16);
                    return new Color(red, green, blue);
                }
            } catch (Exception exception) {
            }
        }
        return createAlphaColor(Color.BLACK, 0);
    }
    
    /**
     * 指定された色をベースに、指定された不透明度で新しい色を作成する。
     * 
     * @param baseColor
     * @param opacity
     * @return 結果。
     */
    public static Color createAlphaColor(Color baseColor, double opacity) {
        int alpha = (int) (baseColor.getAlpha() * opacity);
        return new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha);
    }
    
    /**
     * GUIのルックアンドフィールを変更する。
     * 
     * @param className LookAndFeelを実装したクラスのJava言語仕様で定義されたバイナリ名。
     * @throws Exception
     */
    public static void setLookAndFeel(String className) throws Exception {
        UIManager.setLookAndFeel(className);
    }
    
    /**
     * 利用できるGUIのルックアンドフィールのバイナリ名を取得する。
     * 
     * @return 結果。
     */
    public static String[] getAvailableLookAndFeel() {
        List<String> list = new ArrayList<>();
        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            list.add(info.getClassName());
        }
        return list.toArray(new String[] {});
    }
    
    private static GraphicsDevice defaultGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

    /**
     * デフォルトのグラフィックデバイスを取得する。
     * 
     * @return 結果。
     */
    public static GraphicsDevice getDefaultGraphicsDevice() {
        return GUI.defaultGraphicsDevice;
    }
    
    /**
     * デフォルトのグラフィックデバイスをセットする。
     * 
     * @param graphicsDevice
     */
    public static void setDefaultGraphicsDevice(GraphicsDevice graphicsDevice) {
        if (graphicsDevice == null) {
            return;
        }
        GUI.defaultGraphicsDevice = graphicsDevice;
    }
    
    /**
     * すべてのグラフィックデバイスを取得する。
     * 
     * @return 結果。
     */
    public static GraphicsDevice[] getGraphicsDevices() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    }
    
    /**
     * 指定されたグラフィックデバイスの表示領域のうち、ウィンドウの表示に使用できる最大領域を取得する。
     * 
     * @param graphicsDevice
     * @return 結果。
     */
    public static Rectangle getMaximumWindowBounds(GraphicsDevice graphicsDevice) {
        GraphicsConfiguration graphicsConfiguration = graphicsDevice.getDefaultConfiguration();
        Rectangle rectangle = graphicsConfiguration.getBounds();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration);
        rectangle.setLocation(rectangle.x + insets.left, rectangle.y + insets.top);
        rectangle.setSize(rectangle.width - insets.left - insets.right, rectangle.height - insets.top - insets.bottom);
        return rectangle;
    }
}
