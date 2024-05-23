package com.hirohiro716.scent.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.hirohiro716.scent.io.ByteArray;

/**
 * 画像のクラス。
 * 
 * @author hiro
*/
public class Image extends ByteArray implements Cloneable {

    /**
     * コンストラクタ。
     * 
     * @param imageFormat
     * @param bytes
     */
    public Image(ImageFormat imageFormat, byte... bytes) {
        super(bytes);
        this.imageFormat = imageFormat;
    }
    
    /**
     * コンストラクタ。<br>
     * byteを16進数二桁に変換して連結した文字列をbyte配列にして画像とする。
     * 
     * @param imageFormat
     * @param stringExpressionOfByteArray byteを16進数ふた桁に変換して連結した文字列。
     */
    public Image(ImageFormat imageFormat, String stringExpressionOfByteArray) {
        super(stringExpressionOfByteArray);
        this.imageFormat = imageFormat;
    }
    
    /**
     * コンストラクタ。<br>
     * 画像形式を指定して、指定されたBufferedImageを初期値とする。
     * 
     * @param imageFormat
     * @param bufferedImage
     * @throws IOException
     */
    public Image(ImageFormat imageFormat, BufferedImage bufferedImage) throws IOException {
        this.loadBufferedImage(imageFormat, bufferedImage);
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
        this.imageFormat = ImageFormat.find(file);
        if (this.imageFormat == null) {
            throw new IOException("The specified file couldn't be recognized as an image.");
        }
    }

    /**
     * コンストラクタ。<br>
     * URLから画像を読み込む。
     * 
     * @param url
     * @throws IOException
     */
    public Image(URL url) throws IOException {
        this.imageFormat = ImageFormat.find(url.getFile());
        if (this.imageFormat == null) {
            throw new IOException("The specified file couldn't be recognized as an image.");
        }
        this.loadBufferedImage(this.imageFormat, ImageIO.read(url));
    }
    
    private ImageFormat imageFormat;
    
    /**
     * コンストラクタ。<br>
     * ファイルを画像として読み込む。
     * 
     * @param file
     * @throws IOException
     */
    public Image(com.hirohiro716.scent.filesystem.File file) throws IOException {
        this(file.toJavaIoFile());
    }
    
    /**
     * BufferedImageからbyte配列を読み込む。
     * 
     * @param imageFormat
     * @param bufferedImage
     * @throws IOException
     */
    public void loadBufferedImage(ImageFormat imageFormat, BufferedImage bufferedImage) throws IOException {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, imageFormat.toString(), stream);
            stream.flush();
            this.set(stream.toByteArray());
        }
        this.imageFormat = imageFormat;
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
        this.loadBufferedImage(this.imageFormat, resized);
    }
    
    /**
     * この画像の長辺を指定された大きさにリサイズする。短辺は長辺のリサイズ比率に応じて自動的にリサイズされる。
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

    @Override
    public Image clone() {
        return new Image(this.imageFormat, this.bytes());
    }

    /**
     * 指定されたファイルを、指定されたサイズで読み込んで、BufferedImageインスタンスを作成する。
     * 
     * @param file
     * @param maximumPixelWidth
     * @param maximumPixelHeight
     * @return 結果。
     * @throws IOException
     */
    public static BufferedImage resize(File file, int maximumPixelWidth, int maximumPixelHeight) throws IOException {
        ImageFormat imageFormat = ImageFormat.find(file);
        if (imageFormat == null) {
            throw new IOException("The specified file couldn't be recognized as an image.");
        }
        BufferedImage originalBufferedImage = ImageIO.read(file);
        double outputRatio = (double) maximumPixelWidth / (double) maximumPixelHeight;
        double inputRatio = (double) originalBufferedImage.getWidth() / (double) originalBufferedImage.getHeight();
        int width = maximumPixelWidth;
        int height = maximumPixelHeight;
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
     * @param maximumPixelWidth
     * @param maximumPixelHeight
     * @return 結果。
     * @throws IOException
     */
    public static Image newInstance(File file, int maximumPixelWidth, int maximumPixelHeight) throws IOException {
        BufferedImage bufferedImage = Image.resize(file, maximumPixelWidth, maximumPixelHeight);
        ImageFormat imageFormat = ImageFormat.find(file);
        Image image = new Image(imageFormat, new byte[] {});
        image.loadBufferedImage(imageFormat, bufferedImage);
        return image;
    }
    
    /**
     * 画像のフォーマットタイプ列挙型。
     * 
     * @author hiro
     *
     */
    public enum ImageFormat {
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
        public static ImageFormat find(String fileName) {
            for (ImageFormat imageFormat : ImageFormat.values()) {
                if (fileName.endsWith("." + imageFormat.toString().toLowerCase())
                        || fileName.endsWith("." + imageFormat.toString().toUpperCase())) {
                    return imageFormat;
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
        public static ImageFormat find(File file) {
            return ImageFormat.find(file.getAbsolutePath());
        }
    }
}
