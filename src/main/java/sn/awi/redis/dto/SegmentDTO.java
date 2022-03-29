package sn.awi.redis.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class SegmentDTO {

    @Getter @Setter
    private Long id;

    @Getter @Setter
    private String code;

    @Getter @Setter
    private String title;

    @Getter @Setter
    private int priority;

    @Getter @Setter
    private List<FilterDTO> filters;

    @Getter @Setter
    private List<DistributionVariantDTO> dvs;

}
