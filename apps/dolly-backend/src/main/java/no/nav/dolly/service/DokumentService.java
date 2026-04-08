package no.nav.dolly.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.Dokument;
import no.nav.dolly.domain.jpa.Dokument.DokumentType;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;
import no.nav.dolly.domain.resultset.histark.RsHistark;
import no.nav.dolly.repository.BestillingMalRepository;
import no.nav.dolly.repository.DokumentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class DokumentService {

    private final DokumentRepository dokumentRepository;
    private final BestillingMalRepository bestillingMalRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Mono<Long> initUpload(DokumentType dokumentType) {

        return dokumentRepository.save(
                        Dokument.builder()
                                .contents("")
                                .dokumentType(dokumentType)
                                .sistOppdatert(LocalDateTime.now())
                                .build()
                )
                .map(Dokument::getId)
                .doOnSuccess(id -> log.info("Dokument-opplasting initiert med id {}", id));
    }

    @Transactional
    public Mono<Void> appendChunk(Long dokumentId, String data) {

        return dokumentRepository.appendContent(dokumentId, data)
                .flatMap(updated -> {
                    if (updated == 0) {
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Dokument med id " + dokumentId + " finnes ikke"));
                    }
                    return Mono.empty();
                });
    }

    @Transactional
    public Flux<Dokument> getDokumenterByBestilling(Long bestillingId) {

        return dokumentRepository.getDokumentsByBestillingId(bestillingId);
    }

    @Transactional
    public Flux<Dokument> getDokumenterByMal(Long malId) {

        return bestillingMalRepository.findById(malId)
                .map(malBestilling -> fromJson(malBestilling.getBestKriterier()))
                .map(kriterier -> Stream.concat(kriterier.getDokarkiv().stream()
                                                .map(RsDokarkiv::getDokumenter)
                                                .flatMap(Collection::stream)
                                                .map(RsDokarkiv.Dokument::getDokumentvarianter)
                                                .flatMap(Collection::stream)
                                                .map(RsDokarkiv.Dokument.DokumentVariant::getDokumentReferanse)
                                                .filter(Objects::nonNull),
                                        nonNull(kriterier.getHistark()) ?
                                                kriterier.getHistark().getDokumenter().stream()
                                                        .map(RsHistark.RsHistarkDokument::getDokumentReferanse)
                                                        .filter(Objects::nonNull) : Stream.empty()
                                )
                                .collect(Collectors.toSet())
                )
                .flatMapMany(dokumentRepository::getDokumentsByIdIsIn);
    }

    @Transactional
    public Flux<Dokument> getDokumenter(List<Long> dokumentIdListe) {

        return dokumentRepository.getDokumentsByIdIsIn(dokumentIdListe);
    }

    private RsDollyUtvidetBestilling fromJson(String json) {

        try {
            return objectMapper.readValue(json, RsDollyUtvidetBestilling.class);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }
}