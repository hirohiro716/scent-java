package com.hirohiro716.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

import com.hirohiro716.StringObject;

/**
 * byte配列のクラス。
 * 
 * @author hiro
 *
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
    public ByteArray(com.hirohiro716.filesystem.File file) throws IOException {
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
     * @return 結果。
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
     * @return 結果。
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
            stream.write(this.bytes);
        }
    }

    /**
     * ファイルに保存する。
     * 
     * @param file
     * @throws IOException
     */
    public void saveToFile(com.hirohiro716.filesystem.File file) throws IOException {
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
     * byte配列をObjectにdeserializeしてインスタンスを復元する。
     * 
     * @param <T> 復元するオブジェクトの型。
     * @return 結果。
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
