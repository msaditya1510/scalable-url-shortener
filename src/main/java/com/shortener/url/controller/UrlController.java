package com.shortener.url.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shortener.url.service.UrlService;

@RestController
public class UrlController {

	private final UrlService urlService;
	
	
	public UrlController(UrlService urlService) {
		super();
		this.urlService = urlService;
	}

	@PostMapping("/shorten")
	public ResponseEntity<String> shortenUrl(@RequestParam(name = "url") String longUrl ){
		
		return urlService.shortenUrl(longUrl);
		
	}
	
	@GetMapping("/{shortCode}")
	public ResponseEntity<?> getUrl(@PathVariable String shortCode){
		String longUrl=urlService.getUrl(shortCode);
		return ResponseEntity.status(HttpStatus.FOUND)
		        .header(HttpHeaders.LOCATION, longUrl)
		        .build();
	}
	
	@GetMapping("/stats/{shortCode}")
	public ResponseEntity<Map<String, Object>> getStats(@PathVariable String shortCode) {

	    long count = urlService.getClickCount(shortCode);

	    return ResponseEntity.ok(Map.of(
	            "shortCode", shortCode,
	            "clicks", count
	    ));
	}
}
