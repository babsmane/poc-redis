package sn.awi.redis.catalog.utils;

import com.allianz.emagin.eqs.engine.catalog.properties.PropertyAgeBand;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyBusinessParameterRef;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyGeoZone;
import com.allianz.emagin.eqs.engine.catalog.repositories.AgeBand;
import com.allianz.emagin.eqs.engine.catalog.repositories.Country;
import com.allianz.emagin.eqs.engine.catalog.repositories.DayOfWeek;
import com.allianz.emagin.eqs.engine.catalog.repositories.Folder;
import com.allianz.emagin.eqs.engine.catalog.repositories.Language;
import com.allianz.emagin.eqs.engine.catalog.repositories.Month;
import com.allianz.emagin.eqs.engine.catalog.repositories.PropertyDefinition;
import com.allianz.emagin.eqs.engine.catalog.repositories.RepositoryType;
import com.fasterxml.jackson.databind.JsonNode;

public class PropertyHelperRepository {

    private PropertyHelperRepository() {
        throw new AssertionError();
    }

    public static void setAgeBand(PropertyAgeBand ageBand, AgeBand ab) {
        if (ab != null) {
            ageBand.setCode(ab.getCode());
            ageBand.setName(ab.getName());
        }
    }

    public static void setCountry(PropertyGeoZone geoZone, Country country) {
        if (country != null) {
            geoZone.getCountries().add(country.getCode());
        }
    }

    public static void setCountry(PropertyBusinessParameterRef pRef, Country country) {
        if (country != null) {
            pRef.setCode(country.getCode());
            pRef.setValue(country.getName());
        }
    }

    public static void setLanguage(PropertyBusinessParameterRef pRef, Language language) {
        if (language != null) {
            pRef.setCode(language.getCode());
            pRef.setValue(language.getName());
        }
    }

    public static void setDayOfWeek(PropertyBusinessParameterRef pRef, DayOfWeek dayOfWeek) {
        if (dayOfWeek != null) {
            pRef.setCode(dayOfWeek.getCode());
            pRef.setValue(dayOfWeek.getName());
        }
    }

    public static void setMonth(PropertyBusinessParameterRef pRef, Month month) {
        if (month != null) {
            pRef.setCode(month.getCode());
            pRef.setValue(month.getName());
        }
    }

    public static void setFolder(PropertyDefinition propertyDefinition, JsonNode jsonNode) {
        propertyDefinition.setFolder(Folder.valueOf(jsonNode.get(PropertyHelper.PropertyDef.PROPERTY_DEF_FOLDER).asText()));
    }

    public static void setPropertyDefnitionTargetREpositoryType(PropertyDefinition propertyDefinition, JsonNode node) {
        propertyDefinition.setTargetRepositoryType(RepositoryType.valueOf(node.asText()));
    }
}
