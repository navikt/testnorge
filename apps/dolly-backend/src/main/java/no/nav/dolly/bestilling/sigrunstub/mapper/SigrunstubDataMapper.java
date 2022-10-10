package no.nav.dolly.bestilling.sigrunstub.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.sigrunstub.KodeverknavnGrunnlag;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.String.format;

@Component
public class SigrunstubDataMapper implements MappingStrategy {

    private static final String OPPGJOER_DATO_NAVN = "skatteoppgjoersdato";
    private static final String OPPGJOER_DATO_VERDI = "%s-05-01";

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(OpprettSkattegrunnlag.class, OpprettSkattegrunnlag.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(OpprettSkattegrunnlag kilde, OpprettSkattegrunnlag destinasjon, MappingContext context) {

                        addOppgjoersdato(destinasjon.getGrunnlag(), destinasjon.getInntektsaar());
                        addOppgjoersdato(destinasjon.getSvalbardGrunnlag(), destinasjon.getInntektsaar());
                    }

                    private void addOppgjoersdato(List<KodeverknavnGrunnlag> spesifiktGrunnlag, String aar) {

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
    }
}
