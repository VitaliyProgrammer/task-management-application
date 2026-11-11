package org.example.infrastructure.health;

import com.dropbox.core.v2.DbxClientV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Adds "dropbox" to /actuator/health: the cheapest authenticated Dropbox call proves that the
 * credentials (refresh token, app key and secret) still work. The uptime monitor reads the
 * overall status, so a broken Dropbox integration is reported by email like any outage.
 */
@Slf4j
@Component("dropbox")
@RequiredArgsConstructor
public class DropBoxHealthIndicator implements HealthIndicator {

    private final DbxClientV2 dbxClient;

    @Override
    public Health health() {

        try {
            dbxClient.users().getCurrentAccount();

            return Health.up().build();
        } catch (Exception exception) {
            // Health details are hidden from the public endpoint, so the reason goes to the log
            log.warn("Dropbox health check failed: {}", exception.getMessage());

            return Health.down().withDetail("error", exception.getClass().getSimpleName()).build();
        }
    }
}
