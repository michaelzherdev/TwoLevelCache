package com.mzherdev.twolevelcache;

import java.io.*;
import java.util.*;

/**
 * Created by mzherdev on 23.09.16.
 */
public class FileCache<K, V extends Serializable> implements Cache<K, V>, CallFrequency<K> {

    public static final String CACHE_DIR_NAME = "cache";
    private static final String CACHE_FILE_EXT = ".temp";

    Map<K, String> fileCashMap;
    TreeMap<K, Integer> frequencyCashMap;

    public FileCache() {
        this.fileCashMap = new HashMap<K, String>();
        this.frequencyCashMap = new TreeMap<K, Integer>();

        File cacheFolder = new File(CACHE_DIR_NAME);
        if (!cacheFolder.exists()) cacheFolder.mkdirs();

    }

    @Override
    public void cache(K key, V value) throws IOException, ClassNotFoundException {
        String fileName = CACHE_DIR_NAME + "/" + UUID.randomUUID() + CACHE_FILE_EXT;

        frequencyCashMap.put(key, 1);
        fileCashMap.put(key, fileName);

        serializeObject(fileName, value);
    }

    @Override
    public V getObject(K key) throws IOException, ClassNotFoundException {
        if (containsObject(key)) {
            String fileName = fileCashMap.get(key);
            return deserializeObject(fileName);
        }
        return null;
    }

    @Override
    public void deleteObject(K key) {
        if (containsObject(key)) {
            File fileToDelete = new File(fileCashMap.remove(key));
            frequencyCashMap.remove(key);
            fileToDelete.setWritable(true); // to actually delete file
            fileToDelete.delete();
        }
    }

    @Override
    public void clear() {
        //delete all files in cache directory
        File cacheDir = new File(CACHE_DIR_NAME);
        if (cacheDir.exists()) {
            File[] files = cacheDir.listFiles();
            if (files == null)
                return;
            for (File file : files) {
                    file.delete();
            }
        }

        fileCashMap.clear();
        frequencyCashMap.clear();
    }

    @Override
    public V moveObjectToOtherCache(K key) throws IOException, ClassNotFoundException {
        if (containsObject(key)) {
            V object = getObject(key);
            deleteObject(key);
            return object;
        }
        return null;
    }

    @Override
    public boolean containsObject(K key) {
        return fileCashMap.containsKey(key);
    }

    @Override
    public int getSize() {
        return fileCashMap.size();
    }

    @Override
    public Set<K> getMostFrequentlyKeys() {
        TreeMap<K, Integer> sortedFileCashMap = new TreeMap<K, Integer>(new KeyComparator(frequencyCashMap));
        sortedFileCashMap.putAll(frequencyCashMap);
        return sortedFileCashMap.keySet();
    }

    @Override
    public int getCallingFrequencyOfObject(K key) {
        return containsObject(key) ? frequencyCashMap.get(key) : 0;
    }

    private void serializeObject(String fileName, V value) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(value);
        objectOutputStream.flush();
        objectOutputStream.close();
        fileOutputStream.close();
    }

    private V deserializeObject(String fileName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            V object = (V) objectInputStream.readObject();
            fileInputStream.close();
            objectInputStream.close();
            return object;
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
