package com.soundcloud.util.comparator.song;

import com.soundcloud.model.POJOs.Song;
import com.soundcloud.util.comparator.AbstractComparator;
import com.soundcloud.util.comparator.Order;

public class CompareSongsByComments extends AbstractComparator {
    public CompareSongsByComments(Order order) {
        super(order);
    }

    @Override
    public int compare(Song o1, Song o2) {
        if (this.order == Order.ASC) {
            return o1.getComments().size() - o2.getComments().size();
        } else {
            return o2.getComments().size() - o1.getComments().size();
        }
    }
}
