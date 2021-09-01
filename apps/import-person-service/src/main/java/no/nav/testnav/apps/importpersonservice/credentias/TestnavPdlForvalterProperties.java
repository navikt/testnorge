package no.nav.testnav.apps.importpersonservice.credentias;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.reactivesecurity.domain.NaisServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-pdl-forvalter")
public class TestnavPdlForvalterProperties extends NaisServerProperties {
}