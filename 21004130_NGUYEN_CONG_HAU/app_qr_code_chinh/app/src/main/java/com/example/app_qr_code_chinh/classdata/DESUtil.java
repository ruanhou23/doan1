package com.example.app_qr_code_chinh.classdata;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;
public class DESUtil {
    private static final String ALGORITHM = "DES";
    private static final String TRANSFORMATION = "DES/ECB/PKCS5Padding";
    private static final String FIXED_KEY = "12345678"; // 8-byte key for DES

    // Method to generate a DES key
    private static Key generateKey() throws Exception {
        return new SecretKeySpec(FIXED_KEY.getBytes(), ALGORITHM);
    }

    // Method to encrypt a string using DES
    public static String encryptDES(String data) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.encodeToString(encryptedData, Base64.DEFAULT);
    }

    // Method to decrypt a string using DES
    public static String decryptDES(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decodedData = Base64.decode(encryptedData, Base64.DEFAULT);
        byte[] decryptedData = cipher.doFinal(decodedData);

        return new String(decryptedData);
    }
}
