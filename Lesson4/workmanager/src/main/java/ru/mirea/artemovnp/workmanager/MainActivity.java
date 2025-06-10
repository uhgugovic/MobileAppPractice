package ru.mirea.artemovnp.workmanager;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import ru.mirea.artemovnp.workmanager.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnStartSync.setOnClickListener(v -> startOneTimeSync());
        binding.btnSchedulePeriodic.setOnClickListener(v -> schedulePeriodicSync());
    }

    private void startOneTimeSync() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();

        Data inputData = new Data.Builder()
                .putString("SERVER_URL", "https://api.example.com/data")
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(DataSyncWorker.class)
                .setInputData(inputData)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this).enqueue(request);
        observeWork(request.getId());
    }

    private void schedulePeriodicSync() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresCharging(true)
                .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                DataSyncWorker.class,
                15, // Интервал (мин)
                TimeUnit.MINUTES
        ).setConstraints(constraints).build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "periodic_sync",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
        );

        Snackbar.make(binding.getRoot(), "Периодическая задача создана", Snackbar.LENGTH_LONG).show();
    }

    private void observeWork(UUID workId) {
        WorkManager.getInstance(this)
                .getWorkInfoByIdLiveData(workId)
                .observe(this, workInfo -> {
                    if (workInfo != null) {
                        binding.statusText.setText("Статус: " + workInfo.getState().name());

                        if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                            String result = workInfo.getOutputData().getString("RESULT");
                            Snackbar.make(binding.getRoot(), result, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }
}