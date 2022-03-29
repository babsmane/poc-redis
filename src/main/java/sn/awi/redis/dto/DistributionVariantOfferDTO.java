package sn.awi.redis.dto;

import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

public class DistributionVariantOfferDTO {

    @Getter @Setter
    private Long id;

    @Getter @Setter
    private Long offerId;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String description;

    @Getter @Setter
    private JsonNode properties;

    @Getter @Setter
    private List<OfferI18NDTO> i18n;

}
