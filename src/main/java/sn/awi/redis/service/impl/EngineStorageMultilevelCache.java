package sn.awi.redis.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import com.allianz.emagin.eqs.engine.catalog.DistributionContext;

import sn.awi.redis.service.EngineStorage;
import sn.awi.redis.utils.Constants;

@ApplicationScope
@Component
@Primary
public class EngineStorageMultilevelCache implements EngineStorage {

    private Cache distributionContextCache;

    public EngineStorageMultilevelCache(CacheManager cacheManager) {
        this.distributionContextCache = cacheManager.getCache("DCX");
    }

    @Override
    public String generateKey(String partner, String market, String salesChannel, String touchPoint) {
        if (StringUtils.isNotBlank(partner) && StringUtils.isNotBlank(market)) {

            StringBuilder client = new StringBuilder(partner);
            client.append(Constants.REDIS_KEYSPACE_SEP);
            client.append(market);

            if (salesChannel != null && !salesChannel.isEmpty()) {
                client.append(Constants.REDIS_KEYSPACE_SEP);
                client.append(salesChannel);
            }

            if (touchPoint != null && !touchPoint.isEmpty()) {
                client.append(Constants.REDIS_KEYSPACE_SEP);
                client.append(touchPoint);
            }

            return client.toString();

        }
        return "Undefined";
    }

    @Override
    public DistributionContext getDcxByClient(String client) {
        if (StringUtils.isBlank(client)) {
            throw new IllegalArgumentException("client is null");
        }
        return distributionContextCache.get(client, DistributionContext.class);
    }

    @Override
    public void invalidateDcxClientCache(String client) {
        if (StringUtils.isBlank(client)) {
            throw new IllegalArgumentException("client is null");
        }
        distributionContextCache.evict(client);
        this.getDcxByClient(client);
    }

    @Override
    public void invalidateDcxCache() {
        distributionContextCache.clear();
    }

}
