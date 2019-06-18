package com.snowriver.lock;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description 请结合ReentrantLock、Condition实现一个简单的阻塞队列，阻塞队列提供两个方法，一个是put、一个是take
 */
public class BlockingQueue {

    private Integer maxSize;
    private static LinkedList<Object> queue = new LinkedList();
    private static ReentrantLock lock = new ReentrantLock();
    private static Condition putCondition = lock.newCondition();
    private static Condition takeCondition = lock.newCondition();

    public BlockingQueue(Integer maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * 向队列添加数据
     * @see  {当队列满了以后，存储元素的线程需要备阻塞直到队列可以添加数据}
     * @param element
     */
    public void put(Object element) {
        lock.lock();

        try {
            if (queue.size() == this.maxSize) {
                System.out.println("队列已满，进入阻塞状态");
                putCondition.await();
            }

            System.out.println("队列新元素: " + element);
            queue.add(element);

            if (queue.size() == 1) {
                takeCondition.signal();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 从阻塞队列获取数据
     * @see {当队列为空时，请求take会被阻塞，直到队列不为空}
     * @return
     */
    public Object take() {
        lock.lock();

        try {
            if (queue.size() == 0) {
                System.out.println("队列为空，进入阻塞状态");
                takeCondition.await();
            }

            Object pop = queue.pop();
            System.out.println("获取元素：" + pop);
            putCondition.signal();
            return pop + Thread.currentThread().getName();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return null;
    }

    public static void main(String[] args) {

        BlockingQueue blockingQueue = new BlockingQueue(10);

        for (int i = 0; i < 20; i++) {
            int finalI = i;
            new Thread(() -> {
                blockingQueue.put("江雪" + finalI + "号");
            }).start();
        }

        for (int i = 0; i < 30; i++) {
            int finalI = i;
            new Thread(() -> {
                Object take = blockingQueue.take();
                System.out.println("take到的元素：" + take);
            }).start();
        }

    }

}