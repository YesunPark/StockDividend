package com.zerobase.StockDividend.scraper;

import com.zerobase.StockDividend.model.Company;
import com.zerobase.StockDividend.model.ScrapedResult;

public interface Scrapper {
    Company scrapCompanyByTicker(String ticker);

    ScrapedResult scrap(Company company);
}
