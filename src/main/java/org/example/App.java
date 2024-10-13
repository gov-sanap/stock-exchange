package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.models.Order;
import org.example.models.OrderStatus;
import org.example.models.OrderType;
import org.example.models.StockSymbol;
import org.example.providers.ITimeProvider;
import org.example.providers.SystemTimeProvider;
import org.example.repositories.IOrderRepository;
import org.example.repositories.ITradeRepository;
import org.example.repositories.InMemoryOrderRepository;
import org.example.repositories.InMemoryTradeRepository;
import org.example.strategies.DefaultOrderExecutionStrategy;
import org.example.strategies.IOrderExecutionStrategy;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Hello world!
 *
 */
@Slf4j
public class App 
{
    public static void main( String[] args )
    {
        IOrderRepository orderRepository = new InMemoryOrderRepository();
        ITradeRepository tradeRepository = new InMemoryTradeRepository();
        ITimeProvider timeProvider = new SystemTimeProvider();
        IOrderExecutionStrategy executionStrategy = new DefaultOrderExecutionStrategy(
                new AtomicInteger(1), orderRepository, tradeRepository, timeProvider);
        log.info("Starting Application");

        TradingSystem tradingSystem = TradingSystem.getInstance(orderRepository, executionStrategy);

        // Create a user and place orders
        Order order0 = Order.builder()
                .orderId(0)
                .userId("user1")
                .stockSymbol(StockSymbol.RELIANCE)
                .quantity(5)
                .price(1490)
                .orderType(OrderType.SELL)
                .status(OrderStatus.PENDING)
                .timestamp(System.currentTimeMillis())
                .expiryTimestamp(System.currentTimeMillis() - 1000000)
                .build();

        tradingSystem.placeOrder(order0);
        log.info("Placed forth order which is expired by default for testing");

        Order order1 = Order.builder()
                .orderId(1)
                .userId("user1")
                .stockSymbol(StockSymbol.RELIANCE)
                .quantity(10)
                .price(1500)
                .orderType(OrderType.BUY)
                .status(OrderStatus.PENDING)
                .timestamp(System.currentTimeMillis())
                .expiryTimestamp(System.currentTimeMillis() + 1000000)
                .build();

        tradingSystem.placeOrder(order1);
        log.info("Placed one order");

        Order order2 = Order.builder()
                .orderId(2)
                .userId("user2")
                .stockSymbol(StockSymbol.RELIANCE)
                .quantity(5)
                .price(1490)
                .orderType(OrderType.SELL)
                .status(OrderStatus.PENDING)
                .timestamp(System.currentTimeMillis())
                .expiryTimestamp(System.currentTimeMillis() + 1000000)
                .build();

        tradingSystem.placeOrder(order2);
        log.info("Placed second order");

        Order order3 = Order.builder()
                .orderId(3)
                .userId("user1")
                .stockSymbol(StockSymbol.WIPRO)
                .quantity(5)
                .price(1490)
                .orderType(OrderType.SELL)
                .status(OrderStatus.PENDING)
                .timestamp(System.currentTimeMillis())
                .expiryTimestamp(System.currentTimeMillis() + 1000000)
                .build();

        tradingSystem.placeOrder(order3);
        log.info("Placed third order");


//         Query orders
        printUserOrders(tradingSystem, "user1", StockSymbol.WIPRO);

        // Modify an order
        order3.setQuantity(15);
        tradingSystem.modifyOrder(order3);

        printUserOrders(tradingSystem,"user1", StockSymbol.WIPRO);

        // Cancel an order
        tradingSystem.cancelOrder(3);

        printUserOrders(tradingSystem,"user1", StockSymbol.WIPRO);
        printUserOrders(tradingSystem,"user1", StockSymbol.RELIANCE);
        printUserOrders(tradingSystem,"user2", StockSymbol.RELIANCE);
    }

    private static void printUserOrders(TradingSystem tradingSystem, String userId, StockSymbol stockSymbol) {
        List<Order> userOrders = tradingSystem.queryOrders(userId, stockSymbol);
        log.info("User Orders: " + userOrders);
    }
}
