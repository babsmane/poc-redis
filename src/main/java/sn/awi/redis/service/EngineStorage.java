package sn.awi.redis.service;

import com.allianz.emagin.eqs.engine.catalog.DistributionContext;

public interface EngineStorage {

    String generateKey(String partner, String market, String salesChannel, String touchPoint);

    DistributionContext getDcxByClient(String client);

    void invalidateDcxClientCache(String client);

    void invalidateDcxCache();
}
