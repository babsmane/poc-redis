package sn.awi.redis.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.allianz.emagin.eqs.engine.catalog.DistributionContext;

import sn.awi.redis.repository.DistributionContextRepository;
import sn.awi.redis.service.DistributionContextService;
import sn.awi.redis.service.TransformDcxService;
import sn.awi.redis.utils.ConfigManager;
import sn.awi.redis.utils.Constants;

@Service
public class DistributionContextServiceImpl implements DistributionContextService {

	@Autowired
	private ConfigManager configManager;
	
	@Autowired
	private DistributionContextRepository distributionContextRepository;

	@Autowired
	private TransformDcxService transformDcxService;

	@Override
	public DistributionContext getDistributionContextByKey(String key) {
		String redisPrefixKey = String.join(Constants.REDIS_KEYSPACE_SEP, configManager.getEnvironment(),
				configManager.getSubEnvironment(), configManager.getTargetApp(), configManager.getTargetTable());
    	String keyfinal = String.join(Constants.REDIS_KEYSPACE_SEP, redisPrefixKey, key).replace(":", "-");
		byte[] result = distributionContextRepository.findById(keyfinal);
		if(result != null){
			return transformDcxService.transformByteToDcx(result);
		}
		return null;
	}

	@Override
	public List<DistributionContext> getAllDistributionContext() {
		List<DistributionContext> dcxs = new ArrayList<>();
		List<byte[]> rawValues = distributionContextRepository.findAll(generateKeyPrefix());
		for(byte[] raw : rawValues){
			dcxs.add(transformDcxService.transformByteToDcx(raw));
		}
		if(!dcxs.isEmpty())
			return dcxs;
		return null;
	}
	
	private String generateKeyPrefix(){
		String redisPrefixKey = String.join(Constants.REDIS_KEYSPACE_SEP, configManager.getEnvironment(),
				configManager.getSubEnvironment(), configManager.getTargetApp(), configManager.getTargetTable());
	        return redisPrefixKey+"*";
	}

}
