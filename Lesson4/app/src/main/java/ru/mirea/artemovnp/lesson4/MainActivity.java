package ru.mirea.artemovnp.lesson4;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ru.mirea.artemovnp.lesson4.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (binding == null) {
            Log.e("MainActivity", "Binding is null!");
            return;
        }
        binding.editTextMirea.setText("Мой номер по списку № 1");

        binding.buttonMirea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = binding.editTextMirea.getText().toString();
                binding.textViewMirea.setText(text);
                Log.d("MainActivity", "Кнопка нажата, текст: " + text);
            }
        });

        binding.openPlayerButton.setOnClickListener(v -> {
            Log.d("MainActivity", "Кнопка openPlayerButton нажата");
            Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
            startActivity(intent);
        });
    }
}