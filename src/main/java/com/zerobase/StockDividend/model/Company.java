package com.zerobase.StockDividend.model;

import lombok.Builder;
import lombok.Data;

@Data // Getter, Setter, RequiredArgsConstructor, ToString, EqualsAndHashCode, Value
@Builder // 디자인 패턴 중 빌더 패턴을 사용할 수 있게 하는 어노테이션
public class Company {
    private String ticker;
    private String name;
}
