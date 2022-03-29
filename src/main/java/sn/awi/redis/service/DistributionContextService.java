package sn.awi.redis.service;

import java.util.List;

import com.allianz.emagin.eqs.engine.catalog.DistributionContext;

public interface DistributionContextService {
	
	DistributionContext getDistributionContextByKey(String key);

	List<DistributionContext> getAllDistributionContext();

}
