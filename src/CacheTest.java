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

        TestDataProvider () {}

        public String get (Integer key) {
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
     * Confirms that the TestDataProvider class returns what is expected.
     */
    @Test
    public void testTestDataProviderWorks() {
        DataProvider<Integer, String> provider = new TestDataProvider();
        String testValue = provider.get(0);
        assertTrue(testValue == "Value for key 0.");
    }

	@Test
	public void leastRecentlyUsedIsCorrect () {
		DataProvider<Integer,String> provider = null; // Need to instantiate an actual DataProvider
		Cache<Integer,String> cache = new LRUCache<Integer,String>(provider, 5);
	}
}
