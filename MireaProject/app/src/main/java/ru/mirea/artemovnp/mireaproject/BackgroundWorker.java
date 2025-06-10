package ru.mirea.artemovnp.mireaproject;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class BackgroundWorker extends Worker {
    private static final String TAG = "BackgroundWorker";

    public BackgroundWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams
    ) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            // Имитация долгой фоновой задачи
            for (int i = 0; i < 100; i++) {
                Log.d(TAG, "Выполнение работы: " + i + "%");
                Thread.sleep(100); // Задержка для имитации работы
            }
            return Result.success();
        } catch (InterruptedException e) {
            Log.e(TAG, "Ошибка выполнения работы", e);
            return Result.failure();
        }
    }
}
