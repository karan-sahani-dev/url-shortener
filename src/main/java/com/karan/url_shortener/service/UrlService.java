package com.karan.url_shortener.service;

import com.karan.url_shortener.DTO.UrlRequest;
import com.karan.url_shortener.DTO.UrlResponse;
import com.karan.url_shortener.Model.UrlEntity;
import com.karan.url_shortener.repository.UrlRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class UrlService {

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public UrlResponse shortenUrl(UrlRequest request) {

        // Short code generate karo
        String shortCode = generateShortCode();

        // Short URL banao
        String shortUrl = "http://localhost:8080/" + shortCode;

        // Entity banao — DB mein save karne ke liye
        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setOriginalUrl(request.getOriginalUrl());
        urlEntity.setShortCode(shortCode);
        urlEntity.setShortUrl(shortUrl);
        urlEntity.setCreatedAt(LocalDateTime.now());
        urlEntity.setClickCount(0L);

        // DB mein save karo
        urlRepository.save(urlEntity);

        // Response return karo
        return new UrlResponse(
                request.getOriginalUrl(),
                shortCode,
                shortUrl
        );
    }

    public String getOriginalUrl(String shortCode) {

        // DB mein dhundho
        Optional<UrlEntity> urlEntity =
                urlRepository.findByShortCode(shortCode);

        // Mila toh original URL return karo
        // Nahi mila toh null return karo
        return urlEntity
                .map(UrlEntity::getOriginalUrl)
                .orElse(null);
    }

    private String generateShortCode() {
        String chars = "abcdefghijklmnopqrstuvwxyz"
                + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789";
        Random random = new Random();
        StringBuilder shortCode = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(chars.length());
            shortCode.append(chars.charAt(index));
        }
        return shortCode.toString();
    }
}