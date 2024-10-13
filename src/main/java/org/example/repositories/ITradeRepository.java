package org.example.repositories;

import org.example.models.StockSymbol;
import org.example.models.Trade;

import java.util.List;
import java.util.Map;

public interface ITradeRepository {

    void saveTrade(Trade trade);

    List<Trade> getTradesByStock(StockSymbol stockSymbol);
    Map<StockSymbol, List<Trade>> getTradeBook();

}
