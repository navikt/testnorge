package no.nav.testnav.apps.statusfrontend.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

public final class FunctionalTestProperties {

    private FunctionalTestProperties() {
    }

    @Getter
    @Setter
    public abstract static class RequestTimeoutProperties {

        private Duration requestTimeout = Duration.ofSeconds(30);
    }

    @Getter
    @Setter
    public abstract static class PollingProperties extends RequestTimeoutProperties {

        private Duration pollInterval = Duration.ofSeconds(2);
        private Duration pollTimeout = Duration.ofMinutes(2);
    }

    @Validated
    @ConfigurationProperties(prefix = "functional-test.pdl")
    @Getter
    @Setter
    public static final class PdlFunctionalTestProperties extends PollingProperties {

        @NotBlank
        private String ident;
    }

    @ConfigurationProperties(prefix = "functional-test.pensjon")
    public static final class PensjonFunctionalTestProperties extends PollingProperties {
    }

    @ConfigurationProperties(prefix = "functional-test.arbeidssoekerregisteret")
    public static final class ArbeidssoekerregisteretFunctionalTestProperties extends PollingProperties {
    }

    @ConfigurationProperties(prefix = "functional-test.instdata")
    public static final class InstdataFunctionalTestProperties extends PollingProperties {
    }

    @ConfigurationProperties(prefix = "functional-test.tps-messaging-egenansatt")
    public static final class TpsMessagingFunctionalTestProperties extends PollingProperties {
    }

    @ConfigurationProperties(prefix = "functional-test.arena")
    public static final class ArenaFunctionalTestProperties extends PollingProperties {
    }

    @ConfigurationProperties(prefix = "functional-test.kontoregister")
    public static final class KontoregisterFunctionalTestProperties extends PollingProperties {
    }

    @ConfigurationProperties(prefix = "functional-test.krr")
    public static final class KrrFunctionalTestProperties extends PollingProperties {
    }

    @ConfigurationProperties(prefix = "functional-test.nom")
    public static final class NomFunctionalTestProperties extends PollingProperties {
    }

    @ConfigurationProperties(prefix = "functional-test.skattekort")
    public static final class SkattekortFunctionalTestProperties extends PollingProperties {
    }

    @ConfigurationProperties(prefix = "functional-test.brregstub")
    public static final class BrregstubFunctionalTestProperties extends PollingProperties {
    }

    @ConfigurationProperties(prefix = "functional-test.inntektstub")
    public static final class InntektstubFunctionalTestProperties extends PollingProperties {
    }

    @ConfigurationProperties(prefix = "functional-test.skjermingsregister")
    public static final class SkjermingsregisterFunctionalTestProperties extends PollingProperties {
    }

    @ConfigurationProperties(prefix = "functional-test.udi")
    public static final class UdiFunctionalTestProperties extends PollingProperties {
    }

    @ConfigurationProperties(prefix = "functional-test.sigrun")
    public static final class SigrunTechnicalStatusProperties extends RequestTimeoutProperties {
    }

    @ConfigurationProperties(prefix = "functional-test.technical-status")
    public static final class SecondBatchTechnicalStatusProperties extends RequestTimeoutProperties {
    }
}
