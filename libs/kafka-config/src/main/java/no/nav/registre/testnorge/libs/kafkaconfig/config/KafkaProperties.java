package no.nav.registre.testnorge.libs.kafkaconfig.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class KafkaProperties {
    private final String bootstrapAddress;
    private final String groupId;
    private final String schemaregistryServers;
    private final String username;
    private final String password;
    private final String truststorePath;
    private final String credstorePassword;

    public KafkaProperties(
            @Value("${kafka.bootstrapservers}") String bootstrapAddress,
            @Value("${kafka.groupid}") String groupId,
            @Value("${kafka.schemaregistryservers}") String schemaregistryServers,
            @Value("${kafka.username}") String username,
            @Value("${kafka.password}") String password,
            @Value("${kafka.truststorepath}") String truststorePath,
            @Value("${kafka.credstorepassword}") String credstorePassword
    ) {
        this.bootstrapAddress = bootstrapAddress;
        this.groupId = groupId;
        this.schemaregistryServers = schemaregistryServers;
        this.username = username;
        this.password = password;
        this.truststorePath = truststorePath;
        this.credstorePassword = credstorePassword;
    }
}
