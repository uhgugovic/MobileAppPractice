package ru.mirea.artemovnp.permissionsexample;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_CAMERA = 101;
    private TextView tvStatus;
    private Button btnCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        btnCheck = findViewById(R.id.btnCheck);

        btnCheck.setOnClickListener(v -> checkCameraPermission());
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            updateStatus("Разрешение камеры получено");
            Toast.makeText(this, "Камера доступна", Toast.LENGTH_SHORT).show();
        } else {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            // Показываем объяснение, если пользователь ранее отказал
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Нужен доступ к камере")
                    .setMessage("Это приложение требует доступ к камере для своей работы")
                    .setPositiveButton("OK", (dialog, which) -> {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                REQUEST_CODE_CAMERA
                        );
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        } else {
            // Запрашиваем разрешение напрямую
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_CAMERA
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateStatus("Камера доступна");
                Toast.makeText(this, "Разрешение получено", Toast.LENGTH_SHORT).show();
            } else {
                updateStatus("Доступ к камере запрещен");
                Toast.makeText(this, "Без камеры функциональность ограничена",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateStatus(String message) {
        tvStatus.setText("Статус: " + message);
    }
}