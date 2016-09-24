package com.mzherdev.twolevelcache;

import java.util.Set;

/**
 * Created by mzherdev on 23.09.16.
 */
public interface CallFrequency<K> {
    Set<K> getMostFrequentlyKeys();

    int getCallingFrequencyOfObject(K key);
}
