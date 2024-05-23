package com.hirohiro716.scent.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import com.hirohiro716.scent.filesystem.File;
import com.hirohiro716.scent.reflection.DynamicClass;
import com.hirohiro716.scent.reflection.Method;

/**
 * QRコードの画像を作成するクラス。<br>
 * ・ZXing core - <a href="https://mvnrepository.com/artifact/com.google.zxing/core">https://mvnrepository.com/artifact/com.google.zxing/core</a><br>
 * ・ZXing Java SE Extension - <a href="https://mvnrepository.com/artifact/com.google.zxing/javase">https://mvnrepository.com/artifact/com.google.zxing/javase</a><br>
 * 
 * @author hiro
*/
public class QRCodeCreator extends DynamicClass {
    
    /**
     * コンストラクタ。<br>
     * ZXingのcoreとjavaseのjarファイルを指定する。
     * 
     * @param zxingLibraryCoreJar 
     * @param zxingLibraryJavaSeJar 
     * @throws ClassNotFoundException
     * @throws Exception
     */
    public QRCodeCreator(File zxingLibraryCoreJar, File zxingLibraryJavaSeJar) throws ClassNotFoundException, Exception {
        super(zxingLibraryCoreJar, zxingLibraryJavaSeJar);
        this.matrixToImageWriterClass = this.loadClass("com.google.zxing.client.j2se.MatrixToImageWriter");
        Map<String, Object> enumConstants = this.getEnumConstants("com.google.zxing.BarcodeFormat");
        this.enumQRCode = enumConstants.get("QR_CODE");
    }
    
    private Class<?> matrixToImageWriterClass;
    
    private Object enumQRCode;
    
    /**
     * QRコードのBufferedImageオブジェクトを作成する。作成に失敗した場合はnullを返す。
     * 
     * @param contents
     * @param pixelSize
     * @return 結果。
     * @throws IOException 
     */
    public BufferedImage createBufferedImage(String contents, int pixelSize) throws IOException {
        try {
            Constructor constructor = new Constructor("com.google.zxing.qrcode.QRCodeWriter");
            Object writer = constructor.newInstance();
            Method encodeMethod = new Method(writer);
            encodeMethod.setParameterTypes(String.class, this.enumQRCode.getClass(), int.class, int.class);
            Object bitMatrix = encodeMethod.invoke("encode", contents, this.enumQRCode, pixelSize, pixelSize);
            Method toBufferedImageMethod = new Method(this.matrixToImageWriterClass, null);
            return toBufferedImageMethod.invoke("toBufferedImage", bitMatrix);
        } catch (Exception exception) {
            if (exception instanceof IOException) {
                throw (IOException) exception;
            }
            exception.printStackTrace();
            return null;
        }
    }
}
