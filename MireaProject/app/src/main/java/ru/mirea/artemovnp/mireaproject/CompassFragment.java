package ru.mirea.artemovnp.mireaproject;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CompassFragment extends Fragment {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private ImageView compassImage;
    private TextView compassText;

    // Слушатель сенсоров
    private final SensorEventListener sensorListener = new SensorEventListener() {
        private float[] lastAccelerometer = new float[3];
        private float[] lastMagnetometer = new float[3];
        private boolean lastAccelerometerSet = false;
        private boolean lastMagnetometerSet = false;
        private float[] rotationMatrix = new float[9];
        private float[] orientation = new float[3];

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
                lastAccelerometerSet = true;
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
                lastMagnetometerSet = true;
            }

            if (lastAccelerometerSet && lastMagnetometerSet) {
                if (SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer)) {
                    SensorManager.getOrientation(rotationMatrix, orientation);
                    float azimuthInDegrees = (float) (Math.toDegrees(orientation[0]) + 360) % 360;

                    if (compassImage != null) {
                        compassImage.setRotation(-azimuthInDegrees);
                    }
                    if (compassText != null) {
                        compassText.setText(String.format("Азимут: %.1f°", azimuthInDegrees));
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Не требуется
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compass, container, false);
        compassImage = view.findViewById(R.id.compassImage);
        compassText = view.findViewById(R.id.compass_text);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(sensorListener, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (magnetometer != null) {
            sensorManager.registerListener(sensorListener, magnetometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorListener);
    }
}