package no.nav.dolly.mapper.strategy;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukertype.UTEN_SERVICEBEHOV;
import static no.nav.dolly.domain.resultset.arenaforvalter.ArenaKvalifiseringsgruppe.IKVAL;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukerUtenServicebehov;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class ArenaMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Arenadata.class, ArenaNyBruker.class)
                .customize(new CustomMapper<Arenadata, ArenaNyBruker>() {
                    @Override public void mapAtoB(Arenadata arenadata, ArenaNyBruker arenaNyBruker, MappingContext context) {

                        if (UTEN_SERVICEBEHOV.equals(arenadata.getArenaBrukertype())) {
                            arenaNyBruker.setUtenServicebehov(new ArenaBrukerUtenServicebehov());

                            arenaNyBruker.setArenaKvalifiseringsgruppe(IKVAL);
                            if (nonNull(arenadata.getInaktiveringDato())) {
                                arenaNyBruker.getUtenServicebehov().setStansDato(arenadata.getInaktiveringDato().toLocalDate());
                            }
                        }
                    }
                })
                .byDefault()
                .register();
    }
}