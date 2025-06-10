package ru.mirea.artemovnp.mireaproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String PREFS_NAME = "ProfilePrefs";
    private EditText etName, etAge, etEmail;
    private RadioGroup rgGender;
    private Button btnSave;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        etName = view.findViewById(R.id.etName);
        etAge = view.findViewById(R.id.etAge);
        etEmail = view.findViewById(R.id.etEmail);
        rgGender = view.findViewById(R.id.rgGender);
        btnSave = view.findViewById(R.id.btnSave);

        loadProfileData();

        btnSave.setOnClickListener(v -> saveProfileData());

        return view;
    }

    private void loadProfileData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        etName.setText(prefs.getString("name", ""));
        etAge.setText(String.valueOf(prefs.getInt("age", 0)));
        etEmail.setText(prefs.getString("email", ""));
        rgGender.check(prefs.getInt("gender", R.id.rbMale));
    }

    private void saveProfileData() {
        String name = etName.getText().toString();
        String ageStr = etAge.getText().toString();
        String email = etEmail.getText().toString();
        int genderId = rgGender.getCheckedRadioButtonId();

        if (name.isEmpty() || ageStr.isEmpty() || email.isEmpty()) {
            Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            if (age <= 0 || age > 120) {
                throw new NumberFormatException();
            }

            SharedPreferences.Editor editor = requireActivity()
                    .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit();
            editor.putString("name", name);
            editor.putInt("age", age);
            editor.putString("email", email);
            editor.putInt("gender", genderId);
            editor.apply();

            Toast.makeText(getContext(), "Данные сохранены", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Введите корректный возраст", Toast.LENGTH_SHORT).show();
        }
    }
}