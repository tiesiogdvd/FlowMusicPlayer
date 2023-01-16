package eu.tutorial.androidapplicationfilesystem.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.PrecomputedText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

import javax.xml.transform.Result;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.fragmentsMainActivity.FragmentLibrarySongs;
import eu.tutorial.androidapplicationfilesystem.classes.MusicData;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;
import eu.tutorial.androidapplicationfilesystem.interfaces.PassMusicStatus;
import eu.tutorial.androidapplicationfilesystem.interfaces.PassSelectionStatus;

public class AdapterLibraryAllSongs extends RecyclerView.Adapter <AdapterLibraryAllSongs.ViewHolder>{
    Context context;
    ArrayList<String> allSongs; //Used on first application load, before metadata and bitmap is loaded into sqlite to display allSongs information
    //Does not contain metadata

    Playlist allSongsMetadata; //Contains loaded metadata and bitmap
    PassMusicStatus passToActivity;
    Playlist allSongsMetadataCopy; //used for filtering purposes

    Boolean selectionEnabled;
    PassSelectionStatus passSelectionStatus;

    public AdapterLibraryAllSongs(FragmentLibrarySongs fragmentLibrarySongs, ArrayList<String> allSongs, Playlist allSongsMetadata){
        this.context = fragmentLibrarySongs.getActivity();
        this.allSongs = allSongs;
        this.allSongsMetadata = allSongsMetadata;
        this.allSongsMetadataCopy = allSongsMetadata;
        this.selectionEnabled = false;


        if(context instanceof PassMusicStatus){ //Checks if there is an implementation of PassMusicStatus in the context of parent activity
            passToActivity = (PassMusicStatus) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement PassDataaaa");
        }

        passSelectionStatus = fragmentLibrarySongs;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView itemImage;
        TextView itemText;
        TextView itemArtist;
        TextView itemAttribute;
        LinearLayout linearLayout;
        LinearLayout backgroundMusic;
        Boolean isSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.imageLibraryItem);
            itemText = itemView.findViewById(R.id.nameLibraryItem);
            itemArtist = itemView.findViewById(R.id.artistLibraryItem);
            itemAttribute = itemView.findViewById(R.id.lengthLibraryItem);
            linearLayout = itemView.findViewById(R.id.btnLibraryItem);
            backgroundMusic = itemView.findViewById(R.id.backgroundLibraryItem);
            isSelected = false;
        }
    }


    @NonNull
    @Override
    public AdapterLibraryAllSongs.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_library_song, parent, false);
        final ViewHolder holder = new ViewHolder(view);
       // Boolean isSelected = false;


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selectionEnabled) {
                    System.out.println(allSongsMetadata.getSong(holder.getAdapterPosition()).getPath());
                    passToActivity.onSongRequest(allSongsMetadata.getSong(holder.getAdapterPosition()).getPath(), allSongsMetadata, holder.getAdapterPosition());
                }else{
                    if(holder.isSelected){
                        passSelectionStatus.onChecked(false, holder.getAdapterPosition());
                        holder.isSelected = false;
                    }else{
                        passSelectionStatus.onChecked(true, holder.getAdapterPosition());
                        holder.isSelected = true;
                    }
                }
            }
        });


        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                System.out.println("LONG CLICK");

                if(!selectionEnabled) {
                    selectionEnabled=true;
                    passSelectionStatus.onChecked(true, holder.getAdapterPosition());
                    holder.isSelected = true;
                }else{
                    if(holder.isSelected){
                        passSelectionStatus.onChecked(false, holder.getAdapterPosition());
                        holder.isSelected = false;
                    }else{
                        passSelectionStatus.onChecked(true, holder.getAdapterPosition());
                        holder.isSelected = true;
                    }
                }
                return true;
            }
        });
        return holder;
    }

    public void disableSelectMode(){
        selectionEnabled = false;
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
            Bitmap bitmap = allSongsMetadata.getSong(position).getBitmap(context);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(viewHolder.itemText.getText() == allSongsMetadata.getSong(position).getTitle()) {
                Glide.with(context)
                        .asBitmap()
                        .load(bitmap)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .override(50,50)
                        .centerCrop()
                        .into(viewHolder.itemImage);
            }
        }
    }



    @Override
    public void onBindViewHolder(@NonNull AdapterLibraryAllSongs.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        setAnimation(holder.itemView, position);

        if(allSongsMetadata!=null) {
            if(allSongsMetadata.getLength()>position) {
                //Method checks that the current position is not bigger than the data to be accessed in the allSongsMetadata
                //If position is bigger, it means either that data is still loading
                //Then paths are used from allSongs array and metadata is extracted directly here
                holder.itemText.setText(allSongsMetadata.getSong(position).getTitle());
                holder.itemArtist.setText(allSongsMetadata.getSong(position).getArtist());



                holder.itemAttribute.setText(allSongsMetadata.getSong(position).getLengthString());
                if(allSongsMetadata.getSong(position).getArtist()!=null){
                    holder.itemArtist.setText(allSongsMetadata.getSong(position).getArtist());
                    holder.itemArtist.setVisibility(View.VISIBLE);
                }else{
                    holder.itemArtist.setVisibility(View.GONE);
                }
                new RetrievingBitmaps(holder, position).execute();

            }else{
                //This is the method called if allSongsMetadata is not available for the current positions' item
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
        if(allSongs.size()==0){
            //if allSongs size is 0, it means that app has already been initialized before
            //in this case allSongsMetadata is already generated
        return allSongsMetadata.getLength();
        }else{
            return  allSongs.size();
        }
    }

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
