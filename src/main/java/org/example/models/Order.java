package org.example.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Order {

    private int orderId;
    private String userId;
    private OrderType orderType;
    private StockSymbol stockSymbol;
    private int quantity;
    private double price;
    private long timestamp;
    private long expiryTimestamp;
    private OrderStatus status;

    public boolean isExpired(){
        return expiryTimestamp < System.currentTimeMillis();
    }

}
