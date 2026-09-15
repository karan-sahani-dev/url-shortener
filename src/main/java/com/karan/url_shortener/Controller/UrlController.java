package com.karan.url_shortener.Controller;

import com.karan.url_shortener.DTO.UrlRequest;
import com.karan.url_shortener.DTO.UrlResponse;
import com.karan.url_shortener.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/urls")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> shortenUrl(
            @Valid @RequestBody UrlRequest request
    ) {
        UrlResponse response = urlService.shortenUrl(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

//    @GetMapping("/{shortCode}")
//    public ResponseEntity<Void> redirectToOriginalUrl(
//            @PathVariable String shortCode
//    ) {
//        String originalUrl = urlService.getOriginalUrl(shortCode);
//
//        return ResponseEntity
//                .status(HttpStatus.FOUND)
//                .location(URI.create(originalUrl))
//                .build();
//    }
}