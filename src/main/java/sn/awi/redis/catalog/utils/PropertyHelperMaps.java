package sn.awi.redis.catalog.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allianz.emagin.eqs.engine.catalog.properties.BaseProperty;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyREF;
import com.allianz.emagin.eqs.engine.catalog.properties.RelationShipStatus;
import com.allianz.emagin.eqs.engine.catalog.properties.rss.RelationShipRule;
import com.allianz.emagin.eqs.engine.catalog.repositories.AgeBand;
import com.allianz.emagin.eqs.engine.catalog.repositories.Country;
import com.allianz.emagin.eqs.engine.catalog.repositories.DayOfWeek;
import com.allianz.emagin.eqs.engine.catalog.repositories.Language;
import com.allianz.emagin.eqs.engine.catalog.repositories.Month;
import com.allianz.emagin.eqs.engine.catalog.repositories.PropertyDefinition;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class PropertyHelperMaps {

    private Map<String, BaseProperty> properties= new HashMap<>();
    private Map<String, PropertyDefinition> propertyDefs= new HashMap<>();
    private Map<String, Country> allCountries= new HashMap<>();
    private Map<String, Language> allLanguages= new HashMap<>();
    private Map<String, DayOfWeek> allDays= new HashMap<>();
    private Map<String, Month> allMonths= new HashMap<>();
    private Map<String, AgeBand> allAgeBands= new HashMap<>();
    private List<PropertyREF> pendindProperties = new ArrayList<>();
    private List<RelationShipStatus> pendindRSStatus = new ArrayList<>();
    private List<RelationShipRule> pendindRSRule = new ArrayList<>();
    
    public Map<String, BaseProperty> getProperties() {
        return this.properties;
    }

    public void setProperties(final Map<String, BaseProperty> properties) {
        this.properties = properties;
    }

    public Map<String, PropertyDefinition> getPropertyDefs() {
        return this.propertyDefs;
    }

    public void setPropertyDefs(final Map<String, PropertyDefinition> propertyDefs) {
        this.propertyDefs = propertyDefs;
    }

    public Map<String, Country> getAllCountries() {
        return this.allCountries;
    }

    public void setAllCountries(final Map<String, Country> allCountries) {
        this.allCountries = allCountries;
    }

    public Map<String, AgeBand> getAllAgeBands() {
        return this.allAgeBands;
    }

    public void setAllAgeBands(final Map<String, AgeBand> allAgeBands) {
        this.allAgeBands = allAgeBands;
    }

    public Map<String, Language> getAllLanguages() {
        return this.allLanguages;
    }

    public void setAllLanguages(final Map<String, Language> allLanguages) {
        this.allLanguages = allLanguages;
    }

    public Map<String, DayOfWeek> getAllDays() {
        return this.allDays;
    }

    public void setAllDays(final Map<String, DayOfWeek> allDays) {
        this.allDays = allDays;
    }

    public Map<String, Month> getAllMonths() {
        return this.allMonths;
    }

    public void setAllMonths(final Map<String, Month> allMonths) {
        this.allMonths = allMonths;
    }

    public List<PropertyREF> getPendindProperties() {
        return this.pendindProperties;
    }

    public void setPendindProperties(final List<PropertyREF> pendindProperties) {
        this.pendindProperties = pendindProperties;
    }

    public List<RelationShipRule> getPendindRSRule() {
        return this.pendindRSRule;
    }

    public void setPendindRSRule(final List<RelationShipRule> pendindRSRule) {
        this.pendindRSRule = pendindRSRule;
    }

    public List<RelationShipStatus> getPendindRSStatus() {
        return this.pendindRSStatus;
    }

    public void setPendindRSStatus(final List<RelationShipStatus> pendindRSStatus) {
        this.pendindRSStatus = pendindRSStatus;
    }

}
