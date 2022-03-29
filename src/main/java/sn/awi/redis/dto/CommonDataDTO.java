package sn.awi.redis.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import sn.awi.redis.dto.property.PropertyDefinitionDTO;

public class CommonDataDTO {

    @Getter @Setter
    private List<CommonDataBaseElementDTO> countries;

    @Getter @Setter
    private List<CommonDataBaseElementDTO> languages;

    @Getter @Setter
    private List<CommonDataBaseElementDTO> salesChannels;

    @Getter @Setter
    private List<CommonDataBaseElementDTO> touchPoints;

    @Getter @Setter
    private List<CommonDataBaseElementDTO> days;

    @Getter @Setter
    private List<CommonDataBaseElementDTO> months;

    @Getter @Setter
    private List<CommonDataBaseElementDTO> ageBands;

    @Getter @Setter
    private List<CurrencyDTO> currencies;

    @Getter @Setter
    private List<PropertyDefinitionDTO> propertyDefinitions;

}
