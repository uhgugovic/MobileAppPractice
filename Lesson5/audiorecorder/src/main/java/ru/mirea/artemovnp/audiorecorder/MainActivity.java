package ru.mirea.artemovnp.audiorecorder;

import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private static final String TAG = "AudioRecorder";

    private boolean wasInSettings = false;

    private Button btnRecord, btnPlay;
    private TextView tvStatus, tvTimer;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String audioFilePath;
    private boolean isRecording = false;
    private boolean isPlaying = false;
    private int recordingTime = 0;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    private final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupAudioFilePath();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermissions();
    }

    private void initViews() {
        btnRecord = findViewById(R.id.btnRecord);
        btnPlay = findViewById(R.id.btnPlay);
        tvStatus = findViewById(R.id.tvStatus);
        tvTimer = findViewById(R.id.tvTimer);
    }

    private void setupAudioFilePath() {
        audioFilePath = getExternalCacheDir().getAbsolutePath() + "/audio_record.3gp";
    }

    private void checkPermissions() {
        if (hasAllPermissions()) {
            initAppComponents();
        } else {
            // Проверяем, есть ли хотя бы часть разрешений
            boolean hasPartialPermissions = false;
            for (String permission : REQUIRED_PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                    hasPartialPermissions = true;
                    break;
                }
            }

            if (hasPartialPermissions) {
                // Если есть часть разрешений, запрашиваем только недостающие
                requestMissingPermissions();
            } else {
                // Если нет ни одного разрешения, показываем диалог
                showPermanentDenialDialog();
            }
        }
    }

    private boolean hasAllPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Для Android 13+ проверяем новое разрешение
            return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } else {
            // Для старых версий
            return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestMissingPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            if (shouldShowPermissionRationale(permissionsToRequest)) {
                showPermissionRationaleDialog(permissionsToRequest);
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        permissionsToRequest.toArray(new String[0]),
                        REQUEST_CODE_PERMISSIONS
                );
            }
        }
    }

    private boolean shouldShowPermissionRationale(List<String> permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }

    private void showPermissionRationaleDialog(List<String> permissions) {
        new AlertDialog.Builder(this)
                .setTitle("Необходимы разрешения")
                .setMessage("Для работы диктофона требуются:\n\n" +
                        "• Запись звука - для использования микрофона\n" +
                        "• Доступ к хранилищу - для сохранения записей")
                .setPositiveButton("Разрешить", (dialog, which) -> {
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            permissions.toArray(new String[0]),
                            REQUEST_CODE_PERMISSIONS
                    );
                })
                .setNegativeButton("Отказать", (dialog, which) -> {
                    showPermanentDenialDialog();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (hasAllPermissions()) {
                initAppComponents();
                Toast.makeText(this, "Разрешения получены", Toast.LENGTH_SHORT).show();
            } else {
                handlePermissionDenial();
            }
        }
    }

    private void handlePermissionDenial() {
        boolean allPermanentlyDenied = true;
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                allPermanentlyDenied = false;
                break;
            }
        }

        if (allPermanentlyDenied) {
            showPermanentDenialDialog();
        } else {
            // Запрашиваем только недостающие разрешения
            requestMissingPermissions();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Проверяем разрешения при каждом возвращении в приложение
        if (wasInSettings) {
            checkPermissions();
            wasInSettings = false;
        }
    }

    private void showPermanentDenialDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Доступ запрещен")
                .setMessage("Вы запретили запрос разрешений. Приложение не может работать без необходимых прав.")
                .setPositiveButton("Настройки", (dialog, which) -> {
                    wasInSettings = true;
                    openAppSettings();
                })
                .setNegativeButton("Закрыть", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void openAppSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Не удалось открыть настройки", Toast.LENGTH_SHORT).show();
        }
    }

    private void initAppComponents() {
        setupButtons();
        initTimer();
        updateUI();
    }

    private void setupButtons() {
        btnRecord.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else {
                startRecording();
            }
        });

        btnPlay.setOnClickListener(v -> {
            if (isPlaying) {
                stopPlaying();
            } else {
                startPlaying();
            }
        });
    }

    private void initTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRecording) {
                    recordingTime++;
                    updateTimer();
                    timerHandler.postDelayed(this, 1000);
                }
            }
        };
    }

    private void startRecording() {
        try {
            releaseMediaRecorder();

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();

            isRecording = true;
            recordingTime = 0;
            updateUI();
            timerHandler.postDelayed(timerRunnable, 1000);
        } catch (IOException e) {
            Log.e(TAG, "Ошибка записи: " + e.getMessage());
            Toast.makeText(this, "Ошибка при запуске записи", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
            } catch (IllegalStateException e) {
                Log.e(TAG, "Ошибка остановки записи: " + e.getMessage());
            }
            releaseMediaRecorder();
        }

        isRecording = false;
        timerHandler.removeCallbacks(timerRunnable);
        updateUI();
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void startPlaying() {
        try {
            releaseMediaPlayer();

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();

            isPlaying = true;
            updateUI();

            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                updateUI();
            });
        } catch (IOException e) {
            Log.e(TAG, "Ошибка воспроизведения: " + e.getMessage());
            Toast.makeText(this, "Ошибка при воспроизведении", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPlaying() {
        releaseMediaPlayer();
        isPlaying = false;
        updateUI();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void updateUI() {
        runOnUiThread(() -> {
            btnRecord.setText(isRecording ? "Остановить запись" : "Начать запись");
            btnRecord.setEnabled(hasAllPermissions());

            boolean fileExists = new File(audioFilePath).exists();
            btnPlay.setText(isPlaying ? "Остановить" : "Воспроизвести");
            btnPlay.setEnabled(fileExists && !isRecording);
            btnPlay.setVisibility(fileExists ? View.VISIBLE : View.GONE);

            if (isRecording) {
                tvStatus.setText("Идет запись...");
            } else if (isPlaying) {
                tvStatus.setText("Идет воспроизведение...");
            } else {
                tvStatus.setText(fileExists ? "Готов к работе" : "Нажмите для записи");
            }
        });
    }

    private void updateTimer() {
        runOnUiThread(() -> {
            int minutes = recordingTime / 60;
            int seconds = recordingTime % 60;
            tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopRecording();
        stopPlaying();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaRecorder();
        releaseMediaPlayer();
        timerHandler.removeCallbacks(timerRunnable);
    }
}