package sn.awi.redis.dto;

import lombok.Getter;
import lombok.Setter;

public class MetadataDTO {

    @Getter @Setter
    private String version;

    @Getter @Setter
    private int publicationId;

    @Getter @Setter
    private int dcxId;

    @Getter @Setter
    private String environment;

    @Getter @Setter
    private String subEnvironment;

    @Getter @Setter
    private String from;

}
