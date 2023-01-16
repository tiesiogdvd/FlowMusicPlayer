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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.imageview.ShapeableImageView;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.classes.MusicData;
import eu.tutorial.androidapplicationfilesystem.classes.Playlist;
import eu.tutorial.androidapplicationfilesystem.interfaces.PassMusicStatus;

public class AdapterPlaylistLibrary extends RecyclerView.Adapter <AdapterPlaylistLibrary.ViewHolder>{
    Context context; //Does not contain metadata

    Playlist playlist; //Contains loaded metadata and bitmap
    PassMusicStatus passToActivity;
    Playlist playlistCopy; //used for filtering purposes

    public AdapterPlaylistLibrary(Context context, Playlist playlist){
        this.context = context;
        this.playlist = playlist;
        this.playlistCopy = playlist;

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



    private class RetrievingBitmaps extends AsyncTask<Void, Void, Bitmap> {
        AdapterPlaylistLibrary.ViewHolder viewHolder;
        int position;
        ImageView imageView;

        public RetrievingBitmaps(AdapterPlaylistLibrary.ViewHolder holder, int position){
            this.viewHolder = holder;
            this.position = position;
            this.imageView = holder.itemImage;
            System.out.println(position);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = playlist.getSong(position).getBitmap(context);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            //Glide.with(context).asBitmap().load(allSongsMetadata.getSong(viewHolder.getAdapterPosition()).getBitmap(context)).into(viewHolder.itemImage);
            //notifyItemChanged(viewHolder.getLayoutPosition());
            if(viewHolder.itemText.getText() == playlist.getSong(position).getTitle()) {
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


    @NonNull
    @Override
    public AdapterPlaylistLibrary.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_library_song, parent, false);
        ViewHolder holder = new ViewHolder(view);

        setAnimation(holder.itemView, holder.getAdapterPosition());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(playlist.getSong(holder.getAdapterPosition()).getPath());
                passToActivity.onSongRequest(playlist.getSong(holder.getAdapterPosition()).getPath(), playlist, holder.getAdapterPosition());
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPlaylistLibrary.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if(playlist !=null) {
            if(playlist.getLength()>position) {
                //Method checks that the current position is not bigger than the data to be accessed in the allSongsMetadata
                //If position is bigger, it means either that data is still loading
                //Then paths are used from allSongs array and metadata is extracted directly here

                Glide.with(context)
                        .asBitmap()
                        .load(playlist.getSong(position).getBitmap(context))
                        .centerCrop()
                        .into(holder.itemImage);
                System.out.println("LOAD GLIDE");


                //holder.itemImage.setImageBitmap(allSongsMetadata.getSong(position).loadBmap(context));
                holder.itemText.setText(playlist.getSong(position).getTitle());
                holder.itemArtist.setText(playlist.getSong(position).getArtist());
                holder.itemAttribute.setText("");


                if(playlist.getSong(position).getArtist()!=null){
                    holder.itemArtist.setText(playlist.getSong(position).getArtist());
                    holder.itemArtist.setVisibility(View.VISIBLE);
                }else{
                    holder.itemArtist.setVisibility(View.GONE);
                }

                new RetrievingBitmaps(holder, position).execute();

            }
        }

    }

    @Override
    public int getItemCount() {
        if(playlist !=null) {
            return playlist.getLength();
        }else{return 0;}
    }

    public void setAnimation(View view, int position){
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        view.startAnimation(animation);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(String text){
        playlist = null;
        playlist = new Playlist();
        if(text.isEmpty()){
            playlist = playlistCopy;
        } else {
            text = text.toLowerCase();
            for(MusicData song: playlistCopy.getSongsArray()){
                if(song.getPath().toLowerCase().contains(text) || song.getTitle().toLowerCase().contains(text)){
                    playlist.addSong(song);
                }
            }
        }
        notifyDataSetChanged();
    }



}
