package com.multimediareview.config;

public class JwtUserDetails {
    private Long userId;
    private String username;

    public JwtUserDetails(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
}
