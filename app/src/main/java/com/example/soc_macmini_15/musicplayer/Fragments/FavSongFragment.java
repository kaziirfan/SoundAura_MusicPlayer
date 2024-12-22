package com.example.soc_macmini_15.musicplayer.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.soc_macmini_15.musicplayer.Adapter.SongAdapter;
import com.example.soc_macmini_15.musicplayer.DB.FavoritesOperations;
import com.example.soc_macmini_15.musicplayer.Model.SongsList;
import com.example.soc_macmini_15.musicplayer.R;

import java.util.ArrayList;

public class FavSongFragment extends ListFragment {

    private FavoritesOperations favoritesOperations;
    private ArrayList<SongsList> songsList = new ArrayList<>();
    private ArrayList<SongsList> filteredList = new ArrayList<>();
    private ListView listView;
    private createDataParsed createDataParsed;

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        FavSongFragment fragment = new FavSongFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        createDataParsed = (createDataParsed) context;
        favoritesOperations = new FavoritesOperations(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.list_playlist);
        loadFavorites();
    }

    /**
     * Loads and sets the favorite songs in the list view.
     */
    private void loadFavorites() {
        songsList = favoritesOperations.getAllFavorites();
        filteredList.clear(); // Clear previous search results
        String query = createDataParsed.queryText();

        // Populate filtered list if a search query exists
        if (!query.isEmpty()) {
            for (SongsList song : songsList) {
                if (song.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(song);
                }
            }
            setListContent(filteredList);
        } else {
            setListContent(songsList);
        }
    }


    private void setListContent(ArrayList<SongsList> list) {
        SongAdapter adapter = new SongAdapter(getContext(), list);
        listView.setAdapter(adapter);

        // Handle song selection
        listView.setOnItemClickListener((parent, view, position, id) -> {
            SongsList selectedSong = list.get(position);
            createDataParsed.onDataPass(selectedSong.getTitle(), selectedSong.getPath());
            createDataParsed.fullSongList(songsList, position);
        });

        // Handle long press for delete option
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteDialog(list.get(position).getPath(), position, list == filteredList);
            return true;
        });
    }

    /**
     * Displays a confirmation dialog to delete a song.
     */
    private void showDeleteDialog(String songPath, int position, boolean isFiltered) {
        if (position == createDataParsed.getPosition()) {
            Toast.makeText(getContext(), "You can't delete the currently playing song.", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete)
                .setMessage(R.string.delete_text)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    favoritesOperations.removeSong(songPath);
                    if (isFiltered) {
                        filteredList.remove(position);
                    } else {
                        songsList.remove(position);
                    }
                    loadFavorites();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    /**
     * Interface for communication with the parent activity.
     */
    public interface createDataParsed {
        void onDataPass(String name, String path);

        void fullSongList(ArrayList<SongsList> songList, int position);

        int getPosition();

        String queryText();
    }
}
