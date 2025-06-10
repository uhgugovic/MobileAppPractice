package ru.mirea.artemovnp.mireaproject;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VoiceNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VoiceNoteFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private MediaRecorder recorder;
    private MediaPlayer player;
    private String recordFilePath;
    private boolean isRecording = false;
    private boolean isPlaying = false;
    private TextView statusText;
    private Button recordButton;
    private Button playButton;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VoiceNoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VoiceNoteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VoiceNoteFragment newInstance(String param1, String param2) {
        VoiceNoteFragment fragment = new VoiceNoteFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voice_note, container, false);

        statusText = view.findViewById(R.id.statusText);
        recordButton = view.findViewById(R.id.recordButton);
        playButton = view.findViewById(R.id.playButton);

        recordButton.setOnClickListener(v -> toggleRecording());
        playButton.setOnClickListener(v -> togglePlayback());

        return view;
    }

    private void toggleRecording() {
        if (isRecording) {
            stopRecording();
            recordButton.setText("Начать запись");
            statusText.setText("Запись завершена: " + recordFilePath);
        } else {
            if (checkPermissions()) {
                startRecording();
                recordButton.setText("Остановить запись");
                statusText.setText("Идет запись...");
            }
        }
    }

    private void togglePlayback() {
        if (isPlaying) {
            stopPlaying();
            playButton.setText("Воспроизвести");
        } else {
            if (recordFilePath != null) {
                startPlaying();
                playButton.setText("Остановить");
            } else {
                Toast.makeText(requireContext(), "Сначала сделайте запись", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
            return false;
        }
        return true;
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        recordFilePath = requireActivity().getExternalFilesDir(Environment.DIRECTORY_MUSIC) +
                "/audio_" + timeStamp + ".3gp";

        recorder.setOutputFile(recordFilePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
            recorder.start();
            isRecording = true;
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Ошибка записи", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
            isRecording = false;
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(recordFilePath);
            player.prepare();
            player.start();
            isPlaying = true;

            player.setOnCompletionListener(mp -> {
                stopPlaying();
                playButton.setText("Воспроизвести");
            });
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Ошибка воспроизведения", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPlaying() {
        if (player != null) {
            player.release();
            player = null;
            isPlaying = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
                recordButton.setText("Остановить запись");
                statusText.setText("Идет запись...");
            } else {
                Toast.makeText(requireContext(), "Нужно разрешение на запись", Toast.LENGTH_SHORT).show();
            }
        }
    }
}