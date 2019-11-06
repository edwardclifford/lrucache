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
     * Confirms that the TestDataProvider class returns what is expected.
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
        Cache<Integer, String> cache = new LRUCache<Integer, String>(provider, 1);
        cache.get(0);
        cache.get(1);
        cache.get(2);
        cache.get(3);
        cache.get(4);
        cache.get(0);
        String fromCache = cache.get(0);
        assertTrue(fromCache == "Value for key 0.");       
        
    }

    @Test
    public void leastRecentlyUsedIsCorrect () {
        DataProvider<Integer,String> provider = null; // Need to instantiate an actual DataProvider
        Cache<Integer,String> cache = new LRUCache<Integer,String>(provider, 5);
    }
}
