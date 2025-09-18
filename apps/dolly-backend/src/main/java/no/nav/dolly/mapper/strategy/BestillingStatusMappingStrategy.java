package no.nav.dolly.mapper.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingStatus;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import static java.util.Objects.isNull;
import static no.nav.dolly.bestilling.service.DollyBestillingService.getEnvironments;
import static no.nav.dolly.mapper.AnnenFeilStatusMapper.buildAnnenFeilStatusMap;
import static no.nav.dolly.mapper.ArbeidsplassenCVStatusMapper.buildArbeidsplassenCVStatusMap;
import static no.nav.dolly.mapper.BestillingAaregStatusMapper.buildAaregStatusMap;
import static no.nav.dolly.mapper.BestillingArbeidssoekerregisteretStatusMapper.buildArbeidssoekerregisteretStatusMap;
import static no.nav.dolly.mapper.BestillingArenaforvalterStatusMapper.buildArenaStatusMap;
import static no.nav.dolly.mapper.BestillingBrregStubStatusMapper.buildBrregStubStatusMap;
import static no.nav.dolly.mapper.BestillingDokarkivStatusMapper.buildDokarkivStatusMap;
import static no.nav.dolly.mapper.BestillingEtterlatteStatusMapper.buildEtterlatteStatusMap;
import static no.nav.dolly.mapper.BestillingFullmaktStatusMapper.buildFullmaktStatusMap;
import static no.nav.dolly.mapper.BestillingHistarkStatusMapper.buildHistarkStatusMap;
import static no.nav.dolly.mapper.BestillingImportFraPdlStatusMapper.buildImportFraPdlStatusMap;
import static no.nav.dolly.mapper.BestillingInntektsmeldingStatusMapper.buildInntektsmeldingStatusMap;
import static no.nav.dolly.mapper.BestillingInntektstubStatusMapper.buildInntektstubStatusMap;
import static no.nav.dolly.mapper.BestillingInstdataStatusMapper.buildInstdataStatusMap;
import static no.nav.dolly.mapper.BestillingKontoregisterStatusMapper.buildKontoregisterStatusMap;
import static no.nav.dolly.mapper.BestillingKrrStubStatusMapper.buildKrrStubStatusMap;
import static no.nav.dolly.mapper.BestillingMedlStatusMapper.buildMedlStatusMap;
import static no.nav.dolly.mapper.BestillingNomStatusMapper.buildNomStatusMap;
import static no.nav.dolly.mapper.BestillingPdlForvalterStatusMapper.buildPdlForvalterStatusMap;
import static no.nav.dolly.mapper.BestillingPdlOrdreStatusMapper.buildPdlOrdreStatusMap;
import static no.nav.dolly.mapper.BestillingPdlPersonStatusMapper.buildPdlPersonStatusMap;
import static no.nav.dolly.mapper.BestillingPensjonforvalterStatusMapper.buildPensjonforvalterStatusMap;
import static no.nav.dolly.mapper.BestillingSigrunStubStatusMapper.buildSigrunStubStatusMap;
import static no.nav.dolly.mapper.BestillingSkattekortStatusMapper.buildSkattekortStatusMap;
import static no.nav.dolly.mapper.BestillingSkjermingsRegisterStatusMapper.buildSkjermingsRegisterStatusMap;
import static no.nav.dolly.mapper.BestillingSykemeldingStatusMapper.buildSykemeldingStatusMap;
import static no.nav.dolly.mapper.BestillingTpsMessagingStatusMapper.buildTpsMessagingStatusMap;
import static no.nav.dolly.mapper.BestillingUdiStubStatusMapper.buildUdiStubStatusMap;
import static no.nav.dolly.mapper.BestillingYrkesskadeStatusMapper.buildYrkesskadeStatusMap;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Component
@RequiredArgsConstructor
public class BestillingStatusMappingStrategy implements MappingStrategy {

    private static final String EMPTY_JSON = "{}";
    private final ObjectMapper objectMapper;

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Bestilling.class, RsBestillingStatus.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Bestilling bestilling, RsBestillingStatus bestillingStatus, MappingContext context) {

                        var ident = (String) context.getProperty("ident");
                        try {
                            bestillingStatus.setBestilling(
                                    objectMapper.readTree(isNull(bestilling.getBestKriterier()) ||
                                            EMPTY_JSON.equals(bestilling.getBestKriterier()) ? EMPTY_JSON :
                                            bestilling.getBestKriterier()));
                        } catch (JsonProcessingException e) {
                            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
                        }

                        bestillingStatus.setBruker(mapperFacade.map(bestilling.getBruker(), RsBrukerUtenFavoritter.class));

                        var progresser = bestilling.getProgresser().stream()
                                .filter(progress -> isBlank(ident) || ident.equals(progress.getIdent()))
                                .toList();

                        bestillingStatus.setAntallLevert((int) progresser.stream()
                                .filter(BestillingProgress::isIdentGyldig)
                                .count());

                        bestillingStatus.setEnvironments(getEnvironments(bestilling.getMiljoer()));
                        bestillingStatus.setGruppeId(bestilling.getGruppeId());
                        bestillingStatus.getStatus().addAll(buildImportFraPdlStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildPdlForvalterStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildPdlOrdreStatusMap(progresser, objectMapper));
                        bestillingStatus.getStatus().addAll(buildPdlPersonStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildNomStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildPensjonforvalterStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildEtterlatteStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildKontoregisterStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildTpsMessagingStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildAaregStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildInntektstubStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildKrrStubStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildFullmaktStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildMedlStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildSigrunStubStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildArenaStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildInstdataStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildUdiStubStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildInntektsmeldingStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildBrregStubStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildDokarkivStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildHistarkStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildSykemeldingStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildSkjermingsRegisterStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildArbeidsplassenCVStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildSkattekortStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildYrkesskadeStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildArbeidssoekerregisteretStatusMap(progresser));
                        bestillingStatus.getStatus().addAll(buildAnnenFeilStatusMap(progresser));
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
