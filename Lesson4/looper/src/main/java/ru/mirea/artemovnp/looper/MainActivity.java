package ru.mirea.artemovnp.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ru.mirea.artemovnp.looper.databinding.ActivityMainBinding;
import ru.mirea.artemovnp.looper.MyLooper;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MyLooper myLooper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Handler mainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String result = msg.getData().getString("RESULT");
                binding.resultTextView.setText(result);
                Log.d("MainActivity", "Получено: " + result);
            }
        };

        myLooper = new MyLooper(mainHandler);
        myLooper.start();

        binding.sendButton.setOnClickListener(v -> {
            try {
                int age = Integer.parseInt(binding.inputAge.getText().toString());
                String job = binding.inputJob.getText().toString();

                if (age <= 0) {
                    throw new NumberFormatException();
                }

                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putInt("AGE", age);
                bundle.putString("JOB", job);
                msg.setData(bundle);

                myLooper.mHandler.sendMessage(msg);

            } catch (NumberFormatException e) {
                binding.resultTextView.setText("Ошибка: введите корректный возраст!");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myLooper != null && myLooper.mHandler != null) {
            myLooper.mHandler.getLooper().quit();
        }
    }
}