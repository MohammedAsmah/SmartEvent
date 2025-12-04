package com.SmartEvent.SmartEvent.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tokens")
public class Token {

    @Id
    private String id;
    private String username;
    private String accessToken;
    private String refreshToken;
    private Long expirationTime;

    // Constructors
    public Token() {}

    public Token(String username, String accessToken, String refreshToken, Long expirationTime) {
        this.username = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expirationTime = expirationTime;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    public Long getExpirationTime() {
        return expirationTime;
    }
    public void setExpirationTime(Long expirationTime) {
        this.expirationTime = expirationTime;
    }
}
