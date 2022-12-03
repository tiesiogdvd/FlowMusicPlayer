package eu.tutorial.androidapplicationfilesystem.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.adapters.AdapterStorage;

public class FileListActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {

                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult");
                    System.out.println("wtf");
                    if(result.getResultCode()==1){
                        Intent intent = result.getData();
                        if(intent!=null){
                            //Data extracted here
                            String data = intent.getStringExtra("result");
                            System.out.println(data);
                            //enterText.setText(data);
                            intent.putExtra("result",data);
                            setResult(1, intent);
                            finish();
                        }
                    }
                }

            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        getSupportActionBar().hide();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        TextView noFilesText = findViewById(R.id.noFiles);
        TextView pathText = findViewById(R.id.dataPath);
        Button button = findViewById(R.id.backButton);

        //This path is essentially the starting path a device user can access without root
        String path = getIntent().getStringExtra("path");//  ||  extra data name must exactly match specified in the intent of previous activity
        pathText.setText(path);
        File root = new File(path);  //  || specifies the path which in this case is /storage/emulated/0
        Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
        File [] filesAndFolders = root.listFiles(); //  || Creates an array of all items in the specified path of root

        //checks if there is any files in filesAndFolders File array
        if(filesAndFolders.equals(null) || filesAndFolders.length == 0){
           noFilesText.setVisibility(View.VISIBLE); return;
        }else noFilesText.setVisibility(View.GONE);

        //LayoutManager positions RecyclerView's items and tells them when to recycle items that have transitioned off-screen
        //There is 3 default Layout Manager types. Linear, Grid and Staggered Grid.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //filesAndFolders in this case is a list of data objects which need to be bound to ViewHolders.
        //Adapter is the thing used to bind the data to these Views.
        //recyclerView.setAdapter(new Adapter(getApplicationContext(),filesAndFolders,activityLauncher));
        recyclerView.setAdapter(new AdapterStorage(this,filesAndFolders,activityLauncher));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                String a = "TestInput";
                intent.putExtra("result",a);
                setResult(2, intent);
                finish();
                //SecondActivity.super.onBackPressed();
            }
        });

    }
}