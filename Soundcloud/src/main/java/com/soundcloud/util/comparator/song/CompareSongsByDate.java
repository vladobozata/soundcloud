package com.soundcloud.util.comparator.song;

import com.soundcloud.model.POJOs.Song;
import com.soundcloud.util.comparator.AbstractComparator;
import com.soundcloud.util.comparator.Order;

public class CompareSongsByDate extends AbstractComparator {
    public CompareSongsByDate(Order order) {
        super(order);
    }

    @Override
    public int compare(Song o1, Song o2) {
        if (this.order == Order.ASC) {
            return o1.getCreatedAt().compareTo(o2.getCreatedAt());
        } else {
            return o2.getCreatedAt().compareTo(o1.getCreatedAt());
        }
    }
}
