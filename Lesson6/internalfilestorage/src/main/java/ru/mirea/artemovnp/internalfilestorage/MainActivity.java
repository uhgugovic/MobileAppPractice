package ru.mirea.artemovnp.internalfilestorage;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private static final String FILE_NAME = "memorable_date.txt";
    private EditText etDate, etDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDate = findViewById(R.id.etDate);
        etDescription = findViewById(R.id.etDescription);
        Button btnSave = findViewById(R.id.btnSave);

        loadData();

        btnSave.setOnClickListener(v -> saveData());
    }

    private void saveData() {
        String date = etDate.getText().toString();
        String description = etDescription.getText().toString();
        String data = date + "\n" + description;

        try (FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE)) {
            fos.write(data.getBytes());
            Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadData() {
        try (FileInputStream fis = openFileInput(FILE_NAME);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

            String line;
            StringBuilder content = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            String[] parts = content.toString().split("\n");
            if (parts.length >= 2) {
                etDate.setText(parts[0]);
                etDescription.setText(parts[1]);
            }
        } catch (IOException e) {
            // Файл не существует (первый запуск)
        }
    }
}
