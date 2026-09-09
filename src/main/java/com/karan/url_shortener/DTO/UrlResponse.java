package com.karan.url_shortener.DTO;


import lombok.Data;

@Data
public class UrlResponse {
     private  String originalUrl;
     private String shortCode;
     private String shortUrl;
     public UrlResponse(String originalUrl, String shortCode, String shortUrl) {
         this.originalUrl = originalUrl;
         this.shortCode = shortCode;
         this.shortUrl = shortUrl;
     }
}
