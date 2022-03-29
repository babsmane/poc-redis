package sn.awi.redis.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class PricingStrategyDTO {

    @Getter @Setter
    private Long id;

    @Getter @Setter
    private String appliedTo;

    @Getter @Setter
    private String roundingMethod;

    @Getter @Setter
    private BigDecimal roundedTo;

    @Getter @Setter
    private BigDecimal plusMinusValue;

}
