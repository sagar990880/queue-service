package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryPriorityQueueTest {

    @Test
    void testPriorityAndFifoBehaviour() {

        InMemoryPriorityQueue queue = new InMemoryPriorityQueue();

        //  add messages with different priorities
        queue.push("orders", "low", 3);
        queue.push("orders", "high-1", 1);
        queue.push("orders", "high-2", 1);
        queue.push("orders", "medium", 2);

        Message first = queue.pull("orders");
        Message second = queue.pull("orders");
        Message third = queue.pull("orders");
        Message fourth = queue.pull("orders");

       //-------- We can check here for OutPut
        System.out.println(first.getBody());
        System.out.println(second.getBody());
        System.out.println(third.getBody());
        System.out.println(fourth.getBody());

        //  high priority first, FIFO within same priority
        assertEquals("high-1", first.getBody());
        assertEquals("high-2", second.getBody());
        assertEquals("medium", third.getBody());
        assertEquals("low", fourth.getBody());
    }


    @Test
    void testDeleteAfterPull() {

        InMemoryPriorityQueue queue = new InMemoryPriorityQueue();

        queue.push("orders", "task-1", 1);
        queue.push("orders", "task-2", 1);

        Message pulled = queue.pull("orders");

        // delete first pulled message
        queue.delete("orders", pulled.getReceiptId());

        Message next = queue.pull("orders");

        assertEquals("task-2", next.getBody());
    }


    @Test
    void testEmptyQueueReturnsNull() {

        InMemoryPriorityQueue queue = new InMemoryPriorityQueue();

        Message result = queue.pull("orders");

        assertNull(result);
    }
}