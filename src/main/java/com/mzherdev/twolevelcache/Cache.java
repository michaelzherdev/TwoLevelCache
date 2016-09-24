package com.mzherdev.twolevelcache;

import java.io.IOException;

/**
 * Created by mzherdev on 23.09.16.
 */
public interface Cache<K, V> {

    void cache(K key, V value) throws IOException, ClassNotFoundException;

    V getObject(K key) throws IOException, ClassNotFoundException;

    void deleteObject(K key);

    void clear();

    V moveObjectToOtherCache(K key) throws IOException, ClassNotFoundException;

    boolean containsObject(K key);

    int getSize();
}
