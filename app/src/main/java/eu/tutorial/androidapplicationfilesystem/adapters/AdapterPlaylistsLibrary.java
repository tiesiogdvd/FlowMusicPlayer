package eu.tutorial.androidapplicationfilesystem.adapters;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;
import eu.tutorial.androidapplicationfilesystem.classes.ViewModelMain;

public class AdapterPlaylistsLibrary extends RecyclerView.Adapter <AdapterPlaylistsLibrary.ViewHolder> {

    Context context;
    ArrayList<Playlist> musicPlaylists;
    ViewModelMain viewModelMain;

    public AdapterPlaylistsLibrary(Context context, ArrayList<Playlist> musicPlaylists){
    this.context = context;
    this.musicPlaylists = musicPlaylists;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView playlistName;
        ImageView bitmap;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistName = itemView.findViewById(R.id.textLibraryPlaylist);
            bitmap = itemView.findViewById(R.id.imageLibraryPlaylist);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_library, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Playlist selectedPlaylist = musicPlaylists.get(position);
        holder.playlistName.setText(selectedPlaylist.getPlaylistName());
        holder.bitmap.setImageBitmap(selectedPlaylist.getPlaylistBitmap());
    }

    @Override
    public int getItemCount() {
        return musicPlaylists.size();
    }

}
