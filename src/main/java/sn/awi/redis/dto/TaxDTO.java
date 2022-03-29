package sn.awi.redis.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class TaxDTO {

    @Getter @Setter
    private String taxName;

    @Getter @Setter
    private List<FilterDTO> filters;

    @Getter @Setter
    private List<TaxRateDTO> rates;

}
