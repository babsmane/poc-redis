package sn.awi.redis.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
public class CategoryDTO {

    @Getter @Setter
    private Long id;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String title;

    @Getter @Setter
    private String description;

    @Getter @Setter
    private int priority;

    @Getter @Setter
    private Long highlightOffer;

    @Getter @Setter
    private List<Long> offers;

    @Getter @Setter
    private List<CategoryTranslateDTO> i18n;

}
