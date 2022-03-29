package sn.awi.redis.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Some fields were removed, it will be need to add them in future:
 * 
 * List<Property> properties;
 * 
 * int[] pricingCriterias;
 * 
 * int defaultSegment;
 * 
 * @author E105857
 *
 */

public class DistributionContextDTO {

    @Getter @Setter
    private Long id;

    @Getter @Setter
    private DictionaryDTO partner;

    @Getter @Setter
    private int mainLanguage;

    @Getter @Setter
    private int defaultLanguage;

    @Getter @Setter
    private int quotationCurrency;

    @Getter @Setter
    private List<DisplayCurrencyDTO> displayCurrencies;

    @Getter @Setter
    private int[] countries;

    @Getter @Setter
    private int[] languages;

    @Getter @Setter
    private int[] salesChannels;

    @Getter @Setter
    private int[] touchPoints;

    @Getter @Setter
    private boolean activated;

    @Getter @Setter
    private String premiumType;

    @Getter @Setter
    private PricingStrategyDTO pricingStrategy;

    @Getter @Setter
    private String domain;

    @Getter @Setter
    private List<CategoryDTO> categories;

    @Getter @Setter
    private List<SegmentDTO> segments;

    @Getter @Setter
    private Long defaultSegment;

    @Getter @Setter
    private List<DiscountDTO> discounts;

}
