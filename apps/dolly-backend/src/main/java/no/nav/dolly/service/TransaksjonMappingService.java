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
import java.util.List;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransaksjonMappingService {

    private final TransaksjonMappingRepository transaksjonMappingRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Flux<RsTransaksjonMapping> getTransaksjonMapping(String system, String ident, Long bestillingId) {

        return transaksjonMappingRepository.findAllByBestillingIdAndIdent(bestillingId, ident)
                .filter(transaksjon -> isNull(system) || system.equals(transaksjon.getSystem()))
                .map(this::toDTO);
    }

    public Mono<Boolean> existAlready(SystemTyper system, String ident, String miljoe, Long bestillingId) {

        return transaksjonMappingRepository.findAllBySystemAndIdent(system.name(), ident)
                .filter(mapping -> (isBlank(miljoe) || miljoe.equals(mapping.getMiljoe())) &&
                        (isNull(bestillingId) || bestillingId.equals(mapping.getBestillingId())))
                .next()
                .thenReturn(true)
                .switchIfEmpty(Mono.just(false));
    }

    public void saveAll(Collection<TransaksjonMapping> entries) {
        entries.forEach(this::save);
    }

    @Transactional
    public void save(TransaksjonMapping entry) {
        transaksjonMappingRepository.save(entry);
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
