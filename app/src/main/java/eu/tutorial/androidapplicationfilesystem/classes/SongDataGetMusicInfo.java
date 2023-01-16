package eu.tutorial.androidapplicationfilesystem.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;

public class SongDataGetMusicInfo {
    @SuppressLint("Range")
    public static ArrayList<SongDataTest> getMusicInfo(Context context) {

        ArrayList<SongDataTest> musicInfo = new ArrayList<SongDataTest>();

        @SuppressLint("Recycle") Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor == null) {
            return null;
        }

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();

            if(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)) >= Settings.getLowestSongLength()){



            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));

            if (isMusic != 0) {
                SongDataTest music = new SongDataTest();

                music.path = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                if (!new File(music.path).exists()) {
                    continue;
                }

                music.songId = cursor.getLong(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));

                music.songTitle = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));


                //Returns file name and its type
                /*music.songTitle = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                System.out.println(music.songTitle);*/

                music.album = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));


                music.songArtist = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

                music.duration = cursor
                        .getInt(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

                /*MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(music.path);
                music.albumArt = getBitmap(mmr.getEmbeddedPicture());
                mmr.release();*/
                //System.out.println(music.path);
                //System.out.printf(cursor.toString());
                musicInfo.add(music);
                }
            }
        }

        return musicInfo;
    }
}
