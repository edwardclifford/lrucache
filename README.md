# lrucache
LRUCache CS2103 Assignment

Edward Clifford and Marie Tessier

[Assignment Link](https://web.cs.wpi.edu/~cs2103/b19/Project2/Project2.html)

<h1>Introduction</h1>
In this project you will use object-oriented design techniques to
implement and encapsulate an abstract data type (ADT) known
as a <b>cache</b>. A cache is  a data structure 
that provides <b>accelerated access</b> to a relatively small <b>subset</b> of data
that normally take a relatively long time to access.
Caches are <b>associative</b> such that the user/client (we will use both names interchangeably)
requests the <b>value</b> associated with a certain <b>key</b>.
For example, in a web cache, a key might be the URL of a page on a web server, and
the associated value might be the contents of the web page itself. For an image cache that
allows the client to access frequently used images more quickly than having to load them from disk,
the key might be the name of the image (e.g., "smurf"), and the value would be the image itself
(e.g., the bytes contents of a PNG file).
A cache serves as an <b>intermediary</b> between the user/client and a <b>data provider</b> -- the web server
or file system, in our two examples above. By using a cache to store a small subset of data in faster storage,
the client application can often be accelerated significantly.
Caches are ubiquitous in modern computer design; CPU caches, disk caches, web caches, etc.,
are all commonplace.<p>

By definition, caches can only store a <b>subset</b> of all possible (key,value) combinations
that a user might request. Why? The reason is that caches provide <b>accelerated</b> access to
data -- if they could contain an infinite number of data and provide them faster than the data provider
(the web server in our example above), then there would be no need for the data provider at all -- you
would just store everything in the cache. The fact that caches can only store a relatively small
number of (key,value) pairs compared to the set of all possible such pairs that the user might
request, means that the implementer of the cache has to think carefully about <b>which</b> data
should be stored. In addition, the implementer of the cache must ensure that access
to data is as fast as possible -- determining whether or not a (key,value) pair is already
stored in the cache should happen in <b>constant time</b>. Recall that <b>constant time</b> means that
the amount of time <b>does not depend on the number of data in the cache</b>.

<h1>How caches work</h1>
<img width="700" border="5" src="https://web.cs.wpi.edu/~cs2103/b19/Project2/Cache.svg"/><br>
The figure above provides a schematic for how caches work. Suppose you wish to cache a large
collection of images in RAM so that your program doesn't have to load them from disk 
every time it needs to display them. The "data provider" in this example would thus be the disk
(the definitive location where all the images are stored). Whenever the client needs to access
a particular image, it calls the <tt>get</tt> method of the cache and specifies the name of the image 
that it wants -- "smurf", in this example.

<h2>Cache miss</h2>
In the cache's implementation of the <tt>get</tt> method, the first thing it does is check whether
an object with a key of "smurf" is already stored in the cache. Let's suppose the cache is empty,
so that "smurf" is not found. In this case, we have a <b>cache miss</b> -- a key requested 
by the client was not found in the cache. Whenever the cache has a miss, it must forward
the <tt>get</tt> request to (i.e., call the <tt>get</tt> method of) the data provider. While the data provider has access to
all possible images the client might ever request, it is also relatively <b>slow</b> compared to the cache --
disk (even SSD) is typically slower than RAM, for example. Once the data provider has determined
the value that is associated with the key -- i.e., in our example, loaded the image with the specified name
from disk -- it returns the result back to the cache. The cache then
stores the (key,value) pair in its own internal memory -- which, by assumption, is much faster
than whatever storage is used by the data provider -- and then returns the result back to the client.
All in all, the amount of time taken by the cache to execute the <tt>get</tt> method is represented
by "Delta T for cache miss" in the figure.<p>

<h2>Cache hit</h2>
Now, let's suppose that, at some time in the near future, the client requests the "smurf" image
<b>again</b> from the cache. Since not much time has passed (and, more to the point, not many other
other images have been requested from the cache), it is likely that the "smurf" image is still stored
in the cache. In this case, the cache can return the value associated with the key "smurf" <b>directly</b> --
<b>without needing to consult the data provider</b>. The total time needed to service the
client's <tt>get</tt> request is now much reduced -- represented in the figure by "Delta T for cache hit".
<b>This is the whole point of the cache -- hopefully, for a large fraction of requested keys, the associated
values can be retrieved more quickly than by consulting the data provider itself.</b>
<p>

<h2>Eviction</h2>
Whenever the cache has a miss, it needs to consult the data provider, store the (key,value) pair in its
own storage, and then return the value to the client. Keep in mind that the cache's internal storage
is finite (and usually much smaller compared to the data provider's). If the cache's storage is full,
some <b>existing</b> (key,value) pair must be <b>evicted</b> from the cache and replaced with the
new pair. But which (key,value) pair should be replaced?<p>

There is no definitive answer to this question, and different programmers have different strategies
for trying to determine which will give the highest performance. In general, you want to pick your
<b>eviction strategy</b> so as to minimize the expected number of cache misses. In this assignment,
you will implement a commonly used heuristic known as <b>least-recently used (LRU)</b> -- it assumes that
the (key,value) pair that was requested <b>least recently</b> will likely not be requested in the near future.
In other words, when you need to evict an element stored in the cache's internal memory, you should evict
the item that was least recently used.<p>

Here's an example of how the LRU eviction strategy works. Suppose the cache has enough internal memory
to store just 2 (key,value) pairs, and suppose that the following sequence of keys is requested
by the client:
<tt>smurf garfield marge snoopy garfield snoopy</tt>.
How would the cache's internal memory look when serving each <tt>get</tt> request, and does the cache
hit or miss for each request? The answers are shown in the table below:
<table border=1>
	<tr><td><b>Requested key</b></td><td><b>Cache's memory before request</b></td><td><b>Hit or miss</b></td><td><b>Eviction</b></td></tr>
	<tr><td>smurf</td><td>-, -</td><td>miss</td><td>-</td></tr>
	<tr><td>garfield</td><td>smurf, -</td><td>miss</td><td>-</td></tr>
	<tr><td>marge</td><td>smurf, garfield</td><td>miss</td><td>smurf</td></tr>
	<tr><td>snoopy</td><td>marge, garfield</td><td>miss</td><td>garfield</td></tr>
	<tr><td>garfield</td><td>marge, snoopy</font></td><td>miss</td><td>marge</td></tr>
	<tr><td>snoopy</td><td>garfield, snoopy</td><td>hit</td><td>-</td></tr>
</table>

<h1>Implementing an LRU cache in Java</h1>
In this project, you will implement and encapsulate an LRU cache in a Java class
called <tt>LRUCache</tt>. You will also write test code both to verify that
<b>your own</b> cache is written correctly <b>and</b> to catch errors in other
people's possibly incorrectly written (buggy) caches. You will be graded on the correctness
of both the cache and the cache tester.<p>

The <tt>LRUCache</tt> class you create must have a public constructor that takes
two parameters. The first is the <tt>DataProvider</tt> that the cache will consult for
every cache miss. How exactly is the data provider defined? We want to be flexible, so we
will define a data provider as any Java class that implements a single method:
<tt>U get (T key)</tt>.
Notice that, in contrast to <tt>ArrayList</tt>, which takes only one type parameter, our cache takes
two type parameters: Here, <tt>T</tt> is the type of the key, and <tt>U</tt> is the type of the value associated with the
key. For example, <tt>T</tt> might be <tt>String</tt> (the name of the image),
and <tt>U</tt> might be <tt>Image</tt>.
See the <tt>DataProvider</tt> interface in the project Zip file.<p>

In addition to specifying the data provider, you also have to specify the <b>capacity</b> of the cache,
which is equal to the maximum number of elements (i.e., (key,value) pairs) that the cache can store at
one time. It is <b>crucial</b> that your cache allocate exactly enough memory to accomodate 
<tt>capacity</tt> memory slots -- <b>no more and no less</b> -- so that our automatic test code will evaluate
your cache correctly.<p>

The only methods that your LRU cache needs to implement are 
<tt>U get (T key)</tt> and <tt>int getNumMisses ()</tt>. The <tt>get</tt>
method has the exact same signature as in the <tt>DataProvider</tt> interface -- indeed
the <tt>Cache</tt> interface is actually a subinterface of <tt>DataProvider</tt>. The <tt>
getNumMisses</tt> reports the number of cache "misses" that have occurred <b>since the Cache
was instantiated</b>.

<h2>Usage example</h2>
Here is an example of how the cache might be used: First, there needs to be a data provider.
In our image cache example, this might look something like this:
<pre>
public class ImageLoader implements DataProvider&lt;String, Image&gt; {
	private final String _imageRootDirectory;
	public ImageLoader (String imageRootDirectory) {
		_imageRootDirectory = imageRootDirectory;
	}
	public Image get (String key) {
		return loadImageFromDiskAtDirectory(key, imageRootDirectory);
	}
}
</pre>
Next, we instantiate an <tt>LRUCache</tt> object and pass it an <tt>ImageLoader</tt> as the
data provider along with an initial capacity (I chose 128 arbitrarily):
<pre>
final Cache&lt;String, Image&gt; cache = new LRUCache&lt;String, Image&gt;(new ImageLoader("pathToMyImages"), 128);
</pre>
Now that the cache has been instantiated, we can use it:
<pre>
final Image smurf = cache.get("smurf");
final Image garfield = cache.get("garfield");
// ...
</pre>
Note that this was just an example. While you are free to test your cache with whatever objects
you like, I would suggest something simpler than actually loading images from disk.

<h2>LRU eviction policy</h2>
The hardest part of this assignment is implementing the LRU eviction policy so that
it executes in <b>constant time</b> in the <b>average case</b>. In particular, your <tt>LRUCache</tt>
should <b>not</b> just do a linear search through the cache's internal memory looking for (and updating)
the least-recently used item -- you must devise a faster procedure. We suggest that you creatively
use both a <b>linked list</b> and a <b>hashtable</b> to do so. Accordingly, <b>you are welcome
to use Java's built-in <tt>HashMap</tt> class</b>, which provides an off-the-shelf, high-performance
hashtable. See the <a href="https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html">Javadoc on <tt>HashMap</tt></a>
for more information.<p>

<b>Important</b>: In this assignment you <b>may not</b> use the Java SDK <tt>LinkedHashMap</tt> class (as that would ruin all the fun).

<h1>Testing an LRU cache in Java</h1>
In this project, you will be graded not only on your <em>implementation</em> of the  <tt>LRUCache</tt>, but 
also on how thoroughly you <em>test</em> it. Your test code should hopefully help you debug your own cache
implementation; in addition, we will also <b>test your tester</b> by using it to validate <em>other</em>
students' cache implementations. In particular, we will use your submitted <tt>CacheTest</tt>  to
test a variety of <tt>LRUCache</tt> implementations -- some of which are buggy, some of which are fully
correct -- and give you credit according to how accurately your tester reports errors.<p>

During your testing, you should create your own <tt>DataProvider</tt> -- whose inner workings you can observe and control completely since you are writing it -- that you should instantiate and pass to the <tt>LRUCache</tt> constructor.<p>

As noted in class, it is not mathematically possible to test for all possible errors. We will test 
your tester against a set of errors that commonly occur when students try to implement an <tt<LRUCache</tt>
(but fail). Also: your tester is <b>not</b> required to verify that the asymptotic time-costs of 
the <tt>LRUCache</tt> methods are constant.  As long as the public methods of the <tt>LRUCache</tt> you are 
testing operate correctly, then your tester should consider them "correct".

<h1>Requirements</h1>
<ol>
<li><b>R1 (35 points)</b>: Implement the <tt>Cache</tt> interface in an <tt>LRUCache</tt> class that implements
a least-recently-used (LRU) eviction policy:
<ul>
  <li>The <tt>LRUCache</tt> class should
contain a public constructor with the following signature: <tt>public LRUCache (DataProvider<T, U> provider, int capacity)</tt>.</li>
  <li>Your <tt>LRUCache</tt> must contain <b>exactly</b>
  the number of (key,value) pairs specified in the constructor's <tt>capacity</tt> parameter.
  Our automatic testing code will verify that the (key,value) pairs that are evicted -- and thus must be
  reloaded from the <tt>DataProvider</tt> -- are exactly correct according to the LRU policy described above.</li>
  <li>The <tt>get</tt> method of your <tt>LRUCache</tt> must execute in <b>constant time</b> in the <b>average case</b>.
  This is actually the crux of the assignment -- devising an algorithm that can find the LRU item, update it, and return
  the value associated with the key, all within a constant amount of time.</li>
  <li>All of the gritty details of your cache implementation should be <b>hidden</b> -- i.e., <b>encapsulated</b> --
  from the client/user. In addition, you should not make any unnecessary variables, methods, or utility classes 
  visible (public) to the client.</li>
  <li>Your cache must not only return the correct value associated with
a key, but it must also do so in constant time. <b>Submissions that do not achieve constant time in the average case for the
<tt>get</tt> method will receive at most 50% of the total points for this part of the assignment.</b></li>
</ul>
<li><b>R2 (15 points)</b>: Implement a class called <tt>CacheTest</tt> that instantiates one or more <tt>LRUCache</tt> objects
and conducts <tt>junit</tt> unit tests them for correctness. Your <tt>CacheTest</tt> must satisfy the following criteria:
  <ul>
    <li><b>All</b> tests must pass on a correctly implemented <tt>LRUCache</tt> implementation.</li>
    <li><b>At least 1</b> unit test must fail on an incorrectly implemented <tt>LRUCache</tt> implementation.</li>
  </ul>
</ol>


<h1>Teamwork</h1>
You may work as a team on this project; the maximum team size is 2.

<h1>Style</h1>
Your code must adhere to reasonable Java style. In particular, please adhere to the following guidelines:
<ul>
<li>Each class name should be a singular noun that can be easily pluralized.</li>
<li>Class names should be in <tt>CamelCase</tt>; variables should be in <tt>mixedCase</tt>.</li>
<li>Avoid "magic numbers" in your code (e.g., <tt>for (int i = 0; i < 999 /*magic number*/; i++)</tt>). Instead,
use <b>constants</b>, e.g., <tt>private static final int NUM_ELEPHANTS_IN_THE_ROOM = 999;</tt>, defined at the top of your class file.</li>

<li>Use whitespace consistently.</li>
<li>No method should exceed 50 lines of code (for a "reasonable" maximum line length, e.g., 100 characters). If your method is larger than
that, it's probably a sign it should be decomposed into a few helper methods.</li>
<li>Use comments to explain non-trivial aspects of code.</li>
<li>Use a comment to explain what each method does, what parameters it takes, and what it returns.</li>
<li>Use the <tt>final</tt> keyword whenever possible.</li>
<li>Use the <b>most restrictive</b> access modifiers (e.g., <tt>private</tt>, default, <tt>protected</tt>>, <tt>public</tt>),
for both variables and methods, that you can.</li>
<li>Declare variables using the <b>weakest type</b> (e.g., an interface rather than a specific class implementation) you can;
ithen instantiate new objects according to the actual class you need. This will help to ensure <b>maximum flexibility</b> of your code.
For example, instead of<br>
<tt>final ArrayList&lt;String&gt; list = new ArrayList<String>();</tt><br>use<br>
<tt>final List&lt;String&gt; list = new ArrayList&lt;String&gt;();</tt>.</li>
</ul>

<h1>Getting started</h1>
Download the <a href="https://www.cs.wpi.edu/~cs2103/b19/Project2/Project2.zip">Project2 starter file</a>.

<h1>Unit testing</h1>
When testing your cache, it will be necessary to implement a <tt>DataProvider</tt>. We recommend that you
endow your provider with some additional methods that will enable you to verify that the cache
is working correctly. For instance, if you know that a (key,value) pair should already
be stored in the cache, and the client calls <tt>get</tt> on that same key (resulting in a cache hit), then the cache should <b>not</b> call
the <tt>get</tt> method of your data provider -- the cache should just return the value associated with the key
immediately. By implementing a "test" data provider that offers some auxiliary methods (e.g., <tt>getNumFetches</tt>), you can
verify the correctness (or incorrectness) of the <tt>LRUCache</tt> you are testing.


<h1>What to Submit</h1>
Create a Zip file containing <tt>LRUCache.java</tt>, <tt>CacheTest.java</tt>, and whatever other helper
classes are needed to compile your code.
Submit the Zip file you created to Canvas. <b>Submission deadline</b>: Wednesday, November 6, at 11:59pm EDT.
