package com.example.soc_macmini_15.musicplayer.Fragments;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.soc_macmini_15.musicplayer.Adapter.SongAdapter;
import com.example.soc_macmini_15.musicplayer.Model.SongsList;
import com.example.soc_macmini_15.musicplayer.R;

import java.util.ArrayList;
import java.util.Collections;

public class AllSongFragment extends ListFragment {

    private static ContentResolver contentResolver1;
    private ArrayList<SongsList> songsList;
    private ArrayList<SongsList> filteredList;
    private ListView listView;
    private createDataParse createDataParse;
    private ContentResolver contentResolver;

    public static Fragment getInstance(int position, ContentResolver mContentResolver) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        AllSongFragment tabFragment = new AllSongFragment();
        tabFragment.setArguments(bundle);
        contentResolver1 = mContentResolver;
        return tabFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        createDataParse = (createDataParse) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.list_playlist);
        contentResolver = contentResolver1;
        initializeSongList();
    }

    /**
     * Initializes and populates the song list.
     */
    private void initializeSongList() {
        songsList = new ArrayList<>();
        filteredList = new ArrayList<>();
        fetchMusicFiles();

        // Sort songs alphabetically by title
        Collections.sort(songsList, SongsList.SongTitleComparator);

        // Initialize adapter and set to ListView
        SongAdapter adapter = new SongAdapter(getContext(), songsList);

        // Filter list if a query exists
        if (!createDataParse.queryText().isEmpty()) {
            adapter = filterSongs(createDataParse.queryText());
        }

        createDataParse.getLength(songsList.size());
        listView.setAdapter(adapter);

        // Handle item clicks
        SongAdapter finalAdapter = adapter;
        listView.setOnItemClickListener((parent, view, position, id) -> {
            SongsList selectedSong = finalAdapter.getItem(position);
            if (selectedSong != null) {
                createDataParse.onDataPass(selectedSong.getTitle(), selectedSong.getPath());
                createDataParse.fullSongList(songsList, position);
            }
        });

        // Long click for "play next" functionality
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            showPlayNextDialog(position);
            return true;
        });
    }

    /**
     * Fetches music files from the device.
     */
    private void fetchMusicFiles() {
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);
        if (songCursor != null) {
            int songTitleColumn = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtistColumn = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songPathColumn = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            while (songCursor.moveToNext()) {
                String title = songCursor.getString(songTitleColumn);
                String artist = songCursor.getString(songArtistColumn);
                String path = songCursor.getString(songPathColumn);
                songsList.add(new SongsList(title, artist, path));
            }
            songCursor.close();
        }
    }


    private SongAdapter filterSongs(String query) {
        String lowerCaseQuery = query.toLowerCase();
        filteredList.clear();

        for (SongsList song : songsList) {
            if (song.getTitle().toLowerCase().contains(lowerCaseQuery)) {
                filteredList.add(song);
            }
        }

        return new SongAdapter(getContext(), filteredList);
    }


    @SuppressLint("StringFormatInvalid")
    private void showPlayNextDialog(int position) {
        SongsList selectedSong = songsList.get(position);

        new android.support.v7.app.AlertDialog.Builder(getContext())
                .setMessage(getString(R.string.play_next, selectedSong.getTitle()))
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    createDataParse.currentSong(selectedSong);
                    Toast.makeText(getContext(), selectedSong.getTitle() + " will play next", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.no, null)
                .create()
                .show();
    }

    /**
     * Interface for communication with the parent activity.
     */
    public interface createDataParse {
        void onDataPass(String name, String path);

        void fullSongList(ArrayList<SongsList> songList, int position);

        String queryText();

        void currentSong(SongsList songsList);

        void getLength(int length);
    }
}
