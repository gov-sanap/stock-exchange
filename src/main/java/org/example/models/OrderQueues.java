package org.example.models;

import lombok.Builder;
import lombok.Data;

import java.util.PriorityQueue;

@Data
@Builder
public class OrderQueues {
    PriorityQueue<Order> buyOrders;
    PriorityQueue<Order> sellOrders;
}
