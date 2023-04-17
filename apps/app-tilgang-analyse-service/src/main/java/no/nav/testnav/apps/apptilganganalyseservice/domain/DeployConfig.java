package no.nav.testnav.apps.apptilganganalyseservice.domain;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.apptilganganalyseservice.repository.entity.DocumentEntity;

import java.util.regex.Pattern;


@Slf4j
public class DeployConfig {
    private final String content;

    public DeployConfig(DocumentEntity entity) {
        content = entity.getContent();
    }

    public String getCluster() {
        return findCluster(content);
    }

    public boolean isDeploying(ApplicationConfig applicationConfig) {
        return content.contains("resource: '" + applicationConfig.getPath() + "'");
    }

    private String findCluster(String value) {
        log.info("Prøver å utlede cluster fra: {}", value);
        var pattern = Pattern.compile("(cluster: ')([a-zA-Z\\-]+)(')", Pattern.MULTILINE);
        var matcher = pattern.matcher(value);
        if (matcher.find()) {
            return matcher.group(2);
        }
        throw new RuntimeException("Fant ikke cluster.");
    }

}
