package com.hirohiro716.scent;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import com.hirohiro716.scent.io.ByteArray;

/**
 * 文字列をハッシュ化するクラス。
 * 
 * @author hiro
 *
 */
public class Hasher {
    
    /**
     * コンストラクタ。<br>
     * 使用するアルゴリズムとハッシュ化する値を指定する。
     * 
     * @param algorithm
     * @param value
     * @throws NoSuchAlgorithmException 
     */
    public Hasher(String algorithm, String value) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        messageDigest.update(value.getBytes());
        this.hash = new ByteArray(messageDigest.digest());
    }
    
    private ByteArray hash;
    
    /**
     * このインスタンスのハッシュをbyte配列で取得する。
     * 
     * @return ByteArray
     */
    public ByteArray getHash()  {
        return this.hash;
    }
    
    /**
     * このインスタンスのハッシュと、指定されたハッシュが同じ場合はtrueを返す。
     * 
     * @param hash
     * @return 結果。
     */
    public boolean verify(ByteArray hash) {
        return this.hash.equals(hash);
    }
    
    /**
     * 利用可能なアルゴリズムを取得する。
     * 
     * @return 結果。
     */
    public static String[] getAvailableAlgorithms() {
        return Security.getAlgorithms("MessageDigest").toArray(new String[] {});
    }
}
