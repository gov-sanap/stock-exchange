package org.example;

import org.example.models.Order;
import org.example.models.OrderQueues;
import org.example.models.OrderType;
import org.example.models.StockSymbol;
import org.example.repositories.IOrderRepository;
import org.example.strategies.IOrderExecutionStrategy;
import org.example.workers.OrderExecutionWorker;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class TradingSystem {
    private IOrderRepository orderRepository;
    private Map<StockSymbol, OrderQueues> stockOrderQueues;
    private static final AtomicReference<TradingSystem> INSTANCE = new AtomicReference<>();

    private TradingSystem() {
    }

    public static TradingSystem getInstance(IOrderRepository orderRepository, IOrderExecutionStrategy orderExecutionStrategy) {
        TradingSystem instance = INSTANCE.get();
        if (instance == null) {
            synchronized (TradingSystem.class) {
                instance = INSTANCE.get();
                if (instance == null) {
                    instance = new TradingSystem(orderRepository, orderExecutionStrategy);
                    INSTANCE.set(instance);
                }
            }
        }
        return instance;
    }

    private TradingSystem(IOrderRepository orderRepository, IOrderExecutionStrategy orderExecutionStrategy) {
        this.orderRepository = orderRepository;
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        stockOrderQueues = new ConcurrentHashMap<>();
        for (StockSymbol stock : StockSymbol.values()) {
            stockOrderQueues.putIfAbsent(stock,
                    OrderQueues.builder()
                            .buyOrders(new PriorityQueue<>(Comparator.comparingDouble(Order::getPrice).reversed().thenComparing(Order::getTimestamp)))
                            .sellOrders(new PriorityQueue<>(Comparator.comparingDouble(Order::getPrice).thenComparing(Order::getTimestamp)))
                            .build());
            executorService.submit(new OrderExecutionWorker(orderExecutionStrategy, stockOrderQueues.get(stock)));
        }
        // If in future we use different data source for Orders then fill data to this stockOrderQueues for unExecuted Orders.

    }

    public void placeOrder(Order order) {
        orderRepository.saveOrder(order);

        OrderQueues orderQueues = stockOrderQueues.get(order.getStockSymbol());
        if (order.getOrderType() == OrderType.BUY) {
            orderQueues.getBuyOrders().add(order);
        } else {
            orderQueues.getSellOrders().add(order);
        }
    }

    public void modifyOrder(Order order) {
        orderRepository.modifyOrder(order);
    }

    public void cancelOrder(int orderId) {
        orderRepository.cancelOrder(orderId);
    }

    public List<Order> queryOrders(String userId, StockSymbol stockSymbol) {
        return orderRepository.findOrdersByUserId(userId, stockSymbol);
    }
}
