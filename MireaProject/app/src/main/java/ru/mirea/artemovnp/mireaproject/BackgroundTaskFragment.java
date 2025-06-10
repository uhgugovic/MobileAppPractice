package ru.mirea.artemovnp.mireaproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BackgroundTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BackgroundTaskFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView statusTextView;
    private Button startButton;
    private Button cancelButton;

    public BackgroundTaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BackgroundTaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BackgroundTaskFragment newInstance(String param1, String param2) {
        BackgroundTaskFragment fragment = new BackgroundTaskFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_background_task, container, false);

        statusTextView = view.findViewById(R.id.statusTextView);
        startButton = view.findViewById(R.id.startButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        startButton.setOnClickListener(v -> startBackgroundTask());
        cancelButton.setOnClickListener(v -> cancelBackgroundTask());

        return view;
    }
    private void startBackgroundTask() {
        statusTextView.setText("Запуск фоновой задачи...");

        // Условия для выполнения задачи (например, только при подключении к интернету)
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(false)
                .build();

        // Создание запроса на выполнение работы
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(BackgroundWorker.class)
                .setConstraints(constraints)
                .build();

        // Запуск задачи
        WorkManager.getInstance(requireContext()).enqueue(workRequest);

        // Отслеживание состояния задачи
        WorkManager.getInstance(requireContext())
                .getWorkInfoByIdLiveData(workRequest.getId())
                .observe(getViewLifecycleOwner(), workInfo -> {
                    if (workInfo != null) {
                        String status = "Статус: " + workInfo.getState().name();
                        statusTextView.setText(status);
                    }
                });
    }

    private void cancelBackgroundTask() {
        WorkManager.getInstance(requireContext()).cancelAllWork();
        statusTextView.setText("Задача отменена");
    }
}