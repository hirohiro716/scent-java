package com.hirohiro716.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.hirohiro716.io.ByteArray;

/**
 * 画像のクラス。
 * 
 * @author hiro
 *
 */
public class Image extends ByteArray {

    /**
     * コンストラクタ。
     * 
     * @param imageFormatName
     * @param bytes
     */
    public Image(ImageFormatName imageFormatName, byte... bytes) {
        super(bytes);
        this.imageFormatName = imageFormatName;
    }
    
    /**
     * コンストラクタ。<br>
     * byteを16進数二桁に変換して連結した文字列をbyte配列にして画像とする。
     * 
     * @param imageFormatName
     * @param stringExpressionOfByteArray byteを16進数ふた桁に変換して連結した文字列。
     */
    public Image(ImageFormatName imageFormatName, String stringExpressionOfByteArray) {
        super(stringExpressionOfByteArray);
        this.imageFormatName = imageFormatName;
    }
    
    /**
     * コンストラクタ。<br>
     * 画像形式を指定して、指定されたBufferedImageを初期値とする。
     * 
     * @param imageFormatName
     * @param bufferedImage
     * @throws IOException
     */
    public Image(ImageFormatName imageFormatName, BufferedImage bufferedImage) throws IOException {
        this.loadBufferedImage(imageFormatName, bufferedImage);
    }
    
    /**
     * コンストラクタ。<br>
     * ファイルを画像として読み込む。
     * 
     * @param file
     * @throws IOException
     */
    public Image(File file) throws IOException {
        super(file);
        this.imageFormatName = ImageFormatName.find(file);
        if (this.imageFormatName == null) {
            throw new IOException("The specified file couldn't be recognized as an image.");
        }
    }
    
    private ImageFormatName imageFormatName;

    /**
     * コンストラクタ。<br>
     * ファイルを画像として読み込む。
     * 
     * @param file
     * @throws IOException
     */
    public Image(com.hirohiro716.filesystem.File file) throws IOException {
        this(file.toJavaIoFile());
    }
    
    /**
     * BufferedImageからbyte配列を読み込む。
     * 
     * @param imageFormatName
     * @param bufferedImage
     * @throws IOException
     */
    public void loadBufferedImage(ImageFormatName imageFormatName, BufferedImage bufferedImage) throws IOException {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, imageFormatName.toString(), stream);
            stream.flush();
            this.set(stream.toByteArray());
        }
        this.imageFormatName = imageFormatName;
    }
    
    /**
     * このbyte配列からBufferedImageを作成する。
     * 
     * @return 結果。
     * @throws IOException
     */
    public BufferedImage createBufferedImage() throws IOException {
        try (ByteArrayInputStream stream = new ByteArrayInputStream(this.bytes())) {
            return ImageIO.read(stream);
        }
    }
    
    /**
     * 指定されたBufferedImageの幅と高さを、指定された大きさにリサイズして、このインスタンスの画像とする。
     * 
     * @param bufferedImage
     * @param pixelWidth
     * @param pixelHeight
     * @param isEnlarged 拡大リサイズを行う場合はtrueを指定。
     * @throws IOException
     */
    private void resize(BufferedImage bufferedImage, int pixelWidth, int pixelHeight, boolean isEnlarged) throws IOException {
        if (isEnlarged == false && bufferedImage.getWidth() <= pixelWidth && bufferedImage.getHeight() <= pixelHeight) {
            return;
        }
        BufferedImage resized = new BufferedImage(pixelWidth, pixelHeight, bufferedImage.getType());
        resized.getGraphics().drawImage(bufferedImage.getScaledInstance(pixelWidth, pixelHeight, java.awt.Image.SCALE_AREA_AVERAGING), 0, 0, pixelWidth, pixelHeight, null);
        this.loadBufferedImage(this.imageFormatName, resized);
    }
    
    /**
     * この画像の幅と高さを指定された大きさにリサイズする。
     * 
     * @param pixelWidth
     * @param pixelHeight
     * @param isEnlarged 拡大リサイズを行う場合はtrueを指定。
     * @throws IOException
     */
    public void resize(int pixelWidth, int pixelHeight, boolean isEnlarged) throws IOException {
        BufferedImage bufferedImage = this.createBufferedImage();
        this.resize(bufferedImage, pixelWidth, pixelHeight, isEnlarged);
    }
    
    /**
     * この画像の長辺を指定された大きさにリサイズする。短辺は長辺のりサイズ比率に応じて自動的にリサイズされる。
     * 
     * @param longSide 画像の長辺。
     * @param isEnlarged 拡大リサイズを行う場合はtrueを指定。
     * @throws IOException
     */
    public void resize(int longSide, boolean isEnlarged) throws IOException {
        BufferedImage bufferedImage = this.createBufferedImage();
        if (bufferedImage.getWidth() > bufferedImage.getHeight()) {
            double rate = (double) bufferedImage.getWidth() / (double) bufferedImage.getHeight();
            this.resize(bufferedImage, longSide, (int) (longSide / rate), isEnlarged);
        } else {
            double rate = (double) bufferedImage.getHeight() / (double) bufferedImage.getWidth();
            this.resize(bufferedImage, (int) (longSide / rate), longSide, isEnlarged);
        }
    }

    /**
     * 指定されたファイルを、指定されたサイズで読み込んで、BufferedImageインスタンスを作成する。
     * 
     * @param file
     * @param maxPixelWidth
     * @param maxPixelHeight
     * @return 結果。
     * @throws IOException
     */
    public static BufferedImage resize(File file, int maxPixelWidth, int maxPixelHeight) throws IOException {
        ImageFormatName imageFormatName = ImageFormatName.find(file);
        if (imageFormatName == null) {
            throw new IOException("The specified file couldn't be recognized as an image.");
        }
        BufferedImage originalBufferedImage = ImageIO.read(file);
        double outputRatio = (double) maxPixelWidth / (double) maxPixelHeight;
        double inputRatio = (double) originalBufferedImage.getWidth() / (double) originalBufferedImage.getHeight();
        int width = maxPixelWidth;
        int height = maxPixelHeight;
        if (outputRatio < inputRatio) {
            height = (int) (width / inputRatio);
        } else {
            width = (int) (height / inputRatio);
        }
        BufferedImage bufferedImage;
        if (originalBufferedImage.getColorModel().hasAlpha()) {
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        } else {
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        }
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(originalBufferedImage, 0, 0, width, height, null);
        graphics2D.dispose();
        return bufferedImage;
    }
    
    /**
     * 指定されたファイルを、指定されたサイズで読み込んでインスタンスを作成する。
     * 
     * @param file
     * @param maxPixelWidth
     * @param maxPixelHeight
     * @return 結果。
     * @throws IOException
     */
    public static Image newInstance(File file, int maxPixelWidth, int maxPixelHeight) throws IOException {
        BufferedImage bufferedImage = resize(file, maxPixelWidth, maxPixelHeight);
        ImageFormatName imageFormatName = ImageFormatName.find(file);
        Image image = new Image(imageFormatName, new byte[] {});
        image.loadBufferedImage(imageFormatName, bufferedImage);
        return image;
    }
    
    /**
     * 画像のフォーマットタイプ列挙型。
     * 
     * @author hiro
     *
     */
    public enum ImageFormatName {
        /**
         * JPEG。
         */
        JPG,
        /**
         * JPEG。
         */
        JPEG,
        /**
         * PNG。
         */
        PNG,
        /**
         * GIF。
         */
        GIF,
        /**
         * BITMAP。
         */
        BMP,
        ;
        
        /**
         * ファイル名の拡張子から画像のフォーマットタイプ列挙型を取得する。失敗した場合はnullを返す。
         * 
         * @param fileName
         * @return 結果。
         */
        public static ImageFormatName find(String fileName) {
            for (ImageFormatName imageFormatName : ImageFormatName.values()) {
                if (fileName.endsWith("." + imageFormatName.toString().toLowerCase())
                        || fileName.endsWith("." + imageFormatName.toString().toUpperCase())) {
                    return imageFormatName;
                }
            }
            return null;
        }
        
        /**
         * java.io.Fileオブジェクトの拡張子から画像のフォーマットタイプ列挙型を取得する。失敗した場合はnullを返す。
         * 
         * @param file
         * @return 結果。
         */
        public static ImageFormatName find(File file) {
            return find(file.getAbsolutePath());
        }
    }
}
