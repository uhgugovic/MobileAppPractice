package ru.mirea.artemovnp.cameraapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.mirea.artemovnp.cameraapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION = 100;
    private static final int CAMERA_REQUEST = 0;
    private boolean isWork = false;
    private Uri imageUri;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActivityResultLauncher<String> permLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            isWork = granted;
            if (!granted) Log.e("CAM", "Пользователь не дал камеру");
        });
        permLauncher.launch(Manifest.permission.CAMERA);

        ActivityResultCallback<ActivityResult> callback = result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                binding.imageView.setImageURI(imageUri);
            }
        };

        ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), callback);
        //	Обработчик	нажатия	на	компонент	«imageView»
        binding.imageView.setOnClickListener(v ->
        {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //	проверка на наличие разрешений для камеры
            if (isWork) {
                try {
                    File photoFile = createImageFile();
                    //	генерирование пути к файлу на основе authorities
                    String authorities = getApplicationContext().getPackageName() + ".fileprovider";
                    imageUri = FileProvider.getUriForFile(MainActivity.this, authorities, photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    cameraActivityResultLauncher.launch(cameraIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDirectory);
    }
}