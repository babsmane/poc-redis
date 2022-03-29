package sn.awi.redis.catalog.loader;

import com.allianz.emagin.eqs.engine.catalog.businessrules.Filter;

import sn.awi.redis.dto.FilterDTO;

public class DcxDefinitionLoaderFilter {

    private DcxDefinitionLoaderFilter() {
        // unreachable
    }

    public static Filter createFilter(FilterDTO fDto) {
        Filter filter = new Filter();
        filter.setCriteria(fDto.getCriteria());
        filter.setMinValue(fDto.getMin());
        filter.setMaxValue(fDto.getMax());
        filter.setValues(fDto.getValues());
        filter.setDataToLoopOn(fDto.getDataToLoopOn());
        return filter;
    }
}
