package juc.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

@Slf4j
public class MessageQueueDemo {

    public static void main(String[] args) {
        MessageQueue queue = new MessageQueue(2);
        for (int i = 0; i < 3; i++) {
            int id = i;
            new Thread(() -> {
                queue.put(new Message(id, "值" + id));
            }, "生产者" + i).start();
        }
        new Thread(() -> {
            while (true){
                Sleeper.sleep(1);
                Message message = queue.take();
            }
        }, "消费者").start();

    }
}

@Slf4j
class MessageQueue{

    // 消息的队列集合
    private final LinkedList<Message> list = new LinkedList<>();
    // 容量
    private final int capacity;

    public MessageQueue(int capacity) {
        this.capacity = capacity;
    }

    // 获取消息
    public Message take() {
        synchronized (list){
            while (list.isEmpty()){
                try {
                    log.info("队列为空，消费者线程等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Message message = list.removeFirst();
            log.info("已消费消息 {}", message);
            list.notifyAll();
            return message;
        }
    }

    // 存入消息
    public void put(Message message) {
        synchronized (list){
            while (list.size() == capacity){
                try {
                    log.info("队列已满，生产者线程等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            list.addLast(message);
            log.info("已生产消息 {}", message);
            list.notifyAll();
        }
    }
}

@Getter
@Setter
@AllArgsConstructor
class Message{
    int id;
    String message;
}
