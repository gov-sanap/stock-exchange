package org.example.strategies;

import org.example.models.*;
import org.example.providers.ITimeProvider;
import org.example.repositories.IOrderRepository;
import org.example.repositories.ITradeRepository;
import org.example.repositories.InMemoryOrderRepository;
import org.example.repositories.InMemoryTradeRepository;
import org.junit.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.*;


public class DefaultOrderExecutionStrategyTest {

    private DefaultOrderExecutionStrategy orderExecutionStrategy;
    private ITradeRepository tradeRepository;

    @BeforeMethod
    public void setup(){
        IOrderRepository orderRepository = new InMemoryOrderRepository();
        tradeRepository = new InMemoryTradeRepository();
        ITimeProvider timeProvider = mock(ITimeProvider.class);
        when(timeProvider.currentTimeMillis()).thenReturn(1633046400000L);
        orderExecutionStrategy = new DefaultOrderExecutionStrategy(new AtomicInteger(1), orderRepository,
                tradeRepository, timeProvider);
    }

    @DataProvider(name="provideOrderQueuesAndTrades")
    public static Object[][] provideOrderQueuesAndTrades() {
        return new Object[][] {
                {getBuyOrdersScenario1(),
                    getSellOrdersScenario1(),
                    getTradeBookScenario1()},
                {getBuyOrdersScenario2(),
                        getSellOrdersScenario2(),
                        getDefaultTradeBook()},
                {getBuyOrdersScenario3(),
                        getSellOrdersScenario3(),
                        getTradeBookScenario3()},
                {getBuyOrdersScenario4(),
                        getSellOrdersScenario4(),
                        getDefaultTradeBook()},
                {getBuyOrdersScenario5(),
                        getSellOrdersScenario5(),
                        getDefaultTradeBook()},
        };
    }

    @Test(description = "Based on current state of OrderQueues Trades should be created." +
            "Given Different states of orderQueues provided using dataProvider " +
            "When execute method is called it should create Trades accordingly." +
            "Scenario1 : There is a buy order of higher value available than sell order." +
            "Scenario2 : There is a buy order of lower value available than sell order." +
            "Scenario3 : There is a buy order of same value available than sell order." +
            "Scenario4 : There is only buy order present." +
            "Scenario5 : There is only sell order present.",
            dataProvider = "provideOrderQueuesAndTrades")
    public void testExecute(List<Order> buyOrders, List<Order> sellOrders, Map<StockSymbol, List<Trade>> tradesByStock) {
        PriorityBlockingQueue<Order> buyOrderQueue = getBuyOrderQueue();
        buyOrderQueue.addAll(buyOrders);
        PriorityBlockingQueue<Order> sellOrderQueue = getSellOrderQueue();
        sellOrderQueue.addAll(sellOrders);
        orderExecutionStrategy.execute(buyOrderQueue, sellOrderQueue);
        Assert.assertEquals(tradeRepository.getTradeBook(), tradesByStock);
    }

    private PriorityBlockingQueue<Order> getSellOrderQueue() {
        return new PriorityBlockingQueue<>(1, Comparator.comparingDouble(Order::getPrice).thenComparing(Order::getTimestamp));
    }

    private PriorityBlockingQueue<Order> getBuyOrderQueue() {
        return new PriorityBlockingQueue<>(1, Comparator.comparingDouble(Order::getPrice).reversed().thenComparing(Order::getTimestamp));
    }

    private static List<Order> getSellOrdersScenario1() {
        return Arrays.asList(getOrder("user2", 2, 5, 1490, OrderType.SELL, StockSymbol.RELIANCE));
    }

    private static List<Order> getBuyOrdersScenario1() {
        return Arrays.asList(getOrder("user1", 1, 10, 1500, OrderType.BUY, StockSymbol.RELIANCE));
    }

    private static Map<StockSymbol, List<Trade>> getTradeBookScenario1() {
        Map<StockSymbol, List<Trade>> tradeBook = new ConcurrentHashMap<>();
        tradeBook.put(StockSymbol.RELIANCE, Arrays.asList(
                new Trade(1,
                1,
                2,
                StockSymbol.RELIANCE,
                5,
                1490,
                1633046400000L)));
        return tradeBook;
    }

    private static List<Order> getSellOrdersScenario2() {
        return Arrays.asList(getOrder("user2", 2, 5, 1500, OrderType.SELL, StockSymbol.RELIANCE));
    }

    private static List<Order> getBuyOrdersScenario2() {
        return Arrays.asList(getOrder("user1", 1, 10, 1490, OrderType.BUY, StockSymbol.RELIANCE));
    }

    private static List<Order> getSellOrdersScenario3() {
        return Arrays.asList(getOrder("user2", 2, 5, 1500, OrderType.SELL, StockSymbol.RELIANCE));
    }

    private static List<Order> getBuyOrdersScenario3() {
        return Arrays.asList(getOrder("user1", 1, 10, 1500, OrderType.BUY, StockSymbol.RELIANCE));
    }

    private static Map<StockSymbol, List<Trade>> getTradeBookScenario3() {
        Map<StockSymbol, List<Trade>> tradeBook = new ConcurrentHashMap<>();
        tradeBook.put(StockSymbol.RELIANCE, Arrays.asList(
                new Trade(1,
                        1,
                        2,
                        StockSymbol.RELIANCE,
                        5,
                        1500,
                        1633046400000L)));
        return tradeBook;
    }

    private static List<Order> getSellOrdersScenario4() {
        return new ArrayList<>();
    }

    private static List<Order> getBuyOrdersScenario4() {
        return Arrays.asList(getOrder("user1", 1, 10, 1490, OrderType.BUY, StockSymbol.RELIANCE));
    }

    private static List<Order> getSellOrdersScenario5() {
        return Arrays.asList(getOrder("user2", 2, 5, 1500, OrderType.SELL, StockSymbol.RELIANCE));
    }

    private static List<Order> getBuyOrdersScenario5() {
        return new ArrayList<>();
    }

    private static Order getOrder(String userId, int orderId, int quantity, double price, OrderType orderType, StockSymbol stockSymbol){
        return Order.builder()
                .orderId(orderId)
                .userId(userId)
                .stockSymbol(stockSymbol)
                .quantity(quantity)
                .price(price)
                .orderType(orderType)
                .status(OrderStatus.PENDING)
                .timestamp(System.currentTimeMillis())
                .expiryTimestamp(System.currentTimeMillis() + 1000000)
                .build();
    }

    private static Map<StockSymbol, List<Trade>> getDefaultTradeBook() {
        return new ConcurrentHashMap<>();
    }
}
