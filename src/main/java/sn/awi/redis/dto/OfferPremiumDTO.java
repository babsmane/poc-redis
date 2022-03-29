package sn.awi.redis.dto;

import lombok.Getter;
import lombok.Setter;

public class OfferPremiumDTO {

    @Getter @Setter
    private Long id;

    @Getter @Setter
    private String type;

    @Getter @Setter
    private String premiumCalc;

    @Getter @Setter
    private String premiumCalcType;

    @Getter @Setter
    private OfferPremiumRangeDTO totalRange;

    @Getter @Setter
    private OfferPremiumRangeDTO individualRange;

    @Getter @Setter
    private OfferPremiumRangeDTO totalExcludingRange;

    @Getter @Setter
    private OfferPremiumRangeDTO individualExcludingRange;

}
