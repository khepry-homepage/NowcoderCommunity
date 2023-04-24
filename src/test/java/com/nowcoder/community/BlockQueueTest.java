package com.nowcoder.community;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;
public class BlockQueueTest {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(100);
        new Thread(new Sender(queue)).start();
        new Thread(new Receiver(queue)).start();
        new Thread(new Receiver(queue)).start();

    }
}

class Sender implements Runnable {
    private BlockingQueue<String> queue;
    public Sender(BlockingQueue<String> queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                Thread.sleep(200);
                this.queue.put("Hello");
                System.out.println(Thread.currentThread().getName() + "发送消息" + i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
class Receiver implements Runnable {
    private BlockingQueue queue;
    private static AtomicInteger num = new AtomicInteger(0);
    public Receiver(BlockingQueue<String> queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        try {
            while (num.get() <= 100) {
                Thread.sleep(new Random().nextInt(2000));
                this.queue.take();
                System.out.println(Thread.currentThread().getName() + "收到消息" + num.getAndIncrement());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
