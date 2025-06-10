package ru.mirea.artemovnp.simplefragmentapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {
    private static final String CURRENT_FRAGMENT_KEY = "current_fragment";
    private static final int FRAGMENT_FIRST = 1;
    private static final int FRAGMENT_SECOND = 2;

    private int currentFragmentId = FRAGMENT_FIRST;
    private Button btnFirst, btnSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация кнопок
        btnFirst = findViewById(R.id.btn_first);
        btnSecond = findViewById(R.id.btn_second);

        // Восстановление состояния
        if (savedInstanceState != null) {
            currentFragmentId = savedInstanceState.getInt(CURRENT_FRAGMENT_KEY, FRAGMENT_FIRST);
        }

        // Настройка фрагментов в зависимости от ориентации
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setupLandscapeMode();
            // Скрываем кнопки в ландшафтной ориентации
            btnFirst.setVisibility(View.GONE);
            btnSecond.setVisibility(View.GONE);
        } else {
            setupPortraitMode();
            // Настраиваем обработчики только для портретной ориентации
            setupButtonListeners();
        }
        if (btnFirst != null && btnSecond != null) {
            setupButtonListeners();
        }
    }

    private void setupLandscapeMode() {
        // В ландшафтном режиме показываем оба фрагмента
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_first, new FirstFragment())
                .replace(R.id.fragment_second, new SecondFragment())
                .commit();
    }

    private void setupPortraitMode() {
        // В портретном режиме показываем один фрагмент
        showFragment(currentFragmentId);
    }

    private void setupButtonListeners() {
        btnFirst.setOnClickListener(v -> showFragment(FRAGMENT_FIRST));
        btnSecond.setOnClickListener(v -> showFragment(FRAGMENT_SECOND));
    }

    private void showFragment(int fragmentId) {
        currentFragmentId = fragmentId;
        Fragment fragment = null;

        switch (fragmentId) {
            case FRAGMENT_FIRST:
                fragment = new FirstFragment();
                break;
            case FRAGMENT_SECOND:
                fragment = new SecondFragment();
                break;
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_FRAGMENT_KEY, currentFragmentId);
    }
}