package ru.mirea.artemovnp.lesson5;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        ArrayList<HashMap<String, Object>> sensorList = new ArrayList<>();

        for (Sensor sensor : sensors) {
            HashMap<String, Object> sensorData = new HashMap<>();
            sensorData.put("Name", sensor.getName());
            sensorData.put("Value", "Max range: " + sensor.getMaximumRange());
            sensorList.add(sensorData);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                sensorList,
                android.R.layout.simple_list_item_2,
                new String[]{"Name", "Value"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );

        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }
}