package org.example.repositories;

import org.example.models.StockSymbol;
import org.example.models.Trade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTradeRepository implements ITradeRepository {

    private final ConcurrentHashMap<StockSymbol, List<Trade>> tradesByStock = new ConcurrentHashMap<>();
    @Override
    public void saveTrade(Trade trade) {
        tradesByStock.computeIfAbsent(trade.getStockSymbol(), k -> new ArrayList<>()).add(trade);
    }

    @Override
    public List<Trade> getTradesByStock(StockSymbol stockSymbol) {
        return tradesByStock.getOrDefault(stockSymbol, new ArrayList<>());
    }

    @Override
    public Map<StockSymbol, List<Trade>> getTradeBook() {
        return tradesByStock;
    }
}
