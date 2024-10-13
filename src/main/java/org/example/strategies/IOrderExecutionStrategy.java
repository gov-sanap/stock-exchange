package org.example.strategies;

import org.example.models.Order;

import java.util.concurrent.PriorityBlockingQueue;

public interface IOrderExecutionStrategy {
    void execute(PriorityBlockingQueue<Order> buyOrders, PriorityBlockingQueue<Order> sellOrders);
}
