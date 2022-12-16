package eu.tutorial.androidapplicationfilesystem.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class PlaylistDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "PlaylistLibrary.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME_PLAYLISTS = "playlists_table";
    private static final String COLUMN_ID_PLAYLIST = "id";
    private static final String COLUMN_PLAYLIST_NAME = "playlist_name";
    private static final String COLUMN_DATE_PLAYLIST_CREATED = "playlist_created";

    private static final String TABLE_NAME_SONGS = "songs_table";
    private static final String COLUMN_ID_SONG = "id";
    private static final String COLUMN_PATH = "file_path";
    private static final String COLUMN_SONG_PLAYLIST = "playlist_name";
    private static final String COLUMN_DATE_SONG_ADDED = "file_added";

    public PlaylistDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTablePlaylist = "CREATE TABLE " + TABLE_NAME_PLAYLISTS +
              " ("+COLUMN_ID_PLAYLIST+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PLAYLIST_NAME + " TEXT UNIQUE, " +
                COLUMN_DATE_PLAYLIST_CREATED + " TEXT);";

        String createTableSong = "CREATE TABLE " + TABLE_NAME_SONGS +
                "("+COLUMN_ID_SONG +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SONG_PLAYLIST + " TEXT, " +
                COLUMN_PATH + " TEXT, " +
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



    //

    public void addSong(String path, String playlistName, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PATH,path);
        cv.put(COLUMN_PLAYLIST_NAME,playlistName);
        cv.put(COLUMN_DATE_SONG_ADDED,date);
        long result = db.insert(TABLE_NAME_SONGS,null,cv);
        if (result == -1){
            Toast.makeText(context, "Failed to insert song to database", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Item inserted to song database", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeSong(String path, String playlistName){
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + TABLE_NAME_SONGS + " WHERE " + COLUMN_PLAYLIST_NAME + " = '" + playlistName + "'" + " AND " + COLUMN_PATH + " = '" + path + "'";
        db.execSQL(deleteQuery);
    }




    public ArrayList<Playlist> getPlaylists (){
        ArrayList<Playlist> playlists = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME_PLAYLISTS;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){
            do{
                Playlist playlist = new Playlist(
                        cursor.getString(1),
                        cursor.getString(2)
                );
                ArrayList <MusicData> songs = getSongs(playlist.getPlaylistName());
                playlist.setSongsArray(songs);
                playlists.add(playlist);
            }while(cursor.moveToNext());
            //cursor.close();
        }
        cursor.close();
        db.close();
        return playlists;
    }


    public ArrayList<MusicData> getSongs (String playlistName){ //Given playlist name to find songs for that specific playlsit
        ArrayList<MusicData> songs = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_NAME_SONGS + " WHERE " + COLUMN_PLAYLIST_NAME + " = '" + playlistName + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){  //loops through all result rows
            do{
                MusicData song = new MusicData(
                    cursor.getString(2), //collumn 2 file_path location
                    cursor.getString(3)  //collumn 3 file_added date
                );
                songs.add(song);
            } while (cursor.moveToNext());
            //cursor.close();
        }
        cursor.close();
        db.close();
        return songs;
    }
}
