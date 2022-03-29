package sn.awi.redis.catalog.utils;

import java.util.Iterator;
import java.util.Map;

import com.allianz.emagin.eqs.engine.catalog.properties.BaseProperty;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyAgeBand;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyBand;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyBusinessParameterRef;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyCodeValue;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyComposite;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyCoverage;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyCoverageOffer;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyGeoZone;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyNumeric;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyREF;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertySeason;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyString;
import com.allianz.emagin.eqs.engine.catalog.repositories.PropertyDefType;
import com.allianz.emagin.eqs.engine.catalog.repositories.PropertyDefinition;
import com.fasterxml.jackson.databind.JsonNode;

public class PropertyHelperJson {

    private static final String PROPERTY_CHILDREN = "children";
    private static final String PROPERTY_CODE = "code";
    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_LABEL = "label";
    private static final String PROPERTY_MIN = "min";
    private static final String PROPERTY_MAX = "max";
    private static final String PROPERTY_MIN_DAY = "minDay";
    private static final String PROPERTY_MIN_MONTH = "minMonth";
    private static final String PROPERTY_MAX_DAY = "maxDay";
    private static final String PROPERTY_MAX_MONTH = "maxMonth";
    private static final String PROPERTY_AGE_BAND_REF_ID = "ageBandRefId";
    private static final String PROPERTY_DOMESTIC = "domestic";
    private static final String PROPERTY_PRIORITY = "priority";
    private static final String PROPERTY_VALUES = "values";
    private static final String PROPERTY_ID = "id";
    private static final String PROPERTY_VALUE = "value";
    private static final String PROPERTY_REPO_PARAM_ENUM = "repositoryParameterEnum";
    private static final String PROPERTY_REPO_PARAM_ID = "repositoryParameterId";
    private static final String PROPERTY_DESCRIPTION = "description";
    private static final String PROPERTY_ORDER = "order";
    private static final String PROPERTY_COVERAGE_TYPE_ID = "coverageTypeId";
    private static final String PROPERTY_DEFINITION_ID = "definitionId";

    private PropertyHelperJson() {
        throw new AssertionError();
    }

    public static void initProperty(final JsonNode jsonNode, final BaseProperty baseProperty, final PropertyHelper propertyHelper) {
        // PHJ1 OK
        baseProperty.setId(jsonNode.get(PROPERTY_ID).asLong());
        final JsonNode jsonDef = jsonNode.get(PROPERTY_DEFINITION_ID);
        if (jsonDef != null && !jsonDef.isNull()) {
            baseProperty.setDefinitionId(jsonDef.asInt() + "");
            baseProperty.setPropertyDefinition(propertyHelper.getPropertyHelperMaps().getPropertyDefs().get(baseProperty.getDefinitionId()));
        }

    }

    public static BaseProperty buildPropertyComposite(final JsonNode jsonNode, final PropertyHelper propertyHelper) {
        /* PHJ2 -> {
            PHJA -> PHJ1 + PHJ12 OK
            PHJ3 -> PHJ1 + PHJ4 (-> PHJC) OK
            PHJB -> PHJB -> PHJ1 + PHJ5 OK
            } OK
         */
        BaseProperty result = null;
        final PropertyDefinition pDef = propertyHelper.getPropertyHelperMaps().getPropertyDefs().get(jsonNode.get(PROPERTY_DEFINITION_ID).asInt() + "");
        if (pDef.getPrimitiveType() == PropertyDefType.COVERAGE) {
            result = PropertyHelperJson.buildCoverageFromComposite(jsonNode, propertyHelper);
        } else if (pDef.getPrimitiveType() == PropertyDefType.COVERAGE_OFFER) {
            result = PropertyHelperJson.buildCoverageOfferFromComposite(jsonNode, propertyHelper);
        } else {
            result = PropertyHelperJson.buildComposite(jsonNode, propertyHelper);
        }
        propertyHelper.getPropertyHelperMaps().getProperties().put(result.getId().toString(), result);
        return result;
    }

    public static PropertyCoverageOffer buildCoverageOfferFromComposite(final JsonNode jsonNode, final PropertyHelper propertyHelper) {
        // PHJ3 -> PHJ1 + PHJ4 (-> PHJC) OK
        final PropertyCoverageOffer p = new PropertyCoverageOffer();
        PropertyHelperJson.initProperty(jsonNode, p, propertyHelper);
        return PropertyHelperJson.buildCoverageOfferFromComposite(jsonNode, p, propertyHelper);
    }

    public static PropertyCoverageOffer buildCoverageOfferFromComposite(final JsonNode jsonNode, final PropertyCoverageOffer p, final PropertyHelper propertyHelper) {
        // PHJ4 -> PHJC OK
        final JsonNode jChildrens = jsonNode.get(PROPERTY_CHILDREN);
        final Iterator<JsonNode> jValuesIter = jChildrens.elements();
        while (jValuesIter.hasNext()) {
            final BaseProperty baseProperty = propertyHelper.buildProperty(jValuesIter.next());
            if ("CoverageLink".equalsIgnoreCase(baseProperty.getPropertyDefinition().getCode())) {
                p.setCoverage((PropertyCoverage) (((PropertyREF) baseProperty).getProperty()));
            }
            if ("Value".equalsIgnoreCase(baseProperty.getPropertyDefinition().getCode())) {
                p.setValue(((PropertyString) baseProperty).getValue());
                PropertyHelperJson.buildCoverageOfferFromCompositeTranslations(p, (PropertyString) baseProperty);
            }
        }

        return p;
    }

    public static PropertyComposite buildComposite(final JsonNode jsonNode, final PropertyComposite p, final PropertyHelper propertyHelper) {
        // PHJ5 -> PH4...
        final JsonNode jChildrens = jsonNode.get(PROPERTY_CHILDREN);
        final Iterator<JsonNode> jValuesIter = jChildrens.elements();
        while (jValuesIter.hasNext()) {
            final BaseProperty baseProperty = propertyHelper.buildProperty(jValuesIter.next());
            p.getProperties().add(baseProperty);
        }
        return p;
    }

    public static PropertyCodeValue buildKeyValue(final JsonNode jsonNode, final PropertyCodeValue pCodeValue) {
        // PHJ6 OK
        pCodeValue.setCode(jsonNode.get(PROPERTY_CODE).asText());
        JsonNode label = jsonNode.get(PROPERTY_NAME);
        if (label==null || label.isNull())
            label = jsonNode.get(PROPERTY_LABEL);
        if (label!=null && !label.isNull())
            pCodeValue.setValue(label.asText());
        
        return pCodeValue;
    }

    public static PropertyBand buildBand(final JsonNode jsonNode, final PropertyBand pBand) {
        // PHJ7 OK
        final JsonNode jsonNodeCode = jsonNode.get(PROPERTY_VALUE);
        if (jsonNodeCode != null) {
            pBand.setValue(jsonNodeCode.asText());
            //pBand.setName(jsonNodeCode.asText());
        } else {
            return null;
        }
        final JsonNode minNode = jsonNode.get(PROPERTY_MIN);
        final JsonNode maxNode = jsonNode.get(PROPERTY_MAX);
        if ((minNode == null || minNode.isNull()) && (maxNode == null || maxNode.isNull())) {
            return null;
        }
        if (minNode != null && !minNode.isNull()) {
            pBand.setMin(minNode.asDouble());
        }
        if (maxNode != null && !maxNode.isNull()) {
            pBand.setMax(maxNode.asDouble());
        }
        return pBand;
    }

    public static PropertyAgeBand buildAgeBand(final JsonNode jsonNode, final PropertyAgeBand ageBand, final PropertyHelper propertyHelper) {
        // PHJ8 OK
        final JsonNode ageBandRef = jsonNode.get(PROPERTY_AGE_BAND_REF_ID);
        if (ageBandRef != null && !ageBandRef.isNull()) {
            PropertyHelperRepository.setAgeBand(ageBand, propertyHelper.getPropertyHelperMaps().getAllAgeBands().get(ageBandRef.asInt() + ""));
        } else {
            final JsonNode jCode = jsonNode.get(PROPERTY_CODE);
            if (jCode != null && !jCode.isNull()) {
                ageBand.setCode(jCode.asText());
            }
            final JsonNode jName = jsonNode.get(PROPERTY_NAME);
            if (jName != null && !jName.isNull()) {
                ageBand.setName(jName.asText());
            }
        }
        ageBand.setAgemin(jsonNode.get(PROPERTY_MIN).asInt());
        ageBand.setAgemax(jsonNode.get(PROPERTY_MAX).asInt());

        return ageBand;
    }

    public static PropertySeason buildBandSeason(final JsonNode jsonNode, final PropertySeason pBandSeason) {
        // PHJ9 OK
        final JsonNode jsonNodeCode = jsonNode.get(PROPERTY_VALUE);
        if (jsonNodeCode != null) {
            pBandSeason.setCode(jsonNodeCode.asText());
        }
        pBandSeason.setMinDay(jsonNode.get(PROPERTY_MIN_DAY).asInt());
        pBandSeason.setMinMonth(jsonNode.get(PROPERTY_MIN_MONTH).asInt());
        pBandSeason.setMaxDay(jsonNode.get(PROPERTY_MAX_DAY).asInt());
        pBandSeason.setMaxMonth(jsonNode.get(PROPERTY_MAX_MONTH).asInt());
        return pBandSeason;

    }

    public static PropertyGeoZone buildGeozone(final JsonNode jsonNode, final PropertyGeoZone geoZone, final PropertyHelper propertyHelper) {
        // PHJ10 OK
        geoZone.setCode(jsonNode.get(PROPERTY_CODE).asText());
        final JsonNode domesticJson = jsonNode.get(PROPERTY_DOMESTIC);
        if (domesticJson != null && !domesticJson.isNull()) {
            geoZone.setDomestic(domesticJson.asBoolean());
            // TODO : if true, do it dcx country geozonepreprocessing by using request.country
        }
        geoZone.setName(jsonNode.get(PROPERTY_NAME).asText());
        geoZone.setPriority(jsonNode.get(PROPERTY_PRIORITY).asInt());

        final JsonNode jValues = jsonNode.get(PROPERTY_VALUES);
        final Iterator<JsonNode> jValuesIter = jValues.elements();
        while (jValuesIter.hasNext()) {
            final JsonNode jsCountry = jValuesIter.next();
            PropertyHelperRepository.setCountry(geoZone, propertyHelper.getPropertyHelperMaps().getAllCountries().get(jsCountry.asInt() + ""));
        }
        return geoZone;
    }

    public static PropertyBusinessParameterRef buildBusinessParameterRef(
            final JsonNode jsonNode, final PropertyBusinessParameterRef pRef, final PropertyHelper propertyHelper) {
        // PHJ11 OK
        final String repo = jsonNode.get(PROPERTY_REPO_PARAM_ENUM).asText();
        pRef.setReferentialObjectType(repo);
        final String key = jsonNode.get(PROPERTY_REPO_PARAM_ID).asInt() + "";
        pRef.setReferentialObjectId(key);

        switch (repo) {
            case "COUNTRY" :
                PropertyHelperRepository.setCountry(pRef, propertyHelper.getPropertyHelperMaps().getAllCountries().get(key));
                break;
            case "LANGUAGE":
                PropertyHelperRepository.setLanguage(pRef, propertyHelper.getPropertyHelperMaps().getAllLanguages().get(key));
                break;
            case "DAY":
                PropertyHelperRepository.setDayOfWeek(pRef, propertyHelper.getPropertyHelperMaps().getAllDays().get(key));
                break;
            case "MONTH":
                PropertyHelperRepository.setMonth(pRef, propertyHelper.getPropertyHelperMaps().getAllMonths().get(key));
                break;
            default:
                throw new IllegalStateException("wrong repo enum value");
        }

        return pRef;
    }

    public static PropertyCoverage buildCoverageFromComposite(
            final JsonNode jsonNode, final PropertyCoverage p, final PropertyHelper propertyHelper) {
        /* PHJ12 -> {
                PHJD OK
                PHJC OK
                } OK
         */
        final JsonNode jChildrens = jsonNode.get(PROPERTY_CHILDREN);
        final Iterator<JsonNode> jValuesIter = jChildrens.elements();
        Map<String, String> nameTranslations = null;
        Map<String, String> descriptionTranslations = null;
        while (jValuesIter.hasNext()) {
            final BaseProperty baseProperty= propertyHelper.buildProperty(jValuesIter.next());
            switch(baseProperty.getPropertyDefinition().getCode()) {
                case "Label":
                case "Name":
                    nameTranslations= PropertyHelperJson.buildCoverageFromCompositeName(p, (PropertyString)baseProperty);
                    break;
                case "Description":
                    descriptionTranslations= PropertyHelperJson.buildCoverageFromCompositeDescription(p, (PropertyString) baseProperty);
                    break;
                case "Order":
                    p.setOrder(((PropertyNumeric) baseProperty).getValue().intValue());
                    break;
                case "Type":
                    p.setCoverageType(baseProperty);
                    break;
                default:
                    throw new IllegalStateException("unexpected code");
            }
        }
        if (nameTranslations!=null && descriptionTranslations!=null) {
            for (final String key : nameTranslations.keySet()) {
                p.translate(key, nameTranslations.get(key), descriptionTranslations.get(key));
            }
        }
        return p;
    }

    public static PropertyCoverage buildCoverage(final JsonNode jsonNode, final PropertyCoverage p, final PropertyHelper propertyHelper) {
        // PHJ13 OK
        p.setName(jsonNode.get(PROPERTY_VALUE).asText());
        p.setDescription(jsonNode.get(PROPERTY_DESCRIPTION).asText());
        p.setOrder(jsonNode.get(PROPERTY_ORDER).asInt());

        final JsonNode coverageTypeId = jsonNode.get(PROPERTY_COVERAGE_TYPE_ID);
        if (coverageTypeId != null && !coverageTypeId.isNull()) {
            if (propertyHelper.getPropertyHelperMaps().getProperties().containsKey(coverageTypeId.asInt() + "")) {
                p.setCoverageType(propertyHelper.getPropertyHelperMaps().getProperties().get(coverageTypeId.asInt() + ""));
            } else {
                // TODO : revenir le chercher plutard
            }

        }
        return p;
    }

    private static PropertyCoverage buildCoverageFromComposite(final JsonNode jsonNode, final PropertyHelper propertyHelper) {
        // PHJA -> PHJ1 + PHJ12 -> {
        //                PHJD
        //                PHJC
        //                } OK
        final PropertyCoverage p = new PropertyCoverage();
        PropertyHelperJson.initProperty(jsonNode, p, propertyHelper);
        return PropertyHelperJson.buildCoverageFromComposite(jsonNode, p, propertyHelper);
    }

    private static PropertyComposite buildComposite(final JsonNode jsonNode, final PropertyHelper propertyHelper) {
        // PHJB -> PHJ1 + PHJ5 OK
        final PropertyComposite p = new PropertyComposite();
        PropertyHelperJson.initProperty(jsonNode, p, propertyHelper);
        return PropertyHelperJson.buildComposite(jsonNode, p, propertyHelper);
    }

    private static void buildCoverageOfferFromCompositeTranslations(final PropertyCoverageOffer p, final PropertyString baseProperty) {
        // PHJC OK
        if (baseProperty.hasTranslations()) {
            final Map<String, String> valueTranslations = baseProperty.getTranslations();
            if (valueTranslations!=null) {
                for (final String key : valueTranslations.keySet()) {
                    p.translate(key, valueTranslations.get(key));
                }
            }
        }
    }

    private static Map<String, String> buildCoverageFromCompositeDescription(final PropertyCoverage p, final PropertyString baseProperty) {
        // PHJC OK
        p.setDescription((baseProperty).getValue());
        if (baseProperty.hasTranslations()) {
            return (baseProperty).getTranslations();
        }
        return null;
    }

    private static Map<String, String> buildCoverageFromCompositeName(
            final PropertyCoverage p, final PropertyString baseProperty) {
        // PHJD OK
        p.setName(baseProperty.getValue());
        if (baseProperty.hasTranslations()) {
            return baseProperty.getTranslations();
        }
        return null;
    }

}
