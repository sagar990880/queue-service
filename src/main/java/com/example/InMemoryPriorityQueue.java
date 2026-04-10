package com.example;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class InMemoryPriorityQueue implements QueueService {

    //queueUrl -> (priority -> list of messages)
    //TreeMap used to keep priority sorted automatically
    //LinkedList used to maintain FIFO within same priority
    private final Map<String,TreeMap<Integer,LinkedList<Message>>>queues;

    //     visibility timeout in seconds
    private long visibilityTimeout;

    InMemoryPriorityQueue()
    {
        this.queues=new ConcurrentHashMap<>();
        //   load visibility timeout from config.properties
        String propFileName="config.properties";
        Properties confInfo=new Properties();
        try (InputStream inStream=getClass().getClassLoader()
                .getResourceAsStream(propFileName)) {
            confInfo.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.visibilityTimeout=Integer.parseInt(confInfo.getProperty("visibilityTimeout", "30"));
    }

    //    push with default low priority
    @Override
    public void push(String queueUrl, String msgBody) {
        push(queueUrl, msgBody, Integer.MAX_VALUE);
    }

    //     push message with priority
    public void push(String queueUrl, String msgBody, int priority) {

        //computeIfAbsent ensures queue exists
        //TreeMap sorts by priority
        //LinkedList maintains FIFO
        queues.computeIfAbsent(queueUrl, k -> new TreeMap<>())
                .computeIfAbsent(priority, k -> new LinkedList<>())
                .add(new Message(msgBody));
    }

    //     pull high priority visible message
    @Override
    public Message pull(String queueUrl) {
        TreeMap<Integer, LinkedList<Message>> priorityMap = queues.get(queueUrl);

        //     return null if queue empty.
        if (priorityMap == null || priorityMap.isEmpty())
            return null;
        long nowTime = now();

        //    iterate priorities in sorted order (highest priority first)
        for (Map.Entry<Integer, LinkedList<Message>> entry : priorityMap.entrySet()) {
            LinkedList<Message> bucket = entry.getValue();

            //    find first visible message (FIFO)
            Optional<Message> msgOpt = bucket.stream()
                    .filter(m -> m.isVisibleAt(nowTime))
                    .findFirst();

            if (msgOpt.isPresent()) {
                Message msg = msgOpt.get();
                //    assign receiptId when message pulled
                msg.setReceiptId(UUID.randomUUID().toString());
                //    increment delivery attempts
                msg.incrementAttempts();
                //    hide message for visibility timeout
                msg.setVisibleFrom(System.currentTimeMillis()
                        + TimeUnit.SECONDS.toMillis(visibilityTimeout));
                //    return message body + receiptId
                return new Message(msg.getBody(), msg.getReceiptId());
            }
        }

        return null;
    }

    //    delete message using receiptId
    @Override
    public void delete(String queueUrl, String receiptId) {
        TreeMap<Integer, LinkedList<Message>> priorityMap = queues.get(queueUrl);
        if (priorityMap == null)
            return;
        long nowTime = now();

        //    iterate all priorities
        for (LinkedList<Message> bucket : priorityMap.values()) {
            Iterator<Message> it = bucket.iterator();
            while (it.hasNext()) {
                Message msg = it.next();

                //    delete only invisible message with matching receiptId
                if (!msg.isVisibleAt(nowTime)
                        && msg.getReceiptId().equals(receiptId)) {
                    it.remove();
                    return;
                }
            }
        }
    }
    long now() {
        return System.currentTimeMillis();
    }
}