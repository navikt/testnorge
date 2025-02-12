package no.nav.dolly.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.dokarkiv.domain.JoarkTransaksjon;
import no.nav.dolly.bestilling.histark.domain.HistarkTransaksjon;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingMal;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.repository.BestillingMalRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
@Slf4j
@Service
@RequiredArgsConstructor
public class MigrateDokumentService {
    private final BestillingRepository bestillingRepository;
    private final BestillingMalRepository bestillingMalRepository;
    private final TransaksjonMappingRepository transaksjonMappingRepository;
    private final ObjectMapper objectMapper;

    public void migrateDokumenter() {
        migrateBestillinger(bestillingRepository.findAllByDokument());
        migrateMalBestillinger(bestillingMalRepository.findAllByDokument());
        migrateTransaksjonMapping(transaksjonMappingRepository.findAllByDokarkiv(), JoarkTransaksjon.class);
        migrateTransaksjonMapping(transaksjonMappingRepository.findAllByHistark(), HistarkTransaksjon.class);
    }
    private void migrateMalBestillinger(Iterable<BestillingMal> query) {
        StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                        query.iterator(), Spliterator.ORDERED), false)
                .forEach(malBestilling -> {
                    try {
                        oppdaterMalDokument(malBestilling);
                    } catch (RuntimeException e) {
                        log.error("Lagring av malbestilling {} feilet: {} ", malBestilling.getId(), e.getMessage(), e);
                    }
                });
    }

    private void oppdaterMalDokument(BestillingMal malBestilling) {
        log.info("Migrerer malbestilling for id {} ... ", malBestilling.getId());
        var utvidetBestilling = fromJson(malBestilling.getBestKriterier(), malBestilling.getId());
        if (nonNull(utvidetBestilling)) {
            var oppdatertBestilling = toJson(utvidetBestilling, malBestilling.getId());
            malBestilling.setBestKriterier(isNotBlank(oppdatertBestilling) ? oppdatertBestilling : malBestilling.getBestKriterier());
            bestillingMalRepository.save(malBestilling);
        }
        log.info("Malbestilling med id {} ferdig!", malBestilling.getId());
    }

    private void migrateBestillinger(Iterable<Bestilling> query) {
        StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                        query.iterator(), Spliterator.ORDERED), false)
                .forEach(bestilling -> {
                    try {
                        oppdaterDokument(bestilling);
                    } catch (RuntimeException e) {
                        log.error("Lagring av bestilling {} feilet: {} ", bestilling.getId(), e.getMessage(), e);
                    }
                });
    }

    private void oppdaterDokument(Bestilling bestilling) {
        log.info("Migrerer bestilling for id {} ... ", bestilling.getId());
        bestillingRepository.findById(bestilling.getId());
        var utvidetBestilling = fromJson(bestilling.getBestKriterier(), bestilling.getId());
        if (nonNull(utvidetBestilling)) {
            var oppdatertBestilling = toJson(utvidetBestilling, bestilling.getId());
            bestilling.setBestKriterier(isNotBlank(oppdatertBestilling) ? oppdatertBestilling : bestilling.getBestKriterier());
            bestillingRepository.save(bestilling);
        }
        log.info("Bestilling med id {} ferdig!", bestilling.getId());
    }

    private <T> void migrateTransaksjonMapping(Iterable<TransaksjonMapping> query, Class<T> clazz) {

        StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                        query.iterator(), Spliterator.ORDERED), false)
                .forEach(mapping -> {
                    try {
                        oppdaterDokument(mapping, clazz);
                    } catch (RuntimeException e) {
                        log.error("Lagring av transaksjonmapping {} feilet: {} ", mapping.getId(), e.getMessage(), e);
                    }
                });
    }

    private <T> void oppdaterDokument(TransaksjonMapping transaksjonMapping, Class<T> clazz) {
        log.info("Migrerer transaksjonMapping for id {} ... ", transaksjonMapping.getId());
        try {
            var transaksjonId = objectMapper.readValue(transaksjonMapping.getTransaksjonId(), clazz);
            var transaksjonIdAsAList = objectMapper.writeValueAsString(List.of(transaksjonId));
            transaksjonMapping.setTransaksjonId(transaksjonIdAsAList);
            transaksjonMappingRepository.save(transaksjonMapping);
        } catch (JsonProcessingException e) {
            log.error("Konvertering fra JSON av transaksjonmapping {} feilet: {} ", transaksjonMapping.getId(), e.getMessage(), e);
        }
        log.info("Transaksjonsmapping med id {} ferdig!", transaksjonMapping.getId());
    }

    private RsDollyUtvidetBestilling fromJson(String kriterier, Long bestillingId) {
        try {
            return objectMapper.readValue(kriterier, RsDollyUtvidetBestilling.class);
        } catch (JsonProcessingException e) {
            log.error("Konvertering fra JSON av bestilling {} feilet: {} ", bestillingId, e.getMessage(), e);
            return null;
        }
    }

    private String toJson(RsDollyUtvidetBestilling dollyUtvidetBestilling, Long bestillingId) {
        try {
            return objectMapper.writeValueAsString(dollyUtvidetBestilling);
        } catch (JsonProcessingException e) {
            log.error("Konvertering til JSON av bestilling {} feilet, {} ", bestillingId, e.getMessage(), e);
            return null;
        }
    }
}