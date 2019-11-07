import java.util.HashMap;

/**
 * An implementation of <tt>Cache</tt> that uses a least-recently-used (LRU)
 * eviction policy.
 */
public class LRUCache<T, U> implements Cache<T, U> {
	//creates the cache as a HashMap that takes a key type T and an object
    private HashMap<T, Element<T, U>> _cache;
    //creates a DataProvider to be used in the class
    private DataProvider<T, U> _provider;

    //the first term in the hash table
    private T _head;
    //the last term in the hash table
    private T _tail;

    //maximum capacity of the cache
    private int _maxCapacity;
    //counts the number of misses when calling the cache
    private int _missCounter = 0;

    /**
     * Implements an Element, which stores a value and keys to the previous and next Elements in
     * the linked list.
     */
    private class Element<T, U> {
        protected U _value;   			
        protected T _nextKey;    //a pointer to the next value 
        protected T _lastKey;    //a pointer to the previous value 
        
        //initializes the value and keys for Element 
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
        	_missCounter++;
        	U value = (U) _provider.get(key);

        	addElement(key, value);
            return value;
        }

        //Value located in cache, update to recently used 
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
    		
    		//updating head to the next element and than to previous value pointer to null
    		_head = _cache.get(_head)._nextKey;   
    		_cache.get(_head)._lastKey = null;
    		 //updating new tail and setting its pointer to null
    		currentElement._lastKey = _tail;	   
    		currentElement._nextKey = null; 	
    		//updating the old tail and setting the new tail
    		_cache.get(_tail)._nextKey = key; 	
    		_tail = key;						
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
    	final Element<T, U> entry = new Element<T, U>(value, null, _tail);
        
        _cache.put(key, entry);

        //Cache is empty
    	if (_head == null) {
    		_head = key;
    		_tail = key;
    	}
        //Cache is full
    	else if (_cache.size() - 1 == _maxCapacity) {
            final T tempKey = _head;
            _head = _cache.get(_head)._nextKey;
            _cache.get(_head)._lastKey = null;
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
