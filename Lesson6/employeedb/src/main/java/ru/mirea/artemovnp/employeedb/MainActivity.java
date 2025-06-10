package ru.mirea.artemovnp.employeedb;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText etName, etPower, etStrength;
    private TextView tvOutput;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etPower = findViewById(R.id.etPower);
        etStrength = findViewById(R.id.etStrength);
        tvOutput = findViewById(R.id.tvOutput);
        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnShowAll = findViewById(R.id.btnShowAll);

        db = App.getInstance().getDatabase();

        btnAdd.setOnClickListener(v -> addHero());
        btnShowAll.setOnClickListener(v -> showAllHeroes());
    }

    private void addHero() {
        String name = etName.getText().toString();
        String power = etPower.getText().toString();
        int strength = Integer.parseInt(etStrength.getText().toString());

        SuperHero hero = new SuperHero(name, power, strength);
        db.superHeroDao().insert(hero);
        Toast.makeText(this, "Герой добавлен!", Toast.LENGTH_SHORT).show();
    }

    private void showAllHeroes() {
        List<SuperHero> heroes = db.superHeroDao().getAll();
        StringBuilder sb = new StringBuilder();

        for (SuperHero hero : heroes) {
            sb.append("ID: ").append(hero.id)
                    .append("\nИмя: ").append(hero.name)
                    .append("\nСила: ").append(hero.power)
                    .append("\nУровень: ").append(hero.strengthLevel)
                    .append("\n\n");
        }

        tvOutput.setText(sb.toString());
    }
}