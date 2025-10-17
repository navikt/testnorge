package no.nav.dolly.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Comparator;

import static java.util.Objects.isNull;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_AP;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_AP_NY_UTTAKSGRAD;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_AP_REVURDERING;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransaksjonMappingService {

    private final TransaksjonMappingRepository transaksjonMappingRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Flux<RsTransaksjonMapping> getTransaksjonMapping(String system, String ident, Long bestillingId) {

        return transaksjonMappingRepository.findAllByBestillingIdAndIdent(bestillingId, ident)
                .filter(transaksjon ->
                        isNull(system) || system.equals(transaksjon.getSystem()) || isPenAp(system, transaksjon))
                .sort(Comparator.comparing(TransaksjonMapping::getMiljoe)
                        .thenComparing(TransaksjonMapping::getDatoEndret))
                .map(this::toDTO);
    }

    private boolean isPenAp(String system, TransaksjonMapping transaksjon) {

        return system.equals(PEN_AP.name()) &&
                (transaksjon.getSystem().equals(PEN_AP_REVURDERING.name()) ||
                        transaksjon.getSystem().equals(PEN_AP_NY_UTTAKSGRAD.name()));
    }

    public Flux<TransaksjonMapping> getTransaksjonMapping(String ident, String miljoe) {

        return transaksjonMappingRepository.findAllByIdentAndMiljoe(ident, miljoe);
    }

    public Mono<Boolean> existAlready(SystemTyper system, String ident, String miljoe, Long bestillingId) {

        return transaksjonMappingRepository.findAllBySystemAndIdent(system.name(), ident)
                .filter(mapping -> (isBlank(miljoe) || miljoe.equals(mapping.getMiljoe())) &&
                        (isNull(bestillingId) || bestillingId.equals(mapping.getBestillingId())))
                .collectList()
                .thenReturn(true)
                .switchIfEmpty(Mono.just(false));
    }

    public Mono<Void> saveAll(Collection<TransaksjonMapping> entries) {

        return Flux.fromIterable(entries)
                .flatMap(this::save)
                .collectList()
                .then();
    }

    @Transactional
    public Mono<TransaksjonMapping> save(TransaksjonMapping entry) {

        return transaksjonMappingRepository.save(entry);
    }


    @Transactional
    public Mono<Void> delete(String ident, String miljoe, String system) {

        return transaksjonMappingRepository.deleteByIdentAndMiljoeAndSystem(ident, miljoe, system);
    }

    @Transactional
    public Mono<Void> delete(String ident, String miljoe, String system, Long bestillingId) {

        return transaksjonMappingRepository.deleteByIdentAndMiljoeAndSystemAndBestillingId(ident, miljoe, system, bestillingId);
    }

    private RsTransaksjonMapping toDTO(TransaksjonMapping transaksjonMapping) {

        JsonNode innhold;
        try {
            innhold = objectMapper.readTree(transaksjonMapping.getTransaksjonId());

        } catch (JsonProcessingException e) {
            log.error("Feilet å konvertere {} til JsonNode", transaksjonMapping.getTransaksjonId());
            innhold = new TextNode("{\"error\":\"" + e.getMessage() + "\"}");
        }

        return RsTransaksjonMapping.builder()
                .bestillingId(transaksjonMapping.getBestillingId())
                .miljoe(transaksjonMapping.getMiljoe())
                .id(transaksjonMapping.getId())
                .ident(transaksjonMapping.getIdent())
                .system(transaksjonMapping.getSystem())
                .datoEndret(transaksjonMapping.getDatoEndret())
                .transaksjonId(innhold)
                .build();
    }
}
