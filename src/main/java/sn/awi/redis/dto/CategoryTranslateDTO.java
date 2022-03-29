package sn.awi.redis.dto;

import lombok.Getter;
import lombok.Setter;

public class CategoryTranslateDTO {

    @Getter @Setter
    private Long languageId;

    @Getter @Setter
    private String description;

    @Getter @Setter
    private String displayName;

}
