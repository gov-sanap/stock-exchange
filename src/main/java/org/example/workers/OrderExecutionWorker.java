package org.example.workers;

import lombok.AllArgsConstructor;
import org.example.models.OrderQueues;
import org.example.models.StockSymbol;
import org.example.models.Trade;
import org.example.repositories.IOrderRepository;
import org.example.strategies.IOrderExecutionStrategy;

import java.util.*;

@AllArgsConstructor
public class OrderExecutionWorker implements Runnable{
//    private final IOrderRepository orderRepository;
//    private final List<Trade> trades;
    private final IOrderExecutionStrategy orderExecutionStrategy;
    private OrderQueues orderQueues;
//    PriorityQueue<Order> buyOrders = new PriorityQueue<>(Comparator.comparingDouble(Order::getPrice).reversed().thenComparing(Order::getTimestamp));
//    PriorityQueue<Order> sellOrders = new PriorityQueue<>(Comparator.comparingDouble(Order::getPrice).thenComparing(Order::getTimestamp));


    @Override
    public void run() {
        while (true) {
            orderExecutionStrategy.execute(orderQueues.getBuyOrders(), orderQueues.getSellOrders());
        }
    }
}
