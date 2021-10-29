package no.nav.dolly;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.servletcore.util.VaultUtil;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.List;

@EnableAutoConfiguration
@Slf4j
public class DollyBackendApplicationStarter {
    private static final List<String> deployEnvironments = List.of("prod", "dev");

    public static void main(String[] args) {

        if (deployEnvironments.stream().anyMatch(env -> env.equals(System.getProperty("spring.profiles.active")))) {
            log.info("Starter lesing av Vault token ...");
            VaultUtil.initCloudVaultToken();
        }

        new SpringApplicationBuilder()
                .sources(ApplicationConfig.class)
                .run(args);
    }
}