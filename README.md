# redblack
A Red-Black Tree implementation in Java 8 that is
* Immutable: mutator operations create new trees
* Strict set: duplicates are not allowed
* Space efficient: trees created by mutator operations share most of the data with the original tree

#### Public API:
| | |
| --- | --- |
| Tree<T>() | Creates an empty Tree | 
| boolean contains(T value) | Returns true if this element is contained in the tree |
| Tree<T> insert(T value) | Returns a copy of the tree with an element inserted |
| boolean isEmpty() | Returns true if the tree is empty |
| Iterator<T> iterator() | Returns an iterator over the contents of the tree |
| Tree<T> remove(T value) | Returns a copy of the tree with an element removed |
| int size() | Returns size of the tree |

Run `mvn package` to build the project jar and javadoc, `mvn test` to run functional tests

Tested with JDK 8_131 and Maven 3.0.5