package com.soundcloud.util.comparator.song;

import com.soundcloud.model.POJOs.Song;
import com.soundcloud.util.comparator.AbstractComparator;
import com.soundcloud.util.comparator.Order;

public class ViewsComparator extends AbstractComparator {
    public ViewsComparator(Order order) {
        super(order);
    }

    @Override
    public int compare(Song o1, Song o2) {
        if (this.order == Order.ASC) {
            return o1.getViews() - o2.getViews();
        } else {
            return o2.getViews() - o1.getViews();
        }
    }
}
