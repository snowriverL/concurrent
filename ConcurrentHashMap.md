## ConcurrentHashMap1.8中是基于什么机制来保证线程安全性的

>- 通过使用CounterCell控制并发下Map结构中数据个数得计算。
>- 当前 nodes 数组对应位置节点的头节点为空，则直接通过cas将新的值封装成node插入即可；不为空，则给当前node数组得头节点加锁，更细粒度得加锁，减少并发冲突的概率。


## ConcurrentHashMap通过get方法获取数据的时候，是否需要通过加锁来保证数据的可见性？为什么？
```java
    public V get(Object key) {
        Node<K,V>[] tab; Node<K,V> e, p; int n, eh; K ek;
        int h = spread(key.hashCode());
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (e = tabAt(tab, (n - 1) & h)) != null) {
            if ((eh = e.hash) == h) {
                if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                    return e.val;
            }
            else if (eh < 0)
                return (p = e.find(h, key)) != null ? p.val : null;
            while ((e = e.next) != null) {
                if (e.hash == h &&
                    ((ek = e.key) == key || (ek != null && key.equals(ek))))
                    return e.val;
            }
        }
        return null;
    }
```
- 可以看到会使用tabAt获取到值所在的node数组
```java

    static final <K,V> Node<K,V> tabAt(Node<K,V>[] tab, int i) {
        return (Node<K,V>)U.getObjectVolatile(tab, ((long)i << ASHIFT) + ABASE);
    }
    
```
- 调用了Unsafe类的getObjectVolatile方法来获取对象，通过volatile来保证对数组内元素的写操作一定Happens Before于对数组内元素的读操作，再通过hash值得比较获取到值，保证了数据得可见性

## ConcurrentHashMap1.7和ConcurrentHashMap1.8有哪些区别？
### JDK1.7
>- ConrruentHashMap 由一个个Segment组成，简单来说，ConcurrentHashMap是一个Segment数组，它通过继承ReentrantLock 来进行加锁，通过每次锁住一个segment来保证每个segment内的操作的线程安全性从而实现全局线程安全。
### JDK1.8
>- 取消了 segment 分段设计，直接使用 Node数组来保存数据，并且采用 Node 数组元素作为锁来实现每一行数据进行加锁来进一步减少并发冲突的概率
>- 将原本数组+单向链表的数据结构变更为了数组+单向链表+红黑树的结构。

## ConcurrentHashMap1.8为什么要引入红黑树？

- 在正常情况下， key hash 之后如果能够很均匀的分散在数组中，那么 table 数组中的每个队列的长度主要为 0 或者 1.但是实际情况下，还是会存在一些队列长度过长的情况。如果还采用单向列表方式，那么查询某个节点的时间复杂度就变为 O(n); 因此对于队列长度超过8并且数组长度大于等于64的列表， JDK1.8 采用了红黑树的结构，那么查询的时间复杂度就会降低到O(logN),可以提升查找的性能；