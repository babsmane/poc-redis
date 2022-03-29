package sn.awi.redis.dto.property;

import lombok.Getter;
import lombok.Setter;

public class PropertyRefDTO extends PropertyDTO {

    @Getter @Setter
    private PropertyDTO value;

}
