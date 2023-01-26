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
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.classes.MusicData;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;
import eu.tutorial.androidapplicationfilesystem.classes.ViewModelMain;


public class AdapterPlaylistsDialogLibrary extends RecyclerView.Adapter <AdapterPlaylistsDialogLibrary.ViewHolder>{

    Context context;
    ArrayList<Playlist> musicPlaylists; //All playlists with song data
    ArrayList<MusicData> songsList;
    ViewModelMain viewModelMain;


    public AdapterPlaylistsDialogLibrary(Context context, ArrayList<Playlist> musicPlaylists, ArrayList<MusicData> songs) {
        this.context = context;
        this.songsList = songs;
        viewModelMain = new ViewModelProvider((ViewModelStoreOwner) context).get(ViewModelMain.class);
        this.musicPlaylists = viewModelMain.getPlaylists().getValue(); //playlists retrieved from viewModel livedata
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
    public AdapterPlaylistsDialogLibrary.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Retrieves playlist and sets ViewHolder with it's name
        Playlist selectedPlaylist = musicPlaylists.get(position);
        holder.playlistName.setText(selectedPlaylist.getPlaylistName());
        holder.icon.setImageResource(R.drawable.ic_action_note);

        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            final String playlistName = selectedPlaylist.getPlaylistName();
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    viewModelMain.addSongList(songsList,playlistName);
                }else{
                    viewModelMain.removeSongList(songsList,playlistName);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(musicPlaylists!=null) {
            return musicPlaylists.size();
        }
        return 0;
    }


}
