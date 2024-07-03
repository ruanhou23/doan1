package com.example.app_qr_code_chinh.ui;
import android.util.Base64;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
public class EncryptionUtil {
    private static final String ALGORITHM = "AES";

    public static String generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(256); // key size
        Key key = keyGen.generateKey();
        return Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
    }

    public static String encrypt(String data, String key) throws Exception {
        Key secretKey = new SecretKeySpec(Base64.decode(key, Base64.DEFAULT), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    public static String decrypt(String encryptedData, String key) throws Exception {
        Key secretKey = new SecretKeySpec(Base64.decode(key, Base64.DEFAULT), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] original = cipher.doFinal(Base64.decode(encryptedData, Base64.DEFAULT));
        return new String(original, "UTF-8");
    }
}
