package sn.awi.redis.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class OfferDTO {

    @Getter @Setter
    private Long id;

    @Getter @Setter
    private String code;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String description;

    @Getter @Setter
    private String productId;

    @Getter @Setter
    private Long distributionContextId;

    @Getter @Setter
    private boolean activated;

    @Getter @Setter
    private String sellType;

    @Getter @Setter
    private String offerType;

    @Getter @Setter
    private String addOnType;

    @Getter @Setter
    private List<OfferI18NDTO> i18n;

    @Getter @Setter
    private List<DocumentDTO> documents;

    @Getter @Setter
    private List<DocumentDTO> staticDocuments;

    @Getter @Setter
    private List<TaxDTO> taxes;

    @Getter @Setter
    private OfferPremiumDTO premium;

    @Getter @Setter
    private String recurrentType;

    @Getter @Setter
    private String periodType;

    @Getter @Setter
    private String period;

    @Getter @Setter
    private boolean renewal;

    @Getter @Setter
    private String subscriptionDeactivatedDate;

    @Getter @Setter
    private Double policyDelayedStartDuringTrip;

    @Getter @Setter
    private Double policyDelayedStart;

}
