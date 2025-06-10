package ru.mirea.artemovnp.serviceapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ru.mirea.artemovnp.serviceapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkPermissions();

        binding.btnStart.setOnClickListener(v -> startService());
        binding.btnStop.setOnClickListener(v -> stopService());
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    PERMISSION_REQUEST_CODE);
        }
    }

    private void startService() {
        Intent serviceIntent = new Intent(this, PlayerService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
        Toast.makeText(this, "Сервис запущен", Toast.LENGTH_SHORT).show();
    }

    private void stopService() {
        Intent serviceIntent = new Intent(this, PlayerService.class);
        stopService(serviceIntent);
        Toast.makeText(this, "Сервис остановлен", Toast.LENGTH_SHORT).show();
    }
}