## 为什么要使用线程池？
>- 如果每个请求到达就创建一个新线程， 创建和销毁线程花费的时间和消耗的系统
资源都相当大，甚至可能要比在处理实际的用户请求的时间和资源要多的多
>- 降低创建线程和销毁线程的性能开销
>- 提高响应速度，当有新任务需要执行是不需要等待线程创建就可以立马执行
>- 合理的设置线程池大小可以避免因为线程数超过硬件资源瓶颈带来的问题
## Executors提供的四种线程池:newSingleThreadExecutor,newFixedThreadPool,newCachedThreadPool,newScheduledThreadPool ，请说出他们的区别以及应用场景
>- newFixedThreadPool： 该方法返回一个固定数量的线程池，线程数不变，当有一个任务提交
时，若线程池中空闲，则立即执行，若没有，则会被暂缓在一个任务队列中，等待有空闲的
线程去执行。FixedThreadPool 用于负载比较大的服务器，为了资源的合理利用，需要限制当前线
程数量
>- newSingleThreadExecutor: 创建一个线程的线程池，若空闲则执行，若没有空闲线程则暂缓
在任务队列中。保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行
>- newCachedThreadPool： 返回一个可根据实际情况调整线程个数的线程池，不限制最大线程
数量，若用空闲的线程则执行任务，若无任务则不创建线程。并且每一个空闲线程会在 60 秒
后自动回收
>- newScheduledThreadPool: 创建一个可以指定线程的数量的线程池，但是这个线程池还带有
延迟和周期性执行任务的功能，类似定时器。
## 线程池有哪几种工作队列？
> 1. ArrayBlockingQueue：基于数组的先进先出队列，此队列创建时必须指定大小；
> 2. LinkedBlockingQueue：基于链表的先进先出队列，如果创建时没有指定此队列大小，则默
认为 Integer.MAX_VALUE；
> 3. SynchronousQueue：这个队列比较特殊，它不会保存提交的任务，而是将直接新建一个
线程来执行新来的任务。
## 线程池默认的拒绝策略有哪些
> 1. AbortPolicy：直接抛出异常，默认策略；
> 2. CallerRunsPolicy：用调用者所在的线程来执行任务；
> 3. DiscardOldestPolicy：丢弃阻塞队列中靠最前的任务，并执行当前任务；
> 4. DiscardPolicy：直接丢弃任务；
## 如何理解有界队列和无界队列
> 有界队列：就是有固定大小的队列。比如设定了固定大小的 LinkedBlockingQueue，又或者大小为 0，只是在生产者和消费者中做中转用的 SynchronousQueue。

> 无界队列：指的是没有设置固定大小的队列。这些队列的特点是可以直接入列，直到溢出。当然现实几乎不会有到这么大的容量（超过 Integer.MAX_VALUE），所以从使用者的体验上，就相当于 “无界”。比如没有设定固定大小的 LinkedBlockingQueue。

## 线程池是如何实现线程回收的？ 以及核心线程能不能被回收？
> 如果当前线程池的线程数量超过了 corePoolSize 且小于maximumPoolSize，并且 workQueue 已满时，则可以增加工作线程，
  但这时如果超时没有获取到任务，也就是 timedOut 为 true 的情况，说明 workQueue 已经为空了，也就说明了当前线程池中不需要那么多线程来执行任务了，
  可以把多于 corePoolSize 数量的线程销毁
掉，保持线程数量在 corePoolSize 即可

> 当线程空闲超过keepAliveTime，非核心线程会被回收，若allowCoreThreadTimeOut为true则核心线程也会被回收,若allowCoreThreadTimeOut为false则核心线程会保持存活直到线程池关闭
## FutureTask是什么
> FutureTask 是 Runnable 和 Future 的结合，如果我们把 Runnable 比作是生产者， Future 比作是消费者，那么 FutureTask 是被这两者共享的，
  生产者运行 run 方法计算结果，消费者通过 get 方法获取结果。
## Thread.sleep(0)的作用是什么
> Sleep 的意思是告诉操作系统自己要休息 n 毫秒，这段时间就让给另一个就绪的线程吧。当 n=0 的时候，意思是要放弃自己剩下的时间片，但是仍然是就绪状态，
  其实意思和 Yield 有点类似。但是 Sleep(0) 只允许那些优先级相等或更高的线程使用当前的CPU，其它线程只能等着挨饿了。如果没有合适的线程，
  那当前线程会重新使用 CPU 时间片。
## 如果提交任务时，线程池队列已满，这时会发生什么
> 线程队列已满得情况下，此时工作线程超过了核心线程，如果此时工作线程数量小于最大线程数量，会将工作线程数通过cas操作+1，然后创建一个工作线程
## 如果一个线程池中还有任务没有执行完成，这个时候是否允许被外部中断？
> shutdown()：不会立即终止线程池，而是要等所有任务缓存队列中
的任务都执行完后才终止，但再也不会接受新的任务 

> shutdownNow()：立即终止线程池，并尝试打断正在执行的任务，并且清空任务缓存队列，返回尚未执行的任务