package com.mzherdev.twolevelcache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by mzherdev on 23.09.16.
 */
public class RamCacheTest {

    public RamCacheTest() {
    }

    private RamCache<Integer, String> ramCache;

    @Before
    public void init() {
        ramCache = new RamCache<>();
    }

    @After
    public void clearCache() {
        ramCache.clear();
    }

    @Test
    public void testPutIntoCache() throws IOException, ClassNotFoundException {
        assertTrue(ramCache.getObject(0) == null);
        ramCache.cache(0, "First Object");
        assertTrue(ramCache.getObject(0).equals("First Object"));
    }

    @Test
    public void testDeleteFromCache() throws IOException, ClassNotFoundException {
        ramCache.cache(0, "First Object");
        assertTrue(ramCache.getObject(0).equals("First Object"));
        ramCache.deleteObject(0);
        assertTrue(ramCache.getObject(0) == null);
    }

    @Test
    public void testGetCacheSize() throws IOException, ClassNotFoundException {
        assertTrue(ramCache.getSize() == 0);
        ramCache.cache(0, "First Object");
        assertTrue(ramCache.getSize() == 1);
        ramCache.cache(1, "Second Object");
        assertTrue(ramCache.getSize() == 2);
        ramCache.cache(2, "Third Object");
        assertTrue(ramCache.getSize() == 3);
    }

    @Test
    public void testCacheContainsObject() throws IOException, ClassNotFoundException {
        assertFalse(ramCache.containsObject(0));
        ramCache.cache(0, "First Object");
        assertTrue(ramCache.containsObject(0));
    }

    @Test
    public void testClearCache() throws IOException, ClassNotFoundException {
        assertTrue(ramCache.getSize() == 0);
        for (int i = 0; i < 10; i++) {
            ramCache.cache(i, "object number " + i);
        }
        assertTrue(ramCache.getSize() == 10);
        ramCache.clear();
        assertTrue(ramCache.getSize() == 0);
    }
}
