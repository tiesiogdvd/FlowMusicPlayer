package eu.tutorial.androidapplicationfilesystem.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.classes.MusicData;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;
import eu.tutorial.androidapplicationfilesystem.interfaces.PassMusicStatus;

public class AdapterLibraryFolders extends RecyclerView.Adapter <AdapterLibraryFolders.ViewHolder>{
    Context context;
    ArrayList<String> allSongs; //Used on first application load, before metadata and bitmap is loaded into sqlite to display allSongs information
    //Does not contain metadata

    Playlist allSongsMetadata; //Contains loaded metadata and bitmap
    PassMusicStatus passToActivity;
    Playlist allSongsMetadataCopy; //used for filtering purposes

    public AdapterLibraryFolders(Context context, ArrayList<String> allSongs, Playlist allSongsMetadata){
        this.context = context;
        this.allSongs = allSongs;
        this.allSongsMetadata = allSongsMetadata;
        this.allSongsMetadataCopy = allSongsMetadata;

        if(context instanceof PassMusicStatus){ //Checks if there is an implementation of PassMusicStatus in the context of parent activity
            passToActivity = (PassMusicStatus) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement PassData");
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView itemImage;
        TextView itemText;
        TextView itemArtist;
        TextView itemAttribute;
        LinearLayout linearLayout;
        LinearLayout backgroundMusic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.imageLibraryItem);
            itemText = itemView.findViewById(R.id.nameLibraryItem);
            itemArtist = itemView.findViewById(R.id.artistLibraryItem);
            itemAttribute = itemView.findViewById(R.id.lengthLibraryItem);
            linearLayout = itemView.findViewById(R.id.btnLibraryItem);
            backgroundMusic = itemView.findViewById(R.id.backgroundLibraryItem);
        }
    }

    @NonNull
    @Override
    public AdapterLibraryFolders.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_library_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLibraryFolders.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        setAnimation(holder.itemView, position);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Listens to
                System.out.println(allSongsMetadata.getSong(position).getPath());
                passToActivity.onSongRequest(allSongsMetadata.getSong(position).getPath(), allSongsMetadata, holder.getAdapterPosition());
            }
        });


        if(allSongsMetadata!=null) {
            if(allSongsMetadata.getLength()>position) {
                //Method checks that the current position is not bigger than the data to be accessed in the allSongsMetadata
                //If position is bigger, it means either that data is still loading
                //Then paths are used from allSongs array and metadata is extracted directly here

                Glide.with(context)
                        .asBitmap()
                        .load(allSongsMetadata.getSong(position).getBitmap(context))
                        .centerCrop()
                        .into(holder.itemImage);
                System.out.println("LOAD GLIDE");


                //holder.itemImage.setImageBitmap(allSongsMetadata.getSong(position).loadBmap(context));
                holder.itemText.setText(allSongsMetadata.getSong(position).getTitle());
                holder.itemArtist.setText(allSongsMetadata.getSong(position).getArtist());
                holder.itemAttribute.setText("");


                if(allSongsMetadata.getSong(position).getArtist()!=null){
                    holder.itemArtist.setText(allSongsMetadata.getSong(position).getArtist());
                    /*Glide.with(context)
                            .asBitmap()
                            .load(MusicDataMetadata.getBitmap(allSongs.get(position)))
                            .centerCrop()
                            .into(holder.itemImage);*/
                    holder.itemArtist.setVisibility(View.VISIBLE);
                }else{
                    holder.itemArtist.setVisibility(View.GONE);
                }

            }else{
                //This is the method called if allSongsMetadata is not available for the current positions' item
                //Creates a new MusicData element and retrieves the metadata
                MusicData selectedSong = new MusicData(allSongs.get(position),context);
                holder.itemImage.setImageBitmap(selectedSong.getBitmap(context));
                holder.itemText.setText(selectedSong.getTitle());
                System.out.println("ELSE SITUATION");
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
        if(allSongs.size()==0){
            //if allSongs size is 0, it means that app has already been initialized before
            //in this case allSongsMetadata is already generated
        return allSongsMetadata.getLength();
        }else{
            return  allSongs.size();
        }
    }

    /*public void filter(String text) {
        final ArrayList<MusicData> filteredList = new ArrayList<>();
        if (text.isEmpty()) {
            allSongsMetadata.setSongsArray(allSongsMetadataCopy.getSongsArray());
        } else {
            text = text.toLowerCase();

            for (MusicData song : allSongsMetadataCopy.getSongsArray()) {
                if (song.getPath().toLowerCase().contains(text) || song.getTitle().toLowerCase().contains(text)) {
                    filteredList.add(song);
                }
            }
            allSongsMetadata.setSongsArray(filteredList);
        }

        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return allSongsMetadata.getSongsArray().size();
            }

            @Override
            public int getNewListSize() {
                return filteredList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return allSongsMetadata.getSongsArray().get(oldItemPosition).getPath().equals(filteredList.get(newItemPosition).getPath());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                MusicData oldSong = allSongsMetadata.getSongsArray().get(oldItemPosition);
                MusicData newSong = filteredList.get(newItemPosition);
                return Objects.equals(oldSong.getPath(), newSong.getPath())
                        && Objects.equals(oldSong.getTitle(), newSong.getTitle())
                        && Objects.equals(oldSong.getArtist(), newSong.getArtist())
                        && Objects.equals(oldSong.getAlbum(), newSong.getAlbum());
            }
        });

        result.dispatchUpdatesTo(this);
    }*/


        public void setAnimation(View view, int position){
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            view.startAnimation(animation);
        }

        @SuppressLint("NotifyDataSetChanged")
        public void filter(String text){
        allSongsMetadata = null;
        allSongsMetadata = new Playlist();
        if(text.isEmpty()){
            allSongsMetadata = allSongsMetadataCopy;
        } else {
            text = text.toLowerCase();
            for(MusicData song: allSongsMetadataCopy.getSongsArray()){
                if(song.getPath().toLowerCase().contains(text) || song.getTitle().toLowerCase().contains(text)){
                    allSongsMetadata.addSong(song);
                }
            }
        }
        notifyDataSetChanged();
    }



}
