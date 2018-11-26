package com.practice.zookeeper;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class DistributedLockDemo {

    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        int count = 10;
        final CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < count; i++) {
            final Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    DistributedLock distributedLock = null;
                    try {
                        countDownLatch.await();
                        distributedLock = new DistributedLock();
                        distributedLock.lock();
                    } catch (IOException | KeeperException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "thread-" + i);
            t.start();
            countDownLatch.countDown();
        }
        System.in.read();
    }

}
