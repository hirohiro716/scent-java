package com.hirohiro716.scent;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.hirohiro716.scent.io.ByteArray;

/**
 * 文字列を暗号化するクラス。
 * 
 * @author hiro
 *
 */
public class Encrypter {
    
    /**
     * コンストラクタ。<br>
     * 使用するアルゴリズム、共通鍵、初期ベクトルを指定する。
     * 
     * @param algorithm "AES/CBC/PKCS5Padding"など、Cipherで使用できるアルゴリズム。
     * @param key 共通鍵。
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public Encrypter(String algorithm, ByteArray key) throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.cipher = Cipher.getInstance(algorithm);
        this.firstAlgorithm = StringObject.newInstance(algorithm).split("/")[0];
        if (key == null || key.length() == 0) {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(this.firstAlgorithm);
            this.key = new ByteArray(keyGenerator.generateKey().getEncoded());
        } else {
            this.key = key;
        }
    }
    
    /**
     * コンストラクタ。<br>
     * 使用するするアルゴリズムを指定する。共通鍵は内部で自動生成される。
     * 
     * @param algorithm "AES/CBC/PKCS5Padding"など、Cipherで使用できるアルゴリズム。
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public Encrypter(String algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException {
        this(algorithm, null);
    }
    
    private String firstAlgorithm;
    
    private Cipher cipher;
    
    private ByteArray key;
    
    /**
     * このインスタンスで使用した共通鍵を取得する。
     * 
     * @return 結果。
     */
    public ByteArray getKey() {
        return this.key;
    }
    
    /**
     * このインスタンスで使用した初期ベクトルを取得する。
     * 
     * @return 結果。
     */
    public ByteArray getIV() {
        if (this.cipher.getIV() == null) {
            return null;
        }
        return new ByteArray(this.cipher.getIV());
    }
    
    /**
     * 指定された値を暗号化する。
     * 
     * @param value
     * @return 結果。
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public ByteArray encrypt(String value) throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(this.key.bytes(), this.firstAlgorithm);
        this.cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return new ByteArray(this.cipher.doFinal(value.getBytes()));
    }
    
    /**
     * 暗号化されたbyte配列を復号化する。
     * 
     * @param encrypted
     * @param iv 暗号化する際に使用された初期ベクトル。nullを指定可能。
     * @return 結果。
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String decrypt(ByteArray encrypted, ByteArray iv) throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(this.key.bytes(), this.firstAlgorithm);
        IvParameterSpec ivParameterSpec = null;
        if (iv != null) {
            ivParameterSpec = new IvParameterSpec(iv.bytes());
        }
        this.cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        return new String(this.cipher.doFinal(encrypted.bytes()));
    }
    
    /**
     * 暗号化されたbyte配列を復号化する。
     * 
     * @param encrypted
     * @return 結果。
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String decrypt(ByteArray encrypted) throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        return this.decrypt(encrypted, null);
    }

    /**
     * 利用可能なアルゴリズムを取得する。
     * 
     * @return 結果。
     */
    public static String[] getAvailableAlgorithms() {
        List<String> algorithms = new ArrayList<>();
        for (String algorithm: Security.getAlgorithms("Cipher")) {
            try {
                Cipher cipher = Cipher.getInstance(algorithm);
                KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
                byte[] key = keyGenerator.generateKey().getEncoded();
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, algorithm));
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, algorithm));
                algorithms.add(algorithm);
            } catch (Exception exception) {
            }
        }
        return algorithms.toArray(new String[] {});
    }
}
