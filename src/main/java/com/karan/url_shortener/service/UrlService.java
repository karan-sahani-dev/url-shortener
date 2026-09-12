package com.karan.url_shortener.service;

import com.karan.url_shortener.DTO.UrlRequest;

import com.karan.url_shortener.DTO.UrlResponse;
import com.karan.url_shortener.Model.UrlEntity;
import com.karan.url_shortener.exception.UrlNotFoundException;
import com.karan.url_shortener.repository.UrlRepository;
import org.springframework.stereotype.Repository;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.security.SecureRandom;
import java.time.LocalDateTime;


@Repository
public class UrlService {
    private  final UrlRepository urlRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";
    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

  //  @CachePut(value = "urls", key = "#result.shortCode")
    public UrlResponse shortenUrl(UrlRequest request) {
        String shortCode;
        do{
            shortCode = generateShortCode();
        }
        while(urlRepository.findByShortCode(shortCode).isPresent());

        String shortUrl = "http://localhost:8080/" + shortCode;
        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setOriginalUrl(request.getOriginalUrl());
        urlEntity.setShortCode(shortCode);
        urlEntity.setShortUrl(shortUrl);
        urlEntity.setCreatedAt(LocalDateTime.now());
        urlEntity.setClickCount(0L);

        urlRepository.save(urlEntity);

        return new UrlResponse(
                request.getOriginalUrl(),
                shortCode,
                shortUrl
        );
    }

    @Cacheable(value = "urls", key = "#shortCode")
    public String getOriginalUrl(String shortCode) {
        return urlRepository.findByShortCode(shortCode).map(UrlEntity::getOriginalUrl).orElseThrow(() ->
                new UrlNotFoundException(shortCode));
    }

    private String generateShortCode() {
        StringBuilder shortCode = new StringBuilder();
        for(int i=0; i<6; i++) {
            int index = secureRandom.nextInt(CHARS.length());
            shortCode.append(CHARS.charAt(index));
        }
        return shortCode.toString();
    }
}