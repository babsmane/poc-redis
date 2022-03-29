package sn.awi.redis.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class OfferPremiumRangeDTO {

    @Getter @Setter
    private Long id;

    @Getter @Setter
    private BigDecimal min;

    @Getter @Setter
    private BigDecimal max;

    @Getter @Setter
    private String dataToLoopOn;

    @Getter @Setter
    private Boolean threshold;
    
}
