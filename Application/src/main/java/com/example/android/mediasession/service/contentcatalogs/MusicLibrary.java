/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.mediasession.service.contentcatalogs;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import android.widget.ImageView;

import com.example.android.mediasession.BuildConfig;
import com.example.android.mediasession.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import cierzo.model.FacadeKt;
import cierzo.model.objects.Playlist;
import cierzo.model.objects.Song;


public class MusicLibrary {

    private static final TreeMap<String, MediaMetadataCompat> music = new TreeMap<>();
    private static final HashMap<String, Integer> albumRes = new HashMap<>();
    private static final HashMap<String, String> musicFileName = new HashMap<>();

    private static final TreeMap<String, Song> songs = new TreeMap<>();
    private static final HashMap<String, String> imageURL = new HashMap<>();
    private static final HashMap<String, Bitmap> bitmaps = new HashMap<>();
    private static final HashMap<String, String> fileURL = new HashMap<>();
    public static Playlist currentPlaylist = null;

    static {
        createMediaMetadataCompat(
                "9999",
                "Jazz in Paris",
                "Media Right Productions",
                "Jazz & Blues",
                "Jazz",
                103,
                TimeUnit.SECONDS,
                "jazz_in_paris.mp3",
                R.drawable.album_jazz_blues,
                "album_jazz_blues");
        createMediaMetadataCompat(
                "9998",
                "The Coldest Shoulder",
                "The 126ers",
                "Youtube Audio Library Rock 2",
                "Rock",
                160,
                TimeUnit.SECONDS,
                "the_coldest_shoulder.mp3",
                R.drawable.album_youtube_audio_library_rock_2,
                "album_youtube_audio_library_rock_2");
        createMediaMetadataCompat(
                "9997",
                "La epica historia de pipo",
                "Alexelcapo",
                "Canciones sobre Youtubers",
                "Gringe",
                96,
                TimeUnit.SECONDS,
                "La Leyenda del Perro Pipo Gambino -Alexelcapo.mp3",
                R.drawable.album_youtube_audio_library_rock_2,
                "album_youtube_audio_library_rock_2");
        createMediaMetadataCompat(
                "9996",
                "Feel Invencible",
                "Skillet",
                "NewAlbum",
                "Rock",
                227,
                TimeUnit.SECONDS,
                "Skillet - Feel Invincible.mp3",
                R.drawable.album_jazz_blues,
                "album_youtube_audio_library_rock_2");
    }

    public static List<MediaMetadataCompat> getAllSongs() {
        return new ArrayList(music.values());
    }

    public static String getRoot() {
        return "root";
    }

    private static String getAlbumArtUri(String albumArtResName) {
        return ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                BuildConfig.APPLICATION_ID + "/drawable/" + albumArtResName;
    }

    public static String getMusicFilename(String mediaId) {
        return musicFileName.containsKey(mediaId) ? musicFileName.get(mediaId) : null;
    }

    private static int getAlbumRes(String mediaId) {
        if (albumRes.containsKey(mediaId)) {
            return albumRes.containsKey(mediaId) ? albumRes.get(mediaId) : 0;
        } else if (imageURL.containsKey(mediaId)){
            return -1;
        } else {
            return -2;
        }
    }

    public static Bitmap getAlbumBitmap(Context context, String mediaId) {
        int albumRes = MusicLibrary.getAlbumRes(mediaId);
        if (bitmaps.containsKey(mediaId)) {
            return bitmaps.get(mediaId);
        } else {
            if (albumRes > -1) {
                return BitmapFactory.decodeResource(context.getResources(), albumRes);
            } else if (albumRes == -1) {
                try {
                    Bitmap bitmap = Ion.with(context)
                            .load(imageURL.get(mediaId))
                            .withBitmap()
                            .placeholder(R.drawable.gray_background)
                            .asBitmap()
                            .setCallback(new FutureCallback<Bitmap>() {
                                @Override
                                public void onCompleted(Exception e, Bitmap result) {
                                }
                            }).get();
                    bitmaps.put(mediaId, bitmap);
                    return bitmap;
                } catch (Exception e) {
                    Log.e("getAlbumBitmap", e.toString());
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    public static void putAlbumBitmap(ImageView imageView, String mediaId) {
        Ion.with(imageView)
                .placeholder(R.drawable.gray_background)
                .load(imageURL.get(mediaId));
    }

    public static List<MediaBrowserCompat.MediaItem> getMediaItems() {
        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();
        for (MediaMetadataCompat metadata : music.values()) {
            result.add(
                    new MediaBrowserCompat.MediaItem(
                            metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }
        return result;
    }

    public static MediaMetadataCompat getMetadata(Context context, String mediaId) {
        if (music.get(mediaId) == null) mediaId = "0";
        MediaMetadataCompat metadataWithoutBitmap = music.get(mediaId);
        Bitmap albumArt = getAlbumBitmap(context, mediaId);

        // Since MediaMetadataCompat is immutable, we need to create a copy to set the album art.
        // We don't set it initially on all items so that they don't take unnecessary memory.
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        for (String key :
                new String[]{
                        MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                        MediaMetadataCompat.METADATA_KEY_ALBUM,
                        MediaMetadataCompat.METADATA_KEY_ARTIST,
                        MediaMetadataCompat.METADATA_KEY_GENRE,
                        MediaMetadataCompat.METADATA_KEY_TITLE
                }) {
            builder.putString(key, metadataWithoutBitmap.getString(key));
        }
        builder.putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                metadataWithoutBitmap.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt);
        return builder.build();
    }

    public static MediaMetadataCompat getMetadata(String mediaId) {
        if (music.get(mediaId) == null) mediaId = "0";
        MediaMetadataCompat metadataWithoutBitmap = music.get(mediaId);
        //Bitmap albumArt = getAlbumBitmap(context, mediaId);

        // Since MediaMetadataCompat is immutable, we need to create a copy to set the album art.
        // We don't set it initially on all items so that they don't take unnecessary memory.
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        for (String key :
                new String[]{
                        MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                        MediaMetadataCompat.METADATA_KEY_ALBUM,
                        MediaMetadataCompat.METADATA_KEY_ARTIST,
                        MediaMetadataCompat.METADATA_KEY_GENRE,
                        MediaMetadataCompat.METADATA_KEY_TITLE
                }) {
            builder.putString(key, metadataWithoutBitmap.getString(key));
        }
        builder.putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                metadataWithoutBitmap.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        //builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt);
        return builder.build();
    }

    public static Uri getMusicUri(String mediaId) {
        if (fileURL.containsKey(mediaId)) {
            return Uri.parse(fileURL.get(mediaId));
        } else {
            return null;
        }
    }

    private static void removeAllMusic() {
        music.clear();
        albumRes.clear();
        musicFileName.clear();

        songs.clear();
        imageURL.clear();
        fileURL.clear();
    }

    public static void replaceWithSong(Uri uri, Context context) {
        removeAllMusic();

        MediaMetadataRetriever metaRetriver;
        metaRetriver = new MediaMetadataRetriever();
        metaRetriver.setDataSource(context, uri);

        //byte[] cover = metaRetriver.getEmbeddedPicture();
        //Bitmap cover_image = BitmapFactory.decodeByteArray(cover, 0, cover.length);

        createMediaMetadataCompat(
                "0",
                metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE),
                Integer.parseInt(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)),
                TimeUnit.MILLISECONDS,
                "",
                R.drawable.caratula_acdc,
                "");
    }

    private static void createMediaMetadataCompat(
            String mediaId,
            String title,
            String artist,
            String album,
            String genre,
            long duration,
            TimeUnit durationUnit,
            String musicFilename,
            int albumArtResId,
            String albumArtResName) {
        music.put(
                mediaId,
                new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                                 TimeUnit.MILLISECONDS.convert(duration, durationUnit))
                        .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                        .putString(
                                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                                getAlbumArtUri(albumArtResName))
                        .putString(
                                MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                                getAlbumArtUri(albumArtResName))
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                        .build());
        albumRes.put(mediaId, albumArtResId);
        musicFileName.put(mediaId, musicFilename);
    }

    public static String getMusicFileURL(String songId) {
        return fileURL.containsKey(songId) ? fileURL.get(songId) : null;
    }

    private static String getMusicImageURL(String songId) {
        return imageURL.containsKey(songId) ? imageURL.get(songId) : null;
    }

    public static void replaceWithSongs(Playlist playlist) {
        removeAllMusic();
        currentPlaylist = playlist;

        for (Song song : currentPlaylist.getSongs()) {
            createMediaMetadataCompat(song);
        }
    }


    private static void createMediaMetadataCompat(Song song) {
        music.put(
                song.getId(),
                new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.getId())
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.getAlbumName())
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getAuthorName())
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                                TimeUnit.MILLISECONDS.convert(Long.parseLong(song.getLength()), TimeUnit.SECONDS))
                        .putString(MediaMetadataCompat.METADATA_KEY_GENRE, song.getGenre().get(0))
                        .putString(
                                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                                Uri.parse(song.getImageURL()).toString())
                        .putString(
                                MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                                Uri.parse(song.getImageURL()).toString())
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getName())
                        .build());
        imageURL.put(song.getId(), song.getImageURL());
        fileURL.put(song.getId(), song.getFileURL());
        songs.put(song.getId(), song);
        musicFileName.put(song.getId(), song.getName());
    }

    static class GetSongFromServerTask extends AsyncTask<String, Void, Song> {
        @Override
        protected Song doInBackground(String... strings) {
            return (Song) cierzo.model.FacadeKt.getFromServer(FacadeKt.SONG, strings[0]);
        }
    }
}