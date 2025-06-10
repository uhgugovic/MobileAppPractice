package ru.mirea.artemovnp.thread;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ru.mirea.artemovnp.thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.calculateButton.setOnClickListener(v -> {
            try {
                int totalPairs = Integer.parseInt(binding.inputTotalPairs.getText().toString());
                int studyDays = Integer.parseInt(binding.inputStudyDays.getText().toString());

                if (studyDays == 0) {
                    throw new ArithmeticException("Деление на ноль!");
                }

                new Thread(() -> {
                    double averagePairs = (double) totalPairs / studyDays;
                    runOnUiThread(() -> {
                        binding.resultTextView.setText(
                                String.format("Среднее: %.2f пар в день", averagePairs)
                        );
                    });
                }).start();

            } catch (NumberFormatException e) {
                binding.resultTextView.setText("Ошибка: введите числа!");
            } catch (ArithmeticException e) {
                binding.resultTextView.setText("Ошибка: учебных дней не может быть 0!");
            }
        });
    }
}