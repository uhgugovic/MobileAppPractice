package ru.mirea.artemovnp.httpurlconnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView textViewIP, textViewCity, textViewRegion, textViewWeather;
    private Button buttonGetData;
    private final String IP_API_URL = "https://api.ipify.org?format=json";
    private final String WEATHER_API_URL = "https://api.open-meteo.com/v1/forecast";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewIP = findViewById(R.id.textViewIP);
        textViewCity = findViewById(R.id.textViewCity);
        textViewRegion = findViewById(R.id.textViewRegion);
        textViewWeather = findViewById(R.id.textViewWeather);
        buttonGetData = findViewById(R.id.buttonGetData);

        buttonGetData.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                new DownloadDataTask().execute();
            } else {
                Toast.makeText(this, "Нет интернета", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private class DownloadDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            // Пробуем несколько API последовательно
            String[] ipApis = {
                    "https://ipinfo.io/json?",
                    "https://ip-api.com/json",
                    "https://api.ipify.org?format=json",
                    "http://ip.jsontest.com"
            };

            String ipData = null;
            for (String apiUrl : ipApis) {
                ipData = downloadUrl(apiUrl);
                if (ipData != null) break;
            }

            if (ipData == null) return "Ошибка: Все API IP недоступны";

            try {
                JSONObject ipJson = new JSONObject(ipData);

                // Обрабатываем разные форматы ответов
                String ip, city = "Не определен", region = "Не определен";

                if (ipJson.has("query")) { // ip-api.com формат
                    ip = ipJson.getString("query");
                    city = ipJson.optString("city");
                    region = ipJson.optString("regionName", region);
                } else if (ipJson.has("ip")) { // ipify.org формат
                    ip = ipJson.getString("ip");
                } else {
                    return "Ошибка: Неизвестный формат ответа";
                }

                String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=55.75&longitude=37.61&current_weather=true";
                String weatherData = downloadUrl(weatherUrl);

                if (weatherData == null) {
                    return ip + "|" + city + "|" + region + "|" + "Погода: сервис недоступен";
                }

                JSONObject weatherJson = new JSONObject(weatherData);
                double temperature = weatherJson.getJSONObject("current_weather").getDouble("temperature");

                return ip + "|" + city + "|" + region + "|" + temperature + "°C";

            } catch (Exception e) {
                return "Ошибка парсинга: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.startsWith("Ошибка")) {
                textViewIP.setText(result);
                textViewCity.setText("");
                textViewRegion.setText("");
                textViewWeather.setText("");
            } else {
                String[] parts = result.split("\\|");
                textViewIP.setText("IP: " + parts[0]);
                textViewCity.setText("Город: " + parts[1]);
                textViewRegion.setText("Регион: " + parts[2]);
                textViewWeather.setText("Погода: " + parts[3]);
            }
        }
    }

    private String downloadUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();
                inputStream.close();
                return stringBuilder.toString();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}