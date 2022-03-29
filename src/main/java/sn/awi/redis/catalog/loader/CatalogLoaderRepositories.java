package sn.awi.redis.catalog.loader;

import com.allianz.emagin.eqs.engine.catalog.DistributionContext;
import com.allianz.emagin.eqs.engine.catalog.repositories.Currency;
import com.allianz.emagin.eqs.engine.catalog.repositories.Language;

public class CatalogLoaderRepositories {

    private CatalogLoaderRepositories() {
        throw new AssertionError();
    }

    public static void setQuotationCurrency(Currency currency, DistributionContext dcx) {
        dcx.setQuotationCurrency(currency.getCode(), currency.getPrecision());
    }

    public static void setMainLanguage(Language language, DistributionContext dcx) {
        dcx.setMainLanguage(language.getCode());
    }

    public static void setDefaultLanguage(Language language, DistributionContext dcx) {
        dcx.setDefaultLanguage(language.getCode());
    }
}
