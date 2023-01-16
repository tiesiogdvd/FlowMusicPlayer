package eu.tutorial.androidapplicationfilesystem.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.fragmentsMainActivity.FragmentLibrarySongs;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;

public class AdapterLibraryPlaylists extends RecyclerView.Adapter <AdapterLibraryPlaylists.ViewHolder> {

    Context context;
    ArrayList<Playlist> musicPlaylists;
    ArrayList<Playlist> musicPlaylistsCopy;
    String libraryFragmentPlaylistTag = "playlist";

    public AdapterLibraryPlaylists(Context context, ArrayList<Playlist> musicPlaylists){
    this.context = context;
    this.musicPlaylists = musicPlaylists;
    this.musicPlaylistsCopy = musicPlaylists;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView playlistName;
        ImageView bitmap;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistName = itemView.findViewById(R.id.textLibraryPlaylist);
            bitmap = itemView.findViewById(R.id.imageLibraryPlaylist);
            linearLayout = itemView.findViewById(R.id.layoutLibraryPlaylist);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_library, parent, false);
        final ViewHolder holder= new ViewHolder(view);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Playlist selectedPlaylist = musicPlaylists.get(holder.getAdapterPosition());
                FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
                FragmentLibrarySongs fragmentLibrarySongs = new FragmentLibrarySongs();
                System.out.println(selectedPlaylist.getPlaylistName());
                Bundle bundle = new Bundle();
                bundle.putString("action","playlist");
                bundle.putString("playlist",selectedPlaylist.getPlaylistName());
                fragmentLibrarySongs.setArguments(bundle);
                fragmentManager.beginTransaction()
                        // .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                        .setCustomAnimations(R.anim.pop_in, R.anim.fade_out, R.anim.pop_in, R.anim.fade_out)
                        .replace(R.id.fragmentContainerView, fragmentLibrarySongs, libraryFragmentPlaylistTag)
                        .addToBackStack(libraryFragmentPlaylistTag)
                        //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        //.setReorderingAllowed(true)
                        .commit();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Playlist selectedPlaylist = musicPlaylists.get(position);
        holder.playlistName.setText(selectedPlaylist.getPlaylistName());
        holder.bitmap.setImageBitmap(selectedPlaylist.getPlaylistBitmap(context));
    }


    @SuppressLint("NotifyDataSetChanged")
    public void filter(String text){
        musicPlaylists = null;
        musicPlaylists = new ArrayList<>();
        if(text.isEmpty()){
            musicPlaylists = musicPlaylistsCopy;
        } else {
            text = text.toLowerCase();
            for(Playlist playlist: musicPlaylistsCopy){
                if(playlist.getPlaylistName().toLowerCase().contains(text)){
                    System.out.println("Contains");
                    musicPlaylists.add(playlist);
                }
            }
        }
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        System.out.println("zajebaliiiiiiii");
        return musicPlaylists.size();
    }

}
