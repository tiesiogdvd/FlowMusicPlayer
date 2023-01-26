package eu.tutorial.androidapplicationfilesystem.activities.fragmentsSettings;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.android.material.switchmaterial.SwitchMaterial;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.classes.Settings;

public class FragmentSettingsLooks extends Fragment {
    SharedPreferences sh;
    SwitchMaterial imageCovering;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings_looks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initSettings();
        initBarListeners();
        initListeners();
    }



    private void initViews(View view) {
        sh = requireActivity().getSharedPreferences("lastMusic",MODE_PRIVATE);
        Settings.setSettings(sh);
        imageCovering = view.findViewById(R.id.settingsLooksImageCovering);
    }

    private void initSettings() {
        imageCovering.setChecked(Settings.getBackgroundMode());
    }

    private void initListeners() {
        imageCovering.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Settings.setBackgroundMode(isChecked);
            }
        });
    }

    private void initBarListeners() {

    }






}