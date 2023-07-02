package com.zerobase.StockDividend.web;

import com.zerobase.StockDividend.model.Company;
import com.zerobase.StockDividend.persist.entity.CompanyEntity;
import com.zerobase.StockDividend.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/company") // 공통되는 경로 지정
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/autocomplete")
    public ResponseEntity<?> autoComplete(@RequestParam String keyword) {
        return null;
    }

    @GetMapping
    public ResponseEntity<?> searchCompany(final Pageable pageable) {
        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);
        return ResponseEntity.ok(companies);
    }

    @PostMapping
    public ResponseEntity<?> addCompany(@RequestBody String request) { // 강의랑 좀 다름.. 강의처럼 하니 안돼서 스트링으로 함
        String ticker = request.trim();
        if (ObjectUtils.isEmpty(ticker)) { // ticker 를 빈값으로 입력하는 경우
            throw new RuntimeException("=== ticker is empty ===");
        }

        Company company = this.companyService.save(ticker);

        return ResponseEntity.ok(company);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCompany() {
        return null;
    }

}
