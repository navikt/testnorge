package no.nav.dolly.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.Dokument;
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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class DokumentService {

    private final DokumentRepository dokumentRepository;
    private final BestillingMalRepository bestillingMalRepository;
    private final ObjectMapper objectMapper;
    private final Cache<String, StringBuilder> dokumentUploadCache;

    public String initUpload() {

        var uploadId = UUID.randomUUID().toString();
        dokumentUploadCache.put(uploadId, new StringBuilder());
        log.info("Dokument-opplasting initiert med uploadId {}", uploadId);
        return uploadId;
    }

    public void appendChunk(String uploadId, String data) {

        var buffer = dokumentUploadCache.getIfPresent(uploadId);
        if (isNull(buffer)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Upload med id " + uploadId + " finnes ikke eller har utløpt");
        }
        buffer.append(data);
    }

    public String resolveUpload(String uploadId) {

        var buffer = dokumentUploadCache.getIfPresent(uploadId);
        if (isNull(buffer)) {
            return null;
        }
        dokumentUploadCache.invalidate(uploadId);
        return buffer.toString();
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