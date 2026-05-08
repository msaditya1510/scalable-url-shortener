package com.shortener.url.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shortener.url.entities.UrlMapping;

@Repository
public interface UrlRepository extends JpaRepository<UrlMapping, Long> {

	Optional<UrlMapping> findByShortCode(String shortCode);

	@Modifying
	@Query("UPDATE UrlMapping u SET u.clickCount = u.clickCount + :count WHERE u.shortCode = :shortCode")
	void incrementClickCount(String shortCode, long count);

}
