package no.nav.testnav.identpool.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import no.nav.testnav.identpool.repository.IdentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import reactor.core.publisher.Mono;

import java.util.Objects;

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
    public Mono<Long> totaltLedigeGauge(MeterRegistry registry, IdentRepository repository) {

        return Mono.just(0L);
//        return repository.countByRekvireringsstatusAndIdenttype(Rekvireringsstatus.LEDIG, Identtype.FNR)
//                .mapNotNull(antall -> registry.gauge("identer.antall.ledige", antall));
    }
}
