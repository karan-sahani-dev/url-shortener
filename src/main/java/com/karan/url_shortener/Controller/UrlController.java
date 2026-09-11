package com.karan.url_shortener.Controller;


import com.karan.url_shortener.DTO.UrlRequest;
import com.karan.url_shortener.DTO.UrlResponse;
import com.karan.url_shortener.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls")
public class UrlController {
    private final UrlService urlService;
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }
    @PostMapping
    public ResponseEntity<UrlResponse> shortenUrl(@Valid @RequestBody UrlRequest request) {
        UrlResponse response = urlService.shortenUrl(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<String> getOriginalUrl(@PathVariable String shortCode) {
        String originalUrl = urlService.getOriginalUrl(shortCode);
        if(originalUrl == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(originalUrl);
    }
}
