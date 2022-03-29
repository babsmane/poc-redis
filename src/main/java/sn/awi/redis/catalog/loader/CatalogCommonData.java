package sn.awi.redis.catalog.loader;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.allianz.emagin.eqs.engine.catalog.properties.BaseProperty;
import com.allianz.emagin.eqs.engine.catalog.repositories.AgeBand;
import com.allianz.emagin.eqs.engine.catalog.repositories.Country;
import com.allianz.emagin.eqs.engine.catalog.repositories.Currency;
import com.allianz.emagin.eqs.engine.catalog.repositories.DayOfWeek;
import com.allianz.emagin.eqs.engine.catalog.repositories.Language;
import com.allianz.emagin.eqs.engine.catalog.repositories.Month;
import com.allianz.emagin.eqs.engine.catalog.repositories.PropertyDefinition;
import com.allianz.emagin.eqs.engine.catalog.repositories.SalesChannel;
import com.allianz.emagin.eqs.engine.catalog.repositories.Touchpoint;
import com.fasterxml.jackson.databind.JsonNode;

import sn.awi.redis.catalog.utils.CatalogLoaderHelper;
import sn.awi.redis.catalog.utils.PropertyHelper;

public class CatalogCommonData {

    private static final String JSON_CONSTANT_COUNTRIES = "countries";
    private static final String JSON_CONSTANT_LANGUAGES = "languages";
    private static final String JSON_CONSTANT_SALES_CHANNELS = "salesChannels";
    private static final String JSON_CONSTANT_TOUCH_POINTS = "touchPoints";
    private static final String JSON_CONSTANT_DAYS = "days";
    private static final String JSON_CONSTANT_MONTHS = "months";
    private static final String JSON_CONSTANT_AGE_BANDS = "ageBands";
    private static final String JSON_CONSTANT_CURRENCIES = "currencies";
    private static final String JSON_CONSTANT_PROPERTY_DEFINITIONS = "propertyDefinitions";

    private Map<String, Country> allCountries;
    private Map<String, Language> allLanguages;
    private Map<String, Touchpoint> allTouchPoints;
    private Map<String, SalesChannel> allSalesChanels;
    private Map<String, DayOfWeek> allDays;
    private Map<String, Month> allMonths;
    private Map<String, AgeBand> allAgeBands;
    private Map<String, Currency> allCurrencies;
    private Map<String, PropertyDefinition> allPropertyDefinitions;
    private Map<String, BaseProperty> properties;
    private Map<String, String> propDefCodeId;

    private PropertyHelper propertyHelper = new PropertyHelper();

    public CatalogCommonData() {
        allCountries = new ConcurrentHashMap<String, Country>();
        allLanguages = new ConcurrentHashMap<String, Language>();
        allSalesChanels = new ConcurrentHashMap<String, SalesChannel>();
        allTouchPoints = new ConcurrentHashMap<String, Touchpoint>();
        allDays = new ConcurrentHashMap<String, DayOfWeek>();
        allMonths = new ConcurrentHashMap<String, Month>();
        allAgeBands = new ConcurrentHashMap<String, AgeBand>();
        allCurrencies = new ConcurrentHashMap<String, Currency>();
        allPropertyDefinitions = new ConcurrentHashMap<String, PropertyDefinition>();
        properties = new ConcurrentHashMap<String, BaseProperty>();
        propertyHelper.getPropertyHelperMaps().setAllCountries(allCountries);
        propertyHelper.getPropertyHelperMaps().setProperties(properties);
        propertyHelper.getPropertyHelperMaps().setPropertyDefs(allPropertyDefinitions);
        propertyHelper.getPropertyHelperMaps().setAllAgeBands(allAgeBands);
        propertyHelper.getPropertyHelperMaps().setAllLanguages(allLanguages);
        propertyHelper.getPropertyHelperMaps().setAllDays(allDays);
        propertyHelper.getPropertyHelperMaps().setAllMonths(allMonths);
    }

    public void clear() {
        this.allCountries.clear();
        this.allLanguages.clear();
        this.allSalesChanels.clear();
        this.allTouchPoints.clear();
        this.allDays.clear();
        this.allMonths.clear();
        this.allAgeBands.clear();
        this.allCurrencies.clear();
        this.allPropertyDefinitions.clear();
        this.properties.clear();
        
    }

    public Map<String, String> getPropDefCodeId() {
        return propDefCodeId;
    }

    public void load(JsonNode actualObj) {
        propDefCodeId = new ConcurrentHashMap<>();
        // countries
        JsonNode jsonNodeCountry = actualObj.get(JSON_CONSTANT_COUNTRIES);
        Iterator<JsonNode> elms = jsonNodeCountry.elements();
        while (elms.hasNext()) {
            Country d = CatalogLoaderHelper.buildCountry(elms.next());
            this.getAllCountries().put(d.getId() + "", d);
        }
        // languages
        JsonNode jsonNodeLanguage = actualObj.get(JSON_CONSTANT_LANGUAGES);
        elms = jsonNodeLanguage.elements();
        while (elms.hasNext()) {
            Language d = CatalogLoaderHelper.buildLanguage(elms.next());
            this.getAllLanguages().put(d.getId() + "", d);
        }
        // salesChannels
        JsonNode jsonNodeSalesChannel = actualObj.get(JSON_CONSTANT_SALES_CHANNELS);
        elms = jsonNodeSalesChannel.elements();
        while (elms.hasNext()) {
            SalesChannel d = CatalogLoaderHelper.buildSalesChannel(elms.next());
            this.getAllSalesChanels().put(d.getId() + "", d);
        }

        // touchPoints
        JsonNode jsonNodeToucPoint = actualObj.get(JSON_CONSTANT_TOUCH_POINTS);
        elms = jsonNodeToucPoint.elements();
        while (elms.hasNext()) {
            Touchpoint d = CatalogLoaderHelper.buildTouchpoint(elms.next());
            this.getAllTouchPoints().put(d.getId() + "", d);
        }
        // days
        JsonNode jsonNodeDay = actualObj.get(JSON_CONSTANT_DAYS);
        elms = jsonNodeDay.elements();
        while (elms.hasNext()) {
            DayOfWeek d = CatalogLoaderHelper.buildDayofWeek(elms.next());
            this.getAllDays().put(d.getId() + "", d);
        }
        // months
        JsonNode jsonNodeMonth = actualObj.get(JSON_CONSTANT_MONTHS);
        elms = jsonNodeMonth.elements();
        while (elms.hasNext()) {
            Month d = CatalogLoaderHelper.buildMonth(elms.next());
            this.getAllMonths().put(d.getId() + "", d);
        }
        // ageBands
        JsonNode jsonNodeAgeBand = actualObj.get(JSON_CONSTANT_AGE_BANDS);
        elms = jsonNodeAgeBand.elements();
        while (elms.hasNext()) {
            AgeBand d = CatalogLoaderHelper.buildAgeBand(elms.next());
            this.getAllAgeBands().put(d.getId() + "", d);
        }
        // currencies
        JsonNode jsonNodeCurrency = actualObj.get(JSON_CONSTANT_CURRENCIES);
        elms = jsonNodeCurrency.elements();
        while (elms.hasNext()) {
            Currency d = CatalogLoaderHelper.buildCurrency(elms.next());
            this.getAllCurrencies().put(d.getId() + "", d);
        }

        // propertyDefinitions
        JsonNode jsonNodePropertyDefs = actualObj.get(JSON_CONSTANT_PROPERTY_DEFINITIONS);
        elms = jsonNodePropertyDefs.elements();
        while (elms.hasNext()) {
            PropertyDefinition d = this.getPropertyHelper().getPropertyDefHelper().buildPropertyDef(elms.next());
            this.getAllPropertyDefinitions().put(d.getId() + "", d);
            propDefCodeId.put(d.getCode(), d.getId() + "");
        }

    }

    public PropertyHelper getPropertyHelper() {
        return propertyHelper;
    }

    public Map<String, Language> getAllLanguages() {
        return allLanguages;
    }

    public void setAllLanguages(Map<String, Language> allLanguages) {
        this.allLanguages = allLanguages;
    }

    public Map<String, Currency> getAllCurrencies() {
        return allCurrencies;
    }

    public Map<String, Touchpoint> getAllTouchPoints() {
        return allTouchPoints;
    }

    public void setAllTouchPoints(Map<String, Touchpoint> allTouchPoints) {
        this.allTouchPoints = allTouchPoints;
    }

    public Map<String, SalesChannel> getAllSalesChanels() {
        return allSalesChanels;
    }

    public void setAllSalesChanels(Map<String, SalesChannel> allSalesChanels) {
        this.allSalesChanels = allSalesChanels;
    }

    public Map<String, DayOfWeek> getAllDays() {
        return allDays;
    }

    public void setAllDays(Map<String, DayOfWeek> allDays) {
        this.allDays = allDays;
    }

    public Map<String, Month> getAllMonths() {
        return allMonths;
    }

    public void setAllMonths(Map<String, Month> allMonths) {
        this.allMonths = allMonths;
    }

    public Map<String, AgeBand> getAllAgeBands() {
        return allAgeBands;
    }

    public void setAllAgeBands(Map<String, AgeBand> allAgeBands) {
        this.allAgeBands = allAgeBands;
    }

    public Map<String, PropertyDefinition> getAllPropertyDefinitions() {
        return allPropertyDefinitions;
    }

    public void setAllPropertyDefinitions(Map<String, PropertyDefinition> allPropertyDefinitions) {
        this.allPropertyDefinitions = allPropertyDefinitions;
    }

    public Map<String, Country> getAllCountries() {
        return allCountries;
    }

    public void setAllCountries(Map<String, Country> allCountries) {
        this.allCountries = allCountries;
    }
}
