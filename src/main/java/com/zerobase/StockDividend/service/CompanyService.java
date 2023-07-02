package com.zerobase.StockDividend.service;

import com.zerobase.StockDividend.model.Company;
import com.zerobase.StockDividend.model.ScrapedResult;
import com.zerobase.StockDividend.persist.CompanyRepository;
import com.zerobase.StockDividend.persist.DividendRepository;
import com.zerobase.StockDividend.persist.entity.CompanyEntity;
import com.zerobase.StockDividend.persist.entity.DividendEntity;
import com.zerobase.StockDividend.scraper.Scrapper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service // 프로그램 전체에서 1개의 인스턴스만 사용되어야 할 때 적용되는 디자인 패턴
@AllArgsConstructor
public class CompanyService {

    private final Trie trie; // AppConfig 에서 스프링빈으로 관리
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

    public List<String> getCompanyNamesByKeyword(String keyword) {
        // 자동완성 로직2(쿼리 like 을 이용한. 트라이 관련 로직 필요없어짐)
        Pageable limit = PageRequest.of(0, 10);
        Page<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.stream()
                .map(CompanyEntity::getName)
                .collect(Collectors.toList());
    }

    public void addAutocompleteKeyword(String keyword) { // 트라이에 회사명 저장 로직
        this.trie.put(keyword, null);
        // 아파치의 트라이는 아주 기본 트라이라기보단 응용할 수 있는 형태의 트라이이므로
        // 키/밸류 를 함께 저장하도록 되어있는데 우리는 밸류의 값은 필요없어서 일부러 null 넣음
    }

    public List<String> autocomplete(String keyword) { // 자동완성 로직1(trie 에서 단어를 찾는)
        return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream().collect(Collectors.toList());
    }

    public void deleteAutocompleteKeyword(String keyword) { // 트라이에 저장된 키워드 삭제 로직
        this.trie.remove(keyword);
    }
}
