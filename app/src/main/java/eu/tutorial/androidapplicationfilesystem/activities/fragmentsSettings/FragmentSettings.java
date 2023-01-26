package eu.tutorial.androidapplicationfilesystem.activities.fragmentsSettings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import eu.tutorial.androidapplicationfilesystem.R;

public class FragmentSettings extends Fragment {

    public FragmentSettings() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentSettingsAudioDiscovery fragmentSettingsAudioDiscovery = new FragmentSettingsAudioDiscovery();
        FragmentSettingsLooks fragmentSettingsLooks = new FragmentSettingsLooks();
        String fragmentSettingsTag = "fragmentSettingsAudioDiscovery";
        String fragmentSettingsLooksTag = "fragmentSettingsLooks";
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        LinearLayout linearLayout = view.findViewById(R.id.settingsDiscovery);
        LinearLayout linearLayout1 = view.findViewById(R.id.settingsLooks);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.pop_in, R.anim.fade_out, R.anim.pop_in, R.anim.fade_out)
                        .replace(R.id.settingsFragment, fragmentSettingsAudioDiscovery, fragmentSettingsTag)
                        .addToBackStack(fragmentSettingsTag)
                        //.addSharedElement(imageView ,"toMusicPlayer")
                        .commit();
            }
        });

        linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.pop_in, R.anim.fade_out, R.anim.pop_in, R.anim.fade_out)
                        .replace(R.id.settingsFragment, fragmentSettingsLooks, fragmentSettingsLooksTag)
                        .addToBackStack(fragmentSettingsLooksTag)
                        //.addSharedElement(imageView ,"toMusicPlayer")
                        .commit();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}