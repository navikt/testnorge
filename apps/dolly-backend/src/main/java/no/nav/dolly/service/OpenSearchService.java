package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.elastic.ElasticBestilling;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private final MapperFacade mapperFacade;
    private final BestillingElasticRepository bestillingElasticRepository;

    public void lagreBestillingMedStatus(RsDollyBestilling dollyBestilling, Bestilling bestillingMedStatus) {

        var elasticBestilling = mapperFacade.map(dollyBestilling, ElasticBestilling.class);
        elasticBestilling.setIdenter(bestillingMedStatus.getProgresser().stream()
                .filter(BestillingProgress::isIdentGyldig)
                .filter(progress -> isBlank(progress.getFeil()))
                .map(BestillingProgress::getIdent)
                .toList());

        var resultat = bestillingElasticRepository.save(elasticBestilling);

        log.info("Elastic search record lagret {}", resultat);
    }

    public List<ElasticBestilling> getAll() {

        var all = bestillingElasticRepository.findAll();
        return StreamSupport.stream(all.spliterator(),false)
                .toList();
    }
}
