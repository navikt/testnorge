package no.nav.dolly.mapper.strategy;

import static no.nav.dolly.mapper.BestillingAaregStatusMapper.buildAaregStatusMap;
import static no.nav.dolly.mapper.BestillingArenaforvalterStatusMapper.buildArenaStatusMap;
import static no.nav.dolly.mapper.BestillingInntektsstubStatusMapper.buildInntektsstubStatusMap;
import static no.nav.dolly.mapper.BestillingInstdataStatusMapper.buildInstdataStatusMap;
import static no.nav.dolly.mapper.BestillingKrrStubStatusMapper.buildKrrStubStatusMap;
import static no.nav.dolly.mapper.BestillingPdlForvalterStatusMapper.buildPdldataStatusMap;
import static no.nav.dolly.mapper.BestillingSigrunStubStatusMapper.buildSigrunStubStatusMap;
import static no.nav.dolly.mapper.BestillingTpsfStatusMapper.buildTpsfStatusMap;
import static no.nav.dolly.mapper.BestillingUdiStubStatusMapper.buildUdiStubStatusMap;

import java.io.IOException;
import java.util.Arrays;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingStatus;
import no.nav.dolly.mapper.MappingStrategy;
import springfox.documentation.spring.web.json.Json;

@Slf4j
@Component
@RequiredArgsConstructor
public class BestillingStatusMappingStrategy implements MappingStrategy {

    private final ObjectMapper objectMapper;

    @Override public void register(MapperFactory factory) {
        factory.classMap(Bestilling.class, RsBestillingStatus.class)
                .customize(new CustomMapper<Bestilling, RsBestillingStatus>() {
                    @Override public void mapAtoB(Bestilling bestilling, RsBestillingStatus bestillingStatus, MappingContext context) {

                        RsDollyBestillingRequest bestillingRequest = mapBestillingRequest(bestilling.getBestKriterier());
                        bestillingStatus.setAntallLevert(bestilling.getProgresser().size());
                        bestillingStatus.setEnvironments(Arrays.asList(bestilling.getMiljoer().split(",")));
                        bestillingStatus.setGruppeId(bestilling.getGruppe().getId());
                        bestillingStatus.getStatus().addAll(buildTpsfStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildKrrStubStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildSigrunStubStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildAaregStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildArenaStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildPdldataStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildInstdataStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildUdiStubStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildInntektsstubStatusMap(bestilling.getProgresser()));
                        bestillingStatus.setBestilling(RsBestillingStatus.RsBestilling.builder()
                                .pdlforvalter(bestillingRequest.getPdlforvalter())
                                .aareg(bestillingRequest.getAareg())
                                .krrstub(bestillingRequest.getKrrstub())
                                .arenaforvalter(bestillingRequest.getArenaforvalter())
                                .instdata(bestillingRequest.getInstdata())
                                .inntektsstub(bestillingRequest.getInntektsstub())
                                .sigrunstub(bestillingRequest.getSigrunstub())
                                .udistub(bestillingRequest.getUdistub())
                                .tpsf(mapperFacade.map(bestilling.getTpsfKriterier(), Json.class))
                                .build());
                    }

                    private RsDollyBestillingRequest mapBestillingRequest(String jsonInput) {
                        try {
                            return objectMapper.readValue(jsonInput, RsDollyBestillingRequest.class);
                        } catch (IOException e) {
                            log.error("Mapping av JSON fra database bestKriterier feilet. {}", e.getMessage(), e);
                        }
                        return new RsDollyBestillingRequest();
                    }
                })
                .byDefault()
                .register();
    }
}
