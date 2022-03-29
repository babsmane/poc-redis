package sn.awi.redis.dto;

import lombok.Getter;
import lombok.Setter;

public class DisplayCurrencyDTO {

    @Getter @Setter
    private Long id;

    @Getter @Setter
    private String conversionRate;

}
