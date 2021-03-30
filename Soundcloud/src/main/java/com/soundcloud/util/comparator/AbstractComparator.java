package com.soundcloud.util.comparator;

import com.soundcloud.model.POJOs.Song;

import java.util.Comparator;

public abstract class AbstractComparator implements Comparator<Song> {
    public final Order order;

    protected AbstractComparator (Order order) {
        this.order = order;
    }
}
