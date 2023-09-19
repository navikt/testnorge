package no.nav.dolly.bestilling.sigrunstub.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.sigrunstub.dto.PensjonsgivendeForFolketrygden;
import no.nav.dolly.domain.resultset.sigrunstub.KodeverknavnGrunnlag;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.sigrunstub.RsPensjonsgivendeForFolketrygden;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.String.format;
import static no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag.Tjeneste.BEREGNET_SKATT;
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

        factory.classMap(OpprettSkattegrunnlag.class, OpprettSkattegrunnlag.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(OpprettSkattegrunnlag kilde, OpprettSkattegrunnlag destinasjon, MappingContext context) {

                        destinasjon.setPersonidentifikator((String) context.getProperty("ident"));

                        if (destinasjon.getTjeneste() == BEREGNET_SKATT) {
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

        factory.classMap(RsPensjonsgivendeForFolketrygden.class, PensjonsgivendeForFolketrygden.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsPensjonsgivendeForFolketrygden kilde, PensjonsgivendeForFolketrygden destinasjon, MappingContext context) {

                        destinasjon.setPersonidentifikator((String) context.getProperty("ident"));

                        destinasjon.setTestdataEier(isNotBlank(kilde.getTestdataEier()) ? kilde.getTestdataEier() : "Dolly");

                        try {
                            destinasjon.setPensjonsgivendeInntekt(
                                    objectMapper.readTree(
                                            objectMapper.writeValueAsString(kilde.getPensjonsgivendeInntekt())));
                        } catch (JsonProcessingException e) {
                            log.error("Feilet å gjøre {} om til JSON", kilde.getPensjonsgivendeInntekt());
                        }
                    }
                })
                .exclude("pensjonsgivendeInntekt")
                .byDefault()
                .register();
    }
}
