package sn.awi.redis.catalog.loader;

import java.util.Iterator;

import com.allianz.emagin.eqs.engine.catalog.Offer;
import com.allianz.emagin.eqs.engine.catalog.businessrules.BusinessRule;
import com.allianz.emagin.eqs.engine.catalog.pricing.Chain;
import com.allianz.emagin.eqs.engine.catalog.properties.BaseProperty;
import com.fasterxml.jackson.databind.JsonNode;

import sn.awi.redis.catalog.utils.PricingHelper;

public class CatalogLoaderConstants {

    public static final String JSON_CONSTANT_PROPERTIES = "properties";
    public static final String JSON_CONSTANT_BUSINESS_RULES = "businessRules";
    public static final String JSON_CONSTANT_PRICING_CRITERIAS = "pricingCriterias";
    public static final String JSON_CONSTANT_MD_PUBLICATION_ID = "publicationId";
    public static final String JSON_CONSTANT_MD_ENVIRONMENT = "environment";
    public static final String JSON_CONSTANT_MD_SUB_ENVIRONMENT = "subEnvironment";
    public static final String PUBLICATION_FILE_METADATA = "metadata.json";
    public static final String PUBLICATION_FILE_COMMONDATA = "common_data.json";
    public static final String PUBLICATION_FILE_DCX_STARTER = "dcx_";
    public static final String PUBLICATION_FILE_MASTER_OFFER_STARTER = "product_";
    public static final String PUBLICATION_FILE_OFFER_STARTER = "offer_";

    private CatalogLoaderConstants() {}

    public static void buildProperties(JsonNode actualObj, Offer offer, CatalogCommonData catalogCommonData) {
        // Load properties
        JsonNode jsonNode1 = actualObj.get(CatalogLoaderConstants.JSON_CONSTANT_PROPERTIES);
        Iterator<JsonNode> elms = jsonNode1.elements();
        while (elms.hasNext()) {
            JsonNode elm = elms.next();
            BaseProperty bp = catalogCommonData.getPropertyHelper().buildProperty(elm);
            if (bp == null) {
                CatalogLoaderCounters.incrementNbBasePropertyNullO();
            } else {
                offer.getProperties().add(bp);
                offer.getPropDefProperty().put(bp.getDefinitionId(), bp);
            }
        }
    }

    public static void buildPricingCriterias(JsonNode actualObj, Offer offer) {
        // pricing criteria
        JsonNode jsonNode1 = actualObj.get(CatalogLoaderConstants.JSON_CONSTANT_PRICING_CRITERIAS);
        Chain chain = new PricingHelper().buildChain(jsonNode1);
        if (chain != null) {
            offer.setChain(chain);
        }

    }

    public static void buildBusinessRules(JsonNode actualObj, Offer offer, CatalogCommonData catalogCommonData) {
        // Load business rules
        JsonNode jsonNode1 = actualObj.get(CatalogLoaderConstants.JSON_CONSTANT_BUSINESS_RULES);
        if (jsonNode1 != null && !jsonNode1.isNull()) {
            Iterator<JsonNode> elms = jsonNode1.elements();
            while (elms.hasNext()) {
                BusinessRule b = catalogCommonData.getPropertyHelper().getBusinessRules().buildBusinessRules(elms.next(),
                    catalogCommonData.getPropertyHelper().getPropertyHelperMaps());
                if (b != null) {
                    offer.getBusinessRules().add(b);
                }
            }
        }
    }

}
