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
        // TODO -- implement
        // Should store T object, and two U keys
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

        if (request == null) {
        	U value = (U) _provider.get(key);
        	addElement(key, value);
        	
        	if (value == null) {
        		return null;
        	}
        	else return value;
        }

        update(key);
        return (U) request._value;        
	}



	/**
     * Resorts the linked list when a key/value pair has been accessed
     */
    public boolean update (T key) {
        // TODO -- implement
        return false;
    }

    /**
     * Adds an element to the cache in event of a miss
     */
    public boolean addElement (T key, U value) {
        // TODO -- implement
        // May need helper methods
        // Should create Element (Will be last value in LL)
        // Add element to end of LL
        // Add element to hashmap
    	final Element entry = new Element(value, null, _tail);
    	if (_head == null) {
    		_head = key;
    		_tail = key;
    	}
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
