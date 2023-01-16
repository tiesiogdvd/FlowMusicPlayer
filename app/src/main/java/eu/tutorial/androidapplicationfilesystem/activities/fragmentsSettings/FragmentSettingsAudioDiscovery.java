package eu.tutorial.androidapplicationfilesystem.activities.fragmentsSettings;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

import org.w3c.dom.Text;

import java.util.Set;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.classes.ImageSaver;
import eu.tutorial.androidapplicationfilesystem.classes.Settings;


public class FragmentSettingsAudioDiscovery extends Fragment {

    SharedPreferences sh;
    TextView lowestSizeText,lowestLengthText, mediaStore, scraping;
    SeekBar lowestSizeBar,lowestLengthBar;
    int lowestSize;
    int lowestLength;
    SwitchMaterial imageCaching, imageCachingOnLoad;
    LinearLayout deleteCache;

    private void initViews(View view){
        sh = requireActivity().getSharedPreferences("lastMusic",MODE_PRIVATE);
        Settings.setSettings(sh);
        lowestSizeText = view.findViewById(R.id.settingsDiscoveryLowestSizeText);
        lowestLengthText = view.findViewById(R.id.settingsDiscoveryAudioLengthText);
        lowestSizeBar = view.findViewById(R.id.settingsDiscoveryLowestSizeBar);
        lowestLengthBar = view.findViewById(R.id.settingsDiscoveryAudioLengthBar);
        mediaStore = view.findViewById(R.id.settingsDiscoveryAudioScanMediastore);
        scraping = view.findViewById(R.id.settingsDiscoveryAudioScanScraping);
        imageCaching = view.findViewById(R.id.settingsDiscoveryImageCaching);
        imageCachingOnLoad = view.findViewById(R.id.settingsDiscoveryImageCachingOnLoad);
        deleteCache = view.findViewById(R.id.settingsDiscoveryDeleteCache);

    }

    @SuppressLint("SetTextI18n")
    private void initSettings(){
        lowestSize = Settings.getLowestFileSize();
        lowestSizeText.setText(Integer.toString(lowestSize)+" KB");
        lowestSizeBar.setMax(3072);
        lowestSizeBar.setProgress(lowestSize);

        lowestLength = Settings.getLowestSongLength();
        lowestLengthText.setText(Integer.toString(lowestLength/1000) + " seconds");
        lowestLengthBar.setMax(180000);
        lowestLengthBar.setProgress(lowestLength);

        if(Settings.getUseMediastore()){
            mediaStore.getBackground().setColorFilter(new LightingColorFilter(ContextCompat.getColor(requireContext(),R.color.settings_selected_primary),ContextCompat.getColor(requireContext(),R.color.settings_selected_primary)));
        }else{
            mediaStore.getBackground().setColorFilter(new LightingColorFilter(ContextCompat.getColor(requireContext(),R.color.settings_selected_secondary), ContextCompat.getColor(requireContext(),R.color.settings_selected_secondary)));

        }
        if(!Settings.getUseMediastore()){
            scraping.getBackground().setColorFilter(new LightingColorFilter(ContextCompat.getColor(requireContext(),R.color.settings_selected_primary), ContextCompat.getColor(requireContext(),R.color.settings_selected_primary)));
        }else{
            scraping.getBackground().setColorFilter(new LightingColorFilter(ContextCompat.getColor(requireContext(),R.color.settings_selected_secondary), ContextCompat.getColor(requireContext(),R.color.settings_selected_secondary)));
        }

        System.out.println(Settings.getSaveImageCache());
        imageCaching.setChecked(Settings.getSaveImageCache());
        imageCachingOnLoad.setChecked(Settings.getImageCacheOnLoad());
    }

    public FragmentSettingsAudioDiscovery() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initSettings();
        initBarListeners();
        initListeners();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings_audio_discovery, container, false);
    }


    private void initBarListeners(){
        lowestSizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Settings.setLowestFileSize(progress);
                lowestSizeText.setText(Integer.toString(progress) + " KB");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        lowestLengthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Settings.setLowestSongLength(progress);
                lowestLengthText.setText(Integer.toString(progress/1000) + " seconds");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initListeners(){
        mediaStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaStore.getBackground().setColorFilter(new LightingColorFilter(ContextCompat.getColor(requireContext(),R.color.settings_selected_primary), ContextCompat.getColor(requireContext(),R.color.settings_selected_primary)));
                scraping.getBackground().setColorFilter(new LightingColorFilter(ContextCompat.getColor(requireContext(),R.color.settings_selected_secondary), ContextCompat.getColor(requireContext(),R.color.settings_selected_secondary)));
                Settings.setUseMediastore(true);
            }
        });

        scraping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scraping.getBackground().setColorFilter(new LightingColorFilter(ContextCompat.getColor(requireContext(),R.color.settings_selected_primary), ContextCompat.getColor(requireContext(),R.color.settings_selected_primary)));
                mediaStore.getBackground().setColorFilter(new LightingColorFilter(ContextCompat.getColor(requireContext(),R.color.settings_selected_secondary), ContextCompat.getColor(requireContext(),R.color.settings_selected_secondary)));
                Settings.setUseMediastore(false);
            }
        });

        imageCaching.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                System.out.println(isChecked);
                Settings.setSaveImageCache(isChecked);
            }
        });

        imageCachingOnLoad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Settings.setGetImageCacheOnLoad(isChecked);
            }
        });

        deleteCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageSaver imageSaver = new ImageSaver(requireActivity());
                imageSaver.deleteDirectory();
                Toast.makeText(requireActivity(), "Image cache deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }
}