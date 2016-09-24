package com.mzherdev.twolevelcache;

import java.io.IOException;
import java.util.*;

/**
 * Created by mzherdev on 23.09.16.
 */
public class RamCache<K, V> implements Cache<K, V>, CallFrequency<K> {

    private Map<K, V> memoryCashMap;
    private Map<K, Integer> frequencyCashMap;

    public RamCache() {
        memoryCashMap = new LinkedHashMap<K, V>();
        frequencyCashMap = new TreeMap<K, Integer>();
    }

    @Override
    public void cache(K key, V value) throws IOException, ClassNotFoundException {
        memoryCashMap.put(key, value);
        frequencyCashMap.put(key, 1);
    }

    @Override
    public V getObject(K key) throws IOException, ClassNotFoundException {
        if (containsObject(key)) {
            frequencyCashMap.put(key, frequencyCashMap.get(key) + 1);
            return memoryCashMap.get(key);
        }
        return null;
    }

    @Override
    public void deleteObject(K key) {
        if (containsObject(key)) {
            memoryCashMap.remove(key);
            frequencyCashMap.remove(key);
        }
    }

    @Override
    public void clear() {
        memoryCashMap.clear();
        frequencyCashMap.clear();
    }

    @Override
    public V moveObjectToOtherCache(K key) throws IOException, ClassNotFoundException {
        if (containsObject(key)) {
            V value = getObject(key);
            deleteObject(key);
            return value;
        }
        return null;
    }

    @Override
    public boolean containsObject(K key) {
        return memoryCashMap.containsKey(key);
    }

    @Override
    public int getSize() {
        return memoryCashMap.size();
    }

    @Override
    public Set<K> getMostFrequentlyKeys() {
        TreeMap<K, Integer> sortedMemoryCashMap = new TreeMap<>(new KeyComparator(frequencyCashMap));
        sortedMemoryCashMap.putAll(frequencyCashMap);
        return sortedMemoryCashMap.keySet();
    }

    public Collection<Integer> getMostFrequentlyValues() {
        TreeMap<K, Integer> sortedMemoryCashMap = new TreeMap<>(new KeyComparator(frequencyCashMap));
        sortedMemoryCashMap.putAll(frequencyCashMap);
        return sortedMemoryCashMap.values();
    }

    @Override
    public int getCallingFrequencyOfObject(K key) {
        return containsObject(key) ? frequencyCashMap.get(key) : 0;
    }
}
