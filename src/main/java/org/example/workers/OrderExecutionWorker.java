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
    private final IOrderExecutionStrategy orderExecutionStrategy;
    private OrderQueues orderQueues;


    @Override
    public void run() {
        while (true) {
            orderExecutionStrategy.execute(orderQueues.getBuyOrders(), orderQueues.getSellOrders());
        }
    }
}
