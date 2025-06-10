package ru.mirea.artemovnp.simplefragmentappanp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {
    private Fragment fragment1, fragment2;
    private boolean isLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment1 = new FirstFragment();
        fragment2 = new SecondFragment();

        isLandscape = getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE;

        if (!isLandscape) {
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, fragment1)
                        .commit();
            }

            Button btnFirstFragment = findViewById(R.id.btnFirstFragment);
            btnFirstFragment.setOnClickListener(v -> getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment1)
                    .commit());

            Button btnSecondFragment = findViewById(R.id.btnSecondFragment);
            btnSecondFragment.setOnClickListener(v -> getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment2)
                    .commit());

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }
}