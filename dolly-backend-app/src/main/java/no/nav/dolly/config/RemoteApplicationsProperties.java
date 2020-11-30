package no.nav.dolly.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties
public class RemoteApplicationsProperties {
    private Map<String, String> applications;

    public Map<String, String> getApplications() {
        return applications;
    }

    public String get(String key) {
        return applications.get(key);
    }

    public void setApplications(Map<String, String> applications) {
        this.applications = applications;
    }
}
