package com.karan.url_shortener.Model;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;


@Entity
@Table(name = "url_entity")
@Data
public class UrlEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_url" , nullable = false)
    private String originalUrl;

    @Column(name = "short_code",unique = true,nullable = false)
    private String shortCode;

    @Column(name = "short_url")
    private String shortUrl;

    @Column(name = "created_At")
    private LocalDateTime createdAt;

    @Column(name = "click_count")
    private Long clickCount = 0L;
}
