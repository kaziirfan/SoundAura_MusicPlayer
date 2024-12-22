package com.example.soc_macmini_15.musicplayer.Model;

import java.util.Comparator;

/**
 * Represents a song with a title, subtitle, and file path.
 */
public class SongsList {

    private String title;
    private String subTitle;
    private String path;

    public SongsList(String title, String subTitle, String path) {
        this.title = title;
        this.subTitle = subTitle;
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        this.path = path;
    }

    @Override
    public String toString() {
        return title + " - " + subTitle;
    }

    // Comparator to sort by title
    public static Comparator<SongsList> SongTitleComparator = (song1, song2) -> song1.getTitle().compareToIgnoreCase(song2.getTitle());
}
