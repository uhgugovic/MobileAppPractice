package ru.mirea.artemovnp.cryptoloader;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;

import javax.crypto.SecretKey;

import ru.mirea.artemovnp.cryptoloader.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<String> {
    private static final int LOADER_ID = 101;
    private ActivityMainBinding binding;
    private SecretKey currentKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.encryptButton.setOnClickListener(v -> {
            try {
                String originalText = binding.inputText.getText().toString();
                if (!originalText.isEmpty()) {
                    // 1. Генерация ключа
                    currentKey = CryptoUtils.generateKey();
                    Log.d("MainActivity", "Ключ: " + Arrays.toString(currentKey.getEncoded()));

                    // 2. Шифрование
                    byte[] encrypted = CryptoUtils.encrypt(originalText, currentKey);
                    binding.statusText.setText("Зашифровано: " + CryptoUtils.bytesToHex(encrypted));

                    // 3. Запуск Loader для дешифрования
                    Bundle args = new Bundle();
                    args.putByteArray("ENCRYPTED", encrypted);
                    args.putByteArray("KEY", currentKey.getEncoded());
                    LoaderManager.getInstance(this).restartLoader(LOADER_ID, args, this);
                }
            } catch (Exception e) {
                showError(e.getMessage());
            }
        });
    }

    private void encryptAndLoad(String text) throws Exception {
        SecretKey secretKey = CryptoUtils.generateKey();
        binding.decryptedView.setVisibility(View.VISIBLE);
        byte[] encrypted = CryptoUtils.encrypt(text, secretKey);

        Bundle args = new Bundle();
        args.putByteArray("ENCRYPTED", encrypted);
        args.putByteArray("KEY", secretKey.getEncoded());

        LoaderManager.getInstance(this).restartLoader(
                LOADER_ID,
                args,
                this
        );

        binding.statusText.setText("Шифрование...");
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == LOADER_ID) {
            return new MyLoader(
                    this,
                    args.getByteArray("ENCRYPTED"),
                    args.getByteArray("KEY")
            );
        }
        throw new IllegalArgumentException("Unknown loader id");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String result) {
        // 4. Показываем результат дешифрования

        binding.decryptedView.setText("Расшифровано: " + result);
        Snackbar.make(binding.getRoot(),
                "Успешно дешифровано: " + result,
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), "Ошибка: " + message, Snackbar.LENGTH_LONG).show();
    }
}