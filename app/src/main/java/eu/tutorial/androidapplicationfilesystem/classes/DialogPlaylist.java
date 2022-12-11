package eu.tutorial.androidapplicationfilesystem.classes;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.adapters.AdapterPlaylists;

public class DialogPlaylist {


    public void createDialog(Context context, ArrayList <Playlist> playlist, File musicFile) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout_playlists);
        AppCompatButton newPlaylistButton = dialog.findViewById(R.id.layoutEdit);
        PlaylistDatabaseHelper myDB = new PlaylistDatabaseHelper(context);

        newPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                final Dialog dialogInner = new Dialog(context);
                dialogInner.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogInner.setContentView(R.layout.dialog_layout_new_playlist);
                AppCompatButton addPlaylistButton = dialogInner.findViewById(R.id.newPlaylistButton);
                EditText addPlaylistName = dialogInner.findViewById(R.id.newPlaylistName);

                addPlaylistButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String newPlaylistName = addPlaylistName.getText().toString();
                        if(!newPlaylistName.equals("")){
                            if(!playlistExists(newPlaylistName,playlist)){
                            playlist.add(new Playlist(addPlaylistName.getText().toString()));
                            String lastPlaylistName = playlist.get(playlist.size()-1).getPlaylistName();
                            String lastPlaylistDate = playlist.get(playlist.size()-1).getDate();
                            myDB.addPlaylist(lastPlaylistName,lastPlaylistDate);
                            playlist.get(playlist.size()-1).addSong(musicFile);
                            String songDate = playlist.get(playlist.size()-1).getSongDate(musicFile.getAbsolutePath());
                            myDB.addSong(musicFile.getAbsolutePath(),lastPlaylistName,lastPlaylistDate);
                            //playlist.get(playlist.size()-1).printSongsArray();
                            dialogInner.cancel();
                            }else {Toast.makeText(context, "Playlist already exists", Toast.LENGTH_SHORT).show();}
                        }else {
                            Toast.makeText(context, "Please enter the name of playlist", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialogInner.show();
                dialogInner.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialogInner.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogInner.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialogInner.getWindow().setGravity(Gravity.CENTER);
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        RecyclerView recyclerView = dialog.findViewById(R.id.playlistRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new AdapterPlaylists(context, playlist, musicFile));


    }

    private Boolean playlistExists(String newPlaylistName, ArrayList<Playlist> playlists){
        for(int i=0; i<playlists.size();i++){
            if(playlists.get(i).getPlaylistName().equals(newPlaylistName)){
                return true;
            }
        }
        return false;
    }

}