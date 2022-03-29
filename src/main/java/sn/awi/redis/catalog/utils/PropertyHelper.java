package sn.awi.redis.catalog.utils;

import com.allianz.emagin.eqs.engine.catalog.properties.BaseProperty;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyAgeBand;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyBand;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyBusinessParameterRef;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyCodeValue;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyCoverage;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyGeoZone;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertySeason;
import com.allianz.emagin.eqs.engine.catalog.properties.RelationShipStatus;
import com.allianz.emagin.eqs.engine.catalog.repositories.PropertyDefType;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class PropertyHelper {

    private static final String PROPERTY_TYPE = "type";

    public static final class PropertyDef {

        public static final String PROPERTY_DEF_ID = "id";
        public static final String PROPERTY_DEF_CODE = "code";
        public static final String PROPERTY_DEF_NAME = "name";
        public static final String PROPERTY_DEF_CHILDREN = "children";
        public static final String PROPERTY_DEF_FOLDER = "folder";
        public static final String PROPERTY_DEF_PRIMITIVE_TYPE = "primitiveType";
        public static final String PROPERTY_DEF_IS_KEY = "isKey";
        public static final String PROPERTY_DEF_ORDER = "order";
        public static final String PROPERTY_DEF_TARGET_DEFINITION_ID = "targetDefinitionId";
        public static final String PROPERTY_DEF_TARGET_REPOSITORY_TYPE = "targetRepositoryType";

        public static final String PROPERTY_DEF_CODE_COVERAGE = "Coverage";
        public static final String PROPERTY_DEF_CODE_PRODUCT_COVERAGE = "ProductCoverage";

        private PropertyDef() {
            throw new AssertionError();
        }
    }

    private final PropertyHelperMaps propertyHelperMaps= new PropertyHelperMaps();
    private final PropertyHelperBusinessRules propertyHelperBusinessRules= new PropertyHelperBusinessRules();
    private final PropertyDefHelper propertyDefHelper= new PropertyDefHelper(this.propertyHelperMaps);

    public PropertyHelperMaps getPropertyHelperMaps() {
        return this.propertyHelperMaps;
    } // PH1
    public PropertyHelperBusinessRules getBusinessRules() {
        return this.propertyHelperBusinessRules;
    } // PH2
    public PropertyDefHelper getPropertyDefHelper() {
        return this.propertyDefHelper;
    } // PH3

    public BaseProperty buildProperty(final JsonNode jsonNode) {
        // PH4 -> PHJ2 -> {
        //            PHJA -> PHJ1 + PHJ12 OK
        //            PHJ3 -> PHJ1 + PHJ4 (-> PHJC) OK
        //            PHJB -> PHJB -> PHJ1 + PHJ5 OK
        //            } OK
        final PropertyDefType propDef = PropertyDefType.valueOf(jsonNode.get(PROPERTY_TYPE).asText());
        if(propDef == PropertyDefType.COMPOSITE) {
            return PropertyHelperJson.buildPropertyComposite(jsonNode, this);
        }
        if(this.isPropertyWithoutProperties(propDef)) {
            return PropertyHelperPropWithoutProps.buildPropertyWithoutProperties(jsonNode, propDef, this);
        } else {
            return this.buildPropertyWithProperties(jsonNode, propDef);
        }
    }

    private PropertyCoverage buildCoverage(final JsonNode jsonNode) { // PHA
        final PropertyCoverage p = new PropertyCoverage();
        PropertyHelperJson.initProperty(jsonNode, p, this);
        return PropertyHelperJson.buildCoverage(jsonNode, p, this);
    }

    private PropertyCodeValue buildKeyValue(final JsonNode jsonNode) { // PHB
        final PropertyCodeValue pCodeValue = new PropertyCodeValue();
        PropertyHelperJson.initProperty(jsonNode, pCodeValue, this);
        return PropertyHelperJson.buildKeyValue(jsonNode, pCodeValue);
    }

    private PropertyBand buildBand(final JsonNode jsonNode) { // PHC
        final PropertyBand pBand = new PropertyBand();
        PropertyHelperJson.initProperty(jsonNode, pBand, this);
        return PropertyHelperJson.buildBand(jsonNode, pBand);
    }

    private PropertyAgeBand buildAgeBand(final JsonNode jsonNode) { // PHD
        final PropertyAgeBand ageBand = new PropertyAgeBand();
        PropertyHelperJson.initProperty(jsonNode, ageBand, this);
        return PropertyHelperJson.buildAgeBand(jsonNode, ageBand, this);
    }

    private PropertySeason buildBandSeason(final JsonNode jsonNode) { // PHE
        final PropertySeason pBandSeason = new PropertySeason();
        PropertyHelperJson.initProperty(jsonNode, pBandSeason, this);
        return PropertyHelperJson.buildBandSeason(jsonNode, pBandSeason);
    }
    
    private PropertyGeoZone buildGeozone(final JsonNode jsonNode) { // PHF
        final PropertyGeoZone geoZone = new PropertyGeoZone();
        PropertyHelperJson.initProperty(jsonNode, geoZone, this);
        return PropertyHelperJson.buildGeozone(jsonNode, geoZone, this);
    }

    /**
     * { "id" : 171405, "definitionId" : null, "type" : "REPOSITORY", "valueAtOfferLevel" : null, "repositoryParameterEnum" : "COUNTRY",
     * "repositoryParameterId" : 219 }
     */
    private PropertyBusinessParameterRef buildBusinessParameterRef(final JsonNode jsonNode) { // PHG
        final PropertyBusinessParameterRef pRef = new PropertyBusinessParameterRef();
        PropertyHelperJson.initProperty(jsonNode, pRef, this);
        return PropertyHelperJson.buildBusinessParameterRef(jsonNode, pRef, this);
    }

    private RelationShipStatus buildRelationShipStatus(final JsonNode jsonNode) { // PHH
        final RelationShipStatus rss = new RelationShipStatus();
        PropertyHelperJson.initProperty(jsonNode, rss, this);
        return PropertyHelperJsonRss.buildRelationShipStatus(jsonNode, rss, this);
    }

    private boolean isPropertyWithoutProperties(final PropertyDefType propDef) { // PHI
        switch (propDef) {
            case STRING:
            case TEXT:
            case NUMERIC:
            case NUMERIC_RANGE:
            case REF:
            case SET_BAND:
            case SET_SEASON:
            case SET:
                return true;
            case KEYVALUE:
            case GEOZONE:
            case RELATIONSHIP_STATUS:
            case BAND:
            case COVERAGE:
            case COVERAGE_OFFER:
            case COMPOSITE:
            case AGE_BAND:
            case REPOSITORY:
            case BAND_SEASON:
                return false;
            default:
                throw new IllegalStateException("unexpected enum value");
        }
    }

    private BaseProperty buildPropertyWithProperties(final JsonNode jsonNode, final PropertyDefType propDef) { // PHJ
        BaseProperty result= null;
        switch (propDef) {
            case COVERAGE:
                result = this.buildCoverage(jsonNode);
                break;

            case COVERAGE_OFFER:
                result = PropertyHelperJson.buildCoverageOfferFromComposite(jsonNode, this);
                break;

            case KEYVALUE:
                result = this.buildKeyValue(jsonNode);
                break;

            case GEOZONE:
                result = this.buildGeozone(jsonNode);
                break;

            case RELATIONSHIP_STATUS:
                result = this.buildRelationShipStatus(jsonNode);
                break;

            case BAND:
                result = this.buildBand(jsonNode);
                break;

            case AGE_BAND:
                result = this.buildAgeBand(jsonNode);
                break;

            case REPOSITORY:
                result = this.buildBusinessParameterRef(jsonNode);
                break;

            case BAND_SEASON:
                result = this.buildBandSeason(jsonNode);
                break;
                
            default:
                throw new IllegalStateException("base property is null: " + propDef);
        }
        this.propertyHelperMaps.getProperties().put(result.getId().toString(), result);
        return result;
    }

}
