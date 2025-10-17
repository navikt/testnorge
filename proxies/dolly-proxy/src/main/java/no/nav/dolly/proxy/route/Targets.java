package no.nav.dolly.proxy.route;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static lombok.AccessLevel.PACKAGE;

@Configuration
@ConfigurationProperties(prefix = "targets")
@NoArgsConstructor(access = PACKAGE)
@Getter
@Setter
class Targets {

    String histark;
    String inntektstub;
    String udistub;

}
