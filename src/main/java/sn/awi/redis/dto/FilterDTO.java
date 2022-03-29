package sn.awi.redis.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class FilterDTO {

    @Getter @Setter
    private String id;

    @Getter @Setter
    private String dataToLoopOn;

    @Getter @Setter
    private List<FilterDTO> subRules;

    @Getter @Setter
    private String criteria;

    @Getter @Setter
    private Long definitionId;

    @Getter @Setter
    private Double min;

    @Getter @Setter
    private Double max;

    @Getter @Setter
    private List<String> values;

}
