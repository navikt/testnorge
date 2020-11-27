package no.nav.registre.testnorge.organisasjonmottak.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ApplicationKafkaProperties {
    private final String bootstrapAddress;
    private final String groupId;
    private final String schemaregistryServers;
    private final String username;
    private final String password;

    public ApplicationKafkaProperties(
            @Value("${kafka.bootstrapservers}") String bootstrapAddress,
            @Value("${kafka.groupid}") String groupId,
            @Value("${kafka.schemaregistryservers}") String schemaregistryServers,
            @Value("${SERVICEUSER_USERNAME}") String username,
            @Value("${SERVICEUSER_PASSWORD}") String password
    ) {
        this.bootstrapAddress = bootstrapAddress;
        this.groupId = groupId;
        this.schemaregistryServers = schemaregistryServers;
        this.username = username;
        this.password = password;
    }


}
