package com.example.app_qr_code_chinh.classdata;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
public class AESEncryption {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    // Chìa khóa bí mật cố định (phải là 16 bytes cho AES-128)
    private static final String FIXED_KEY = "0123456789abcdef";

    public static String encrypt(String data) throws Exception {
        SecretKeySpec keySpec = generateKey(FIXED_KEY);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedData = cipher.doFinal(data.getBytes("UTF-8"));
        return Base64.encodeToString(encryptedData, Base64.DEFAULT);
    }

    public static String decrypt(String encryptedData) throws Exception {
        SecretKeySpec keySpec = generateKey(FIXED_KEY);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decodedData = Base64.decode(encryptedData, Base64.DEFAULT);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData, "UTF-8");
    }

    private static SecretKeySpec generateKey(String key) throws Exception {
        byte[] keyBytes = key.getBytes("UTF-8");
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
}