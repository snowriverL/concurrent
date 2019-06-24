package com.snowriver.lock.block;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserService {

    private final ExecutorService single = Executors.newSingleThreadExecutor();

    private volatile boolean isRunning = true;

    ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue(10);

    {
        init();
    }

    private void init() {
        single.execute(() -> {
            while (isRunning) {
                try {
                    User user = (User)blockingQueue.take();
                    sendPoint(user);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean register(){
        User user=new User();
        user.setName("Mic");
        addUser(user);
        blockingQueue.add(user);//添加到异步队列
        return true;
    }

    private void addUser(User user) {
        System.out.println("添加新用户：" + user.getName());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendPoint(User user) {
        System.out.println("发送积分给用户：" + user.getName());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new UserService().register();
    }
}
