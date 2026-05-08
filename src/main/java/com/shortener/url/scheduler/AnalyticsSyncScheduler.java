package com.shortener.url.scheduler;

import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.shortener.url.repository.UrlRepository;

import jakarta.transaction.Transactional;

@Service
public class AnalyticsSyncScheduler {


    private final RedisTemplate<String, String> redisTemplate;

    private final UrlRepository repository;
    
    
    
    public AnalyticsSyncScheduler(RedisTemplate<String, String> redisTemplate, UrlRepository repository) {
		super();
		this.redisTemplate = redisTemplate;
		this.repository = repository;
	}



	@Scheduled(fixedDelay = 60000)
    @Transactional
    public void syncClicksToDb() {

        Set<String> keys = redisTemplate.keys("*:count");

        if (keys == null || keys.isEmpty()) {
            return;
        }

        for (String key : keys) {

            String shortCode = key.replace(":count", "");

            String value = redisTemplate.opsForValue().get(key);

            long count = value == null ? 0 : Long.parseLong(value);

            if (count > 0) {
                repository.incrementClickCount(shortCode, count);

                // reset after sync
                redisTemplate.delete(key);
            }
        }
	}
}
