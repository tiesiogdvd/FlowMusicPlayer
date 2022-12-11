package eu.tutorial.androidapplicationfilesystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;
import eu.tutorial.androidapplicationfilesystem.classes.PlaylistDatabaseHelper;


public class AdapterPlaylists extends RecyclerView.Adapter <AdapterPlaylists.ViewHolder>{

    Context context;
    ArrayList<Playlist> musicPlaylists;
    File musicFile;

    public AdapterPlaylists(Context context, ArrayList<Playlist> musicPlaylists, File musicFile) {
        this.context = context;
        this.musicPlaylists = musicPlaylists;
        this.musicFile = musicFile;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView playlistName;
        CheckBox checkbox;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistName = itemView.findViewById(R.id.playlistName);
            checkbox = itemView.findViewById(R.id.iconPlaylistCheckmark);
            icon = itemView.findViewById(R.id.iconPlaylist);
        }
    }

    @NonNull
    public AdapterPlaylists.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Playlist selectedPlaylist = musicPlaylists.get(position);
        holder.playlistName.setText(selectedPlaylist.getPlaylistName());
        holder.icon.setImageResource(R.drawable.ic_action_note);
        /*if (selectedPlaylist.inPlaylist(path)){
            holder.checkboxImage.setChecked(true);
        }else{
            holder.checkboxImage.setChecked(false);
        }*/
        if (musicPlaylists.get(position).inPlaylist(musicFile)){
            holder.checkbox.setChecked(true);
        }else{
            holder.checkbox.setChecked(false);
        }

        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            PlaylistDatabaseHelper db = new PlaylistDatabaseHelper(context);
            String playlistName = selectedPlaylist.getPlaylistName();
            String filePath = musicFile.getAbsolutePath();
            String date;
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    System.out.println("Checked");
                    selectedPlaylist.addSong(musicFile);
                    date = selectedPlaylist.getSongDate(musicFile.getAbsolutePath());
                    db.addSong(filePath, selectedPlaylist.getPlaylistName(), date);

                }else{
                    System.out.println("Unchecked");
                    System.out.println(selectedPlaylist.removeSong(musicFile));
                    db.removeSong(musicFile.getAbsolutePath(), selectedPlaylist.getPlaylistName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicPlaylists.size();
    }


}
