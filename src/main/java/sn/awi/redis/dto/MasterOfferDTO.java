package sn.awi.redis.dto;

import lombok.Getter;
import lombok.Setter;

public class MasterOfferDTO {

    @Getter @Setter
    private String id;

    @Getter @Setter
    private String code;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String parentId;

    @Getter @Setter
    private boolean activated;

}
