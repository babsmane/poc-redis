package sn.awi.redis;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import sn.awi.redis.utils.ConfigManager;
import sn.awi.redis.utils.RedisCacheSubscriber;
import sn.awi.redis.utils.RedisDirectSerializer;

@Configuration
public class AppConfig {

    private static final CharSequence KEYSPACE_SEP = ":";

    @Autowired
    private ConfigManager configManager;

	@Autowired
	private RedisConnectionFactory redisConnectionFactory;

	@Autowired
	private RedisCacheSubscriber redisCacheSubscriber;

	public AppConfig(ObjectMapper objectMapper) {
		objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
	}

    /*@Bean
    public CacheManager cacheManager(List<Cache2kConfiguration<?, ?>> configuredCaches) {
        SpringCache2kCacheManager cacheManager = new SpringCache2kCacheManager();
        cacheManager.setCaches(configuredCaches);
        cacheManager.setAllowUnknownCache(false);
        return cacheManager;
    }*/

    @Bean
    @Primary
    public RedisTemplate<String, byte[]> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new RedisDirectSerializer());
        template.setEnableTransactionSupport(true);
        return template;
    }

    /*@Bean
    public Cache2kConfiguration<String, DistributionContext> distributionContextCacheBuilder(
        RedisProcessedGzipDistributionContextLoader cacheLoader,
            @Value("${emagin.quotation-engine-v3.storage.cache.size}") int cacheSize) {
        return Cache2kBuilder.of(String.class, DistributionContext.class)
                .name("DCX")
                .enableJmx(true)
                .loader(key -> cacheLoader.load(key))
                .permitNullValues(true)
                .entryCapacity(cacheSize)
                .toConfiguration();
    }*/

    @Bean
    public MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(this.redisCacheSubscriber);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListener(), topic());
        return container;
    }

    @Bean
    public PatternTopic topic() {
        String keySpaceString = "__keyspace*__";
        String keySpaceTopic = String.join(KEYSPACE_SEP, keySpaceString, configManager.getEnvironment(),
        		configManager.getSubEnvironment(), configManager.getTargetApp(), configManager.getTargetTable());
        return new PatternTopic(keySpaceTopic + ":*");
    }
    
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
