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

public class AdapterLibraryFavorites extends RecyclerView.Adapter <AdapterLibraryFavorites.ViewHolder>{
    Context context; //Does not contain metadata

    Playlist favorites; //Contains loaded metadata and bitmap
    PassMusicStatus passToActivity;
    Playlist favoritesCopy; //used for filtering purposes

    public AdapterLibraryFavorites(Context context, Playlist favorites){
        this.context = context;
        this.favorites = favorites;
        this.favoritesCopy = favorites;

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
        AdapterLibraryFavorites.ViewHolder viewHolder;
        int position;
        ImageView imageView;

        public RetrievingBitmaps(AdapterLibraryFavorites.ViewHolder holder, int position){
            this.viewHolder = holder;
            this.position = position;
            this.imageView = holder.itemImage;
            System.out.println(position);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = favorites.getSong(position).getBitmap(context);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            //Glide.with(context).asBitmap().load(allSongsMetadata.getSong(viewHolder.getAdapterPosition()).getBitmap(context)).into(viewHolder.itemImage);
            //notifyItemChanged(viewHolder.getLayoutPosition());
            if(viewHolder.itemText.getText() == favorites.getSong(position).getTitle()) {
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
    public AdapterLibraryFavorites.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_library_song, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        setAnimation(holder.itemView, holder.getAdapterPosition());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(favorites.getSong(holder.getAdapterPosition()).getPath());
                passToActivity.onSongRequest(favorites.getSong(holder.getAdapterPosition()).getPath(), favorites, holder.getAdapterPosition());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLibraryFavorites.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(favorites !=null) {
            if(favorites.getLength()>position) {
                //Method checks that the current position is not bigger than the data to be accessed in the allSongsMetadata
                //If position is bigger, it means either that data is still loading
                //Then paths are used from allSongs array and metadata is extracted directly here

                holder.itemText.setText(favorites.getSong(position).getTitle());
                holder.itemArtist.setText(favorites.getSong(position).getArtist());
                holder.itemAttribute.setText("");
                if(favorites.getSong(position).getArtist()!=null){
                    holder.itemArtist.setText(favorites.getSong(position).getArtist());
                    /*Glide.with(context)
                            .asBitmap()
                            .load(MusicDataMetadata.getBitmap(allSongs.get(position)))
                            .centerCrop()
                            .into(holder.itemImage);*/
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
        if(favorites !=null) {
            return favorites.getLength();
        }else{return 0;}
    }

    public void setAnimation(View view, int position){
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        view.startAnimation(animation);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(String text){
        favorites = null;
        favorites = new Playlist();
        if(text.isEmpty()){
            favorites = favoritesCopy;
        } else {
            text = text.toLowerCase();
            for(MusicData song: favoritesCopy.getSongsArray()){
                if(song.getPath().toLowerCase().contains(text) || song.getTitle().toLowerCase().contains(text)){
                    favorites.addSong(song);
                }
            }
        }
        notifyDataSetChanged();
    }



}
