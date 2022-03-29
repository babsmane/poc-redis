package sn.awi.redis.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class TaxRateDTO {

    @Getter @Setter
    private String taxCode;

    @Getter @Setter
    private String taxName;

    @Getter @Setter
    private String countryCode;

    @Getter @Setter
    private BigDecimal taxRate;

    @Getter @Setter
    private BigDecimal weight;

}
