package org.example.repositories;

import org.example.models.Order;
import org.example.models.StockSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryOrderRepository implements IOrderRepository {
    private final ConcurrentHashMap<StockSymbol, List<Order>> ordersByStock = new ConcurrentHashMap<>();

    @Override
    public void saveOrder(Order order) {
        ordersByStock.computeIfAbsent(order.getStockSymbol(), k -> new ArrayList<>()).add(order);
    }

    @Override
    public void modifyOrder(Order order) {
        List<Order> orders = ordersByStock.get(order.getStockSymbol());
        if (orders != null) {
            for (Order o : orders) {
                if (o.getOrderId() == order.getOrderId()) {
                    o.setStatus(order.getStatus());
                    o.setQuantity(order.getQuantity());
                    o.setPrice(order.getPrice());
                    break;
                }
            }
        }
    }

    @Override
    public void cancelOrder(int orderId) {
        ordersByStock.values().forEach(orders -> orders.removeIf(o -> o.getOrderId() == orderId));
    }

    @Override
    public List<Order> findOrdersByUserId(String userId, StockSymbol stockSymbol) {
        List<Order> orders = ordersByStock.get(stockSymbol);
        if (orders != null) {
            return orders.stream()
                    .filter(o -> o.getUserId().equals(userId)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<Order> getOrdersByStock(StockSymbol stockSymbol) {
        return ordersByStock.getOrDefault(stockSymbol, new ArrayList<>());
    }

    public Map<StockSymbol, List<Order>> getOrderBook() {
        return ordersByStock;
    }
}
