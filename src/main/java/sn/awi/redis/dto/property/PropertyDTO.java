package sn.awi.redis.dto.property;

import lombok.Getter;
import lombok.Setter;

public class PropertyDTO {

    @Getter @Setter
    private int id;

    @Getter @Setter
    private int definitionId;

    @Getter @Setter
    private String type;

    @Getter @Setter
    private boolean valueAtOfferLevel;

}
