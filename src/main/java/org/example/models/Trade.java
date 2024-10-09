package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Trade {
    private int tradeId;
    private int buyerOrderId;
    private int sellerOrderId;
    private StockSymbol stockSymbol;
    private int quantity;
    private double price;
    private long timestamp;
}
