package no.nav.registre.testnorge.avhengighetsanalysefrontend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@Getter
@Setter
@Component
@ConfigurationProperties("dependencies")
public class DependenciesProperties {
    private Set<String> urls;
    private Integer threads;
}
