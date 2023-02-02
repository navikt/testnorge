package no.nav.dolly.mapper.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingStatus;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static no.nav.dolly.bestilling.service.DollyBestillingService.getEnvironments;
import static no.nav.dolly.mapper.BestillingAaregStatusMapper.buildAaregStatusMap;
import static no.nav.dolly.mapper.BestillingArenaforvalterStatusMapper.buildArenaStatusMap;
import static no.nav.dolly.mapper.BestillingBrregStubStatusMapper.buildBrregStubStatusMap;
import static no.nav.dolly.mapper.BestillingDokarkivStatusMapper.buildDokarkivStatusMap;
import static no.nav.dolly.mapper.BestillingImportFraPdlStatusMapper.buildImportFraPdlStatusMap;
import static no.nav.dolly.mapper.BestillingImportFraTpsStatusMapper.buildImportFraTpsStatusMap;
import static no.nav.dolly.mapper.BestillingInntektsmeldingStatusMapper.buildInntektsmeldingStatusMap;
import static no.nav.dolly.mapper.BestillingInntektstubStatusMapper.buildInntektstubStatusMap;
import static no.nav.dolly.mapper.BestillingInstdataStatusMapper.buildInstdataStatusMap;
import static no.nav.dolly.mapper.BestillingKontoregisterStatusMapper.buildKontoregisterStatusMap;
import static no.nav.dolly.mapper.BestillingKrrStubStatusMapper.buildKrrStubStatusMap;
import static no.nav.dolly.mapper.BestillingPdlForvalterStatusMapper.buildPdlForvalterStatusMap;
import static no.nav.dolly.mapper.BestillingPensjonforvalterStatusMapper.buildPensjonforvalterStatusMap;
import static no.nav.dolly.mapper.BestillingSigrunStubStatusMapper.buildSigrunStubStatusMap;
import static no.nav.dolly.mapper.BestillingSkjermingsRegisterStatusMapper.buildSkjermingsRegisterStatusMap;
import static no.nav.dolly.mapper.BestillingSykemeldingStatusMapper.buildSykemeldingStatusMap;
import static no.nav.dolly.mapper.BestillingTpsMessagingStatusMapper.buildTpsMessagingStatusMap;
import static no.nav.dolly.mapper.BestillingUdiStubStatusMapper.buildUdiStubStatusMap;
import static org.apache.commons.lang3.StringUtils.isBlank;
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
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Bestilling bestilling, RsBestillingStatus bestillingStatus, MappingContext context) {

                        List<BestillingProgress> progresser;
                        var ident = (String) context.getProperty("ident");
                        if (isBlank(ident)) {
                            progresser = bestilling.getProgresser();

                        } else {
                            progresser = bestilling.getProgresser().stream()
                                    .filter(progress -> ident.equals(progress.getIdent()))
                                    .toList();
                        }

                        RsDollyBestillingRequest bestillingRequest = jsonBestillingMapper
                                .mapBestillingRequest(bestilling.getId(), bestilling.getBestKriterier());
                        bestillingStatus.setAntallLevert(progresser.stream()
                                .filter(BestillingProgress::isIdentGyldig)
                                .toList().size());
                        bestillingStatus.setAntallIdenter(bestillingStatus.getAntallLevert()); // midlertidig til TPSF har blitt fjernet
                        bestillingStatus.setEnvironments(getEnvironments(bestilling.getMiljoer()));
                        bestillingStatus.setGruppeId(bestilling.getGruppe().getId());
                        bestillingStatus.getStatus().addAll(buildTpsMessagingStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildKrrStubStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildSigrunStubStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildAaregStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildArenaStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildPdlForvalterStatusMap(progresser, objectMapper));
                        bestillingStatus.getStatus().addAll(buildInstdataStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildUdiStubStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildInntektstubStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildPensjonforvalterStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildInntektsmeldingStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildBrregStubStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildDokarkivStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildImportFraTpsStatusMap(bestilling));
                        bestillingStatus.getStatus().addAll(buildImportFraPdlStatusMap(bestilling));
                        bestillingStatus.getStatus().addAll(buildSykemeldingStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildSkjermingsRegisterStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildKontoregisterStatusMap(progresser));
                        bestillingStatus.setBestilling(RsBestillingStatus.RsBestilling.builder()
                                .pdlforvalter(bestillingRequest.getPdlforvalter())
                                .pdldata(bestillingRequest.getPdldata())
                                .aareg(bestillingRequest.getAareg())
                                .krrstub(bestillingRequest.getKrrstub())
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
                                .skjerming(bestillingRequest.getSkjerming())
//                                .tpsf(jsonBestillingMapper.mapTpsfRequest(bestilling.getTpsfKriterier()))
                                .importFraTps(mapIdents(bestilling.getTpsImport()))
                                .tpsMessaging(bestillingRequest.getTpsMessaging())
                                .bankkonto(bestillingRequest.getBankkonto())
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

        factory.classMap(OrganisasjonBestilling.class, RsBestillingStatus.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(OrganisasjonBestilling bestilling, RsBestillingStatus bestillingStatus, MappingContext context) {

                        bestillingStatus.setAntallLevert(0);
                        bestillingStatus.setEnvironments(getEnvironments(bestilling.getMiljoer()));
                    }
                })
                .byDefault()
                .register();

    }
}
