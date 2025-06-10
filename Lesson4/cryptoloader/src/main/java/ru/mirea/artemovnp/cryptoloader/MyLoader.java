package ru.mirea.artemovnp.cryptoloader;

import android.content.Context;
import android.os.Bundle;

import androidx.loader.content.AsyncTaskLoader;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MyLoader extends AsyncTaskLoader<String> {
    private final byte[] encryptedData;
    private final byte[] keyBytes;

    public MyLoader(Context context, byte[] encryptedData, byte[] keyBytes) {
        super(context);
        this.encryptedData = encryptedData;
        this.keyBytes = keyBytes;
    }

    @Override
    public String loadInBackground() {
        try {
            SecretKey originalKey = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
            return CryptoUtils.decrypt(encryptedData, originalKey);
        } catch (Exception e) {
            return "Ошибка дешифрования: " + e.getMessage();
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
