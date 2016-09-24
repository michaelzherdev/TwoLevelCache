package com.mzherdev.twolevelcache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Mikhail on 23.09.2016.
 */
public class FileCacheTest {

    public FileCacheTest() {
    }

    private FileCache<Integer, String> fileCache;

    @Before
    public void init() {
        fileCache = new FileCache<>();
    }

    @After
    public void clearCache() {
        fileCache.clear();
    }

    @Test
    public void testPutIntoCache() throws IOException, ClassNotFoundException {
        assertTrue(fileCache.getObject(0) == null);
        fileCache.cache(0, "First Object");
        assertTrue(fileCache.getObject(0).equals("First Object"));
    }

    @Test
    public void testDeleteFromCache() throws IOException, ClassNotFoundException {
        fileCache.cache(0, "First Object");
        assertTrue(fileCache.getObject(0).equals("First Object"));
        fileCache.deleteObject(0);
        assertTrue(fileCache.getObject(0) == null);
    }

    @Test
    public void testGetCacheSize() throws IOException, ClassNotFoundException {
        assertTrue(fileCache.getSize() == 0);
        fileCache.cache(0, "First Object");
        assertTrue(fileCache.getSize() == 1);
        fileCache.cache(1, "Second Object");
        assertTrue(fileCache.getSize() == 2);
        fileCache.cache(2, "Third Object");
        assertTrue(fileCache.getSize() == 3);
    }

    @Test
    public void testCacheContainsObject() throws IOException, ClassNotFoundException {
        assertFalse(fileCache.containsObject(0));
        fileCache.cache(0, "First Object");
        assertTrue(fileCache.containsObject(0));
    }

    @Test
    public void testClearCache() throws IOException, ClassNotFoundException {
        assertTrue(fileCache.getSize() == 0);
        for (int i = 0; i < 10; i++) {
            fileCache.cache(i, "object number " + i);
        }
        assertTrue(fileCache.getSize() == 10);
        fileCache.clear();
        assertTrue(fileCache.getSize() == 0);
    }
}
