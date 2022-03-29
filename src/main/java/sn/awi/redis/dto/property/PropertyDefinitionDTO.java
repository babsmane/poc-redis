package sn.awi.redis.dto.property;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import sn.awi.redis.dto.CommonDataBaseElementDTO;

public class PropertyDefinitionDTO extends CommonDataBaseElementDTO {

    @Getter @Setter
    private String description;

    @Getter @Setter
    private List<PropertyDefinitionDTO> children;

    @Getter @Setter
    private String folder;

    @Getter @Setter
    private String primitiveType;

    @Getter @Setter
    private int targetDefinitionId;

    @Getter @Setter
    private String targetRepositoryType;

    @Getter @Setter
    private boolean isKey;

    @Getter @Setter
    private int order;

}
