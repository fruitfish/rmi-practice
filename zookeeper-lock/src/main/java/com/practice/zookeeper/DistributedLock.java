package com.practice.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class DistributedLock implements Lock, Watcher {

    private String ROOT_LOCK = "/rootLock";

    private String CURRENT_LOCK = "";

    private String WAIT_LOCK = "";

    private ZooKeeper zk;

    private CountDownLatch countDownLatch;

    public DistributedLock() throws IOException, KeeperException, InterruptedException {
        zk = new ZooKeeper("10.1.29.84:2181", 5000, this);

        Stat stat = zk.exists(ROOT_LOCK, false);
        if(stat == null) {
            // TODO: 2018/11/25  ZooDefs.Ids.OPEN_ACL_UNSAFE  这个定义是什么
            zk.create(ROOT_LOCK, "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }


    @Override
    public void lock() {
        if(this.tryLock()) {
            System.out.println("抢锁成功. " + Thread.currentThread().getName() + ">>>>>>" + CURRENT_LOCK);
            return;
        }
        waitLock();
    }

    private void waitLock() {
        try {
            Stat stat = zk.exists(WAIT_LOCK, true);
            if(stat != null) {
                System.out.println(Thread.currentThread().getName() + " 等待锁 " + WAIT_LOCK + " 释放");
                countDownLatch = new CountDownLatch(1);
                countDownLatch.await();
                System.out.println(Thread.currentThread().getName() + "->" + "获得所成功");
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        try {
            CURRENT_LOCK = zk.create(ROOT_LOCK + "/", "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println("开始争抢锁" + Thread.currentThread().getName() + "  节点:   " + CURRENT_LOCK);
            List<String> children = zk.getChildren(ROOT_LOCK, false);
            SortedSet<String> sortedSet = new TreeSet<>();
            for(String child : children) {
                sortedSet.add(ROOT_LOCK + "/" + child);
            }
            String firstNode = sortedSet.first();
            SortedSet<String> smallThanCurrentNode = (SortedSet<String>) sortedSet.headSet(CURRENT_LOCK);  // 得到比当前节点小的所有节点
            if(CURRENT_LOCK.equals(firstNode)) {
                return true;
            }
            if(!smallThanCurrentNode.isEmpty()) {
                WAIT_LOCK = smallThanCurrentNode.last();
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        try {
            zk.delete(CURRENT_LOCK, -1);
            CURRENT_LOCK = null;
            zk.close();
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    @Override
    public void process(WatchedEvent event) {
        if(countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public String getROOT_LOCK() {
        return ROOT_LOCK;
    }

    public void setROOT_LOCK(String ROOT_LOCK) {
        this.ROOT_LOCK = ROOT_LOCK;
    }

    public String getCURRENT_LOCK() {
        return CURRENT_LOCK;
    }

    public void setCURRENT_LOCK(String CURRENT_LOCK) {
        this.CURRENT_LOCK = CURRENT_LOCK;
    }

    public String getWAIT_LOCK() {
        return WAIT_LOCK;
    }

    public void setWAIT_LOCK(String WAIT_LOCK) {
        this.WAIT_LOCK = WAIT_LOCK;
    }
}
