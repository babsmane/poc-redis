package sn.awi.redis.dto.property;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class PropertySetDTO extends PropertyDTO {

    @Getter @Setter
    private List<PropertyDTO> values;

}
