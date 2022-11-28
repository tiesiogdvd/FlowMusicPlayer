package eu.tutorial.androidapplicationfilesystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.Arrays;

public class FileListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        getSupportActionBar().hide();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        TextView noFilesText = findViewById(R.id.noFiles);
        TextView pathText = findViewById(R.id.dataPath);

        //String path = getIntent().getStringExtra("path");  ||  extra data name must exactly match specified in the intent of previous activity
        //File root = new File(path); || specifies the path which in this case is /storage/emulated/0
        //This path is essentially the starting path a device user can access without root
        //File [] filesAndFolders = root.listFiles(); || Creates an array of all items in the specified path of root
        String path = getIntent().getStringExtra("path");
        pathText.setText(path);
        File root = new File(path);
        Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
        File [] filesAndFolders = root.listFiles();

        //checks if there is any files in filesAndFolders File array
        if(filesAndFolders.equals(null) || filesAndFolders.length == 0){
           noFilesText.setVisibility(View.VISIBLE); return;
        }else noFilesText.setVisibility(View.GONE);

        //LayoutManager positions RecyclerView's items and tells them when to recycle items that have transitioned off-screen
        //There is 3 default Layout Manager types. Linear, Grid and Staggered Grid.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //filesAndFolders in this case is a list of data objects which need to be bound to ViewHolders.
        //Adapter is the thing used to bind the data to these Views.
        recyclerView.setAdapter(new Adapter(getApplicationContext(),filesAndFolders));

    }
}