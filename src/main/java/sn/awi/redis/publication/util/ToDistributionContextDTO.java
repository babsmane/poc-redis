package sn.awi.redis.publication.util;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.allianz.emagin.eqs.engine.catalog.DistributionContext;

import sn.awi.redis.dto.DistributionContextListDTO;

@Component
public class ToDistributionContextDTO {

    @Autowired
    private ModelMapper modelMapper;
	
	public DistributionContextListDTO createDistributionContextListDTO(DistributionContext model){
		DistributionContextListDTO dto = modelMapper.map(model, DistributionContextListDTO.class);
		dto.setKey(model.getClients().get(0));
		return dto;
	}

}
