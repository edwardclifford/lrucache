FLAGS = -g
JC = javac
JVM = java

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
		  Cache.java\
		  CacheTest.java\
		  DataProvider.java\
		  LRUCache.java \

MAIN = LRUCache 

TEST = CacheTest

default: classes

classes: $(CLASSES:.java=.class)

run: classes 
	$(JVM) $(MAIN) 

test: classes 
	$(JVM) org.junit.runner.JUnitCore $(TEST) 


clean:
	$(RM) *.class
