package com.mzherdev.twolevelcache;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by macuser on 23.09.16.
 */
public class KeyComparator implements Comparator {
    Map map;

    public KeyComparator(Map map) {
        this.map = map;
    }

    @Override
    public int compare(Object o1, Object o2) {
        int value1 = (int) map.get(o1);
        int value2 = (int) map.get(o2);
        if (value1 < value2)
            return 1;
        else if (value1 == value2)
            return 0;
        else return -1;
    }
}
