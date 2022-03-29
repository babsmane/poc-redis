package sn.awi.redis.catalog.utils;

import java.util.Iterator;

import com.allianz.emagin.eqs.engine.catalog.businessrules.BusinessRule;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class PropertyHelperBusinessRules {

    private static final String BUSINESS_RULE_DATA_TO_LOOP_ON = "dataToLoopOn";
    private static final String BUSINESS_RULE_SUB_RULES = "subRules";
    private static final String BUSINESS_RULE_DEFINITION_ID = "definitionId";
    private static final String BUSINESS_RULE_CRITERIA = "criteria";

    public BusinessRule buildBusinessRules(final JsonNode jsonNode, final PropertyHelperMaps propertyHelperMaps) {
        final BusinessRule businessRule = new BusinessRule();
        final JsonNode dataToLoopOn = jsonNode.get(BUSINESS_RULE_DATA_TO_LOOP_ON);
        if (dataToLoopOn != null && !dataToLoopOn.isNull()) {
            businessRule.setDataToLoopOn(dataToLoopOn.asText());
            final JsonNode jsonNodeSubRules = jsonNode.get(BUSINESS_RULE_SUB_RULES);
            if(jsonNodeSubRules == null  || jsonNodeSubRules.isNull()) {
                return businessRule;
            }
            final Iterator<JsonNode> elms = jsonNodeSubRules.elements();
            while (elms.hasNext()) {
                final BusinessRule d = this.buildBusinessRules(elms.next(), propertyHelperMaps);
                if (d != null) {
                    businessRule.getSubRules().add(d);
                }
            }

        } else {
            final JsonNode definitionId = jsonNode.get(BUSINESS_RULE_DEFINITION_ID);
            final JsonNode criteria = jsonNode.get(BUSINESS_RULE_CRITERIA);
            if(this.businessRuleIsNull(definitionId, criteria, propertyHelperMaps)) {
                return null;
            }
            businessRule.setCriteria(criteria.asText());
            businessRule.setPropertyDefinition(propertyHelperMaps.getPropertyDefs().get(definitionId.asLong() + ""));
        }
        return businessRule;

    }

    private boolean businessRuleIsNull(final JsonNode definitionId, final JsonNode criteria, final PropertyHelperMaps propertyHelperMaps) {
        if(definitionId == null || definitionId.isNull() || !propertyHelperMaps.getPropertyDefs().containsKey(definitionId.asLong() + "")) {
            return true;
        }
        return criteria == null || criteria.isNull();
    }

}
