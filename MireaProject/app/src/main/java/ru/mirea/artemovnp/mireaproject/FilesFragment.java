package ru.mirea.artemovnp.mireaproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView lvFiles;
    private List<String> filesList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    public FilesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FilesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilesFragment newInstance(String param1, String param2) {
        FilesFragment fragment = new FilesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_files, container, false);

        lvFiles = view.findViewById(R.id.lvFiles);
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, filesList);
        lvFiles.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fabAddFile);
        fab.setOnClickListener(v -> showAddFileDialog());

        Button btnListFiles = view.findViewById(R.id.btnListFiles);
        btnListFiles.setOnClickListener(v -> listFiles());

        Button btnHideText = view.findViewById(R.id.btnHideText);
        btnHideText.setOnClickListener(v -> showHideTextDialog());

        Button btnExtractText = view.findViewById(R.id.btnExtractText);
        btnExtractText.setOnClickListener(v -> showExtractTextDialog());

        return view;
    }

    private void showAddFileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Создать новый файл");

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_new_file, null);
        EditText etFileName = dialogView.findViewById(R.id.etFileName);
        EditText etFileContent = dialogView.findViewById(R.id.etFileContent);

        builder.setView(dialogView);
        builder.setPositiveButton("Создать", (dialog, which) -> {
            String fileName = etFileName.getText().toString();
            String content = etFileContent.getText().toString();
            createFile(fileName, content);
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void createFile(String fileName, String content) {
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(content.getBytes(StandardCharsets.UTF_8));
            }

            Toast.makeText(requireContext(), "Файл создан: " + file.getAbsolutePath(),
                    Toast.LENGTH_SHORT).show();
            listFiles();
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void listFiles() {
        filesList.clear();
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                filesList.add(file.getName());
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void showHideTextDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Скрыть текст в файле");

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_hide_text, null);
        EditText etSourceFile = dialogView.findViewById(R.id.etSourceFile);
        EditText etTextToHide = dialogView.findViewById(R.id.etTextToHide);

        builder.setView(dialogView);
        builder.setPositiveButton("Скрыть", (dialog, which) -> {
            String sourceFile = etSourceFile.getText().toString();
            String textToHide = etTextToHide.getText().toString();
            hideTextInFile(sourceFile, textToHide);
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void hideTextInFile(String fileName, String text) {
        // Простая реализация стеганографии - добавление текста в конец файла
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(dir, fileName);

            try (FileOutputStream fos = new FileOutputStream(file, true)) {
                fos.write(("\n<!--HIDDEN:" + text + "-->").getBytes(StandardCharsets.UTF_8));
            }

            Toast.makeText(requireContext(), "Текст скрыт в файле", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showExtractTextDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Извлечь скрытый текст");

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_extract_text, null);
        EditText etFileToExtract = dialogView.findViewById(R.id.etFileToExtract);

        builder.setView(dialogView);
        builder.setPositiveButton("Извлечь", (dialog, which) -> {
            String fileName = etFileToExtract.getText().toString();
            extractTextFromFile(fileName);
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void extractTextFromFile(String fileName) {
        // Извлечение скрытого текста
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(dir, fileName);
            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));

            int start = content.indexOf("<!--HIDDEN:");
            if (start != -1) {
                int end = content.indexOf("-->", start);
                String hiddenText = content.substring(start + 11, end);

                new AlertDialog.Builder(requireContext())
                        .setTitle("Скрытый текст")
                        .setMessage(hiddenText)
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                Toast.makeText(requireContext(), "Скрытый текст не найден", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}