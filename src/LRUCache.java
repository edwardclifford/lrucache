import java.util.HashMap;

/**
 * An implementation of <tt>Cache</tt> that uses a least-recently-used (LRU)
 * eviction policy.
 */
public class LRUCache<T, U> implements Cache<T, U> {
    private HashMap<T, Element<T, U>> _cache;
    private DataProvider<T, U> _provider;

    private T _head;
    private T _tail;

    private int _maxCapacity;
    private int _missCounter = 0;

    /**
     * Implements an Element, which stores a value and keys to the previous and next Elements in
     * the linked list.
     */
    private class Element<T, U> {
        protected U _value;   			//might mess up auto grader maybe just change to private
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
        _cache = new HashMap<T, Element<T, U>>();
        _provider = provider;    
        _maxCapacity = capacity;
	}
    
	/**
	 * Returns the value associated with the specified key.
	 * @param key the key
	 * @return the value associated with the key
	 */
	public U get (T key) {
        final Element<T, U> request = (Element<T, U>) _cache.get(key);

        //Search the provider
        if (request == null) {
        	_missCounter ++;
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
        System.out.println("Found element in cache.");
        update(key);
        return (U) request._value;        
	}

	/**
     * Re-sorts the linked list when a key/value pair has been accessed
     * @param key the key used to access the object
     */
    private void update (T key) {
		Element<T, U> currentElement = _cache.get(key);
		
    	//checks if the key is the _head
    	if (key.equals(_head)) {
    		if (currentElement._nextKey == null) return;
    		
    		_head = _cache.get(_head)._nextKey;    //updating head to the next element
    		_cache.get(_head)._lastKey = null;     //updating the new head point back to null
    		currentElement._lastKey = _tail;	   //updating new tail
    		currentElement._nextKey = null; 	   //updating new tail to point to null
    		_cache.get(_tail)._nextKey = key; 	   //updating old tail
    		_tail = key;						   //setting new tail
    		return;
    		
    	}
    	//if key is the tail
    	else if (key.equals(_tail)) {
    		return;
    	}
    	//any other case
    	else {

    		//Remove the element from the linked list, set neighbor elements to point to each other


    		_cache.get(currentElement._lastKey)._nextKey = currentElement._nextKey;

    		_cache.get(currentElement._nextKey)._lastKey = currentElement._lastKey;
    		
    		//Move the element to the end of the linked list
    		_cache.get(_tail)._nextKey = key;
    		
    		//Update the element's references
    		currentElement._nextKey = null;
    		currentElement._lastKey = _tail;
    		
    		//Update the tail pointer
    		_tail = key;
    		return;
    	}
        
    }

    /**
     * Adds a key value pair to the cache in event of a miss
     * @param key is the key attribute of the pair
     * @param value is the value attribute of the pair
     * @return true if the operation was successful, false
     * otherwise
     */
    private void addElement (T key, U value) {
        // TODO -- implement
        // May need helper methods
        // Should create Element (Will be last value in LL)
        // Add element to end of LL
        // Add element to hashmap
    	final Element<T, U> entry = new Element<T, U>(value, null, _tail);
        
        _cache.put(key, entry);

        System.out.print("Cache size: ");
        System.out.println(_cache.size());
        //Cache is empty
    	if (_head == null) {
            //System.out.println("Cache is empty");
    		_head = key;
    		_tail = key;
    	}
        //Cache is full
    	else if (_cache.size() - 1 == _maxCapacity) {
            //System.out.println("Cache is full");
            final T tempKey = _head;
            _head = _cache.get(_head)._nextKey;
            _cache.get(_head)._lastKey = null;
            //System.out.println("Trying to remove LUR entry.");
            _cache.remove(tempKey);
            
            _cache.get(_tail)._nextKey = key;
            _tail = key;
    	}
        else {
            _cache.get(_tail)._nextKey = key;
            _tail = key;
        }
    }

	/**
	 * Returns the number of cache misses since the object's instantiation.
	 * @return the number of cache misses since the object's instantiation.
	 */
	public int getNumMisses () {
		return _missCounter;
	}
}
