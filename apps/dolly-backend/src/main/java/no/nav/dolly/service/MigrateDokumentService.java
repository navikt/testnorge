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
import org.springframework.stereotype.Service;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.jpa.Dokument.DokumentType.BESTILLING_DOKARKIV;
import static no.nav.dolly.domain.jpa.Dokument.DokumentType.BESTILLING_HISTARK;
import static no.nav.dolly.domain.jpa.Dokument.DokumentType.MAL_BESTILLING_DOKARKIV;
import static no.nav.dolly.domain.jpa.Dokument.DokumentType.MAL_BESTILLING_HISTARK;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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
                .forEach(malBestilling -> {
                    var utvidetBestilling = fromJson(malBestilling.getBestKriterier(), malBestilling.getId());
                    if (nonNull(utvidetBestilling)) {
                        lagreDokument.apply(utvidetBestilling, malBestilling.getId(), dokumentType);
                        var oppdatertBestilling = toJson(utvidetBestilling, malBestilling.getId());
                        malBestilling.setBestKriterier(isNotBlank(oppdatertBestilling) ? oppdatertBestilling : malBestilling.getBestKriterier());
                    }
                });
    }

    private void migrateBestillinger(Iterable<Bestilling> query, TriConsumer<RsDollyUtvidetBestilling, Long, DokumentType> lagreDokument, DokumentType dokumentType) {

        StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                        query.iterator(), Spliterator.ORDERED), false)
                .forEach(bestilling -> {
                    var utvidetBestilling = fromJson(bestilling.getBestKriterier(), bestilling.getId());
                    if (nonNull(utvidetBestilling)) {
                        lagreDokument.apply(utvidetBestilling, bestilling.getId(), dokumentType);
                        var oppdatertBestilling = toJson(utvidetBestilling, bestilling.getId());
                        bestilling.setBestKriterier(isNotBlank(oppdatertBestilling) ? oppdatertBestilling : bestilling.getBestKriterier());
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
