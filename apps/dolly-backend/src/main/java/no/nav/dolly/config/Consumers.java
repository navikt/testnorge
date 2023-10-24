package no.nav.dolly.config;

import lombok.*;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static lombok.AccessLevel.PACKAGE;

/**
 * Samler alle placeholders for ulike {@code consumers.*}-konfigurasjon her, dvs. subklasser av {@code ServerProperties}.
 *
 * @see ServerProperties
 */
@NoArgsConstructor(access = PACKAGE)
public class Consumers {

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-aareg-proxy")
    public static class AaregProxy extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-amelding-service")
    public static class AmeldingService extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-arbeidsplassencv-proxy")
    public static class ArbeidsplassenProxy extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-arena-forvalteren-proxy")
    public static class ArenaforvalterProxy extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-brregstub-proxy")
    public static class BrregstubProxy extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-dokarkiv-proxy")
    public static class DokarkivProxyService extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-histark-proxy")
    public static class HistarkProxy extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-inntektsmelding-service")
    public static class InntektsmeldingService extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-inntektstub-proxy")
    public static class InntektstubProxy extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-inst-proxy")
    public static class InstProxy extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-kodeverk-proxy")
    public static class KodeverkProxy extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-kontoregister-person-proxy")
    public static class Kontoregister extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-krrstub-proxy")
    public static class KrrstubProxy extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-medl-proxy")
    public static class MedlProxy extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-norg2-proxy")
    public static class Norg2Proxy extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-organisasjon-forvalter")
    public static class OrganisasjonForvalter extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-organisasjon-service")
    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class OrganisasjonService extends ServerProperties {
        private Integer threads;
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-pdl-forvalter")
    public static class PdlDataForvalter extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-pdl-proxy")
    public static class PdlProxy extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-pensjon-testdata-facade-proxy")
    public static class PensjonforvalterProxy extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-person-service")
    public static class PersonService extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-sigrunstub-proxy")
    public static class SigrunstubProxy extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-skjermingsregister-proxy")
    public static class SkjermingsregisterProxy extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-sykemelding-api")
    public static class SykemeldingApi extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-synt-sykemelding-api")
    public static class SyntSykemeldingApi extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-tps-messaging-service")
    public static class TpsMessagingService extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-miljoer-service")
    public static class TpsMiljoer extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.testnav-udistub-proxy")
    public static class UdistubServer extends ServerProperties {
    }

}
