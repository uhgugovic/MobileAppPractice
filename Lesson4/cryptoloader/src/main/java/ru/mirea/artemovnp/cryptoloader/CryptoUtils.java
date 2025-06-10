package ru.mirea.artemovnp.cryptoloader;

import android.util.Log;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

public class CryptoUtils {
    private static final String AES = "AES";
    private static final String SHA1PRNG = "SHA1PRNG";

    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance(SHA1PRNG);
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
        keyGenerator.init(256, secureRandom);
        return keyGenerator.generateKey();
    }

    public static byte[] encrypt(String text, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(text.getBytes());
        Log.d("CryptoUtils", "Зашифровано: " + bytesToHex(encrypted)); // Логируем
        return encrypted;
    }

    public static String decrypt(byte[] encryptedData, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedData);
        Log.d("CryptoUtils", "Расшифровано: " + new String(decryptedBytes)); // Логируем
        return new String(decryptedBytes);
    }

    static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}