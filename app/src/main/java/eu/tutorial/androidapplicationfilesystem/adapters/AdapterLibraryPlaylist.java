package eu.tutorial.androidapplicationfilesystem.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.fragmentsMainActivity.FragmentLibrarySongs;
import eu.tutorial.androidapplicationfilesystem.classes.MusicData;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;
import eu.tutorial.androidapplicationfilesystem.interfaces.PassMusicStatus;

public class AdapterLibraryPlaylist extends RecyclerView.Adapter <AdapterLibraryPlaylist.ViewHolder>{
    Context context;
    FragmentLibrarySongs fragmentLibrarySongs;
    ArrayList<String> allSongs; //Used on first application load, before metadata and bitmap is loaded into sqlite to display allSongs information
    //Does not contain metadata

    Playlist songsList; //Contains loaded metadata and bitmap
    PassMusicStatus passToActivity;
    Playlist songsListCopy; //used for filtering purposes
    ArrayList<Integer> originalIndex; //used when selecting items during search

    Boolean selectionEnabled = false;

    public AdapterLibraryPlaylist(FragmentLibrarySongs fragmentLibrarySongs, ArrayList<String> allSongs, Playlist songsList){
        this.context = fragmentLibrarySongs.getActivity();
        this.fragmentLibrarySongs = fragmentLibrarySongs;
        this.allSongs = allSongs;
        this.songsList = songsList;
        this.songsListCopy = songsList;

        if(context instanceof PassMusicStatus){ //Checks if there is an implementation of PassMusicStatus in the context of parent activity
            passToActivity = (PassMusicStatus) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement PassDataaaa");
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView itemImage;
        TextView itemText;
        TextView itemArtist;
        TextView itemAttribute;
        CheckBox itemSelected;
        LinearLayout itemButton;
        LinearLayout backgroundImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.imageLibraryItem);
            itemText = itemView.findViewById(R.id.nameLibraryItem);
            itemArtist = itemView.findViewById(R.id.artistLibraryItem);
            itemAttribute = itemView.findViewById(R.id.lengthLibraryItem);
            itemButton = itemView.findViewById(R.id.btnLibraryItem);
            backgroundImage = itemView.findViewById(R.id.backgroundLibraryItem);
            itemSelected = itemView.findViewById(R.id.imageLibraryItemSelect);
        }
    }

    @NonNull
    @Override
    public AdapterLibraryPlaylist.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_library_song, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selectionEnabled) {  //If the selection is not enabled regular click will just play the song
                    System.out.println(songsList.getSong(holder.getAdapterPosition()).getPath());
                    passToActivity.onSongRequest(songsList.getSong(holder.getAdapterPosition()).getPath(), songsList, holder.getAdapterPosition());
                }else{ //If the selection is enabled, regular clicks turn into selection clicks
                    checkSelection(holder);
                }
            }
        });

        holder.itemButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Almsot the same as short click listener, except this one always enables the selection
                selectionEnabled=true;
                checkSelection(holder);
                return true; //returns true to not activate regular click listener
            }
        });

        holder.itemSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) { //Checks if it was changed by an actual press or programmatically, avoids methods doubling
                    checkSelection(holder);
                }
            }
        });
        return holder;
    }

    public void disableSelectMode(){
        selectionEnabled = false;
    }

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



    private class RetrievingBitmaps extends AsyncTask<Void, Void, Bitmap>{
        ViewHolder viewHolder;
        int position;
        ImageView imageView;

        public RetrievingBitmaps(ViewHolder holder, int position){
            this.viewHolder = holder;
            this.position = position;
            this.imageView = holder.itemImage;
            System.out.println(position);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            if(songsList.getLength()!=0 && songsList !=null && songsList.getSong(position)!=null) {
                Bitmap bitmap = songsList.getSong(position).getBitmap(context);
                return bitmap;
            }else {return null;}
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(songsList.getLength()!=0 && songsList !=null && songsList.getSong(position)!=null && viewHolder.itemText.getText() == songsList.getSong(position).getTitle()) {
                Glide.with(context)
                        .asBitmap()
                        .load(bitmap)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .override(120,120)
                        .centerCrop()
                        .into(viewHolder.itemImage);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLibraryPlaylist.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        //setFadeInAnimation(holder.itemSelected, position);
        if(selectionEnabled){
            //setAnimation(holder.itemView, position);
            setFadeInAnimation(holder.itemSelected, position);
            setFadeInAnimation(holder.itemImage,position);
            if(originalIndex==null || originalIndex.size()==0) {
                holder.itemSelected.setVisibility(View.VISIBLE);
                if (fragmentLibrarySongs.selectedListContains(holder.getAdapterPosition())) {
                    holder.itemSelected.setChecked(true);
                } else {
                    holder.itemSelected.setChecked(false);
                }
            }else{
                holder.itemSelected.setVisibility(View.VISIBLE);
                if (fragmentLibrarySongs.selectedListContains(originalIndex.get(holder.getAdapterPosition()))) {
                    holder.itemSelected.setChecked(true);
                } else {
                    holder.itemSelected.setChecked(false);
                }
            }
        }else{
            setFadeInAnimation(holder.itemView, position);
            setFadeOutAnimation(holder.itemSelected,position);
            setFadeInAnimation(holder.itemImage,position);
            holder.itemSelected.setVisibility(View.GONE);
        }

        if(songsList !=null) {
            if(songsList.getLength()>position) {//Method checks that the current position is not bigger than the data to be accessed in the allSongsMetadata
                //If position is bigger, it means either that data is still loading
                //Then paths are used from allSongs array and metadata is extracted directly here
                holder.itemText.setText(songsList.getSong(position).getTitle());
                holder.itemArtist.setText(songsList.getSong(position).getArtist());
                holder.itemAttribute.setText(songsList.getSong(position).getLengthString());
                if(songsList.getSong(position).getArtist()!=null){
                    holder.itemArtist.setText(songsList.getSong(position).getArtist());
                    holder.itemArtist.setVisibility(View.VISIBLE);
                }else{
                    holder.itemArtist.setVisibility(View.GONE);
                }
                new RetrievingBitmaps(holder, position).execute();
            }else{//This is the method called if allSongsMetadata is not available for the current positions' item
                //Edge case scenario on first load or during a rescan
                //Creates a new MusicData element and retrieves the metadata
                MusicData selectedSong = new MusicData(allSongs.get(position),context);
                holder.itemImage.setImageBitmap(selectedSong.getBitmap(context));
                holder.itemText.setText(selectedSong.getTitle());
                if(selectedSong.getArtist()!=null){
                    holder.itemArtist.setText(selectedSong.getArtist());
                    holder.itemArtist.setVisibility(View.VISIBLE);
                }else{
                    holder.itemArtist.setVisibility(View.GONE);
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        if(allSongs==null || allSongs.size()==0){
            //if allSongs size is 0, it means that app has already been initialized before
            //in this case allSongsMetadata is already generated
        return songsList.getLength();
        }else{
            return  allSongs.size();
        }
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

    @SuppressLint("NotifyDataSetChanged")
    public void filter(String text){
        songsList = null;
        songsList = new Playlist();
        originalIndex = null;
        originalIndex = new ArrayList<>();
        if(text.isEmpty()){
            songsList = songsListCopy;
        } else {
            text = text.toLowerCase();
            for(int i = 0; i< songsListCopy.getLength(); i++){
                if(songsListCopy.getSong(i).getPath().toLowerCase().contains(text) || songsListCopy.getSong(i).getTitle().toLowerCase().contains(text)){
                    songsList.addSong(songsListCopy.getSong(i));
                    originalIndex.add(i);
                }
            }
        }
        notifyDataSetChanged();
    }
}
