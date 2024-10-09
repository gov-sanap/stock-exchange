package org.example.strategies;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.models.Order;
import org.example.models.OrderStatus;
import org.example.models.Trade;
import org.example.repositories.IOrderRepository;
import org.example.repositories.ITradeRepository;

import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
@Slf4j
public class DefaultOrderExecutionStrategy implements IOrderExecutionStrategy{
    private final AtomicInteger tradeIdGenerator;
    private final IOrderRepository orderRepository;
    private final ITradeRepository tradeRepository;

    @Override
    public void execute(PriorityQueue<Order> buyOrders, PriorityQueue<Order> sellOrders) {
        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Order buyOrder = buyOrders.peek();
            Order sellOrder = sellOrders.peek();

            if (removeIfExpired(buyOrder, buyOrders) || removeIfExpired(sellOrder, sellOrders)){
                continue;
            }

            if (buyOrder.getPrice() >= sellOrder.getPrice()) {
                int tradedQuantity = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());
                Trade trade = new Trade(tradeIdGenerator.getAndIncrement(),
                        buyOrder.getOrderId(),
                        sellOrder.getOrderId(),
                        buyOrder.getStockSymbol(),
                        sellOrder.getQuantity(),
                        sellOrder.getPrice(),
                        System.currentTimeMillis());
                log.info("Trade created with buyOrderId : {} and sellOrderId : {} with quantity : {} and price : {}",
                        buyOrder.getOrderId(), sellOrder.getOrderId(), tradedQuantity, sellOrder.getPrice());
                tradeRepository.saveTrade(trade);
                buyOrder.setQuantity(buyOrder.getQuantity() - tradedQuantity);
                buyOrder.setStatus(OrderStatus.ACCEPTED);
                sellOrder.setQuantity(sellOrder.getQuantity() - tradedQuantity);
                sellOrder.setStatus(OrderStatus.ACCEPTED);

                if (sellOrder.getQuantity() == 0) {
                    sellOrders.poll();
                } else {
                    sellOrders.poll();
                    sellOrders.offer(buyOrder);
                }
                if (buyOrder.getQuantity() == 0) {
                    buyOrders.poll();
                } else {
                    buyOrders.poll();
                    buyOrders.offer(buyOrder);
                }
                orderRepository.modifyOrder(buyOrder);
                orderRepository.modifyOrder(sellOrder);
            } else {
                break;
            }
        }
    }

    private boolean removeIfExpired(Order order, PriorityQueue<Order> orders) {
        if(order.isExpired()){
            orders.poll();
            order.setStatus(OrderStatus.EXPIRED);
            orderRepository.modifyOrder(order);
            log.info("Order with order id : {} is expired", order.getOrderId());
            return true;
        }
        return false;
    }
}
