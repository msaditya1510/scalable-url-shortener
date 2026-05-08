package com.shortener.url;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.shortener.url.entities.UrlMapping;
import com.shortener.url.exception.UrlNotFoundException;
import com.shortener.url.repository.UrlRepository;
import com.shortener.url.service.UrlService;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

	@Mock
	private UrlRepository urlRepo;
	
	@Mock
	private RedisTemplate<String, String> redisTemplate;
	
	@Mock
	private ValueOperations<String, String> valueOperations; 
	// redisTemplate needs opsForValue object to perform get(), set() and increment() operations
	
	@InjectMocks
	private UrlService urlService;
	
	@Test
    /**
     * 
     */
    void getUrl_cacheHit_shouldReturnFromCache() {

        // Arrange
        String shortCode = "abc123";
        String longUrl = "https://google.com";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(shortCode)).thenReturn(longUrl);

        // Act
        String result = urlService.getUrl(shortCode);

        // Assert
        assertEquals(longUrl, result);

        // DB should NOT be called
        verify(urlRepo, never()).findByShortCode(any());

        // Counter should increment
        verify(valueOperations).increment(shortCode + ":count");
    }
	
	
	/**
	 * test for url not found case. 
	 */
	@Test
	void test_url_not_found() {
		
		// Arrange
		String shortCode="abd121";
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(shortCode)).thenReturn(null);
		
		// asserting the UrlNotFoundException must be thrown
		assertThrows(UrlNotFoundException.class,()->urlService.getUrl(shortCode));
		
		// verifying the urlRepo calls to be 1
		verify(urlRepo,times(1)).findByShortCode(shortCode);
		
		// looking for any increment in cache
		verify(valueOperations,never()).increment(shortCode+":count");
		
		// verifying the cache is not updated
		verify(valueOperations,never()).set(any(),any());;
		
	}
	
	@Test
	void stats_aggregation_test() {
		// arrange
		String shortCode="abe123";
		UrlMapping urlMapping=new UrlMapping(1,shortCode,"https://google.com",LocalDateTime.now(),10L);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(shortCode+":count")).thenReturn("5");
		when(urlRepo.findByShortCode(any())).thenReturn(Optional.of(urlMapping));
		
		// act and assert
		assertEquals(15L,urlService.getClickCount(shortCode));
	}
	
	

}
