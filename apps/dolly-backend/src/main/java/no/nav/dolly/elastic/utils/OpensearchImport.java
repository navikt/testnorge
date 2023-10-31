package no.nav.dolly.elastic.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.elastic.ElasticBestilling;
import no.nav.dolly.repository.BestillingRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

@Slf4j
@Profile("!test")
@Component
@RequiredArgsConstructor
public class OpensearchImport implements ApplicationListener<ContextRefreshedEvent> {

    private final BestillingRepository bestillingRepository;
    private final BestillingElasticRepository bestillingElasticRepository;
    private final MapperFacade mapperFacade;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        var start = System.currentTimeMillis();
        var antallLest = new AtomicInteger(0);
        var antallSkrevet = new AtomicInteger(0);
        log.info("OpenSearch database oppdatering starter ...");

        importAll(antallLest, antallSkrevet);

        log.info("OpenSearch database oppdatering ferdig; antall lest {}, antall skrevet {}, medgått tid {} ms",
                antallLest.get(),
                antallSkrevet.get(),
                System.currentTimeMillis() - start);
    }

    private void importAll(AtomicInteger antallLest, AtomicInteger antallSkrevet) {

        StreamSupport.stream(bestillingRepository.findAll().spliterator(), false)
                .peek(bestilling -> antallLest.incrementAndGet())
                .filter(bestilling -> hasNotBestilling(bestilling.getId()))
                .map(bestilling -> mapperFacade.map(bestilling, ElasticBestilling.class))
                .peek(bestilling -> antallSkrevet.incrementAndGet())
                .forEach(bestillingElasticRepository::save);
    }

    private boolean hasNotBestilling(Long id) {

        return !bestillingElasticRepository.existsById(id);
    }
}
