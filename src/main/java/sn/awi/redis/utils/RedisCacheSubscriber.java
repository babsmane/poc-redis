package sn.awi.redis.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import sn.awi.redis.service.EngineStorage;

@Component
public class RedisCacheSubscriber implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheSubscriber.class);

    @Autowired
    private ConfigManager configManager;

    @Autowired
    private EngineStorage engineStorage;

    public void onMessage(Message message, byte[] pattern) {
        String channelString = new String(message.getChannel());
        LOGGER.debug("channelString: " + channelString);
        String keyRedisPrefix = String.join(Constants.REDIS_KEYSPACE_SEP, configManager.getEnvironment(),
        		configManager.getSubEnvironment(), configManager.getTargetApp(), configManager.getTargetTable());
        keyRedisPrefix = keyRedisPrefix + Constants.REDIS_KEYSPACE_SEP;
        String clientDcx = channelString.substring(channelString.lastIndexOf(keyRedisPrefix) + keyRedisPrefix.length());
        // cache uses -
        LOGGER.debug("DCX: " + clientDcx);
        engineStorage.invalidateDcxClientCache(clientDcx);
     }

}