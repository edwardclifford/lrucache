import java.util.HashMap;

/**
 * An implementation of <tt>Cache</tt> that uses a least-recently-used (LRU)
 * eviction policy.
 */
public class LRUCache<T, U> implements Cache<T, U> {
    private HashMap _cache;
    private DataProvider _provider;

    private T _head;
    private T _tail;

    private int _maxCapacity;
    private int _missCounter = 0;

    /**
     * Implements an Element, which stores a value and keys to the previous and next Elements in
     * the linked list.
     */
    private class Element<T, U> {
        protected U _value;
        protected T _nextKey;
        protected T _lastKey;

        Element (U value, T nextKey, T lastKey) {
            _value = value;
            _nextKey = nextKey;
            _lastKey = lastKey;
        }
    }

	/**
	 * @param provider the data provider to consult for a cache miss
	 * @param capacity the exact number of (key,value) pairs to store in the cache
	 */
	public LRUCache (DataProvider<T, U> provider, int capacity) {
        _cache = new HashMap();
        _provider = provider;    
        _maxCapacity = capacity;
	}

	/**
	 * Returns the value associated with the specified key.
	 * @param key the key
	 * @return the value associated with the key
	 */
	public U get (T key) {
        Element request = (Element) _cache.get(key);

        //Search the provider
        if (request == null) {
        	U value = (U) _provider.get(key);
        	
            //Provider returns null
        	if (value == null) return null;

            //Add new value to cache
        	else {
        	    addElement(key, value);
                return value;
            }
        }

        //Value located in cache, update to recently used 
        update(key);
        return (U) request._value;        
	}



	/**
     * Resorts the linked list when a key/value pair has been accessed
     */
    private boolean update (T key) {
        // TODO -- implement
        return false;
    }

    /**
     * Adds a key value pair to the cache in event of a miss
     * @param key is the key attribute of the pair
     * @param value is the value attribute of the pair
     * @return true if the operation was successful, false
     * otherwise
     */
    private boolean addElement (T key, U value) {
        // TODO -- implement
        // May need helper methods
        // Should create Element (Will be last value in LL)
        // Add element to end of LL
        // Add element to hashmap
    	final Element entry = new Element(value, null, _tail);
        
        //Cache is empty
    	if (_head == null) {
    		_head = key;
    		_tail = key;
    	}
        //Cache is full
    	else if (_cache.size() == _maxCapacity) {
    		
    	}
        return false;
    }

	/**
	 * Returns the number of cache misses since the object's instantiation.
	 * @return the number of cache misses since the object's instantiation.
	 */
	public int getNumMisses () {
		return _missCounter;
	}
}
