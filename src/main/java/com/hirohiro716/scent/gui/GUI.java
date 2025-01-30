package com.hirohiro716.scent.gui;

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

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * GUIのグローバルな静的関数のクラス。
 * 
 * @author hiro
*/
public class GUI {
    
    /**
     * GUIライブラリに依存した処理を、専用スレッドで後で安全に実行するようスケジュールする。
     * 
     * @param runnable
     */
    public static void executeLater(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        SwingUtilities.invokeLater(runnable);
    }
    
    /**
     * GUIライブラリに依存した処理を、指定された世代に専用スレッドで安全に実行するようスケジュールする。
     * 
     * @param generationToExecute
     * @param runnable
     */
    public static void executeLater(int generationToExecute, Runnable runnable) {
        if (runnable == null) {
            return;
        }
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
     * @return
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
     * @return
     */
    public static Font[] getAvailableFonts() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    }
    
    /**
     * 利用できるフォント名の配列を取得する。
     * 
     * @return
     */
    public static String[] getAvailableFontNames() {
        List<String> list = new ArrayList<>();
        for (Font font: GUI.getAvailableFonts()) {
            list.add(font.getFontName());
        }
        return list.toArray(new String[] {});
    }
    
    private static String FONT_NAME = null;
    
    /**
     * GUIのコントロール共通のフォント名を取得する。
     * 
     * @return
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
     * @return
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
    
    private static Color TEXT_COLOR = null;
    
    /**
     * 文字色を取得する。
     * 
     * @return
     */
    public static Color getTextColor() {
        if (GUI.TEXT_COLOR == null) {
            GUI.TEXT_COLOR = new Color(UIManager.getColor("text").getRGB());
        }
        return GUI.TEXT_COLOR;
    }
    
    private static Color BORDER_COLOR = null;
    
    /**
     * 境界線色を取得する。
     * 
     * @return
     */
    public static Color getBorderColor() {
        if (GUI.BORDER_COLOR == null) {
            GUI.BORDER_COLOR = new Color(UIManager.getColor("controlDkShadow").getRGB());
        }
        return GUI.BORDER_COLOR;
    }
    
    private static Color BORDER_LIGHT_COLOR = null;
    
    /**
     * 明るい境界線色を取得する。
     * 
     * @return
     */
    public static Color getBorderLightColor() {
        if (GUI.BORDER_LIGHT_COLOR == null) {
            GUI.BORDER_LIGHT_COLOR = new Color(UIManager.getColor("controlShadow").getRGB());
        }
        return GUI.BORDER_LIGHT_COLOR;
    }
    
    private static Color LABEL_FOREGROUND_COLOR = null;
    
    /**
     * ラベルの前景色を取得する。
     * 
     * @return
     */
    public static Color getLabelForegroundColor() {
        if (GUI.LABEL_FOREGROUND_COLOR == null) {
            try {
                GUI.LABEL_FOREGROUND_COLOR = new Color(UIManager.getColor("Label.foreground").getRGB());
            } catch (NullPointerException exception) {
                GUI.LABEL_FOREGROUND_COLOR = GUI.getTextColor();
            }
        }
        return GUI.LABEL_FOREGROUND_COLOR;
    }
    
    private static Color ACTIVE_FOREGROUND_COLOR = null;
    
    /**
     * アクティブな前景色を取得する。
     * 
     * @return
     */
    public static Color getActiveForegroundColor() {
        if (GUI.ACTIVE_FOREGROUND_COLOR == null) {
            try {
                GUI.ACTIVE_FOREGROUND_COLOR = new Color(UIManager.getColor("List.selectionInactiveForeground").getRGB());
            } catch (NullPointerException exception) {
                GUI.ACTIVE_FOREGROUND_COLOR = new Color(UIManager.getColor("textHighlightText").getRGB());
            }
        }
        return GUI.ACTIVE_FOREGROUND_COLOR;
    }
    
    private static Color ACTIVE_BACKGROUND_COLOR = null;
    
    /**
     * アクティブな背景色を取得する。
     * 
     * @return
     */
    public static Color getActiveBackgroundColor() {
        if (GUI.ACTIVE_BACKGROUND_COLOR == null) {
            try {
                GUI.ACTIVE_BACKGROUND_COLOR = new Color(UIManager.getColor("List.selectionInactiveBackground").getRGB());
            } catch (NullPointerException exception) {
                GUI.ACTIVE_BACKGROUND_COLOR = new Color(UIManager.getColor("textHighlight").getRGB());
            }
        }
        return GUI.ACTIVE_BACKGROUND_COLOR;
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
     * GUIのルックアンドフィールを指定されたjavax.swing.LookAndFeelのインスタンスに変更する。
     * 
     * @param lookAndFeel
     * @throws Exception
     */
    public static void setLookAndFeel(LookAndFeel lookAndFeel) throws Exception {
        UIManager.setLookAndFeel(lookAndFeel);
    }
    
    /**
     * 利用できるGUIのルックアンドフィールのバイナリ名を取得する。
     * 
     * @return
     */
    public static String[] getAvailableLookAndFeel() {
        List<String> list = new ArrayList<>();
        for (LookAndFeelInfo info: UIManager.getInstalledLookAndFeels()) {
            list.add(info.getClassName());
        }
        return list.toArray(new String[] {});
    }
    
    private static GraphicsDevice defaultGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    
    /**
     * デフォルトのグラフィックデバイスを取得する。
     * 
     * @return
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
     * @return
     */
    public static GraphicsDevice[] getGraphicsDevices() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    }
    
    /**
     * 指定されたグラフィックデバイスの表示領域のうち、ウィンドウの表示に使用できる最大領域を取得する。
     * 
     * @param graphicsDevice
     * @return
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
