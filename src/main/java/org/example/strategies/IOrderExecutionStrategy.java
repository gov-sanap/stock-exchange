package org.example.strategies;

import org.example.models.Order;

import java.util.PriorityQueue;

public interface IOrderExecutionStrategy {
    void execute(PriorityQueue<Order> buyOrders, PriorityQueue<Order> sellOrders);
}
