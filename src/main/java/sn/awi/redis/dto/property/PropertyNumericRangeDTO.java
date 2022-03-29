package sn.awi.redis.dto.property;

import lombok.Getter;
import lombok.Setter;

public class PropertyNumericRangeDTO extends PropertyDTO {

    @Getter @Setter
    private String min;

    @Getter @Setter
    private String max;

}
