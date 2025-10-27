package com.codewithmosh.store.services;


import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlackListService {

    // Stores blacklisted tokens along with their expiration time
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public void blacklistToken(String token, long expiryEpochSeconds) {
        blacklist.put(token, expiryEpochSeconds);
        System.out.println(blacklist);
    }

    public boolean isBlacklisted(String token) {
        Long expiry = blacklist.get(token);
        if (expiry == null) return false;

        // remove expired entries  // most imp part so , that map doesnot get full.
        if (Instant.now().getEpochSecond() > expiry) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }
}

