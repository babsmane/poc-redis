package sn.awi.redis.dto;

import lombok.Getter;
import lombok.Setter;

public class DocumentDTO {

    @Getter @Setter
    private String url;

    @Getter @Setter
    private String language;

    @Getter @Setter
    private String type;

}
