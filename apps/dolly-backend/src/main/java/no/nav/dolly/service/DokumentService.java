package no.nav.dolly.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class DokumentService {

    private final DokumentRepository dokumentRepository;
    private final BestillingMalRepository bestillingMalRepository;
    private final ObjectMapper objectMapper;

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
                                                .map(RsDokarkiv.Dokument.DokumentVariant::getDokumentReferanse),
                                        nonNull(kriterier.getHistark()) ?
                                                kriterier.getHistark().getDokumenter().stream()
                                                        .map(RsHistark.RsHistarkDokument::getDokumentReferanse) : Stream.empty()
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