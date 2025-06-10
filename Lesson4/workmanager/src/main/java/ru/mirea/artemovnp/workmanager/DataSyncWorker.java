package ru.mirea.artemovnp.workmanager;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Date;

public class DataSyncWorker extends Worker {
    private static final String TAG = "DataSyncWorker";

    public DataSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Log.d(TAG, "Синхронизация начата: " + new Date());
            Thread.sleep(5000);

            String serverUrl = getInputData().getString("SERVER_URL");
            Log.d(TAG, "Сервер: " + serverUrl);

            Data outputData = new Data.Builder()
                    .putString("RESULT", "Успешно: " + new Date())
                    .build();

            return Result.success(outputData);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка", e);
            return Result.failure();
        }
    }
}
