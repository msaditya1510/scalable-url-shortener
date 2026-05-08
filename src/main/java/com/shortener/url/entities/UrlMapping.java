package com.shortener.url.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "url_mappings")
public class UrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "short_code", unique = true, length = 10)
    private String shortCode;

    @Column(name = "long_url", nullable = false)
    private String longUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name="click_count")
    private long clickCount;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    
	public UrlMapping(long id, String shortCode, String longUrl, LocalDateTime createdAt,long clickCount) {
		super();
		this.id = id;
		this.shortCode = shortCode;
		this.longUrl = longUrl;
		this.createdAt = createdAt;
		this.clickCount=clickCount;
	}

	
	public UrlMapping() {
		super();
	}

	public UrlMapping(String longUrl) {
		super();
		this.longUrl=longUrl;
	}
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public String getLongUrl() {
		return longUrl;
	}

	public void setLongUrl(String longUrl) {
		this.longUrl = longUrl;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}


	public long getClickCount() {
		return clickCount;
	}


	public void setClickCount(long clickCount) {
		this.clickCount = clickCount;
	}
	

 
}
