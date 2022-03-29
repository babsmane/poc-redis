package sn.awi.redis.repository;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class DistributionContextRepository {

	@Autowired
	private RedisTemplate<String, byte[]> redisTemplate;

	public void save(byte[] dcx, String key) {
		redisTemplate.opsForValue().set(key, dcx);
	}

	public byte[] findById(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<byte[]> findAll(String key) {
		Set keys = redisTemplate.keys(key);
		if (CollectionUtils.isEmpty(keys))
			return null;
		return redisTemplate.opsForValue().multiGet(keys);
	}

}
