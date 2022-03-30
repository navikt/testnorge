package no.nav.dolly.mapper.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingStatus;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static no.nav.dolly.mapper.BestillingAaregStatusMapper.buildAaregStatusMap;
import static no.nav.dolly.mapper.BestillingArenaforvalterStatusMapper.buildArenaStatusMap;
import static no.nav.dolly.mapper.BestillingBrregStubStatusMapper.buildBrregStubStatusMap;
import static no.nav.dolly.mapper.BestillingDokarkivStatusMapper.buildDokarkivStatusMap;
import static no.nav.dolly.mapper.BestillingImportFraPdlStatusMapper.buildImportFraPdlStatusMap;
import static no.nav.dolly.mapper.BestillingImportFraTpsStatusMapper.buildImportFraTpsStatusMap;
import static no.nav.dolly.mapper.BestillingInntektsmeldingStatusMapper.buildInntektsmeldingStatusMap;
import static no.nav.dolly.mapper.BestillingInntektstubStatusMapper.buildInntektstubStatusMap;
import static no.nav.dolly.mapper.BestillingInstdataStatusMapper.buildInstdataStatusMap;
import static no.nav.dolly.mapper.BestillingKrrStubStatusMapper.buildKrrStubStatusMap;
import static no.nav.dolly.mapper.BestillingNomStatusMapper.buildNomStatusMap;
import static no.nav.dolly.mapper.BestillingPdlForvalterStatusMapper.buildPdlForvalterStatusMap;
import static no.nav.dolly.mapper.BestillingPensjonforvalterStatusMapper.buildPensjonforvalterStatusMap;
import static no.nav.dolly.mapper.BestillingSigrunStubStatusMapper.buildSigrunStubStatusMap;
import static no.nav.dolly.mapper.BestillingSkjermingsRegisterStatusMapper.buildSkjermingsRegisterStatusMap;
import static no.nav.dolly.mapper.BestillingSykemeldingStatusMapper.buildSykemeldingStatusMap;
import static no.nav.dolly.mapper.BestillingTpsMessagingStatusMapper.buildTpsMessagingStatusMap;
import static no.nav.dolly.mapper.BestillingUdiStubStatusMapper.buildUdiStubStatusMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
@RequiredArgsConstructor
public class BestillingStatusMappingStrategy implements MappingStrategy {

    private final JsonBestillingMapper jsonBestillingMapper;
    private final ObjectMapper objectMapper;

    private static List<String> mapIdents(String idents) {
        return isNotBlank(idents) ? Arrays.asList(idents.split(",")) : Collections.emptyList();
    }

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Bestilling.class, RsBestillingStatus.class)
                .customize(new CustomMapper<Bestilling, RsBestillingStatus>() {
                    @Override
                    public void mapAtoB(Bestilling bestilling, RsBestillingStatus bestillingStatus, MappingContext context) {

                        RsDollyBestillingRequest bestillingRequest = jsonBestillingMapper.mapBestillingRequest(bestilling.getBestKriterier());
                        bestillingStatus.setAntallLevert(bestilling.getProgresser().stream()
                                .filter(BestillingProgress::isIdentGyldig)
                                .toList().size());
                        bestillingStatus.setEnvironments(Arrays.asList(bestilling.getMiljoer().split(",")));
                        bestillingStatus.setGruppeId(bestilling.getGruppe().getId());
                        bestillingStatus.getStatus().addAll(buildTpsMessagingStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildKrrStubStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildNomStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildSigrunStubStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildAaregStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildArenaStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildPdlForvalterStatusMap(bestilling.getProgresser(), objectMapper));
                        bestillingStatus.getStatus().addAll(buildInstdataStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildUdiStubStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildInntektstubStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildPensjonforvalterStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildInntektsmeldingStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildBrregStubStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildDokarkivStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildImportFraTpsStatusMap(bestilling));
                        bestillingStatus.getStatus().addAll(buildImportFraPdlStatusMap(bestilling));
                        bestillingStatus.getStatus().addAll(buildSykemeldingStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildSkjermingsRegisterStatusMap(bestilling.getProgresser()));
                        bestillingStatus.setBestilling(RsBestillingStatus.RsBestilling.builder()
                                .pdlforvalter(bestillingRequest.getPdlforvalter())
                                .pdldata(bestillingRequest.getPdldata())
                                .aareg(bestillingRequest.getAareg())
                                .krrstub(bestillingRequest.getKrrstub())
                                .nomData(bestillingRequest.getNomData())
                                .arenaforvalter(bestillingRequest.getArenaforvalter())
                                .instdata(bestillingRequest.getInstdata())
                                .inntektstub(bestillingRequest.getInntektstub())
                                .sigrunstub(bestillingRequest.getSigrunstub())
                                .udistub(bestillingRequest.getUdistub())
                                .pensjonforvalter(bestillingRequest.getPensjonforvalter())
                                .inntektsmelding(bestillingRequest.getInntektsmelding())
                                .brregstub(bestillingRequest.getBrregstub())
                                .dokarkiv(bestillingRequest.getDokarkiv())
                                .sykemelding(bestillingRequest.getSykemelding())
                                .tpsf(jsonBestillingMapper.mapTpsfRequest(bestilling.getTpsfKriterier()))
                                .importFraTps(mapIdents(bestilling.getTpsImport()))
                                .tpsMessaging(bestillingRequest.getTpsMessaging())
                                .importFraPdl(mapIdents(bestilling.getPdlImport()))
                                .kildeMiljoe(bestilling.getKildeMiljoe())
                                .navSyntetiskIdent(bestilling.getNavSyntetiskIdent())
                                .build());
                        bestillingStatus.setBruker(mapperFacade.map(bestilling.getBruker(), RsBrukerUtenFavoritter.class));
                    }
                })
                .exclude("bruker")
                .byDefault()
                .register();
    }
}