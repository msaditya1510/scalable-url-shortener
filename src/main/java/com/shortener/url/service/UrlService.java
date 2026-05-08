package com.shortener.url.service;



import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.shortener.url.entities.UrlMapping;
import com.shortener.url.exception.UrlNotFoundException;
import com.shortener.url.repository.UrlRepository;
import com.shortener.url.util.Base62Encoder;


@Service
public class UrlService {

	private final UrlRepository urlRepo;
    private static final Logger log = LoggerFactory.getLogger(UrlService.class);
    
	private final RedisTemplate<String, String> redisTemplate;
	
	public UrlService(UrlRepository urlRepo, RedisTemplate<String, String> redisTemplate) {
		super();
		this.urlRepo = urlRepo;
		this.redisTemplate=redisTemplate;
	}

	public ResponseEntity<String> shortenUrl(String longUrl) {
		// step 1: save the longUrl to the  data base and generate the id
		UrlMapping savedEntity=urlRepo.save(new UrlMapping(longUrl));
		
		// step 2: use the id and encode the longUrl to get the shortCode
		String shortCode=Base62Encoder.encode(savedEntity.getId());
		
		//step 3: save the shortCode to the entity
		savedEntity.setShortCode(shortCode);
		
		// Step 4: save the entity
		urlRepo.save(savedEntity);
		
		// step 5: return shortCode
		return ResponseEntity.ok(shortCode);
	}

	public String getUrl(String shortCode) {
		String longUrl;
		
	    // 1. Check cache
	    String cached = redisTemplate.opsForValue().get(shortCode);
	    
	    if (cached != null) {
	    	longUrl=cached;
	    	
	    }
	    else {
	    // 2. Fallback to DB
		UrlMapping urlMapping=urlRepo.findByShortCode(shortCode).orElseThrow(
				()-> new UrlNotFoundException("No Mapping found for the short code: "+shortCode));
		
		log.info("Fetching from DB for shortCode: {}", shortCode);
		longUrl= urlMapping.getLongUrl();
		redisTemplate.opsForValue().set(shortCode, longUrl,Duration.ofHours(1));
	    }
	    
	    // increment the hit count
		redisTemplate.opsForValue().increment(shortCode+":count");
		
		return longUrl;
		
	}
	
	public long getClickCount(String shortCode) {

	    // 1. Get DB count
	    long dbCount = urlRepo.findByShortCode(shortCode)
	            .map(UrlMapping::getClickCount)
	            .orElse(0L);

	    // 2. Get Redis count
	    String redisValue = redisTemplate.opsForValue().get(shortCode + ":count");
	    long redisCount = redisValue == null ? 0 : Long.parseLong(redisValue);

	    return dbCount + redisCount;
	}

}
