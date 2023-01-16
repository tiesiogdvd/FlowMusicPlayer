package eu.tutorial.androidapplicationfilesystem.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.util.ArrayList;

public class PlaylistRepository extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "PlaylistLibrary.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME_PLAYLISTS = "playlists_table";
    private static final String COLUMN_ID_PLAYLIST = "id";
    private static final String COLUMN_PLAYLIST_NAME = "playlist_name";
    private static final String COLUMN_PLAYLIST_IMAGE_CACHE = "playlist_image_location"; //
    private static final String COLUMN_DATE_PLAYLIST_CREATED = "playlist_created";


    private static final String TABLE_NAME_SONGS = "songs_table";
    private static final String COLUMN_ID_SONG = "id";
    private static final String COLUMN_SONG_PATH = "file_path";
    private static final String COLUMN_SONG_IMAGE_CACHE = "song_image_location"; //
    private static final String COLUMN_SONG_TITLE = "song_title"; //
    private static final String COLUMN_SONG_ARTIST = "song_artist"; //
    private static final String COLUMN_SONG_ALBUM = "song_album"; //
    private static final String COLUMN_SONG_LENGTH = "song_length"; //
    private static final String COLUMN_SONG_PLAYLIST = "playlist_name"; //
    private static final String COLUMN_DATE_SONG_ADDED = "file_added";

    public PlaylistRepository(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("GENERATING TABLE");
        String createTablePlaylist = "CREATE TABLE " + TABLE_NAME_PLAYLISTS +
              " ("+COLUMN_ID_PLAYLIST+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PLAYLIST_NAME + " TEXT UNIQUE, " +
                COLUMN_PLAYLIST_IMAGE_CACHE + " TEXT, " + //FIX
                COLUMN_DATE_PLAYLIST_CREATED + " TEXT);";

        String createTableSong = "CREATE TABLE " + TABLE_NAME_SONGS +
                "("+COLUMN_ID_SONG +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SONG_PLAYLIST + " TEXT, " +
                COLUMN_SONG_PATH + " TEXT, " +
                COLUMN_SONG_TITLE + " TEXT, " + //
                COLUMN_SONG_ARTIST + " TEXT, " + //
                COLUMN_SONG_ALBUM + " TEXT, " + //
                COLUMN_SONG_LENGTH + " INT, " + //
                COLUMN_DATE_SONG_ADDED + " TEXT, " +
                " FOREIGN KEY " + "("+COLUMN_SONG_PLAYLIST+") REFERENCES " + TABLE_NAME_PLAYLISTS + "("+COLUMN_PLAYLIST_NAME+")ON DELETE CASCADE ON UPDATE CASCADE);";

        db.execSQL(createTableSong);
        db.execSQL(createTablePlaylist);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PLAYLISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SONGS);
        onCreate(db);
    }

    public void addPlaylist(String playlistName, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PLAYLIST_NAME,playlistName);
        cv.put(COLUMN_DATE_PLAYLIST_CREATED,date);
        long result = db.insert(TABLE_NAME_PLAYLISTS,null,cv);
        if (result == -1){
            Toast.makeText(context, "Failed to insert playlist to database", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Item inserted to playlist database", Toast.LENGTH_SHORT).show();
        }
    }

    public void addPlaylist(String playlistName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PLAYLIST_NAME,playlistName);
        long result = db.insert(TABLE_NAME_PLAYLISTS,null,cv);
        if (result == -1){
            Toast.makeText(context, "Failed to insert playlist to database", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Item inserted to playlist database", Toast.LENGTH_SHORT).show();
        }
    }

    public void removePlaylist(String playlistName){
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + TABLE_NAME_PLAYLISTS + " WHERE " + COLUMN_PLAYLIST_NAME + " = '" + playlistName + "'";
        db.execSQL(deleteQuery);
    }



    public void addSong(String playlistName, String path, String title, String artist, String album, int lengthInt, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SONG_PATH,path);
        cv.put(COLUMN_PLAYLIST_NAME,playlistName);
        cv.put(COLUMN_SONG_TITLE, title);
        cv.put(COLUMN_SONG_ARTIST, artist);
        cv.put(COLUMN_SONG_ALBUM, album);
        cv.put(COLUMN_SONG_LENGTH,lengthInt);
        cv.put(COLUMN_DATE_SONG_ADDED,date);
        long result = db.insert(TABLE_NAME_SONGS,null,cv);
        if (result == -1){
            //Toast.makeText(context, "Failed to insert song to database", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(context, "Item inserted to song database", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeSong(String path, String playlistName){
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + TABLE_NAME_SONGS + " WHERE " + COLUMN_PLAYLIST_NAME + " = '" + playlistName + "'" + " AND " + COLUMN_SONG_PATH + " = \"" + path + "\"";
        db.execSQL(deleteQuery);
    }


    public MutableLiveData<ArrayList<Playlist>> getFullPlaylistsData(){
        MutableLiveData<ArrayList<Playlist>> playlists = new MutableLiveData<>();
        ArrayList<Playlist> playlistArray = new ArrayList<>();
        //ArrayList<Playlist> playlists = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME_PLAYLISTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.moveToFirst()){
            do{
                Playlist playlist = new Playlist(
                        cursor.getString(1), //playlistName
                        cursor.getString(2) //playlistDate
                );
                ArrayList <MusicData> songs = getSongs(playlist.getPlaylistName());
                playlist.setSongsArray(songs);
                playlistArray.add(playlist);
            }while(cursor.moveToNext());
            //cursor.close();
        }
        cursor.close();
        db.close();
        playlists.setValue(playlistArray);
        return playlists;
    }


    public ArrayList<MusicData> getSongs (String playlistName){ //Given playlist name to find songs for that specific playlsit
        ArrayList<MusicData> songs = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME_SONGS + " WHERE " + COLUMN_PLAYLIST_NAME + " = '" + playlistName + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){  //loops through all result rows
            do{
                if(new File(cursor.getString((2))).exists()) {
                    MusicData song = new MusicData(
                            cursor.getString(2), //collumn 2 file_path location
                            cursor.getString(3), //collumn 3 song_title
                            cursor.getString(4), //collumn 4 song_artist
                            cursor.getString(5), //collumn 4 song_album
                            cursor.getInt(6), //collumn 4 song_length
                            cursor.getString(7), //collumn 4 date_song_added
                            context
                    );
                    songs.add(song);
                }else{
                    String deleteQuery = "DELETE FROM " + TABLE_NAME_SONGS + " WHERE " + COLUMN_PLAYLIST_NAME + " = '" + playlistName + "'" + " AND " + COLUMN_SONG_PATH + " = \"" + cursor.getString(2) + "\"";
                    db.execSQL(deleteQuery);
                }
            } while (cursor.moveToNext());
            //cursor.close();
        }
        cursor.close();
        db.close();
        return songs;
    }
}
