package ru.mirea.artemovnp.notebook;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION = 100;
    private EditText etFileName, etQuote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etFileName = findViewById(R.id.etFileName);
        etQuote = findViewById(R.id.etQuote);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnLoad = findViewById(R.id.btnLoad);

        // Проверка разрешений
        if (!checkPermissions()) {
            requestPermissions();
        }

        btnSave.setOnClickListener(v -> saveToFile());
        btnLoad.setOnClickListener(v -> loadFromFile());
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_CODE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Разрешения не получены!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveToFile() {
        String fileName = etFileName.getText().toString() + ".txt";
        String quote = etQuote.getText().toString();

        if (fileName.isEmpty() || quote.isEmpty()) {
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(quote);
            osw.close();

            Toast.makeText(this, "Файл сохранён: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadFromFile() {
        String fileName = etFileName.getText().toString() + ".txt";

        if (fileName.isEmpty()) {
            Toast.makeText(this, "Введите название файла!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(dir, fileName);

            if (!file.exists()) {
                Toast.makeText(this, "Файл не найден!", Toast.LENGTH_SHORT).show();
                return;
            }

            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            etQuote.setText(sb.toString());
            reader.close();
        } catch (IOException e) {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}