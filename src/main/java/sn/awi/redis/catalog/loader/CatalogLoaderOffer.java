package sn.awi.redis.catalog.loader;

import java.io.File;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allianz.emagin.eqs.engine.catalog.Offer;
import com.allianz.emagin.eqs.engine.catalog.OfferPeriodType;
import com.allianz.emagin.eqs.engine.catalog.OfferPremium;
import com.allianz.emagin.eqs.engine.catalog.OfferPremiumRange;
import com.allianz.emagin.eqs.engine.catalog.OfferRecurrentType;
import com.allianz.emagin.eqs.engine.catalog.OfferSellType;
import com.allianz.emagin.eqs.engine.catalog.OfferType;
import com.allianz.emagin.eqs.engine.catalog.PremiumType;
import com.allianz.emagin.eqs.engine.catalog.pricing.CatalogTax;
import com.allianz.emagin.eqs.engine.catalog.properties.BaseProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import sn.awi.redis.catalog.exception.CatalogLoaderException;
import sn.awi.redis.dto.DocumentDTO;
import sn.awi.redis.dto.OfferDTO;
import sn.awi.redis.dto.OfferI18NDTO;
import sn.awi.redis.dto.OfferPremiumRangeDTO;
import sn.awi.redis.dto.TaxDTO;

public class CatalogLoaderOffer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogLoaderOffer.class);

    public Offer loadOffer(final CatalogLoaderFiles catalogLoaderFiles, final File file, final ObjectMapper mapper, final CatalogCommonData catalogCommonData) {
        final String jsonString = catalogLoaderFiles.loadFileContent(file);
        OfferDTO beanDTO = null;
        try {
            beanDTO = mapper.readValue(jsonString, OfferDTO.class);
        } catch (final Exception e) {
            LOGGER.error("Loading offer file : " + file.getName(), e);
            throw new CatalogLoaderException("Loading offer file : " + file.getName(), e);
        }
        final Offer offer = new Offer();
        offer.setUid(beanDTO.getId().toString());
        offer.setCode(beanDTO.getCode());
        offer.setTitle(beanDTO.getName());
        offer.setDescription(beanDTO.getDescription());
        offer.setActivated(beanDTO.isActivated());
        try {
            offer.setSellType(OfferSellType.valueOf(beanDTO.getSellType()));
        } catch (Exception ex) {
            LOGGER.warn("Offer Sell Type not found : " + beanDTO.getSellType(), ex);
        }
        if (beanDTO.getOfferType()!=null && !beanDTO.getOfferType().isEmpty())
            offer.setOfferType(OfferType.valueOf(beanDTO.getOfferType()));
        if (beanDTO.getRecurrentType()!=null && !beanDTO.getRecurrentType().isEmpty())
            offer.setRecurrentType(OfferRecurrentType.valueOf(beanDTO.getRecurrentType()));
        if (beanDTO.getPeriodType()!=null && !beanDTO.getPeriodType().isEmpty())
            offer.setPeriodType(OfferPeriodType.valueOf(beanDTO.getPeriodType()));
        offer.setPeriod(beanDTO.getPeriod());
        offer.setRenewal(beanDTO.isRenewal());
        offer.setSubscriptionDeactivatedDate(beanDTO.getSubscriptionDeactivatedDate());
        offer.setPolicyDelayedStart(beanDTO.getPolicyDelayedStart());
        offer.setPolicyDelayedStartDuringTrip(beanDTO.getPolicyDelayedStartDuringTrip());
        offer.setProductId(beanDTO.getProductId());
        // Documents
        this.addAllDocuments(beanDTO, offer);
        if (beanDTO.getStaticDocuments() != null && !beanDTO.getStaticDocuments().isEmpty()) {
            for (final DocumentDTO documentDTO : beanDTO.getStaticDocuments()) {
                offer.addDocument(documentDTO.getType(), documentDTO.getUrl(), documentDTO.getLanguage(), true);
            }
        }
        // Internatioalization
        if (beanDTO.getI18n() != null && !beanDTO.getI18n().isEmpty()) {
            for (final OfferI18NDTO oI18ndto : beanDTO.getI18n()) {
                offer.translate(catalogCommonData.getAllLanguages().get(oI18ndto.getLanguageId().longValue() + "").getCode(),
                    oI18ndto.getDisplayName(), oI18ndto.getDescription());
            }
        }
        try {
            final JsonNode actualObj = mapper.readTree(jsonString);
            this.loadJsonConstants(actualObj, offer, catalogCommonData);

            final JsonNode jsonNode1 = actualObj.get(CatalogLoaderConstants.JSON_CONSTANT_PROPERTIES);
            final Iterator<JsonNode> elms = jsonNode1.elements();
            while (elms.hasNext()) {
                final JsonNode elm = elms.next();
                final BaseProperty bp = catalogCommonData.getPropertyHelper().buildProperty(elm);
                if (bp == null) {
                    CatalogLoaderCounters.incrementNbBasePropertyNullO();
                } else {
                    offer.addProperty(bp);
                    offer.getPropDefProperty().put(bp.getDefinitionId(), bp);
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Loading offer code : " + beanDTO.getCode(), e);
            throw new CatalogLoaderException("Loading offer code : " + beanDTO.getCode(), e);
        }

        // load Taxes
        this.loadtax(offer, beanDTO);

        // Premium strategy
        this.loadOfferPremiumStrategy(offer, beanDTO);

        return offer;
    }

    private void loadtax(final Offer offer, final OfferDTO offerDTO) {
        if (offerDTO.getTaxes() != null && !offerDTO.getTaxes().isEmpty()) {
            for (final TaxDTO tDto : offerDTO.getTaxes()) {
                final CatalogTax catalogTax = new CatalogTax();
                catalogTax.setTaxName(tDto.getTaxName());
                if (tDto.getFilters()!=null && !tDto.getFilters().isEmpty()) {
                    CatalogLoaderOfferTax.loadTaxFilter(tDto, catalogTax);
                }
                if (tDto.getRates()!=null) {
                    CatalogLoaderOfferTax.loadTaxRate(tDto, catalogTax);
                }
                offer.tax(catalogTax);
            }
        }
    }

    private void addAllDocuments(final OfferDTO beanDTO, final Offer offer) {
        if (beanDTO.getDocuments() != null && !beanDTO.getDocuments().isEmpty()) {
            for (final DocumentDTO documentDTO : beanDTO.getDocuments()) {
                offer.addDocument(documentDTO.getType(), documentDTO.getUrl(), documentDTO.getLanguage(), false);
            }
        }
        if (beanDTO.getStaticDocuments() != null && !beanDTO.getStaticDocuments().isEmpty()) {
            for (final DocumentDTO documentDTO : beanDTO.getStaticDocuments()) {
                offer.addDocument(documentDTO.getType(), documentDTO.getUrl(), documentDTO.getLanguage(), true);
            }
        }
    }

    private void loadJsonConstants(final JsonNode actualObj, final Offer offer, final CatalogCommonData catalogCommonData) {
        CatalogLoaderConstants.buildProperties(actualObj, offer, catalogCommonData);
        CatalogLoaderConstants.buildPricingCriterias(actualObj, offer);
        CatalogLoaderConstants.buildBusinessRules(actualObj, offer, catalogCommonData);
    }

    private void loadOfferPremiumStrategy(final Offer offer, final OfferDTO beanDTO) {

        final OfferPremium offerPremium = new OfferPremium();
        offerPremium.setType(PremiumType.GROSS);
        if (beanDTO.getPremium()!=null) {
            final PremiumType premiumType = PremiumType.valueOf(beanDTO.getPremium().getType());
            if (premiumType!=null) {
                offerPremium.setType(premiumType);
            }
            offerPremium.setId(beanDTO.getPremium().getId());
            offerPremium.setPremiumCalc(beanDTO.getPremium().getPremiumCalc());
            offerPremium.setPremiumCalcType(beanDTO.getPremium().getPremiumCalcType());
            if (beanDTO.getPremium().getTotalRange()!=null) {
                final OfferPremiumRange totalRange = new OfferPremiumRange();
                this.mapOfferPremiumRange(totalRange, beanDTO.getPremium().getTotalRange());
                offerPremium.setTotalRange(totalRange);
            }
            if (beanDTO.getPremium().getIndividualRange()!=null) {
                final OfferPremiumRange individualRange = new OfferPremiumRange();
                this.mapOfferPremiumRange(individualRange, beanDTO.getPremium().getIndividualRange());
                offerPremium.setIndividualRange(individualRange);
            }
            if (beanDTO.getPremium().getTotalExcludingRange()!=null) {
                final OfferPremiumRange totalExcludingRange = new OfferPremiumRange();
                this.mapOfferPremiumRange(totalExcludingRange, beanDTO.getPremium().getTotalExcludingRange());
                offerPremium.setTotalExcludingRange(totalExcludingRange);
            }
            if (beanDTO.getPremium().getIndividualExcludingRange()!=null) {
                final OfferPremiumRange individualExcludingRange = new OfferPremiumRange();
                this.mapOfferPremiumRange(individualExcludingRange, beanDTO.getPremium().getIndividualExcludingRange());
                offerPremium.setIndividualExcludingRange(individualExcludingRange);
            }
        }

        offer.setOfferPremium(offerPremium);
    }

    private void mapOfferPremiumRange(final OfferPremiumRange totalRange, final OfferPremiumRangeDTO beanDTO) {
        totalRange.setId(beanDTO.getId());
        totalRange.setMin(beanDTO.getMin());
        totalRange.setMax(beanDTO.getMax());
        totalRange.setDataToLoopOn(beanDTO.getDataToLoopOn());
        totalRange.setThreshold(beanDTO.getThreshold());
    }

}
