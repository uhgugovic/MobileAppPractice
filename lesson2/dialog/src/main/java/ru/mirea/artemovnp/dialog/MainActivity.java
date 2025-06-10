package ru.mirea.artemovnp.dialog;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickShowDialog(View view) {
        MyDialogFragment dialog = new MyDialogFragment();
        dialog.show(getSupportFragmentManager(), "mirea");
    }

    public void onOkClicked() {
        Toast.makeText(getApplicationContext(), "Вы выбрали OK!", Toast.LENGTH_LONG).show();
    }

    public void onCancelClicked() {
        Toast.makeText(getApplicationContext(), "Вы выбрали Отмена!", Toast.LENGTH_LONG).show();
    }

    public void onNeutralClicked() {
        Toast.makeText(getApplicationContext(), "Вы выбрали Позже!", Toast.LENGTH_LONG).show();
    }

    public void showSnackbar(View view) {
        Snackbar.make(
                        findViewById(android.R.id.content),
                        "Это Snackbar-уведомление",
                        Snackbar.LENGTH_LONG
                )
                .setAction("Действие", v -> {
                    Toast.makeText(this, "Действие выполнено!", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    public void showTimePickerDialog(View view) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(
                this,
                (timeView, selectedHour, selectedMinute) -> {
                    String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                    Toast.makeText(this, "Выбрано время: " + time, Toast.LENGTH_SHORT).show();
                },
                hour, minute, true //
        );
        timePicker.show();
    }

    public void showDatePickerDialog(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (dateView, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format("%02d.%02d.%d", selectedDay, selectedMonth + 1, selectedYear);
                    Toast.makeText(this, "Выбрана дата: " + date, Toast.LENGTH_SHORT).show();
                },
                year, month, day
        );
        datePicker.show();
    }

    public void showProgressDialog(View view) {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Загрузка");
        progress.setMessage("Пожалуйста, подождите...");
        progress.setCancelable(false);
        progress.show();

        new Handler().postDelayed(() -> progress.dismiss(), 3000);
    }
}