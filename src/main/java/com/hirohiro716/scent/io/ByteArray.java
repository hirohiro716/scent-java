package com.hirohiro716.scent.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.channels.FileLock;
import java.util.Arrays;

import com.hirohiro716.scent.StringObject;

/**
 * byte配列のクラス。
 * 
 * @author hiro
*/
public class ByteArray {
    
    /**
     * コンストラクタ。
     * 
     * @param bytes
     */
    public ByteArray(byte... bytes) {
        this.bytes = bytes;
    }
    
    /**
     * コンストラクタ。<br>
     * byteを16進数二桁に変換して連結した文字列をbyte配列にする。
     * 
     * @param stringExpressionOfByteArray byteを16進数ふた桁に変換して連結した文字列。
     */
    public ByteArray(String stringExpressionOfByteArray) {
        byte[] bytes = new byte[stringExpressionOfByteArray.length() / 2];
        try {
            for (int index = 0; index < bytes.length; index++) {
                bytes[index] = (byte) Integer.parseInt(stringExpressionOfByteArray.substring(index * 2, (index + 1) * 2), 16);
            }
            this.bytes = bytes;
        } catch (Exception exception) {
        }
    }

    /**
     * コンストラクタ。<br>
     * ファイルをbyte配列として読み込む。
     * 
     * @param file
     * @throws IOException
     */
    public ByteArray(File file) throws IOException {
        try (FileInputStream stream = new FileInputStream(file)) {
            this.bytes = stream.readAllBytes();
        }
    }

    /**
     * コンストラクタ。<br>
     * ファイルをbyte配列として読み込む。
     * 
     * @param file
     * @throws IOException
     */
    public ByteArray(com.hirohiro716.scent.filesystem.File file) throws IOException {
        this(file.toJavaIoFile());
    }
    
    /**
     * コンストラクタ。<br>
     * Serializableインターフェースを実装したクラスのインスタンスをbyte配列にする。
     * 
     * @param serializable
     * @throws IOException
     */
    public ByteArray(Serializable serializable) throws IOException {
        try (ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream()) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(bytesOutputStream)) {
                objectOutputStream.writeObject(serializable);
                this.bytes = bytesOutputStream.toByteArray();
            }
        }
    }
    
    private byte[] bytes = new byte[] {};
    
    /**
     * byte配列を取得する。
     * 
     * @return
     */
    public byte[] bytes() {
        return this.bytes;
    }
    
    /**
     * このインスタンスにbyte配列をセットする。
     * 
     * @param bytes
     */
    protected void set(byte[] bytes) {
        this.bytes = bytes;
    }
    
    /**
     * byte配列の長さを取得する。
     * 
     * @return
     */
    public int length() {
        return this.bytes.length;
    }
    
    /**
     * ファイルに保存する。
     * 
     * @param file
     * @throws IOException
     */
    public void saveToFile(File file) throws IOException {
        try (FileOutputStream stream = new FileOutputStream(file)) {
            try (FileLock fileLock = stream.getChannel().lock()) {
                stream.write(this.bytes);
            }
        }
    }

    /**
     * ファイルに保存する。
     * 
     * @param file
     * @throws IOException
     */
    public final void saveToFile(com.hirohiro716.scent.filesystem.File file) throws IOException {
        this.saveToFile(file.toJavaIoFile());
    }
    
    /**
     * byteを16進数ふた桁に変換して連結した文字列を取得する。
     * 
     * @return 文字列表現。
     */
    @Override
    public String toString() {
        StringObject string = new StringObject();
        for (byte one: this.bytes) {
            int intOne = Byte.toUnsignedInt(one);
            StringObject hex = new StringObject(Integer.toHexString(intOne));
            string.append(hex.paddingLeft('0', 2));
        }
        return string.toString();
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
    
    @Override
    public boolean equals(Object object) {
        try {
            byte[] compare = null;
            if (object instanceof ByteArray) {
                ByteArray byteArray = (ByteArray) object;
                compare = byteArray.bytes();
            }
            if (object instanceof byte[]) {
                compare = (byte[]) object;
            }
           return Arrays.equals(this.bytes, compare);
        } catch (Exception exception) {
            return false;
        }
    }
    
    /**
     * このインスタンスのbyte配列を、指定されたcharsetを使用してテキストとして読み込む。
     * 
     * @param charsetName 
     * @return
     * @throws IOException
     */
    public String readAllText(String charsetName) throws IOException {
        StringObject text = new StringObject();
        try (ByteArrayInputStream stream = new ByteArrayInputStream(this.bytes)) {
            try (InputStreamReader streamReader = new InputStreamReader(stream, charsetName)) {
                try (BufferedReader bufferedReader = new BufferedReader(streamReader)) {
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        text.append(line);
                        line = bufferedReader.readLine();
                    }
                }
            }
        }
        return text.toString();
    }

    /**
     * byte配列をObjectにdeserializeしてインスタンスを復元する。
     * 
     * @param <T> 復元するオブジェクトの型。
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T deserialize() throws ClassNotFoundException, IOException {
        ByteArrayInputStream bytesInputStream = new ByteArrayInputStream(this.bytes);
        try (ObjectInputStream objInputStream = new ObjectInputStream(bytesInputStream)) {
            T object = (T) objInputStream.readObject();
            return object;
        }
    }
}
