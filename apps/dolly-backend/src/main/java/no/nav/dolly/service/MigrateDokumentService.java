package no.nav.dolly.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingMal;
import no.nav.dolly.domain.jpa.Dokument;
import no.nav.dolly.domain.jpa.Dokument.DokumentType;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.repository.BestillingMalRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.DokumentRepository;
import org.opensearch.common.TriConsumer;
import org.springframework.stereotype.Service;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.jpa.Dokument.DokumentType.BESTILLING_DOKARKIV;
import static no.nav.dolly.domain.jpa.Dokument.DokumentType.BESTILLING_HISTARK;
import static no.nav.dolly.domain.jpa.Dokument.DokumentType.MAL_BESTILLING_DOKARKIV;
import static no.nav.dolly.domain.jpa.Dokument.DokumentType.MAL_BESTILLING_HISTARK;

@Slf4j
@Service
@RequiredArgsConstructor
public class MigrateDokumentService {

    private final BestillingRepository bestillingRepository;
    private final BestillingMalRepository bestillingMalRepository;
    private final DokumentRepository dokumentRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void migrateDokumenter() {

        migrateBestillinger(bestillingRepository.findAllByDokumentArkiv(), lagreDokarkiv(), BESTILLING_DOKARKIV);
        migrateBestillinger(bestillingRepository.findAllByHistArk(), lagreHistark(), BESTILLING_HISTARK);
        migrateMalBestillinger(bestillingMalRepository.findAllByDokumentArkiv(), lagreDokarkiv(), MAL_BESTILLING_DOKARKIV);
        migrateMalBestillinger(bestillingMalRepository.findAllByHistArk(), lagreHistark(), MAL_BESTILLING_HISTARK);
    }

    private void migrateMalBestillinger(Iterable<BestillingMal> query, TriConsumer<RsDollyUtvidetBestilling, Long, DokumentType> lagreDokument, DokumentType dokumentType) {

        StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                        query.iterator(), Spliterator.ORDERED), false)
                .forEach(bestilling -> {
                    try {
                        var utvidetBestilling = objectMapper.readValue(bestilling.getBestKriterier(), RsDollyUtvidetBestilling.class);
                        lagreDokument.apply(utvidetBestilling, bestilling.getId(), dokumentType);
                        var oppdatertBestilling = objectMapper.writeValueAsString(utvidetBestilling);
                        bestilling.setBestKriterier(oppdatertBestilling);
                    } catch (JsonProcessingException e) {
                        log.error("Feil ved konvertering av bestilling {}, {} ", bestilling.getId(), e.getMessage(), e);
                    }
                });
    }

    private void migrateBestillinger(Iterable<Bestilling> query, TriConsumer<RsDollyUtvidetBestilling, Long, DokumentType> lagreDokument, DokumentType dokumentType) {

        StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                        query.iterator(), Spliterator.ORDERED), false)
                .forEach(bestilling -> {
                    try {
                        var utvidetBestilling = objectMapper.readValue(bestilling.getBestKriterier(), RsDollyUtvidetBestilling.class);
                        lagreDokument.apply(utvidetBestilling, bestilling.getId(), dokumentType);
                        var oppdatertBestilling = objectMapper.writeValueAsString(utvidetBestilling);
                        bestilling.setBestKriterier(oppdatertBestilling);
                    } catch (JsonProcessingException e) {
                        log.error("Feil ved konvertering av bestilling {}, {} ", bestilling.getId(), e.getMessage(), e);
                    }
                });
    }

    private TriConsumer<RsDollyUtvidetBestilling, Long, DokumentType> lagreDokarkiv() {

        return (utvidetBestilling, bestillingId, dokumentType) ->
                utvidetBestilling.getDokarkiv().getDokumenter().forEach(dokument ->
                        dokument.getDokumentvarianter().forEach(dokumentVariant -> {
                            if (nonNull(dokumentVariant.getFysiskDokument())) {
                                dokumentVariant.setDokumentReferanse(
                                        lagreDokument(dokumentVariant.getFysiskDokument(), bestillingId, dokumentType));
                                dokumentVariant.setFysiskDokument(null);
                            }
                        }));
    }

    private TriConsumer<RsDollyUtvidetBestilling, Long, DokumentType> lagreHistark() {

        return (utvidetBestilling, bestillingId, dokumentType) ->
                utvidetBestilling.getHistark().getDokumenter().forEach(dokument -> {
                    if (nonNull(dokument.getFysiskDokument())) {
                        dokument.setDokumentReferanse(
                                lagreDokument(dokument.getFysiskDokument(), bestillingId, dokumentType));
                        dokument.setFysiskDokument(null);
                    }
                });
    }

    private Long lagreDokument(String fysiskDokument, Long bestillingId, DokumentType dokumentType) {

        return dokumentRepository.save(Dokument.builder()
                .contents(fysiskDokument)
                .dokumentType(dokumentType)
                .bestillingId(bestillingId)
                .build()).getId();
    }
}
