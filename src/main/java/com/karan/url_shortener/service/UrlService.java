package com.karan.url_shortener.service;


import com.karan.url_shortener.DTO.UrlRequest;
import com.karan.url_shortener.DTO.UrlResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UrlService {
    private final Map<String, String> urlStore = new HashMap<>();
    public UrlResponse shortenUrl(UrlRequest request) {
        String shortCode = generateShortCode();

        urlStore.put(shortCode, request.getOriginalUrl());

        String shortUrl = "http://localhost:8080/" + shortCode;
        return new UrlResponse(
                request.getOriginalUrl(),
                shortCode,
                shortUrl
        );
    }
    public String getOriginalUrl(String shortCode) {
        return urlStore.get(shortCode);
    }

    private String generateShortCode() {
        String chars = "abcdefghijklmnopqrstuvwxyz" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";
        Random random = new Random();
        StringBuilder shortCode = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(chars.length());
            shortCode.append(chars.charAt(index));
        }
        return shortCode.toString();
    }
}
