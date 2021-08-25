package no.nav.dolly.bestilling.pdlforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFamilierelasjon;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.Sivilstand;
import no.nav.dolly.bestilling.pdlforvalter.domain.SivilstandWrapper;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.Relasjon;
import no.nav.dolly.domain.resultset.tpsf.Sivilstand.Sivilstatus;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static java.util.Objects.isNull;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlFamilierelasjon.decode;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.Sivilstand.ENKE_ELLER_ENKEMANN;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.Sivilstand.GIFT;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.Sivilstand.GJENLEVENDE_PARTNER;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.Sivilstand.REGISTRERT_PARTNER;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.Sivilstand.SEPARERT;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.Sivilstand.SEPARERT_PARTNER;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.Sivilstand.SKILT;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.Sivilstand.SKILT_PARTNER;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.Sivilstand.UGIFT;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.Sivilstand.UOPPGITT;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

@Component
public class PdlRelasjonerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Relasjon.class, PdlFamilierelasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Relasjon relasjon, PdlFamilierelasjon familierelasjon, MappingContext context) {

                        familierelasjon.setRelatertPerson(relasjon.getPersonRelasjonMed().getIdent());
                        familierelasjon.setRelatertPersonsRolle(decode(relasjon.getRelasjonTypeNavn()));
                        familierelasjon.setMinRolleForPerson(decode(relasjon.getPersonRelasjonTil().getRelasjoner().stream()
                                .filter(relasjon2 -> relasjon.getPersonRelasjonMed().getIdent().equals(relasjon2.getPerson().getIdent()) &&
                                        (relasjon.isForelder() && relasjon2.isBarn() ||
                                        relasjon.isBarn() && relasjon2.isForelder())
                                )
                                .map(Relasjon::getRelasjonTypeNavn)
                                .findFirst().orElse(null)));
                        familierelasjon.setKilde(CONSUMER);
                    }
                })
                .register();

        factory.classMap(Person.class, PdlSivilstand.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlSivilstand sivilstand, MappingContext context) {

                        sivilstand.setType(mapSivilstand(person.getSivilstand()));
                        sivilstand.setSivilstandsdato(mapperFacade.map(person.getSivilstandRegdato(), LocalDate.class));
                        sivilstand.setRelatertVedSivilstand(person.getRelasjoner().stream()
                                .filter(relasjon -> relasjon.isPartner() && person.isSivilstandGift())
                                .map(Relasjon::getPersonRelasjonMed)
                                .filter(Person::isSivilstandGift)
                                .map(Person::getIdent)
                                .findFirst().orElse(null));
                        sivilstand.setKilde(CONSUMER);
                    }
                })
                .register();

        factory.classMap(SivilstandWrapper.class, PdlSivilstand.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SivilstandWrapper wrapper, PdlSivilstand sivilstand, MappingContext context) {

                        sivilstand.setType(mapSivilstand(wrapper.getSivilstand().getSivilstand()));
                        sivilstand.setSivilstandsdato(
                                mapperFacade.map(wrapper.getSivilstand().getSivilstandRegdato(), LocalDate.class));
                        sivilstand.setRelatertVedSivilstand(wrapper.getSivilstand().isGift() ?
                                wrapper.getSivilstand().getPersonRelasjonMed().getIdent() : null);
                        sivilstand.setKilde(CONSUMER);
                    }
                })
                .register();
    }

    private static Sivilstand mapSivilstand(Sivilstatus sivilstatus) {

        if (isNull(sivilstatus)) {
            return UOPPGITT;
        } else {
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
                    return SEPARERT;
                case REPA:
                    return REGISTRERT_PARTNER;
                case SEPA:
                    return SEPARERT_PARTNER;
                case SKPA:
                    return SKILT_PARTNER;
                case GJPA:
                    return GJENLEVENDE_PARTNER;
                case SAMB:
                default:
                    return UOPPGITT;
            }
        }
    }
}
