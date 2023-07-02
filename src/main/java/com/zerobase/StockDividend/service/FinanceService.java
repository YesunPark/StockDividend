package com.zerobase.StockDividend.service;

import com.zerobase.StockDividend.model.Company;
import com.zerobase.StockDividend.model.Dividend;
import com.zerobase.StockDividend.model.ScrapedResult;
import com.zerobase.StockDividend.persist.CompanyRepository;
import com.zerobase.StockDividend.persist.DividendRepository;
import com.zerobase.StockDividend.persist.entity.CompanyEntity;
import com.zerobase.StockDividend.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FinanceService {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public ScrapedResult getDividendByCompanyName(String companyName) {
        // 1. 회사명을 기준으로 회사 정보 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("=== 존재하지 않은 회사명입니다 ==="));
        // orElseThrow 를 사용하면 에러나는 경우에는 에러를, 아닌 경우에는 Optional 이 아닌 CompanyEntity 를 반환

        // 2. 조회된 회사 ID 로 배당금 정보 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());

        // 3. Entity 결과들을 model 클래스들로 변환한 후 조합 후 ScrapedResult 로 반환
        // 변환 방법 1)
        //        List<Dividend> dividends = new ArrayList<>();
        //        for (var entity : dividendEntities) {
        //            dividends.add(Dividend.builder()
        //                    .date(entity.getDate())
        //                    .dividend(entity.getDividend())
        //                    .build());
        //        }

        // 변환 방법 2)
        List<Dividend> dividends = dividendEntities.stream()
                .map(entity -> Dividend.builder()
                        .date(entity.getDate())
                        .dividend(entity.getDividend()).build())
                .collect(Collectors.toList());


        return new ScrapedResult(Company.builder()
                .ticker(company.getTicker())
                .name(company.getName()).build()
                , dividends);
    }

}
