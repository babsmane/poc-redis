package sn.awi.redis.service;

import com.allianz.emagin.eqs.engine.catalog.DistributionContext;

public interface TransformDcxService {

	byte[] transformByteFromDcx(DistributionContext distributionContext);

	DistributionContext transformByteToDcx(byte[] json);

	DistributionContext transformStringToDcx(String json);

}
