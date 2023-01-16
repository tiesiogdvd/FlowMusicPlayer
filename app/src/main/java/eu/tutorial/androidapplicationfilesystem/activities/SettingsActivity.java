package eu.tutorial.androidapplicationfilesystem.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.util.Objects;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.fragmentsSettings.FragmentSettings;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_FULLSCREEN);  //to hide top navbar
        Objects.requireNonNull(getSupportActionBar()).hide(); //to hide top Application Bar
        setContentView(R.layout.activity_settings);

        FragmentSettings fragmentSettings = new FragmentSettings();
        String fragmentSettingsTag = "fragmentSettings";
        FragmentManager fragmentManager = getSupportFragmentManager();

        SharedPreferences sh = getSharedPreferences("lastMusic",MODE_PRIVATE);

        System.out.println(sh.getBoolean("useMediaStore",false));

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.pop_in, R.anim.fade_out, R.anim.pop_in, R.anim.fade_out)
                .replace(R.id.settingsFragment, fragmentSettings, fragmentSettingsTag)
                //.addSharedElement(imageView ,"toMusicPlayer")
                .commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_in, R.anim.fade_out);
    }
}