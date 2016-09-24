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
public class TwoLevelCacheTest {

    public TwoLevelCacheTest() {
    }

    private TwoLevelCache<Integer, String> twoLevelCache;

    @Before
    public void init() {
        twoLevelCache = new TwoLevelCache<>(1, 1);
    }

    @After
    public void clearCache() {
        twoLevelCache.clear();
    }

    @Test
    public void testPutIntoCache() throws IOException, ClassNotFoundException {
        assertTrue(twoLevelCache.getObject(0) == null);
        twoLevelCache.cache(0, "First Object");
        assertTrue(twoLevelCache.getObject(0).equals("First Object"));
    }

    @Test
    public void testDeleteFromCache() throws IOException, ClassNotFoundException {
        twoLevelCache.cache(0, "First Object");
        assertTrue(twoLevelCache.getObject(0).equals("First Object"));
        twoLevelCache.deleteObject(0);
        assertTrue(twoLevelCache.getObject(0) == null);
    }

    @Test
    public void testGetCacheSize() throws IOException, ClassNotFoundException {
        assertTrue(twoLevelCache.getSize() == 0);
        twoLevelCache.cache(0, "First Object");
        assertTrue(twoLevelCache.getSize() == 1);
        twoLevelCache.cache(1, "Second Object");
        assertTrue(twoLevelCache.getSize() == 2);
        twoLevelCache.cache(2, "Third Object");
        assertTrue(twoLevelCache.getSize() == 3);
    }

    @Test
    public void testCacheContainsObject() throws IOException, ClassNotFoundException {
        assertFalse(twoLevelCache.containsObject(0));
        twoLevelCache.cache(0, "First Object");
        assertTrue(twoLevelCache.containsObject(0));
    }

    @Test
    public void testClearCache() throws IOException, ClassNotFoundException {
        assertTrue(twoLevelCache.getSize() == 0);
        for (int i = 0; i < 10; i++) {
            twoLevelCache.cache(i, "object number " + i);
        }
        assertTrue(twoLevelCache.getSize() == 10);
        twoLevelCache.clear();
        assertTrue(twoLevelCache.getSize() == 0);
    }

    @Test
    public void testRecache() throws IOException, ClassNotFoundException {
        for (int i = 0; i < 3; i++) {
            twoLevelCache.cache(i, "object number " + i);
        }

        assertTrue(twoLevelCache.getSize() == 3);

        assertTrue(twoLevelCache.getRamCache().getSize() == 3);
        assertTrue(twoLevelCache.getFileCache().getSize() == 0);

        twoLevelCache.getObject(1);
        twoLevelCache.getObject(1);
        assertTrue(twoLevelCache.getRamCache().getSize() == 2);
        assertTrue(twoLevelCache.getFileCache().getSize() == 1);

        twoLevelCache.getObject(1);
        twoLevelCache.getObject(1);
        assertTrue(twoLevelCache.getRamCache().getSize() == 1);
        assertTrue(twoLevelCache.getFileCache().getSize() == 2);
    }
}
