package ru.mirea.artemovnp.data_thread;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.TimeUnit;

import ru.mirea.artemovnp.data_thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Runnable runn1 = new Runnable() {
            public void run() {
                binding.tvInfo.setText("runn1 выполнен");
                Log.d("ThreadDemo", "runn1 выполнен в runOnUiThread");
            }
        };

        final Runnable runn2 = new Runnable() {
            public void run() {
                binding.tvInfo.setText("runn2 выполнен");
                Log.d("ThreadDemo", "runn2 выполнен через post()");
            }
        };

        final Runnable runn3 = new Runnable() {
            public void run() {
                binding.tvInfo.setText("runn3 выполнен");
                Log.d("ThreadDemo", "runn3 выполнен через postDelayed()");
            }
        };

        binding.btnStart.setOnClickListener(v -> {
            binding.tvInfo.setText("Запуск потоков...");

            new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(2);

                    runOnUiThread(runn1);

                    TimeUnit.SECONDS.sleep(1);

                    binding.tvInfo.postDelayed(runn3, 2000);

                    binding.tvInfo.post(runn2);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }
}