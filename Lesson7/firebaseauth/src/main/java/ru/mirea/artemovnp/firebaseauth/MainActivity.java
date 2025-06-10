package ru.mirea.artemovnp.firebaseauth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonRegister, buttonLogout, buttonVerifyEmail;
    private TextView textViewStatus;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Привязка элементов
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonVerifyEmail = findViewById(R.id.buttonVerifyEmail);
        textViewStatus = findViewById(R.id.textViewStatus);

        // Обработчики кнопок
        buttonLogin.setOnClickListener(v -> signIn(
                editTextEmail.getText().toString(),
                editTextPassword.getText().toString()
        ));

        buttonRegister.setOnClickListener(v -> createAccount(
                editTextEmail.getText().toString(),
                editTextPassword.getText().toString()
        ));

        buttonLogout.setOnClickListener(v -> signOut());
        buttonVerifyEmail.setOnClickListener(v -> sendEmailVerification());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Проверка авторизации при запуске
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Пользователь авторизован
            textViewStatus.setText("Статус: авторизован как " + user.getEmail());
            if (user.isEmailVerified()) {
                buttonVerifyEmail.setVisibility(View.GONE);
            } else {
                buttonVerifyEmail.setVisibility(View.VISIBLE);
            }
            buttonLogout.setVisibility(View.VISIBLE);
            buttonLogin.setVisibility(View.GONE);
            buttonRegister.setVisibility(View.GONE);
            editTextEmail.setVisibility(View.GONE);
            editTextPassword.setVisibility(View.GONE);
        } else {
            // Пользователь не авторизован
            textViewStatus.setText("Статус: не авторизован");
            buttonLogout.setVisibility(View.GONE);
            buttonVerifyEmail.setVisibility(View.GONE);
            buttonLogin.setVisibility(View.VISIBLE);
            buttonRegister.setVisibility(View.VISIBLE);
            editTextEmail.setVisibility(View.VISIBLE);
            editTextPassword.setVisibility(View.VISIBLE);
        }
    }

    private void createAccount(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                        Toast.makeText(this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Ошибка: " + task.getException(),
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void signIn(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                        Toast.makeText(this, "Вход выполнен", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Ошибка: " + task.getException(),
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
        Toast.makeText(this, "Вы вышли", Toast.LENGTH_SHORT).show();
    }

    private void sendEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this,
                                    "Письмо отправлено на " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this,
                                    "Ошибка: " + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}