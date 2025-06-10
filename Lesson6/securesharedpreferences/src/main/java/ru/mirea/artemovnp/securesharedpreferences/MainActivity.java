package ru.mirea.artemovnp.securesharedpreferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import java.io.IOException;
import java.security.GeneralSecurityException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private static final String IMAGE_FILE = "poet_image.jpg";
    private EditText etPoetName;
    private ImageView ivPoetImage;
    private SharedPreferences securePrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPoetName = findViewById(R.id.etPoetName);
        ivPoetImage = findViewById(R.id.ivPoetImage);
        Button btnSelectImage = findViewById(R.id.btnSelectImage);
        Button btnSave = findViewById(R.id.btnSave);

        // Инициализация EncryptedSharedPreferences
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            securePrefs = EncryptedSharedPreferences.create(
                    "secret_prefs",
                    masterKeyAlias,
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        // Загрузка данных
        loadData();

        btnSelectImage.setOnClickListener(v -> openGallery());
        btnSave.setOnClickListener(v -> saveData());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                // Копируем изображение в локальное хранилище
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                saveImageToInternalStorage(bitmap);
                ivPoetImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveImageToInternalStorage(Bitmap bitmap) {
        try {
            File file = new File(getFilesDir(), IMAGE_FILE);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        // Текст
        etPoetName.setText(securePrefs.getString("POET_NAME", ""));

        // Изображение
        File file = new File(getFilesDir(), IMAGE_FILE);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ivPoetImage.setImageBitmap(bitmap);
        }
    }

    private void saveData() {
        securePrefs.edit()
                .putString("POET_NAME", etPoetName.getText().toString())
                .apply();
    }
}