package sn.awi.redis.catalog.loader;

import com.allianz.emagin.eqs.engine.catalog.businessrules.Filter;
import com.allianz.emagin.eqs.engine.catalog.pricing.CatalogDiscount;

import sn.awi.redis.dto.DiscountDTO;
import sn.awi.redis.dto.FilterDTO;

public class DcxDefinitionLoaderRule {

    private DcxDefinitionLoaderRule() {
        // unreachable
    }

    public static void createRule(DiscountDTO dDto, CatalogDiscount catalogDiscount) {
        if (dDto.getRules()==null || dDto.getRules().isEmpty()) {
            return;
        }
        for(FilterDTO fDto : dDto.getRules()) {
            if (fDto.getDataToLoopOn()==null || fDto.getDataToLoopOn().isEmpty()) {
                Filter rule = DcxDefinitionLoaderFilter.createFilter(fDto);
                catalogDiscount.rule(rule);
            } else if (fDto.getSubRules()!=null && !fDto.getSubRules().isEmpty()) {
                Filter rule = DcxDefinitionLoaderFilter.createFilter(fDto);
                for(FilterDTO subFDTO : fDto.getSubRules()) {
                    Filter subRule = DcxDefinitionLoaderFilter.createFilter(subFDTO);
                    rule.subFilter(subRule);
                }
                catalogDiscount.rule(rule);
            }
        }
    }

}
