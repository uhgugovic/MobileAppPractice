package ru.mirea.artemovnp.timeservice;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private Button button;
    private final String HOST = "time.nist.gov"; // Сервер времени
    private final int PORT = 13; // Порт для daytime-сервиса

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);

        button.setOnClickListener(v -> {
            new GetTimeTask().execute(); // Запуск AsyncTask
        });
    }

    // AsyncTask для выполнения сетевого запроса в фоне
    private class GetTimeTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String timeResult = "";
            try {
                // Подключение к серверу
                Socket socket = new Socket(HOST, PORT);
                BufferedReader reader = SocketUtils.getReader(socket);

                // Чтение данных (первая строка игнорируется)
                reader.readLine(); // Пропускаем первую строку
                timeResult = reader.readLine(); // Читаем вторую строку (с датой и временем)

                if (!timeResult.isEmpty()) {
                    String[] parts = timeResult.split(" ");
                    if (parts.length >= 3) {
                        String date = parts[1]; // "24-06-25"
                        String time = parts[2]; // "10:20:30"
                        timeResult = "Дата: " + date + "\nВремя: " + time;
                    }
                }
                socket.close(); // Закрываем соединение
            } catch (IOException e) {
                Log.e("SocketError", "Ошибка подключения: " + e.getMessage());
                timeResult = "Ошибка: " + e.getMessage();
            }
            return timeResult;
        }



        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            textView.setText(result); // Выводим результат на экран
        }
    }
}