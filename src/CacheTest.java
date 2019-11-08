/*
 * Project 2
 * Marie Tessier and Edward Clifford
 */

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;
import java.util.ArrayList;

/**
 * Code to test an <tt>LRUCache</tt> implementation.
 */
public class CacheTest {
    /**
     * Implements a DataProvider that can be asked for keys of value (0..4).
     */
    public static class TestDataProvider implements DataProvider<Integer, String> {
        public boolean _referenced = false;
        public int _timesReferenced = 0;

        /**
         * Returns a string for a given integer key
         * @param key a value 0 through 4
         * @return a string for the value or null if no value was found
         */
        public String get (Integer key) {
            _timesReferenced++;
            _referenced = true;
            switch (key) {
                case 0:
                    return "Value for key 0.";
                case 1:
                    return "Value for key 1.";
                case 2:
                    return "Value for key 2.";
                case 3:
                    return "Value for key 3.";
                case 4:
                    return "Value for key 4.";
            }
            return null;
        }
    }

    /**
     * Impliments a data provider that returns the string of the key passed
     */
    public class EchoDataProvider implements DataProvider<Integer, String> {
        public int _timesReferenced = 0;
        public boolean _referenced = false;

        /**
         * Returns a value for a given key
         * @param key any integer
         * @return the key converted to a string
         */
        public String get (Integer key) {
            _timesReferenced++;
            _referenced = true;
            return Integer.toString(key); 
        } 
    } 

    /**
     * Implements a data provider that uses the Key class
     */
    public class KeyDataProvider implements DataProvider<Key, String> {
        public int _timesReferenced = 0;
        public boolean _referenced = false;
        
        /**
         * Returns a value for the given key
         * @param key a Key type object
         * @return the value which is the string component of the key
         */
        public String get (Key key) {
            _timesReferenced++;
            _referenced = true; 
            return key._stringComponent;
        } 
    }

    /**
     * Implements the Key class used to test an arbitrary type key.
     */
    public class Key {
        int _intComponent;
        String _stringComponent;

        Key (int number, String phrase) {
            _intComponent = number;
            _stringComponent = phrase; 
        } 
        
        @Override
        public boolean equals (Object o) {
            final Key other = (Key) o;
            return _intComponent == other._intComponent && _stringComponent == other._stringComponent;
        }

        @Override
        public int hashCode () {
            int hash = 0;
            String combinedString = "" + _intComponent + _stringComponent;
            int digits = combinedString.length(); 
            String hashString = "";
            for (int i = 0; i < digits; i++) {
                hashString += (int) combinedString.charAt(i);
            }
            return hashString.hashCode();
        }
    }

    /**
     * Tests a cache of one but calling get zero twice
     */
    @Test
    public void testCacheSizeOne() {
        TestDataProvider provider = new TestDataProvider();
        Cache<Integer, String> cache = new LRUCache<Integer, String>(provider, 1);

        //Check that provider is referenced in event of a miss
        cache.get(0);
        assertTrue(provider._referenced);

        //Check that provider is not referenced when the key is in the cache
        provider._referenced = false;
        String fromCache = cache.get(0);
        assertFalse(provider._referenced);

        //Returns the correct value from cache
        assertTrue(fromCache == "Value for key 0.");
    }
    
    /**
     * Tests trying to add a new element to cache after the cache is full
     */
    @Test 
    public void testMax() {
    	TestDataProvider provider = new TestDataProvider();
        Cache<Integer, String> cache = new LRUCache<Integer, String>(provider, 3);

        cache.get(0);
        cache.get(1);
        cache.get(2);
        cache.get(3);
        
        //LRU will be removed, check that provider is referenced
        provider._referenced = false;
        String fromCache = cache.get(0);
        assertTrue(provider._referenced);
        
        //Check for value that should not be removed by cache
        provider._referenced = false;
        fromCache = cache.get(3);
        assertFalse(provider._referenced);
        assertTrue(fromCache == "Value for key 3.");       
    }
    
    /**
     * Tests that the number of misses is correct
     */
    @Test 
    public void testNumberOfMisses() {
    	TestDataProvider provider = new TestDataProvider();
        Cache<Integer, String> cache = new LRUCache<Integer, String>(provider, 4); 
        
        cache.get(0);
        cache.get(1);
        cache.get(2);
        
        //initial check before repeat
        int numMisses = cache.getNumMisses();
        assertTrue(numMisses == 3);
        
        //asks for values already in cache
        cache.get(1);
        cache.get(2);
        int numMisses2 = cache.getNumMisses();
        assertTrue(numMisses2 == 3);
        
        //asks for another value not in cache
        cache.get(3);
        int numMisses3 = cache.getNumMisses();
        assertTrue(numMisses3 == 4);      
    }
    
    /**
     * Tests if you call a value already in cache it should not get from provider
     */
    @Test 
    public void testReferenceTail() {
    	TestDataProvider provider = new TestDataProvider();
        Cache<Integer, String> cache = new LRUCache<Integer, String>(provider, 3);
        cache.get(0);
        assertTrue(provider._referenced);
        provider._referenced = false;
        
        cache.get(1);
        assertTrue(provider._referenced);
        provider._referenced = false;
        
        cache.get(2);
        assertTrue(provider._referenced);
        provider._referenced = false;
        
        //tests that 1 is already in the cache
        cache.get(1);
        assertFalse(provider._referenced);
    }
    
    /**
     * Tests if you call a value already in cache that is the first element added
     */
    @Test 
    public void testReferenceHead() {
    	TestDataProvider provider = new TestDataProvider();
        Cache<Integer, String> cache = new LRUCache<Integer, String>(provider, 3);
        cache.get(0);      
        cache.get(1);
        cache.get(2);
                
        provider._referenced = false;
        String fromCache = cache.get(0);
        assertFalse(provider._referenced);
        assertTrue(fromCache == "Value for key 0.");     
    }
    
    /**
     * Tests trying to reference an element in the middle of the cache
     */
    @Test 
    public void testReferenceMiddle() {
    	TestDataProvider provider = new TestDataProvider();
        Cache<Integer, String> cache = new LRUCache<Integer, String>(provider, 4);
        cache.get(0);
        cache.get(1);
        cache.get(2);
        cache.get(3);
        
        provider._referenced = false;
        String fromCache = cache.get(2);
        assertFalse(provider._referenced);
        assertTrue(fromCache == "Value for key 2.");       
    }
    
    /**
     * Tests trying to reference an element in the end of the cache
     */
    @Test 
    public void testReferenceEnd() {
    	TestDataProvider provider = new TestDataProvider();
        Cache<Integer, String> cache = new LRUCache<Integer, String>(provider, 4);
        cache.get(0);
        cache.get(1);
        cache.get(2);
        cache.get(3);
        
        provider._referenced = false;
        String fromCache = cache.get(3);
        assertFalse(provider._referenced);
        assertTrue(fromCache == "Value for key 3.");       
    }
    
    /**
     * Tests to try and add an element that is not in the Cache or the DataProvider
     */
    @Test 
    public void testNoReference() {
    	TestDataProvider provider = new TestDataProvider();
        Cache<Integer, String> cache = new LRUCache<Integer, String>(provider, 4);
        cache.get(0);
        cache.get(1);

        //not in DataProvider
        assertNull(cache.get(7));

        //Check that cache stored null under key 7
        provider._referenced = false;
        assertNull(cache.get(7));
        assertFalse(provider._referenced);
    }

    /**
     *  Tests that the least recently used element is correct 
     */
    @Test
    public void leastRecentlyUsedIsCorrect () {
    	TestDataProvider provider = new TestDataProvider();
        Cache<Integer, String> cache = new LRUCache<Integer, String>(provider, 3);
        //inserts values into the cache
        cache.get(0);
        cache.get(1);
        cache.get(2);
        
        //inserts a 4th value, larger than the max amount allowed in the cache
        //     the last used value (0 in this case) should be the only one that should not be in the cache now
        cache.get(3);
        
        //makes sure 1 is in the cache
        provider._referenced = false;
        cache.get(1);
        assertFalse(provider._referenced);
        
        //makes sure 2 is in the cache
        provider._referenced = false;
        cache.get(2);
        assertFalse(provider._referenced);
        
        //makes sure 3 is in the cache
        provider._referenced = false;
        cache.get(3);
        assertFalse(provider._referenced);  
        
        //0 is not in the cache because it was least recently used, it should have to get 0 from the data provider
        provider._referenced = false;
        cache.get(0);
        assertTrue(provider._referenced);  
    }
    
    /**
     * Test that a cache can handle consuming a key that is not a primitive type
     */
    @Test
    public void testArbitraryTypeKey () {
        KeyDataProvider provider = new KeyDataProvider();
        Cache<Key, String> cache = new LRUCache<Key, String>(provider, 5);

        //Insert values into cache, key0 will be evicted
        Key key0 = new Key(0, "This is key 0.");
        Key key1 = new Key(1, "This is key 1.");
        Key key2 = new Key(2, "This is key 2.");
        Key key3 = new Key(3, "This is key 3.");
        Key key4 = new Key(4, "This is key 4.");
        Key key5 = new Key(5, "This is key 5.");

        cache.get(key0);
        cache.get(key1);
        cache.get(key2);
        cache.get(key3);
        cache.get(key4);
        cache.get(key5);

        //Evicted value
        provider._referenced = false;
        assertTrue("This is key 0." == cache.get(key0));
        assertTrue(provider._referenced);

        //MRU value
        provider._referenced = false;
        assertTrue("This is key 0." == cache.get(key0));
        assertFalse(provider._referenced);

        //LRU value
        provider._referenced = false;
        assertTrue("This is key 2." == cache.get(key2));
        assertFalse(provider._referenced);

        //Middle value
        provider._referenced = false;
        assertTrue("This is key 4." == cache.get(key4));
        assertFalse(provider._referenced);

        assertTrue(cache.getNumMisses() == provider._timesReferenced);
    }

    /**
     * Test a cache with size 25 and getting 100 random values
     */
    @Test
    public void testRandom100 () {
        randomHelper(25, 4);
    } 

    /**
     * Test a cache with size 100 and getting 200 random values
     */
    @Test
    public void testRandom200 () {
        randomHelper(100, 2);
    }

    /**
     * Test a cache with size 100 and getting 1000 random values
     */
    @Test
    public void testRandom1000 () {
        randomHelper(100, 10);
    }

    /**
     * Test a cache with size 1000 and getting 1000 random values
     * Will be getting hits most of the time
     */
    @Test
    public void testRandomHighAccessRate () {
        randomHelper(1000, 1);
    }

    /**
     * Test a cache with size 1000 and getting 100000 random values
     * Will be getting misses most of the time
     */
    @Test
    public void testRandomLowAccessRate () {
        randomHelper(1000, 100);
    }
    
    /**
     * Create a cache of a size and request an amount of elements to test access: 
     * The purpose of this helper is to emulate a cache that is LRU, but is not complex and
     * only has the time complexity of O(n).
     * @param size the size of the cache
     * @param nameSpaceScalar how much larger the nameSpace should be than the cache
     */ 
    public void randomHelper (int size, int nameSpaceScalar) {
        // Create provider and cache
        EchoDataProvider provider = new EchoDataProvider();
        Cache<Integer, String> cache = new LRUCache<Integer, String>(provider, size);
        int nameSpace = size * nameSpaceScalar;

        // Create slow cache to track what keys are stored in the cache 
        ArrayList<Integer> fakeCache = new ArrayList<Integer>();
        int fakeCacheMisses = 0;
        Random rand = new Random();

        // Insert an ammount of keys into the cache and track which ones are stored
        for (int i = 0; i <= nameSpace; i++) {
            int key = rand.nextInt(nameSpace);
            cache.get(key);
            if (fakeCache.contains(key)) {
                fakeCache.remove(fakeCache.indexOf(key));
                fakeCache.add(0, key);
            }
            else {
                fakeCacheMisses++;
                fakeCache.add(0, key);
                if (fakeCache.size() > size) {
                    fakeCache.remove(size);
                }
            } 
        }

        // Read from cache and compare to slow cache
        for (int i = 0; i <= nameSpace; i++) {
            int key = rand.nextInt(nameSpace); 
            provider._referenced = false;
            cache.get(key);
            if (fakeCache.contains(key)) {
                // In event of a hit, provider should not be called, update fake cache
                assertFalse(provider._referenced);
                fakeCache.remove(fakeCache.indexOf(key));
                fakeCache.add(0, key);
            }
            else {
                // In event of a miss, provider should be called, add to fake cache
                fakeCacheMisses++;
                assertTrue(provider._referenced);
                fakeCache.add(0, key);
                if (fakeCache.size() > size) {
                    fakeCache.remove(size);
                }
            }
        }
        // Check that the cache misses are equal to the times the provider was called
        assertTrue(cache.getNumMisses() == provider._timesReferenced && 
                   cache.getNumMisses() == fakeCacheMisses);
    }
    
    @Test
    public void checkComparison () {
        Key key1 = new Key(1, "string1");
        Key key1Clone = new Key(1, "string1");
        Key key2 = new Key(2, "string2");

        assertTrue(key1.equals(key1Clone));
        assertFalse(key1 == key1Clone);
        assertFalse(key1.equals(key2));
    }
}
