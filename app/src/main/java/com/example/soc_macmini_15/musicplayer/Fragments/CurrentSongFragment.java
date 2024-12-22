package com.example.soc_macmini_15.musicplayer.Fragments;

import android.content.Context;
import android.os.Bundle;
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

public class CurrentSongFragment extends ListFragment {

    private ArrayList<SongsList> songsList = new ArrayList<>();
    private ListView listView;
    private createDataParsed createDataParsed;

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        CurrentSongFragment fragment = new CurrentSongFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        createDataParsed = (createDataParsed) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.list_playlist);
        initializeSongList();
    }

    /**
     * Initializes and updates the content of the song list.
     */
    private void initializeSongList() {
        SongsList currentSong = createDataParsed.getSong();
        boolean isPlaylistFlag = createDataParsed.getPlaylistFlag();

        // Clear and reset the list if needed
        if (isPlaylistFlag && !songsList.isEmpty()) {
            songsList.clear();
        }

        // Add current song if it exists and not already added
        if (currentSong != null && !songsList.contains(currentSong)) {
            songsList.add(currentSong);
        }

        // Set up the adapter
        SongAdapter adapter = new SongAdapter(getContext(), songsList);
        listView.setAdapter(adapter);

        // Handle item click for playing selected song
        listView.setOnItemClickListener((parent, view, position, id) -> {
            SongsList selectedSong = songsList.get(position);
            createDataParsed.onDataPass(selectedSong.getTitle(), selectedSong.getPath());
            createDataParsed.fullSongList(songsList, position);
        });

        // Handle long click for additional actions (if needed later)
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Toast.makeText(getContext(), "Long press detected on: " + songsList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    /**
     * Interface for communication with the parent activity.
     */
    public interface createDataParsed {
        void onDataPass(String name, String path);

        void fullSongList(ArrayList<SongsList> songList, int position);

        SongsList getSong();

        boolean getPlaylistFlag();
    }
}
