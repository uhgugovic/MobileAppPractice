package ru.mirea.artemovnp.mireaproject;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.mirea.artemovnp.mireaproject.databinding.FragmentCameraNoteBinding;

public class CameraNoteFragment extends Fragment {
    private FragmentCameraNoteBinding binding;
    private boolean isWork = false;
    private Uri imageUri;
    private ActivityResultLauncher<String> permLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // permission launcher
        permLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), granted -> {
                    isWork = granted;
                    if (!granted) {
                        Toast.makeText(requireContext(), "Нужно разрешение на камеру", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        // camera intent launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            binding.photoView.setImageURI(imageUri);
                            Toast.makeText(requireContext(), "Фото сохранено", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCameraNoteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // сразу запросим разрешение
        permLauncher.launch(Manifest.permission.CAMERA);

        binding.btnTakePhoto.setOnClickListener(v -> {
            if (isWork) {
                launchCamera();
            } else {
                permLauncher.launch(Manifest.permission.CAMERA);
            }
        });
    }

    private void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File photoFile = createImageFile();
            String authorities = requireContext().getPackageName() + ".fileprovider";
            imageUri = FileProvider.getUriForFile(requireContext(), authorities, photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraLauncher.launch(cameraIntent);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Ошибка при создании файла", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}