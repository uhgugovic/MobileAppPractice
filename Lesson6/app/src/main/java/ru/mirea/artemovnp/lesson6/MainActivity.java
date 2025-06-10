package ru.mirea.artemovnp.lesson6;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText etGroup, etNumber, etMovie;
    private Button btnSave;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etGroup = findViewById(R.id.etGroup);
        etNumber = findViewById(R.id.etNumber);
        etMovie = findViewById(R.id.etMovie);
        btnSave = findViewById(R.id.btnSave);

        // Инициализация SharedPreferences (файл настроек "mirea_settings")
        sharedPref = getSharedPreferences("mirea_settings", MODE_PRIVATE);

        loadData();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    private void saveData() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("GROUP", etGroup.getText().toString());
        editor.putInt("NUMBER", Integer.parseInt(etNumber.getText().toString()));
        editor.putString("MOVIE", etMovie.getText().toString());
        editor.apply(); // Асинхронное сохранение
    }

    private void loadData() {
        String group = sharedPref.getString("GROUP", "");
        int number = sharedPref.getInt("NUMBER", 0);
        String movie = sharedPref.getString("MOVIE", "");

        etGroup.setText(group);
        etNumber.setText(String.valueOf(number));
        etMovie.setText(movie);
    }
}