package com.BillardManagement.Service;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Lưu token hết hiệu lực tới khi nó tự hết hạn (exp). */
@Component
public class TokenBlacklist {

    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public void blacklist(String token, long expiresAtEpochSeconds) {
        blacklist.put(token, expiresAtEpochSeconds);
    }

    public boolean isBlacklisted(String token) {
        Long exp = blacklist.get(token);
        if (exp == null) return false;
        if (Instant.now().getEpochSecond() > exp) {
            blacklist.remove(token); // dọn rác
            return false;
        }
        return true;
    }
}