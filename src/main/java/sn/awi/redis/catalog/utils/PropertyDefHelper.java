package sn.awi.redis.catalog.utils;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allianz.emagin.eqs.engine.catalog.repositories.PropertyDefType;
import com.allianz.emagin.eqs.engine.catalog.repositories.PropertyDefinition;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.EqualsAndHashCode;
import sn.awi.redis.catalog.utils.PropertyHelper.PropertyDef;

@EqualsAndHashCode
public class PropertyDefHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyHelper.class);

    private final PropertyHelperMaps propertyHelperMaps;

    public PropertyDefHelper(final PropertyHelperMaps propertyHelperMaps) {
        this.propertyHelperMaps= propertyHelperMaps;
    }

    public PropertyDefinition buildPropertyDef(final JsonNode jsonNode) {
        final PropertyDefinition propertyDefinition = new PropertyDefinition(jsonNode.get(PropertyDef.PROPERTY_DEF_ID).asLong(),
            jsonNode.get(PropertyDef.PROPERTY_DEF_NAME).asText(), jsonNode.get(PropertyDef.PROPERTY_DEF_CODE).asText());

        PropertyHelperRepository.setFolder(propertyDefinition, jsonNode);
        final PropertyDefType propDef = PropertyDefType.valueOf(jsonNode.get(PropertyDef.PROPERTY_DEF_PRIMITIVE_TYPE).asText());
        LOGGER.debug(propertyDefinition.getId() + "- code - " + propertyDefinition.getCode());

        propertyDefinition.setPrimitiveType(propDef);

        propertyDefinition.setOrder(jsonNode.get(PropertyDef.PROPERTY_DEF_ORDER).asInt());

        this.buildPropertyDefIfKey(jsonNode.get(PropertyDef.PROPERTY_DEF_IS_KEY), propertyDefinition);
        this.buildPropertyDefIfTargetDefinition(jsonNode.get(PropertyDef.PROPERTY_DEF_TARGET_DEFINITION_ID), propertyDefinition);
        this.buildPropertyDefIfTargetRepository(jsonNode.get(PropertyDef.PROPERTY_DEF_TARGET_REPOSITORY_TYPE), propertyDefinition);
        // Coverage Management
        if (propertyDefinition.getCode().equals(PropertyDef.PROPERTY_DEF_CODE_COVERAGE)
            && propertyDefinition.getPrimitiveType() == PropertyDefType.COMPOSITE) {
            propertyDefinition.setPrimitiveType(PropertyDefType.COVERAGE);
        } else if (propertyDefinition.getCode().equals(PropertyDef.PROPERTY_DEF_CODE_PRODUCT_COVERAGE)
            && propertyDefinition.getPrimitiveType() == PropertyDefType.COMPOSITE) {
            propertyDefinition.setPrimitiveType(PropertyDefType.COVERAGE_OFFER);
        }
        this.buildPropertyDefIfChildren(jsonNode.get(PropertyDef.PROPERTY_DEF_CHILDREN), propertyDefinition);
        this.propertyHelperMaps.getPropertyDefs().put(propertyDefinition.getId() + "", propertyDefinition);

        return propertyDefinition;
    }

    private void buildPropertyDefIfChildren(final JsonNode node, final PropertyDefinition propertyDefinition) {
        final Iterator<JsonNode> elms = node.elements();
        while (elms.hasNext()) {
            final PropertyDefinition d = this.buildPropertyDef(elms.next());
            propertyDefinition.addChild(d);
        }
    }

    private void buildPropertyDefIfTargetRepository(final JsonNode node, final PropertyDefinition propertyDefinition) {
        if (node != null && !node.isNull()) {
            PropertyHelperRepository.setPropertyDefnitionTargetREpositoryType(propertyDefinition, node);
        }
    }

    private void buildPropertyDefIfTargetDefinition(final JsonNode node, final PropertyDefinition propertyDefinition) {
        if (node != null && !node.isNull() && this.propertyHelperMaps.getPropertyDefs().containsKey(node.asInt() + "")) {
            propertyDefinition.setTargetDefinition(this.propertyHelperMaps.getPropertyDefs().get(node.asInt() + ""));
        }
    }

    private void buildPropertyDefIfKey(final JsonNode node, final PropertyDefinition propertyDefinition) {
        if (node != null && !node.isNull()) {
            propertyDefinition.setKey(node.asBoolean());
        }
    }

}
