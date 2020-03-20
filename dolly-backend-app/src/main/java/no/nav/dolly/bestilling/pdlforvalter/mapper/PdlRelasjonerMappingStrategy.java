package no.nav.dolly.bestilling.pdlforvalter.mapper;

import static no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient.KILDE;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlFamilierelasjon.decode;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.SIVILSTAND.ENKE_ELLER_ENKEMANN;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.SIVILSTAND.GIFT;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.SIVILSTAND.GJENLEVENDE_PARTNER;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.SIVILSTAND.REGISTRERT_PARTNER;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.SIVILSTAND.SEPARERT;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.SIVILSTAND.SEPARERT_PARTNER;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.SIVILSTAND.SKILT;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.SIVILSTAND.SKILT_PARTNER;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.SIVILSTAND.UGIFT;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.SIVILSTAND.UOPPGITT;

import java.time.LocalDate;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFamilierelasjon;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.SIVILSTAND;
import no.nav.dolly.domain.resultset.tpsf.Relasjon;
import no.nav.dolly.domain.resultset.tpsf.Sivilstand;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class PdlRelasjonerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Relasjon.class, PdlFamilierelasjon.class)
                .customize(new CustomMapper<Relasjon, PdlFamilierelasjon>() {
                    @Override
                    public void mapAtoB(Relasjon relasjon, PdlFamilierelasjon familierelasjon, MappingContext context) {

                        familierelasjon.setRelatertPerson(relasjon.getPersonRelasjonMed().getIdent());
                        familierelasjon.setRelatertPersonsRolle(decode(relasjon.getRelasjonTypeNavn()));
                        relasjon.getPersonRelasjonTil().getRelasjoner().forEach(relasjon1 -> {
                            if (relasjon1.getPersonRelasjonMed().getIdent().equals(relasjon.getPerson().getIdent())) {
                                familierelasjon.setMinRolleForPerson(decode(relasjon1.getRelasjonTypeNavn()));
                            }
                        });
                        familierelasjon.setKilde(KILDE);
                    }
                })
                .register();

        factory.classMap(Relasjon.class, PdlSivilstand.class)
                .customize(new CustomMapper<Relasjon, PdlSivilstand>() {
                    @Override
                    public void mapAtoB(Relasjon relasjon, PdlSivilstand sivilstand, MappingContext context) {

                        sivilstand.setType(mapSivilstand(relasjon.getPersonRelasjonMed().getSivilstand()));
                        sivilstand.setSivilstandsdato(mapperFacade.map(relasjon.getPersonRelasjonMed().getSivilstandRegdato(), LocalDate.class));

                        sivilstand.setKilde(KILDE);
                    }

                    private SIVILSTAND mapSivilstand(Sivilstand.SIVILSTATUS sivilstatus) {

                        switch (sivilstatus) {
                        case UGIF:
                            return UGIFT;
                        case GIFT:
                            return GIFT;
                        case ENKE:
                            return ENKE_ELLER_ENKEMANN;
                        case SKIL:
                            return SKILT;
                        case SEPR:
                            return SEPARERT_PARTNER;
                        case REPA:
                            return REGISTRERT_PARTNER;
                        case SEPA:
                            return SEPARERT;
                        case SKPA:
                            return SKILT_PARTNER;
                        case GJPA:
                            return GJENLEVENDE_PARTNER;
                        case SAMB:
                        default:
                            return UOPPGITT;
                        }
                    }
                })
                .register();
    }
}
