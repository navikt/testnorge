package no.nav.identpool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.repository.postgres.IdentRepository;

@Configuration
@EnableAspectJAutoProxy
public class MetricsConfiguration {

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    public Counter batchNyeIdenterCounter(MeterRegistry registry) {
        return Counter.builder("identer.antall.opprettet").
                description("Antall identer som ble opprettet under batch kjøring").
                register(registry);
    }

    @Bean
    public Long totaltLedigeGauge(MeterRegistry registry, IdentRepository repository) {
        return registry.gauge("identer.antall.ledige", repository.countByRekvireringsstatusAndIdenttype(Rekvireringsstatus.LEDIG, Identtype.FNR));
    }
}
