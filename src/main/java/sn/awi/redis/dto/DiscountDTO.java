package sn.awi.redis.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class DiscountDTO {

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String type;

    @Getter @Setter
    private Date startDate;

    @Getter @Setter
    private Date endDate;

    @Getter @Setter
    private String promotionCode;

    @Getter @Setter
    private List<FilterDTO> rules;

    @Getter @Setter
    private Boolean flat = Boolean.TRUE;

    @Getter @Setter
    private BigDecimal value;

    @Getter @Setter
    private List<Long> offers;

}
