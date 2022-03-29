package sn.awi.redis.dto;

import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

public class DistributionVariantDTO {

    @Getter @Setter
    private Long id;

    @Getter @Setter
    private String code;

    @Getter @Setter
    private String title;

    @Getter @Setter
    private Long group;

    @Getter @Setter
    private Double trafficAllocation;

    @Getter @Setter
    private Boolean customizeMarketingContent = Boolean.FALSE;

    @Getter @Setter
    private JsonNode displayDcx;

    @Getter @Setter
    private List<DistributionVariantOfferDTO> displays;

}
