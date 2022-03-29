package sn.awi.redis.catalog.utils;

import java.math.BigDecimal;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allianz.emagin.eqs.engine.catalog.pricing.AbstractPricingRuleCondition;
import com.allianz.emagin.eqs.engine.catalog.pricing.Chain;
import com.allianz.emagin.eqs.engine.catalog.pricing.PricingFactorType;
import com.allianz.emagin.eqs.engine.catalog.pricing.PricingRuleConditionNumeric;
import com.allianz.emagin.eqs.engine.catalog.pricing.PricingRuleConditionString;
import com.allianz.emagin.eqs.engine.catalog.pricing.PricingRuleOperationVariable;
import com.allianz.emagin.eqs.engine.catalog.pricing.RatingFactor;
import com.allianz.emagin.eqs.engine.catalog.pricing.RatingFactorForeach;
import com.allianz.emagin.eqs.engine.catalog.pricing.RatingFactorRoute;
import com.allianz.emagin.eqs.engine.catalog.pricing.RatingFactorSimple;
import com.allianz.emagin.eqs.engine.catalog.pricing.TargetCriteria;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

public class PricingHelper {

    private static final String JSON_PROPERTY_ID = "id";
    private static final String JSON_PROPERTY_NAME = "name";
    private static final String JSON_PROPERTY_TYPE = "type";
    private static final String JSON_PROPERTY_MODE = "mode";
    private static final String JSON_PROPERTY_CRITERIA_CONDITION = "criteriaCondition";
    private static final String JSON_PROPERTY_RULES = "rules";
    private static final String JSON_PROPERTY_JOIN_OPERATION = "joinOperation";
    private static final String JSON_PROPERTY_PARALLEL_COMPUTATION = "parallelComputation";
    private static final String JSON_PROPERTY_ROUTES = "routes";
    private static final String JSON_PROPERTY_DATA_TO_LOOP_ON = "dataToLoopOn";
    private static final String JSON_PROPERTY_TARGET_CRITERIAS = "targetCriterias";
    private static final String JSON_PROPERTY_ROUTE_CONDITION = "condition";

    private  static final Logger LOGGER = LoggerFactory.getLogger(PricingHelper.class);

    public Chain buildChain(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        Iterator<JsonNode> elms = node.elements();
        if (elms.hasNext()) {
            Chain chain = new Chain();
            while (elms.hasNext()) {
                chain.getAtoms().add(buildAtom(elms.next()));
            }
            return chain;
        }

        return null;
    }

    private RatingFactor buildAtom(JsonNode jsonNode) {
        String type = jsonNode.get(JSON_PROPERTY_TYPE).asText();
        PricingFactorType atomType = PricingFactorType.valueOf(type);

        RatingFactor result = null;
        switch (atomType) {
            case SIMPLE:
                result = buildAtomSimple(jsonNode);
                break;

            case ROUTE:
                result = buildAtomRoute(jsonNode);
                break;

            case FOREACH:
                result = buildAtomForeach(jsonNode);
                break;

            case TECHNICAL:
                // call buildAtomTechnical
                break;
        }
        return result;
    }

    private void buildAtomCommon(JsonNode jsonNode, RatingFactor ratingFactor) {
        ratingFactor.setId(jsonNode.get(JSON_PROPERTY_ID).asLong());
        ratingFactor.setName(jsonNode.get(JSON_PROPERTY_NAME).asText());
    }

    private RatingFactorSimple buildAtomSimple(JsonNode jsonNode) {
        RatingFactorSimple ratingFactor = new RatingFactorSimple();
        buildAtomCommon(jsonNode, ratingFactor);
        ratingFactor.setMode(jsonNode.get(JSON_PROPERTY_MODE).asText());
        JsonNode jNodeCondition = jsonNode.get(JSON_PROPERTY_CRITERIA_CONDITION);
        if (jNodeCondition != null && !jNodeCondition.isNull()) {
            ratingFactor.setCriteriaCondition(jNodeCondition.asText());
        }
        JsonNode rules = jsonNode.get(JSON_PROPERTY_RULES);
        Iterator<JsonNode> rulesIter = rules.elements();
        while (rulesIter.hasNext()) {
            this.buildAtomSimpleRulesElems(rulesIter, ratingFactor);
        }

        return ratingFactor;
    }

    private void buildAtomSimpleRulesElems(Iterator<JsonNode> rulesIter,
        RatingFactorSimple ratingFactor) {
        JsonNode rule = rulesIter.next();
        this.buildAtomSimpleRulesElemsCondition(rule, ratingFactor);
        this.buildAtomSimpleRulesElemsMinMax(rule, ratingFactor);
        this.buildAtomSimpleRulesElemsValue(rule, ratingFactor);
    }

    private void buildAtomSimpleRulesElemsValue(JsonNode rule, RatingFactorSimple ratingFactor) {
        JsonNode jValue = rule.get(JsonProperty.JSON_PROPERTY_RULE_VALUE);
        if (jValue != null && !jValue.isNull()) {
            String nodeValue = jValue.asText();
            if (nodeValue != null && !nodeValue.trim().equals("")) {
                nodeValue = nodeValue.trim();
                boolean flat;
                if (nodeValue.startsWith("*")) {
                    flat = false;
                    nodeValue = nodeValue.substring(1);
                }
                else {
                    JsonNode jsonNodeFlat = rule.get(JsonProperty.JSON_PROPERTY_RULE_FLAT);
                    flat= !jsonNodeFlat.isNull() && jsonNodeFlat.asBoolean();
                }
                BigDecimal value = new BigDecimal(nodeValue);

                JsonNode multipleBy = rule.get(JsonProperty.JSON_PROPERTY_RULE_MULIPLE_BY);
                String criteriaOperation = "";
                BigDecimal multiplePower = null;
                if (multipleBy != null && !multipleBy.isNull()) {
                    JsonNode jMultiplePowerValue = rule.get(JsonProperty.JSON_PROPERTY_RULE_MULIPLE_POWER);
                    criteriaOperation = multipleBy.asText();
                    if (jMultiplePowerValue != null && !jMultiplePowerValue.isNull()) {
                        multiplePower = new BigDecimal(jMultiplePowerValue.asText());
                    }
                }
                PricingRuleOperationVariable pOperation = new PricingRuleOperationVariable(criteriaOperation, value, flat, multiplePower);
                ratingFactor.getOperations().add(pOperation);
            }
        }
    }

    private void buildAtomSimpleRulesElemsMinMax(JsonNode rule, RatingFactorSimple ratingFactor) {
        JsonNode jMin = rule.get(JsonProperty.JSON_PROPERTY_RULE_MIN);
        JsonNode jMax = rule.get(JsonProperty.JSON_PROPERTY_RULE_MAX);
        if((jMin instanceof NullNode || jMin == null) && (jMax instanceof NullNode || jMax == null)) {
            return;
        }
        BigDecimal from= BigDecimal.ZERO;
        if (jMin != null && !jMin.isNull()) {
            from = new BigDecimal(jMin.asText());
        }
        BigDecimal to= null;
        if (jMax != null && !jMax.isNull()) {
            to= new BigDecimal(jMax.asText());
        }
        PricingRuleConditionNumeric pCondition = new PricingRuleConditionNumeric(from, to);
        ratingFactor.getConditions().add(pCondition);
    }

    private void buildAtomSimpleRulesElemsCondition(JsonNode rule, RatingFactorSimple ratingFactor) {
        JsonNode jCondition = rule.get(JsonProperty.JSON_PROPERTY_RULE_CONDITION);
        if (jCondition != null && !jCondition.isNull() && jCondition.asText() != null) {
            PricingRuleConditionString pCondition = new PricingRuleConditionString(jCondition.asText());
            ratingFactor.getConditions().add(pCondition);
        }
    }

    private RatingFactorRoute buildAtomRoute(JsonNode jsonNode) {
        RatingFactorRoute ratingFactor = new RatingFactorRoute();
        buildAtomCommon(jsonNode, ratingFactor);
        JsonNode jNodeCondition = jsonNode.get(JSON_PROPERTY_CRITERIA_CONDITION);
        if (jNodeCondition != null && !jNodeCondition.isNull()) {
            ratingFactor.setCriteriaCondition(jNodeCondition.asText());
        }
        JsonNode jNodeJO = jsonNode.get(JSON_PROPERTY_JOIN_OPERATION);
        if (jNodeJO != null && !jNodeJO.isNull()) {
            ratingFactor.setJoinOperation(jNodeJO.asText());
        }
        JsonNode jNodePC = jsonNode.get(JSON_PROPERTY_PARALLEL_COMPUTATION);
        if (jNodePC != null && !jNodePC.isNull()) {
            ratingFactor.setParallelComputation(jNodePC.asBoolean());
        }

        JsonNode jNodeRoutes = jsonNode.get(JSON_PROPERTY_ROUTES);
        if (jNodeRoutes == null || jNodeRoutes.isNull()) {
            return ratingFactor;
        }
        this.buildAtomRouteValuesIter(jNodeRoutes.elements(), ratingFactor);
        return ratingFactor;
    }

    private void buildAtomRouteValuesIter(Iterator<JsonNode> jValuesIter, RatingFactorRoute ratingFactor) {
        while (jValuesIter.hasNext()) {
            JsonNode jNodeRoute = jValuesIter.next();
            JsonNode jCondition = jNodeRoute.get(JSON_PROPERTY_ROUTE_CONDITION);
            JsonNode jMin = jNodeRoute.get(JsonProperty.JSON_PROPERTY_RULE_MIN);
            JsonNode jMax = jNodeRoute.get(JsonProperty.JSON_PROPERTY_RULE_MAX);
            AbstractPricingRuleCondition condition= buildAtomRouteValuesIterCondition(jCondition, jMin, jMax, ratingFactor.isParallelComputation());
            JsonNode jNodeTC = jNodeRoute.get(JSON_PROPERTY_TARGET_CRITERIAS);
            if (jNodeTC != null && !jNodeTC.isNull()) {
                Chain targetChain = buildChain(jNodeTC);
                if(targetChain == null) {
                    continue;
                }
                if(ratingFactor.isParallelComputation()) {
                    ratingFactor.getTargetChains().add(targetChain);
                } else {
                    //ratingFactor.getTargetCriterias().put(condition, targetChain);
                	ratingFactor.getTargetCriterias().add(new TargetCriteria(condition, targetChain));
                }
            } else {
                LOGGER.warn("no target criterias for rating factor");
            }
        }
    }

    private AbstractPricingRuleCondition buildAtomRouteValuesIterCondition(
            JsonNode jCondition, JsonNode jMin, JsonNode jMax, boolean parallelComputation) {
        AbstractPricingRuleCondition condition;
        if (jCondition != null && !jCondition.isNull() && jMin != null && !jMin.isNull()) {
            throw new IllegalStateException("rule condition is both string and numeric");
        }
        if(parallelComputation) {
            return buildAtomRouteValuesIterConditionParallelComputation(jCondition, jMin);
        } else if (jCondition != null && !jCondition.isNull()) {
            condition= buildAtomRouteValuesIterWoJmax(jCondition, jMax);
        } else if (jMin != null && !jMin.isNull()) {
            condition = buildAtomRouteValuesIterJmin(jMin, jMax);
        } else if (jMax != null && !jMax.isNull()) {
            condition = buildAtomRouteValuesIterJmax(jMin, jMax);
        } else {
            throw new IllegalStateException("no conditions on non parallel calculation route");
        }
        return condition;
    }

    private AbstractPricingRuleCondition buildAtomRouteValuesIterConditionParallelComputation(JsonNode jCondition, JsonNode jMin) {
        if (jCondition != null && !jCondition.isNull()) {
            throw new IllegalStateException("string rule condition should not exist when parallel computation");
        }
        if (jMin != null && !jMin.isNull()) {
            throw new IllegalStateException("numeric rule condition should not exist when parallel computation");
        }
        // parallel computation has no conditions
        return null;
    }

    private AbstractPricingRuleCondition buildAtomRouteValuesIterWoJmax(JsonNode jCondition, JsonNode jMax) {
        if(jMax != null && !jMax.isNull()) {
            throw new IllegalStateException("string rule condition cannot have a max value");
        }
        return new PricingRuleConditionString(jCondition.asText());
    }

    private AbstractPricingRuleCondition buildAtomRouteValuesIterJmin(JsonNode jMin, JsonNode jMax) {
        BigDecimal from= new BigDecimal(jMin.asText());
        BigDecimal to= null;
        if (jMax != null && !jMax.isNull()) {
            to= new BigDecimal(jMax.asText());
        }
        return new PricingRuleConditionNumeric(from, to);
    }

    private AbstractPricingRuleCondition buildAtomRouteValuesIterJmax(JsonNode jMin, JsonNode jMax) {
        BigDecimal to= new BigDecimal(jMax.asText());
        BigDecimal from= null;
        if (jMin != null && !jMin.isNull()) {
            from= new BigDecimal(jMin.asText());
        }
        return new PricingRuleConditionNumeric(from, to);
    }

    private RatingFactorForeach buildAtomForeach(JsonNode jsonNode) {
        RatingFactorForeach ratingFactor = new RatingFactorForeach();
        buildAtomCommon(jsonNode, ratingFactor);
        JsonNode jNodeJO = jsonNode.get(JSON_PROPERTY_JOIN_OPERATION);
        if (jNodeJO != null && !jNodeJO.isNull()) {
            ratingFactor.setJoinOperation(jNodeJO.asText());
        }
        JsonNode jNodeDTL = jsonNode.get(JSON_PROPERTY_DATA_TO_LOOP_ON);
        if (jNodeDTL != null && !jNodeDTL.isNull()) {
            ratingFactor.setDataToLoopOn(jNodeDTL.asText());
        }

        JsonNode jNodeTC = jsonNode.get(JSON_PROPERTY_TARGET_CRITERIAS);
        if (jNodeTC != null && !jNodeTC.isNull()) {
            Chain targetChain = buildChain(jNodeTC);
            if (targetChain != null) {
                ratingFactor.setTargetChain(targetChain);
            }
        }
        return ratingFactor;
    }

    private static final class JsonProperty {

        public static final String JSON_PROPERTY_RULE_CONDITION = "condition";
        public static final String JSON_PROPERTY_RULE_MIN = "min";
        public static final String JSON_PROPERTY_RULE_MAX = "max";
        public static final String JSON_PROPERTY_RULE_VALUE = "value";
        public static final String JSON_PROPERTY_RULE_FLAT = "flat";
        public static final String JSON_PROPERTY_RULE_MULIPLE_BY = "multipleBy";
        public static final String JSON_PROPERTY_RULE_MULIPLE_POWER = "powerOf";
    }

}