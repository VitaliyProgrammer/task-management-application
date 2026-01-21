package org.example.configuration.dropbox;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(DropBoxProperties.class)
public class DropBoxConfiguration {

    private final DropBoxProperties dropBoxProperties;

    @Bean
    public DbxClientV2 dbxClientV2() {

        DbxRequestConfig configuration =
                DbxRequestConfig.newBuilder("task-management-application")
                        .build();

        return new DbxClientV2(configuration, dropBoxProperties.accessToken());
    }
}
