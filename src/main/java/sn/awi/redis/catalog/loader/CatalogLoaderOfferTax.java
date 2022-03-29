package sn.awi.redis.catalog.loader;

import com.allianz.emagin.eqs.engine.catalog.businessrules.Filter;
import com.allianz.emagin.eqs.engine.catalog.pricing.CatalogTax;

import sn.awi.redis.dto.FilterDTO;
import sn.awi.redis.dto.TaxDTO;
import sn.awi.redis.dto.TaxRateDTO;

public class CatalogLoaderOfferTax {

    private CatalogLoaderOfferTax() {}

    public static void loadTaxRate(TaxDTO tDto, CatalogTax catalogTax) {
        for (TaxRateDTO taxRateDTO : tDto.getRates()) {
            catalogTax.rate(
                    taxRateDTO.getTaxCode(),
                    taxRateDTO.getTaxName(),
                    taxRateDTO.getCountryCode(),
                    taxRateDTO.getTaxRate(),
                    taxRateDTO.getWeight()
            );
        }
    }

    public static void loadTaxFilter(TaxDTO tDto, CatalogTax catalogTax) {
        for(FilterDTO fDto : tDto.getFilters()) {
            if (fDto.getDataToLoopOn()==null || fDto.getDataToLoopOn().isEmpty()) {
                Filter rule = DcxDefinitionLoaderFilter.createFilter(fDto);
                catalogTax.filter(rule);
            } else if (fDto.getSubRules()!=null && !fDto.getSubRules().isEmpty()) {
                Filter rule = DcxDefinitionLoaderFilter.createFilter(fDto);
                for(FilterDTO subFDTO : fDto.getSubRules()) {
                    Filter subRule = DcxDefinitionLoaderFilter.createFilter(subFDTO);
                    rule.subFilter(subRule);
                }
                catalogTax.filter(rule);
            }
        }
    }


}
