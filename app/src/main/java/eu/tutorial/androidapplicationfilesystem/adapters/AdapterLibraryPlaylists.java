package eu.tutorial.androidapplicationfilesystem.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.fragmentsMainActivity.FragmentLibrarySongs;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;

public class AdapterLibraryPlaylists extends RecyclerView.Adapter <AdapterLibraryPlaylists.ViewHolder> {

    Context context;
    FragmentLibrarySongs fragmentLibrarySongs;

    ArrayList<Playlist> musicPlaylists;
    ArrayList<Playlist> musicPlaylistsCopy;
    String libraryFragmentPlaylistTag = "playlist";

    ArrayList<Integer> originalIndex;
    Boolean selectionEnabled = false;

    public AdapterLibraryPlaylists(FragmentLibrarySongs fragmentLibrarySongs, ArrayList<Playlist> musicPlaylists){
    this.context = fragmentLibrarySongs.getActivity();
    this.fragmentLibrarySongs = fragmentLibrarySongs;
    this.musicPlaylists = musicPlaylists;
    this.musicPlaylistsCopy = musicPlaylists;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView playlistName;
        ImageView bitmap;
        ConstraintLayout itemButton;
        CheckBox itemSelected;

        TextView songsSize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistName = itemView.findViewById(R.id.textLibraryPlaylist);
            bitmap = itemView.findViewById(R.id.imageLibraryPlaylist);
            itemButton = itemView.findViewById(R.id.layoutLibraryPlaylist);
            itemSelected = itemView.findViewById(R.id.checkboxLibraryPlaylist);
            songsSize = itemView.findViewById(R.id.sizeLibraryPlaylist);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_library_playlists, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selectionEnabled) {
                    Playlist selectedPlaylist = musicPlaylists.get(holder.getAdapterPosition());
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    FragmentLibrarySongs fragmentLibrarySongs = new FragmentLibrarySongs();
                    System.out.println(selectedPlaylist.getPlaylistName());
                    Bundle bundle = new Bundle();
                    bundle.putString("action", "playlist");
                    bundle.putString("playlist", selectedPlaylist.getPlaylistName());
                    fragmentLibrarySongs.setArguments(bundle);
                    fragmentManager.beginTransaction()
                            // .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                            .setCustomAnimations(R.anim.pop_in, R.anim.fade_out, R.anim.pop_in, R.anim.fade_out)
                            .replace(R.id.fragmentContainerView, fragmentLibrarySongs, libraryFragmentPlaylistTag)
                            .addToBackStack(libraryFragmentPlaylistTag)
                            //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            //.setReorderingAllowed(true)
                            .commit();
                }else{
                    checkSelection(holder);
                }

            }
        });

        holder.itemButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                selectionEnabled = true;
                checkSelection(holder);
                return true;
            }
        });

        holder.itemSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isPressed()){
                    checkSelection(holder);
                }
            }
        });
        return holder;
    }


    public void setFadeInAnimation(View view, int position){
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animation.setStartOffset(100);
        view.startAnimation(animation);
    }

    public void setFadeOutAnimation(View view, int position){
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        animation.setStartOffset(100);
        view.startAnimation(animation);
    }


    public void disableSelectionMode(){selectionEnabled = false;}


    private void checkSelection(ViewHolder holder){
        if(originalIndex==null ||  originalIndex.size()==0) { //OriginalIndex is null or empty if there is no filtering from the search bar
            if (fragmentLibrarySongs.selectedListContains(holder.getAdapterPosition())) { //In this case it gets the actual adapter position to check the selectionList and add to it
                fragmentLibrarySongs.onChecked(false, holder.getAdapterPosition()); //If it the index was found, item gets unchecked and removed from list
                holder.itemSelected.setChecked(false);
            } else {
                fragmentLibrarySongs.onChecked(true, holder.getAdapterPosition());
                holder.itemSelected.setChecked(true);
            }
        }else{ //In this case there is filtering, and for that reason originalIndex is used with adapter position to get songs indexes from full list
            if (fragmentLibrarySongs.selectedListContains(originalIndex.get(holder.getAdapterPosition()))) {
                fragmentLibrarySongs.onChecked(false, originalIndex.get(holder.getAdapterPosition()));
                holder.itemSelected.setChecked(false);
            } else {
                fragmentLibrarySongs.onChecked(true, originalIndex.get(holder.getAdapterPosition()));
                holder.itemSelected.setChecked(true);
            }
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //setAnimation(holder.itemSelected,position);
        Playlist selectedPlaylist = musicPlaylists.get(position);
        holder.playlistName.setText(selectedPlaylist.getPlaylistName());
        holder.bitmap.setImageBitmap(selectedPlaylist.getPlaylistBitmap(context));
        holder.songsSize.setText(selectedPlaylist.getLength() + " songs");
        if(selectionEnabled){
            setFadeInAnimation(holder.itemSelected, position);
            if(originalIndex==null || originalIndex.size()==0) {
                System.out.println("VISIBILITY");
                holder.itemSelected.setVisibility(View.VISIBLE);
                if (fragmentLibrarySongs.selectedListContains(holder.getAdapterPosition())) {
                    holder.itemSelected.setChecked(true);
                } else {
                    holder.itemSelected.setChecked(false);
                }
            }else{
                System.out.println("VISIBILITY");
                holder.itemSelected.setVisibility(View.VISIBLE);
                if (fragmentLibrarySongs.selectedListContains(originalIndex.get(holder.getAdapterPosition()))) {
                    holder.itemSelected.setChecked(true);
                } else {
                    holder.itemSelected.setChecked(false);
                }
            }
        }else{
            System.out.println("VISIBILITYOFF");
            setFadeOutAnimation(holder.itemSelected,position);
            holder.itemSelected.setVisibility(View.GONE);
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    public void filter(String text){
        musicPlaylists = null;
        musicPlaylists = new ArrayList<>();
        originalIndex = null;
        originalIndex = new ArrayList<>();
        if(text.isEmpty()){
            musicPlaylists = musicPlaylistsCopy;
        } else {
            text = text.toLowerCase();
            for(int i=0; i<musicPlaylistsCopy.size();i++){
                if(musicPlaylistsCopy.get(i).getPlaylistName().toLowerCase().contains(text)){
                    musicPlaylists.add(musicPlaylistsCopy.get(i));
                    originalIndex.add(i);
                }
            }
        }
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return musicPlaylists.size();
    }

}
