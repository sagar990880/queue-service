package com.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UpstashQueueServiceTest {

    @Test
    void testPushPull() {

        UpstashQueueService queue = new UpstashQueueService();

        queue.push("orders", "A");
        queue.push("orders", "B");
        queue.push("orders", "C");

        Message m1 = queue.pull("orders");
        Message m2 = queue.pull("orders");
        Message m3 = queue.pull("orders");

        assertEquals("A", m1.getBody());
        assertEquals("B", m2.getBody());
        assertEquals("C", m3.getBody());
    }

    @Test
    void testEmptyQueue()
    {
        UpstashQueueService queue = new UpstashQueueService();
        Message msg = queue.pull("new-queue");
        assertNull(msg);
    }
}