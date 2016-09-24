package com.mzherdev.twolevelcache;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Created by mzherdev on 23.09.16.
 */
public class TwoLevelCache<K, V extends Serializable> implements Cache<K, V>, CallFrequency<K> {

    private RamCache<K, V> ramCache;
    private FileCache<K, V> fileCache;
    private int maxRamCacheCapacity;
    private int numOfRequests;
    private int numOfRequestsToRecache;

    public TwoLevelCache(int maxRamCacheCapacity, int numOfRequestsToRecache) {
        ramCache = new RamCache<K, V>();
        fileCache = new FileCache<K, V>();
        numOfRequests = 0;
        this.maxRamCacheCapacity = maxRamCacheCapacity;
        this.numOfRequestsToRecache = numOfRequestsToRecache;
    }

    @Override
    public void cache(K key, V value) throws IOException, ClassNotFoundException {
        ramCache.cache(key, value);
    }

    @Override
    public V getObject(K key) throws IOException, ClassNotFoundException {
        V value = null;
        if (ramCache.containsObject(key)) {
            checkCache();
            value = ramCache.getObject(key);
        }
        if (fileCache.containsObject(key)) {
            checkCache();
            value = fileCache.getObject(key);
        }
        return value;
    }

    private void checkCache() throws IOException, ClassNotFoundException {
        numOfRequests++;
        if (numOfRequests > numOfRequestsToRecache) {
            recache();
            numOfRequests = 0;
        }
    }

    @Override
    public void deleteObject(K key) {
        if (ramCache.containsObject(key)) ramCache.deleteObject(key);
        if (fileCache.containsObject(key)) fileCache.deleteObject(key);
    }

    @Override
    public void clear() {
        ramCache.clear();
        fileCache.clear();
    }

    @Override
    public V moveObjectToOtherCache(K key) throws IOException, ClassNotFoundException {
        V value = null;
        if (ramCache.containsObject(key)) {
            value = ramCache.moveObjectToOtherCache(key);
        }
        if (fileCache.containsObject(key))
            value = fileCache.moveObjectToOtherCache(key);
        return value;
    }

    @Override
    public boolean containsObject(K key) {
        return ramCache.containsObject(key) || fileCache.containsObject(key);
    }

    @Override
    public int getSize() {
        return ramCache.getSize() + fileCache.getSize();
    }

    @Override
    public Set<K> getMostFrequentlyKeys() {
        TreeSet<K> set = new TreeSet<K>(ramCache.getMostFrequentlyKeys());
        set.addAll(fileCache.getMostFrequentlyKeys());
        return set;
    }

    @Override
    public int getCallingFrequencyOfObject(K key) {
        return ramCache.containsObject(key) ? ramCache.getCallingFrequencyOfObject(key) :
                (fileCache.containsObject(key)) ? fileCache.getCallingFrequencyOfObject(key) : 0;
    }

    public void recache() throws IOException, ClassNotFoundException {
        Collection<Integer> mostFrequentlyValues = ramCache.getMostFrequentlyValues();
        int frequency = mostFrequentlyValues.stream().mapToInt(Integer::intValue).sum();
        int count = mostFrequentlyValues.size() == 0 ? 1 : mostFrequentlyValues.size();
        final int middleFrequency = frequency / count;

        for (K key : ramCache.getMostFrequentlyKeys())
            if (ramCache.getCallingFrequencyOfObject(key) <= middleFrequency)
                fileCache.cache(key, ramCache.moveObjectToOtherCache(key));

        for (K key : fileCache.getMostFrequentlyKeys()) {
            try {
                if (fileCache.getCallingFrequencyOfObject(key) > middleFrequency)
                    ramCache.cache(key, fileCache.moveObjectToOtherCache(key));
            } catch (IOException ex) {
                fileCache.deleteObject(key);
            }
        }
    }

    public RamCache<K, V> getRamCache() {
        return ramCache;
    }

    public FileCache<K, V> getFileCache() {
        return fileCache;
    }
}
