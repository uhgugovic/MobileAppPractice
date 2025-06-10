package ru.mirea.artemovnp.buttonclicker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private TextView textViewStudent;
    private TextView textViewStatus;
    private Button btnWhoAmI;
    private Button btnItIsNotMe;
    private CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        textViewStudent = findViewById(R.id.tvOut);
        textViewStatus = findViewById(R.id.tvO);
        checkBox = findViewById(R.id.checkBox);
        btnWhoAmI = findViewById(R.id.btnWhoAmI);
        btnItIsNotMe = findViewById(R.id.btnItIsNotMe);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        View.OnClickListener oclBtnWhoAmI = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewStudent.setText("Мой номер по списку № 1");
            }
        };
        btnWhoAmI.setOnClickListener(oclBtnWhoAmI);
    }
    public void onMyButtonClick(View view){
        toggleCheckBox();
        updateStatusText("Ещё один способ!");
    }

    private void toggleCheckBox() {
        checkBox.setChecked(!checkBox.isChecked());
    }

    private void updateStatusText(String message) {
        textViewStatus.setText(message);
    }
}