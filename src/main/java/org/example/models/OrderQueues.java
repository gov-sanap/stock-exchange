package org.example.models;

import lombok.Builder;
import lombok.Data;

import java.util.concurrent.PriorityBlockingQueue;

@Data
@Builder
public class OrderQueues {
    PriorityBlockingQueue<Order> buyOrders;
    PriorityBlockingQueue<Order> sellOrders;
}
