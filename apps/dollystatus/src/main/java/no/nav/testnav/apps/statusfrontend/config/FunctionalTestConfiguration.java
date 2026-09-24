package no.nav.testnav.apps.statusfrontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties({
        FunctionalTestProperties.PdlFunctionalTestProperties.class,
        FunctionalTestProperties.PensjonFunctionalTestProperties.class,
        FunctionalTestProperties.ArbeidssoekerregisteretFunctionalTestProperties.class,
        FunctionalTestProperties.InstdataFunctionalTestProperties.class,
        FunctionalTestProperties.TpsMessagingFunctionalTestProperties.class,
        FunctionalTestProperties.ArenaFunctionalTestProperties.class,
        FunctionalTestProperties.KontoregisterFunctionalTestProperties.class,
        FunctionalTestProperties.KrrFunctionalTestProperties.class,
        FunctionalTestProperties.NomFunctionalTestProperties.class,
        FunctionalTestProperties.SkattekortFunctionalTestProperties.class,
        FunctionalTestProperties.SecondBatchTechnicalStatusProperties.class,
        FunctionalTestProperties.BrregstubFunctionalTestProperties.class,
        FunctionalTestProperties.InntektstubFunctionalTestProperties.class,
        FunctionalTestProperties.SkjermingsregisterFunctionalTestProperties.class,
        FunctionalTestProperties.UdiFunctionalTestProperties.class,
        FunctionalTestProperties.SigrunTechnicalStatusProperties.class,
        SlackProperties.class
})
public class FunctionalTestConfiguration {

    @Bean
    Clock functionalTestClock() {
        return Clock.systemUTC();
    }

    @Bean
    Scheduler functionalTestScheduler() {
        return Schedulers.newParallel("functional-test");
    }
}
