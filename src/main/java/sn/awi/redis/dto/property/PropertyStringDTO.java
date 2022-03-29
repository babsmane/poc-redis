package sn.awi.redis.dto.property;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
public class PropertyStringDTO extends PropertyDTO {

    @Getter @Setter
    private String value;

    @Getter @Setter
    private List<String> i18n;

}
