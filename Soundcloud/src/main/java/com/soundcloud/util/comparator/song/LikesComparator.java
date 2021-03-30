package com.soundcloud.util.comparator.song;

import com.soundcloud.model.POJOs.Song;
import com.soundcloud.util.comparator.AbstractComparator;
import com.soundcloud.util.comparator.Order;

public class LikesComparator extends AbstractComparator {
    public LikesComparator (Order order) {
        super(order);
    }

    @Override
    public int compare(Song o1, Song o2) {
        if (this.order == Order.ASC) {
            return o1.getLikers().size() - o2.getLikers().size();
        } else {
            return o2.getLikers().size() - o1.getLikers().size();
        }
    }
}