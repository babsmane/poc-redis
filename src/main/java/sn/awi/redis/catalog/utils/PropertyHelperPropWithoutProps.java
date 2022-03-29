package sn.awi.redis.catalog.utils;

import java.util.Iterator;

import com.allianz.emagin.eqs.engine.catalog.properties.BaseProperty;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyNumeric;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyNumericRange;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyREF;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertySet;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyString;
import com.allianz.emagin.eqs.engine.catalog.repositories.Language;
import com.allianz.emagin.eqs.engine.catalog.repositories.PropertyDefType;
import com.fasterxml.jackson.databind.JsonNode;

public class PropertyHelperPropWithoutProps {

    private static final String PROPERTY_ID = "id";
    private static final String PROPERTY_VALUE = "value";
    private static final String PROPERTY_VALUES = "values";
    private static final String PROPERTY_MIN = "min";
    private static final String PROPERTY_MAX = "max";
    private static final String PROPERTY_I18N = "i18n";
    private static final String PROPERTY_I18N_LANGUAGE = "languageId";

    private PropertyHelperPropWithoutProps() {
        throw new AssertionError();
    }

    public static BaseProperty buildPropertyWithoutProperties(JsonNode jsonNode, PropertyDefType propDef, PropertyHelper ph) {
        BaseProperty result = null;
        switch (propDef) {
            case STRING:
            case TEXT:
                result = buildStringText(jsonNode, ph);
                break;

            case NUMERIC:
                result = buildNumeric(jsonNode, ph);
                break;

            case NUMERIC_RANGE:
                result = buildNumericRange(jsonNode, ph);
                break;

            case REF:
                result = buildRef(jsonNode, ph);
                break;

            case SET_BAND:
            case SET:
            case SET_SEASON:
                result = buildSet(jsonNode, ph);
                break;

            default:
        }
        return result;
    }

    private static PropertyString buildStringText(JsonNode jsonNode, PropertyString p) {
        p.setValue(jsonNode.get(PROPERTY_VALUE).asText());

        return p;
    }

    public static PropertyNumericRange buildNumericRange(JsonNode jsonNode, PropertyNumericRange pNumericRange) {
        JsonNode minNode = jsonNode.get(PROPERTY_MIN);
        JsonNode maxNode = jsonNode.get(PROPERTY_MAX);
        if ((minNode == null || minNode.isNull()) && (maxNode == null || maxNode.isNull())) {
            return null;
        }
        if (minNode != null && !minNode.isNull()) {
            pNumericRange.setMin(minNode.asDouble());
        }
        if (maxNode != null && !maxNode.isNull()) {
            pNumericRange.setMax(maxNode.asDouble());
        }
        return pNumericRange;
    }

    private static PropertyNumeric buildNumeric(JsonNode jsonNode, PropertyNumeric p) {
        p.setValue(jsonNode.get(PROPERTY_VALUE).asDouble());
        return p;
    }

    public static PropertyREF buildRef(JsonNode jsonNode, PropertyREF pRef, PropertyHelper propertyHelper) {
        String key = jsonNode.get(PROPERTY_VALUE).get(PROPERTY_ID).asInt() + "";
        if (propertyHelper.getPropertyHelperMaps().getProperties().containsKey(key)) {
            pRef.setProperty(propertyHelper.getPropertyHelperMaps().getProperties().get(key));
        } else {
            pRef.setPropertyPendingId(key);
            propertyHelper.getPropertyHelperMaps().getPendindProperties().add(pRef);

        }
        return pRef;
    }

    public static PropertySet buildSet(JsonNode jsonNode, PropertySet pSet, PropertyHelper propertyHelper) {
        JsonNode jValues = jsonNode.get(PROPERTY_VALUES);
        Iterator<JsonNode> jValuesIter = jValues.elements();
        while (jValuesIter.hasNext()) {
            BaseProperty baseProperty = propertyHelper.buildProperty(jValuesIter.next());
            pSet.getValues().add(baseProperty);
        }
        return pSet;
    }

    private static PropertyString buildStringText(JsonNode jsonNode, PropertyHelper propertyHelper) {
        PropertyString p = new PropertyString();
        PropertyHelperJson.initProperty(jsonNode, p, propertyHelper);
        p = PropertyHelperPropWithoutProps.buildStringText(jsonNode, p);
        JsonNode i18nNode = jsonNode.get(PROPERTY_I18N);
        if (i18nNode == null || i18nNode.isNull()) {
            return p;
        } else {
            Iterator<JsonNode> i18nNodesIter = i18nNode.elements();
            while (i18nNodesIter.hasNext()) {
                JsonNode n = i18nNodesIter.next();
                PropertyHelperPropWithoutProps.propertyWoPropertiesTranslate(n, i18nNode, p, propertyHelper);
            }
        }
        return p;
    }

    private static void propertyWoPropertiesTranslate(JsonNode n, JsonNode i18nNode,
        PropertyString p, PropertyHelper propertyHelper) {
        if (n != null && !i18nNode.isNull()) {
            JsonNode nLanguage = n.get(PROPERTY_I18N_LANGUAGE);
            JsonNode nValue = n.get(PROPERTY_VALUE);
            if (nLanguage != null && !nLanguage.isNull() && nValue != null && !nValue.isNull()) {
                Language l = propertyHelper.getPropertyHelperMaps().getAllLanguages().get(nLanguage.asText());
                p.translate(l.getCode(), nValue.asText());
            }
        }
    }

    private static PropertyNumeric buildNumeric(JsonNode jsonNode, PropertyHelper propertyHelper) {
        PropertyNumeric p = new PropertyNumeric();
        PropertyHelperJson.initProperty(jsonNode, p, propertyHelper);
        return PropertyHelperPropWithoutProps.buildNumeric(jsonNode, p);
    }

    private static PropertyNumericRange buildNumericRange(JsonNode jsonNode, PropertyHelper propertyHelper) {
        PropertyNumericRange pNumericRange = new PropertyNumericRange();
        PropertyHelperJson.initProperty(jsonNode, pNumericRange, propertyHelper);
        return PropertyHelperPropWithoutProps.buildNumericRange(jsonNode, pNumericRange);
    }

    private static PropertyREF buildRef(JsonNode jsonNode, PropertyHelper propertyHelper) {
        PropertyREF pRef = new PropertyREF();
        PropertyHelperJson.initProperty(jsonNode, pRef, propertyHelper);
        return PropertyHelperPropWithoutProps.buildRef(jsonNode, pRef, propertyHelper);
    }

    private static PropertySet buildSet(JsonNode jsonNode, PropertyHelper propertyHelper) {
        PropertySet pSet = new PropertySet();
        PropertyHelperJson.initProperty(jsonNode, pSet, propertyHelper);
        return PropertyHelperPropWithoutProps.buildSet(jsonNode, pSet, propertyHelper);
    }


}
