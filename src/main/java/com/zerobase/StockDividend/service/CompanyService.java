package com.zerobase.StockDividend.service;

import com.zerobase.StockDividend.model.Company;
import com.zerobase.StockDividend.model.ScrapedResult;
import com.zerobase.StockDividend.persist.CompanyRepository;
import com.zerobase.StockDividend.persist.DividendRepository;
import com.zerobase.StockDividend.persist.entity.CompanyEntity;
import com.zerobase.StockDividend.persist.entity.DividendEntity;
import com.zerobase.StockDividend.scraper.Scrapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {
    private final Scrapper yahooFinanceScrapper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker); // 우리 디비에 이 회사의 존재여부 확인
        if (exists) {
            throw new RuntimeException("=== already exists ticker -> " + ticker + " ===");
        }
        return this.storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return this.companyRepository.findAll(pageable);
    }

    private Company storeCompanyAndDividend(String ticker) {
        // ticker 를 기준으로 회사를 스크래핑
        Company company = this.yahooFinanceScrapper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("=== failed to scrap ticker -> " + ticker + " ===");
        }

        // 해당 회사가 존재할 경우, 회사의 배당금 정보 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScrapper.scrap(company);

        // 스프래핑 결과 저장
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntityList = scrapedResult.getDividends().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .collect(Collectors.toList());
        this.dividendRepository.saveAll(dividendEntityList);

        return company;
    }
}
