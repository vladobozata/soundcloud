package com.soundcloud.util.comparator.song;

import com.soundcloud.model.POJOs.Song;
import com.soundcloud.util.comparator.AbstractComparator;
import com.soundcloud.util.comparator.Order;

public class DislikesComparator extends AbstractComparator {
    public DislikesComparator (Order order) {
        super(order);
    }

    @Override
    public int compare(Song o1, Song o2) {
        if (this.order == Order.ASC) {
            return o1.getDislikers().size() - o2.getDislikers().size();
        } else {
            return o2.getDislikers().size() - o1.getDislikers().size();
        }
    }
}