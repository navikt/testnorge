package no.nav.dolly.bestilling.sigrunstub.mapper;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubLignetInntektRequest;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubPensjonsgivendeInntektRequest;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubSummertskattegrunnlagRequest;
import no.nav.dolly.domain.resultset.sigrunstub.KodeverknavnGrunnlag;
import no.nav.dolly.domain.resultset.sigrunstub.RsLignetInntekt;
import no.nav.dolly.domain.resultset.sigrunstub.RsPensjonsgivendeForFolketrygden;
import no.nav.dolly.domain.resultset.sigrunstub.RsSummertSkattegrunnlag;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.String.format;
import static no.nav.dolly.domain.resultset.sigrunstub.RsLignetInntekt.Tjeneste.BEREGNET_SKATT;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
@RequiredArgsConstructor
public class SigrunstubDataMapper implements MappingStrategy {

    private static final String OPPGJOER_DATO_NAVN = "skatteoppgjoersdato";
    private static final String OPPGJOER_DATO_VERDI = "%4d-05-01";

    private final ObjectMapper objectMapper;

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsLignetInntekt.class, SigrunstubLignetInntektRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsLignetInntekt kilde, SigrunstubLignetInntektRequest destinasjon, MappingContext context) {

                        destinasjon.setPersonidentifikator((String) context.getProperty("ident"));

                        if (kilde.getTjeneste() == BEREGNET_SKATT) {
                            // BEREGNET_SKATT er blitt deprecated hos mottager
                            destinasjon.setTjeneste(SigrunstubLignetInntektRequest.Tjeneste.SUMMERT_SKATTEGRUNNLAG);
                            addOppgjoersdato(destinasjon.getGrunnlag(), Integer.parseInt(destinasjon.getInntektsaar()) + 1);
                            addOppgjoersdato(destinasjon.getSvalbardGrunnlag(), Integer.parseInt(destinasjon.getInntektsaar()) + 1);
                        }
                    }

                    private void addOppgjoersdato(List<KodeverknavnGrunnlag> spesifiktGrunnlag, Integer aar) {

                        if (!spesifiktGrunnlag.isEmpty() && spesifiktGrunnlag.stream()
                                .noneMatch(grunnlag -> OPPGJOER_DATO_NAVN.equals(grunnlag.getTekniskNavn()))) {

                            spesifiktGrunnlag.add(KodeverknavnGrunnlag.builder()
                                    .tekniskNavn(OPPGJOER_DATO_NAVN)
                                    .verdi(format(OPPGJOER_DATO_VERDI, aar))
                                    .build());
                        }
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsPensjonsgivendeForFolketrygden.class, SigrunstubPensjonsgivendeInntektRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsPensjonsgivendeForFolketrygden kilde, SigrunstubPensjonsgivendeInntektRequest destinasjon, MappingContext context) {

                        destinasjon.setNorskident((String) context.getProperty("ident"));

                        destinasjon.setTestdataEier(isNotBlank(kilde.getTestdataEier()) ? kilde.getTestdataEier() : "Dolly");
                        destinasjon.setInntektsaar(kilde.getInntektsaar());

                        try {
                            destinasjon.setPensjonsgivendeInntekt(
                                    objectMapper.readTree(
                                            objectMapper.writeValueAsString(kilde.getPensjonsgivendeInntekt())));
                        } catch (JacksonException e) {
                            log.error("Feilet å gjøre {} om til JSON", kilde.getPensjonsgivendeInntekt());
                        }
                    }
                })
                .register();

        factory.classMap(RsSummertSkattegrunnlag.class, SigrunstubSummertskattegrunnlagRequest.Summertskattegrunnlag.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsSummertSkattegrunnlag kilde, SigrunstubSummertskattegrunnlagRequest.Summertskattegrunnlag destinasjon, MappingContext context) {
                        log.info("Mottok summertSkattegrunnlag {}", kilde);

                        destinasjon.setPersonidentifikator((String) context.getProperty("ident"));
                    }
                })
                .byDefault()
                .register();
    }
}
