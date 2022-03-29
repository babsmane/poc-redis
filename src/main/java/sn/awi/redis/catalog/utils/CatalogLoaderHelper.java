package sn.awi.redis.catalog.utils;

import com.allianz.emagin.eqs.engine.catalog.repositories.AgeBand;
import com.allianz.emagin.eqs.engine.catalog.repositories.Country;
import com.allianz.emagin.eqs.engine.catalog.repositories.Currency;
import com.allianz.emagin.eqs.engine.catalog.repositories.DayOfWeek;
import com.allianz.emagin.eqs.engine.catalog.repositories.Language;
import com.allianz.emagin.eqs.engine.catalog.repositories.Month;
import com.allianz.emagin.eqs.engine.catalog.repositories.SalesChannel;
import com.allianz.emagin.eqs.engine.catalog.repositories.Touchpoint;
import com.fasterxml.jackson.databind.JsonNode;

public class CatalogLoaderHelper {

    private static final String JSON_CONSTANT_ID = "id";
    private static final String JSON_CONSTANT_CODE = "code";
    private static final String JSON_CONSTANT_NAME = "name";
    private static final String JSON_CONSTANT_PRECISION = "precision";

    private CatalogLoaderHelper() {
        throw new AssertionError();
    }

    public static AgeBand buildAgeBand(JsonNode jsonNode) {
        return new AgeBand(jsonNode.get(JSON_CONSTANT_ID).asLong(), jsonNode.get(JSON_CONSTANT_NAME).asText(),
            jsonNode.get(JSON_CONSTANT_CODE).asText());
    }

    public static Language buildLanguage(JsonNode jsonNode) {
        return new Language(jsonNode.get(JSON_CONSTANT_ID).asLong(), jsonNode.get(JSON_CONSTANT_NAME).asText(),
            jsonNode.get(JSON_CONSTANT_CODE).asText());
    }

    public static Country buildCountry(JsonNode jsonNode) {
        return new Country(jsonNode.get(JSON_CONSTANT_ID).asLong(), jsonNode.get(JSON_CONSTANT_NAME).asText(),
            jsonNode.get(JSON_CONSTANT_CODE).asText());
    }

    public static SalesChannel buildSalesChannel(JsonNode jsonNode) {
        return new SalesChannel(jsonNode.get(JSON_CONSTANT_ID).asLong(), jsonNode.get(JSON_CONSTANT_NAME).asText(),
            jsonNode.get(JSON_CONSTANT_CODE).asText());
    }

    public static Touchpoint buildTouchpoint(JsonNode jsonNode) {
        return new Touchpoint(jsonNode.get(JSON_CONSTANT_ID).asLong(), jsonNode.get(JSON_CONSTANT_NAME).asText(),
            jsonNode.get(JSON_CONSTANT_CODE).asText());
    }

    public static DayOfWeek buildDayofWeek(JsonNode jsonNode) {
        return new DayOfWeek(jsonNode.get(JSON_CONSTANT_ID).asLong(), jsonNode.get(JSON_CONSTANT_NAME).asText(),
            jsonNode.get(JSON_CONSTANT_CODE).asText());
    }

    public static Month buildMonth(JsonNode jsonNode) {
        return new Month(jsonNode.get(JSON_CONSTANT_ID).asLong(), jsonNode.get(JSON_CONSTANT_NAME).asText(),
            jsonNode.get(JSON_CONSTANT_CODE).asText());
    }

    public static Currency buildCurrency(JsonNode jsonNode) {
        return new Currency(jsonNode.get(JSON_CONSTANT_ID).asLong(), jsonNode.get(JSON_CONSTANT_NAME).asText(),
            jsonNode.get(JSON_CONSTANT_CODE).asText(), (byte) jsonNode.get(JSON_CONSTANT_PRECISION).asInt());
    }
}
