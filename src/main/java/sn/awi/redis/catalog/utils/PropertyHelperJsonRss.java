package sn.awi.redis.catalog.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.allianz.emagin.eqs.engine.catalog.properties.BaseProperty;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyAgeBand;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyBand;
import com.allianz.emagin.eqs.engine.catalog.properties.RelationShipStatus;
import com.allianz.emagin.eqs.engine.catalog.properties.rss.RelationShipRule;
import com.fasterxml.jackson.databind.JsonNode;

public class PropertyHelperJsonRss {

    private static final String PROPERTY_CODE = "code";
    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_MIN = "min";
    private static final String PROPERTY_MAX = "max";
    private static final String PROPERTY_PRIORITY = "priority";
    private static final String PROPERTY_RELATION_SHIP_RULES = "rules";
    private static final String PROPERTY_RELATION_SHIP_RULE_ID = "id";
    private static final String PROPERTY_RELATION_SHIP_RULE_NAME = "name";
    private static final String PROPERTY_RELATION_SHIP_RULE_CODE = "code";
    private static final String PROPERTY_RELATION_SHIP_RULE_MIN = "min";
    private static final String PROPERTY_RELATION_SHIP_RULE_MAX = "max";
    private static final String PROPERTY_RELATION_SHIP_RULE_AGEBANDREFS = "ageBandRefs";
    private static final String PROPERTY_AGE_BAND_REFS = "ageBandRefs";
    private static final String PROPERTY_RELATION_SHIP_INCLUDE_OBJECT = "includingObject";

    private PropertyHelperJsonRss() {
        throw new AssertionError();
    }

    private static void addRelatinShipRule(final RelationShipStatus rss, final JsonNode jsonNode, final List<Long> ageBandIds, final ArrayList<String> ageBandsAccepted,
                                           final PropertyHelper propertyHelper) {
        final JsonNode ruleId = jsonNode.get(PROPERTY_RELATION_SHIP_RULE_ID);
        final JsonNode ruleName = jsonNode.get(PROPERTY_RELATION_SHIP_RULE_NAME);
        final JsonNode ruleCode = jsonNode.get(PROPERTY_RELATION_SHIP_RULE_CODE);
        final JsonNode ruleMin = jsonNode.get(PROPERTY_RELATION_SHIP_RULE_MIN);
        final JsonNode ruleMax = jsonNode.get(PROPERTY_RELATION_SHIP_RULE_MAX);
        Long id = null;
        if (ruleId != null) {
            id = ruleId.asLong();
        }
        String code = null;
        if (ruleCode != null) {
            code = ruleCode.asText();
        }
        String name = null;
        if (ruleName != null) {
            name = ruleName.asText();
        }
        Integer min = null;
        if (ruleMin != null) {
            min = ruleMin.asInt();
        }
        Integer max = null;
        if (ruleMax != null) {
            max = ruleMax.asInt();
        }
        final RelationShipRule rsr = rss.addRelationShipRule(id, name, (double)min, (double)max, ageBandIds, ageBandsAccepted);
        if (!ageBandIds.isEmpty()) {
            propertyHelper.getPropertyHelperMaps().getPendindRSRule().add(rsr);
        }
    }

    public static void processPendingRelationShipRules(final PropertyHelper propertyHelper) {
        if (propertyHelper.getPropertyHelperMaps().getPendindRSRule() == null ||
            propertyHelper.getPropertyHelperMaps().getPendindRSRule().isEmpty()) {
            return;
        }
        final List<RelationShipRule> toremove = new ArrayList<>();
        for(final RelationShipRule rsr : propertyHelper.getPropertyHelperMaps().getPendindRSRule()) {
            final ArrayList<String> ageBandsAccepted = PropertyHelperJsonRss.loadRuleAgeBandRefs(rsr.getAgeBandIds(), propertyHelper);
            rsr.getAgeBandCodes().addAll(ageBandsAccepted);
            if (rsr.getAgeBandIds()==null || rsr.getAgeBandIds().isEmpty()) {
                toremove.add(rsr);
            }
        }
        propertyHelper.getPropertyHelperMaps().getPendindRSRule().removeAll(toremove);
    }
    
    private static RelationShipStatus loadRules(final JsonNode jsonNode, final RelationShipStatus rss, final PropertyHelper propertyHelper) {
        final JsonNode jRSSRules = jsonNode.get(PROPERTY_RELATION_SHIP_RULES);
        if (jRSSRules == null || jRSSRules.isNull()) {
            return rss;
        }
        final Iterator<JsonNode> jValuesIter = jRSSRules.elements();
        while (jValuesIter.hasNext()) {
            final JsonNode jsRSSRule = jValuesIter.next();
            if (jsRSSRule != null && !jsRSSRule.isNull()) {
                final ArrayList<Long> ageBandIds = loadRuleAgeBandIds(jsRSSRule);
                final ArrayList<String> ageBandsAccepted = PropertyHelperJsonRss.loadRuleAgeBandRefs(ageBandIds, propertyHelper);
                PropertyHelperJsonRss.addRelatinShipRule(rss, jsRSSRule, ageBandIds, ageBandsAccepted, propertyHelper);
            }
        }
        return rss;
    }

    private static ArrayList<Long> loadRuleAgeBandIds(final JsonNode jsonNode) {
        final JsonNode jAgeBandValues = jsonNode.get(PROPERTY_RELATION_SHIP_RULE_AGEBANDREFS);
        final ArrayList<Long> ageBandIds = new ArrayList<Long>();
        if (jAgeBandValues != null && !jAgeBandValues.isNull()) {
            final Iterator<JsonNode> jAgeBandValuesIter = jAgeBandValues.elements();
            while (jAgeBandValuesIter.hasNext()) {
                ageBandIds.add(jAgeBandValuesIter.next().asLong());
            }
        }
        return ageBandIds;
    }

    private static ArrayList<String> loadRuleAgeBandRefs(final List<Long> ageBandIds, final PropertyHelper propertyHelper) {
        final List<Long> toremove = new ArrayList<>();
        final ArrayList<String> ageBandsAccepted = new ArrayList<String>();
        if (ageBandIds != null && !ageBandIds.isEmpty()) {
            for (final Long ageBandId : ageBandIds) {
                if (propertyHelper.getPropertyHelperMaps().getProperties().containsKey(ageBandId.longValue() + "")) {
                    final BaseProperty bp =propertyHelper.getPropertyHelperMaps().getProperties().get(ageBandId.longValue() + "");
                    PropertyHelperJsonRss.loadRuleAgeBandRefsToRemove(bp, ageBandsAccepted, toremove, ageBandId);
                }
            }
        }
        if (!toremove.isEmpty() && ageBandIds != null) {
            ageBandIds.removeAll(toremove);
        }
        
        return ageBandsAccepted;
    }

    private static void loadRuleAgeBandRefsToRemove(final BaseProperty bp, final ArrayList<String> ageBandsAccepted, final List<Long> toremove,
                                                    final Long ageBandId) {
        if (bp instanceof PropertyAgeBand) {
            final PropertyAgeBand pab = (PropertyAgeBand)bp;
            ageBandsAccepted.add(pab.getCode());
            toremove.add(ageBandId);
        } else if (bp instanceof PropertyBand) {
            final PropertyBand pband = (PropertyBand)bp;
            ageBandsAccepted.add(pband.getKeyValue());
            toremove.add(ageBandId);
        }
    }

    public static RelationShipStatus buildRelationShipStatus(final JsonNode jsonNode, final RelationShipStatus rss, final PropertyHelper propertyHelper) {
        final JsonNode jsonNodeCode = jsonNode.get(PROPERTY_CODE);
        if (jsonNodeCode != null) {
            rss.setCode(jsonNodeCode.asText());
        }
        final JsonNode jsonNodeName = jsonNode.get(PROPERTY_NAME);
//        if (jsonNodeName != null) {
//            rss.setName(jsonNodeName.asText());
//        }
        rss.setMax(jsonNode.get(PROPERTY_MAX).asInt());
        rss.setMin(jsonNode.get(PROPERTY_MIN).asInt());
        rss.setPriority(jsonNode.get(PROPERTY_PRIORITY).asInt());

        final JsonNode jValues = jsonNode.get(PROPERTY_AGE_BAND_REFS);
        if (jValues != null) {
            final Iterator<JsonNode> jValuesIter = jValues.elements();
            while (jValuesIter.hasNext()) {
                rss.getAgeBands().add(propertyHelper.getPropertyHelperMaps().getProperties().get(jValuesIter.next() + ""));
            }
        }

        final JsonNode jsonNodeIO = jsonNode.get(PROPERTY_RELATION_SHIP_INCLUDE_OBJECT);
        if (jsonNodeIO != null) {
            rss.setIncludingObject(jsonNodeIO.asBoolean());
        }

        // Rules loading
        PropertyHelperJsonRss.loadRules(jsonNode, rss, propertyHelper);
        return rss;
    }
}
