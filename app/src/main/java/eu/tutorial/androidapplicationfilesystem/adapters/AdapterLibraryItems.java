package eu.tutorial.androidapplicationfilesystem.adapters;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.classes.MusicData;
import eu.tutorial.androidapplicationfilesystem.classes.MusicDataMetadata;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;

public class AdapterLibraryItems extends RecyclerView.Adapter <AdapterLibraryItems.ViewHolder>{
    Context context;
    ArrayList<String> allSongs; //Used on first application load, before metadata and bitmap is loaded into sqlite to display allSongs information
    //Does not contain metadata

    Playlist allSongsMetadata; //Contains loaded metadata and bitmap

    public AdapterLibraryItems(Context context, ArrayList<String> allSongs, Playlist allSongsMetadata){
        this.context = context;
        this.allSongs = allSongs;
        this.allSongsMetadata = allSongsMetadata;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView itemImage;
        TextView itemText;
        TextView itemArtist;
        TextView itemAttribute;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.imageLibraryItem);
            itemText = itemView.findViewById(R.id.nameLibraryItem);
            itemArtist = itemView.findViewById(R.id.artistLibraryItem);
            itemAttribute = itemView.findViewById(R.id.lengthLibraryItem);
            linearLayout = itemView.findViewById(R.id.btnLibraryItem);
        }
    }

    @NonNull
    @Override
    public AdapterLibraryItems.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_library_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLibraryItems.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if(allSongsMetadata!=null) {
            if(allSongsMetadata.getLength()>position) {
                //Method checks that the current position is not bigger than the data to be accessed in the allSongsMetadata
                //If position is bigger, it means either that data is still loading
                //Then paths are used from allSongs array and metadata is extracted directly here
                holder.itemImage.setImageBitmap(allSongsMetadata.getSong(position).loadBmap(context));
                holder.itemText.setText(allSongsMetadata.getSong(position).getTitle());
                holder.itemArtist.setText(allSongsMetadata.getSong(position).getArtist());
                holder.itemAttribute.setText("");

                holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Listens to
                        System.out.println(allSongsMetadata.getSong(position).getPath());
                    }
                });

                if(allSongsMetadata.getSong(position).getArtist()!=null){
                    holder.itemArtist.setText(allSongsMetadata.getSong(position).getArtist());
                    holder.itemArtist.setVisibility(View.VISIBLE);
                }else{
                    holder.itemArtist.setVisibility(View.GONE);
                }
            }else{
                //This is the method called if allSongsMetadata is not available for the current positions' item
                //Creates a new MusicData element and retrieves the metadata
                MusicData selectedSong = new MusicData(allSongs.get(position),context);
                selectedSong.loadBmap(context);
                holder.itemImage.setImageBitmap(selectedSong.getBitmap());
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
}
