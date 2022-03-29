package sn.awi.redis.dto;

import com.allianz.emagin.eqs.engine.catalog.DistributionContext;
import com.fasterxml.jackson.annotation.JsonFilter;

import lombok.Getter;
import lombok.Setter;

@JsonFilter("myFilter")
@Setter
@Getter
public class DistributionContextListDTO extends DistributionContext{

	private String key;

}
