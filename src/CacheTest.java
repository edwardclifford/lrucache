import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Code to test an <tt>LRUCache</tt> implementation.
 */
public class CacheTest {
    /**
     * Implements a DataProvider that can be asked for keys of value (0..4).
     */
    public static class TestDataProvider implements DataProvider<Integer, String> {

        private boolean _referenced = false;
        public int timesReferenced = 0;

        /**
         * Returns a string for a given integer key
         * @param key a value 0 through 4
         * @return a string for the value or null if no value was found
         */
        public String get (Integer key) {
            timesReferenced ++;
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

        /**
         * Allows the referenced flag to be reset
         */
        public void resetReferenced () {
            _referenced = false;
        }

        /**
         * Checks if the TestDataProvider was used by the cache.
         * @return true if referenced, false otherwise.
         */
        public boolean wasReferenced () {
            return _referenced;
        }
    }

    /**
     * Confirms that the TestDataProvider class returns what is expected.
     */
    @Test
    public void testTestDataProviderWorks() {
        DataProvider<Integer, String> provider = new TestDataProvider();
        String testValue = provider.get(0);
        assertTrue(testValue == "Value for key 0.");
    }
    
    /**
     * Tests a cache of one but calling get zero twice
     */
    @Test
    public void testCacheSizeOne() {
        TestDataProvider provider = new TestDataProvider();
        Cache<Integer, String> cache = new LRUCache<Integer, String>(provider, 1);
        cache.get(0);
        assertTrue(provider.wasReferenced());
        provider.resetReferenced();
        String fromCache = cache.get(0);
        assertFalse(provider.wasReferenced());
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
        
        provider.resetReferenced();
        String fromCache = cache.get(0);
        assertTrue(provider.wasReferenced());
        
        provider.resetReferenced();
        fromCache = cache.get(3);
        assertFalse(provider.wasReferenced());
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
        assertTrue(provider.wasReferenced());
        provider.resetReferenced();
        
        cache.get(1);
        assertTrue(provider.wasReferenced());
        provider.resetReferenced();
        
        cache.get(2);
        assertTrue(provider.wasReferenced());
        provider.resetReferenced();
        
        //tests that 1 is already in the cache
        cache.get(1);
        assertFalse(provider.wasReferenced());
        
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
                
        provider.resetReferenced();
        String fromCache = cache.get(0);
        assertFalse(provider.wasReferenced());
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
        
        provider.resetReferenced();
        String fromCache = cache.get(2);
        assertFalse(provider.wasReferenced());
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
        
        provider.resetReferenced();
        String fromCache = cache.get(3);
        assertFalse(provider.wasReferenced());
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
        
    }
    
    
    

    @Test
    public void leastRecentlyUsedIsCorrect () {
        DataProvider<Integer,String> provider = null; // Need to instantiate an actual DataProvider
        Cache<Integer,String> cache = new LRUCache<Integer,String>(provider, 5);
    }
}
