package org.example.repositories;

import org.example.models.Order;
import org.example.models.StockSymbol;

import java.util.List;
import java.util.Map;

public interface IOrderRepository {

    void saveOrder(Order order);
    void modifyOrder(Order order);
    void cancelOrder(int orderId);
    List<Order> findOrdersByUserId(String userId, StockSymbol stockSymbol);
    List<Order> getOrdersByStock(StockSymbol stockSymbol);
    Map<StockSymbol, List<Order>> getOrderBook();

}
