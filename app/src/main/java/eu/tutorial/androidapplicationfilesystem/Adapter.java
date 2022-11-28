package eu.tutorial.androidapplicationfilesystem;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;


//RecyclerView adapter binds data to ViewHolders. ViewHolder is a container of an item. ViewGroup is a group of these containers.
//Point of a RecyclerView is to not load any data which is outside of view area. It recycles ViewGroups which go out of bounds and reuses them for other data
//public class adapterClassName extends RecyclerView.Adapter <adapterClassName.ViewHolder>{}
public class Adapter extends RecyclerView.Adapter <Adapter.ViewHolder> {

    Context context;
    File[] filesAndFolders;


    public Adapter(Context context, File[] filesAndFolders) {
        this.context = context;
        this.filesAndFolders = filesAndFolders;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        //Views are grabbed from recycler_item layout file
        //These views get values assigned to them
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iconView);
            textView = itemView.findViewById(R.id.fileNameTextView);
        }
    }



    @NonNull
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //This is where the layout gets inflated (Giving a look to all rows)
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }




    public int getItemCount() {
        return filesAndFolders.length;

        //recycler view wants to know the number of items to be displayed in total
    }




    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        //Here values get assigned to the views created in the recycler_item layout file
        //It's based on the position of the recycler view

        //it grabs data template from public Class ViewHolder and in this case has imageView and textView data
        File selectedFile = filesAndFolders[position];
        holder.textView.setText(selectedFile.getName());
        if (selectedFile.isDirectory()){
            holder.imageView.setImageResource(R.drawable.ic_folder);
        }else{
            holder.imageView.setImageResource(R.drawable.ic_file);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedFile.isDirectory()){
                    //opens selected directory on click
                    Intent intent = new Intent(context, FileListActivity.class); //Creates an intent from the current Activity to another FileListActivity's class
                    String path = selectedFile.getAbsolutePath(); //gets an absolute path of selectedFile
                    intent.putExtra("path", path);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //it will open the same activity over and over
                    context.startActivity(intent);

                }else {
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        String type = "image/*";
                        intent.setDataAndType(Uri.parse(selectedFile.getAbsolutePath()), type);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        //opens selected file on click
                    } catch(Exception e){
                        Toast.makeText(context, "Cannot open file", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
