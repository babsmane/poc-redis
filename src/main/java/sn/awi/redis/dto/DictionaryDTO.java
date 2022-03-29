package sn.awi.redis.dto;

import lombok.Getter;
import lombok.Setter;

public class DictionaryDTO {

    @Getter @Setter
    private Long id;

    @Getter @Setter
    private String code;

    @Getter @Setter
    private String name;

}
