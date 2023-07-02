package com.zerobase.StockDividend.scheduler;

import com.zerobase.StockDividend.model.Company;
import com.zerobase.StockDividend.model.ScrapedResult;
import com.zerobase.StockDividend.persist.CompanyRepository;
import com.zerobase.StockDividend.persist.DividendRepository;
import com.zerobase.StockDividend.persist.entity.CompanyEntity;
import com.zerobase.StockDividend.persist.entity.DividendEntity;
import com.zerobase.StockDividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ScraperScheduler {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Scraper yahooFinanceScraper;

    @Scheduled(cron = "${scheduler.scrap.yahoo}") // 직접 작성보단 application.yml 파일에서 관리하는게 편함!
    public void yahooFinanceScheduling() {
        // 1. 저장된 회사 목록 조회
        List<CompanyEntity> companies = this.companyRepository.findAll();

        // 2. 회사마다 배당금 정보를 새로 스크래핑
        for (CompanyEntity company : companies) {
            log.info("=== Scraping scheduler is started " + company.getName() + " ===");
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(
                    Company.builder()
                            .name(company.getName())
                            .ticker(company.getTicker())
                            .build()
            );
            // 3. 스크래핑한 정보 중 디비에 없는 값은 새로 저장
            scrapedResult.getDividends().stream()
                    // Dividend 모델을 DividendEntity 로 매핑
                    .map(dividend -> new DividendEntity(company.getId(), dividend))
                    // 반복문을 통해 디비에 존재 여부 확인,
                    .forEach(dvdnEntity -> {
                        boolean exists = this.dividendRepository
                                .existsByCompanyIdAndDate(dvdnEntity.getCompanyId(), dvdnEntity.getDate());
                        // 없는 경우에만 저장
                        if (!exists) {
                            this.dividendRepository.save(dvdnEntity);
                        }
                    });
            // 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시 정지
            try {
                Thread.sleep(3000); // 3초, sleep : 실행 중인 스레드를 잠시 멈추게 함
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
