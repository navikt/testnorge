package no.nav.dolly.bestilling.pdlforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlDoedfoedtBarn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlForelderBarnRelasjon;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpplysning.Master;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand.Sivilstand;
import no.nav.dolly.bestilling.pdlforvalter.domain.SivilstandWrapper;
import no.nav.dolly.domain.resultset.IdentType;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.Relasjon;
import no.nav.dolly.domain.resultset.tpsf.Sivilstand.Sivilstatus;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.dolly.util.DatoFraIdentUtil;
import no.nav.dolly.util.IdentTypeUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlForelderBarnRelasjon.decode;
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

    private static Sivilstand mapSivilstand(Sivilstatus sivilstatus, boolean isMyndig) {

        if (isNull(sivilstatus)) {
            return isMyndig ? UGIFT : UOPPGITT;
        } else {
            return switch (sivilstatus) {
                case UGIF, SAMB -> UGIFT;
                case GIFT -> GIFT;
                case ENKE -> ENKE_ELLER_ENKEMANN;
                case SKIL -> SKILT;
                case SEPR -> SEPARERT;
                case REPA -> REGISTRERT_PARTNER;
                case SEPA -> SEPARERT_PARTNER;
                case SKPA -> SKILT_PARTNER;
                case GJPA -> GJENLEVENDE_PARTNER;
                default -> UOPPGITT;
            };
        }
    }

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Relasjon.class, PdlForelderBarnRelasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Relasjon relasjon, PdlForelderBarnRelasjon familierelasjon, MappingContext context) {

                        if (!relasjon.isPartner() && nonNull(relasjon.getPersonRelasjonTil()) &&
                         IdentType.FDAT != IdentTypeUtil.getIdentType(relasjon.getPersonRelasjonMed().getIdent())) {

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
                            familierelasjon.setMaster(Master.FREG);
                        }
                    }
                })
                .register();

        factory.classMap(Person.class, PdlSivilstand.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlSivilstand sivilstand, MappingContext context) {

                        sivilstand.setType(mapSivilstand(person.getSivilstand(), person.isMyndig()));
                        sivilstand.setSivilstandsdato(mapperFacade.map(person.getSivilstandRegdato(), LocalDate.class));
                        sivilstand.setRelatertVedSivilstand(person.getRelasjoner().stream()
                                .filter(relasjon -> relasjon.isPartner() && person.isSivilstandGift())
                                .map(Relasjon::getPersonRelasjonMed)
                                .filter(Person::isSivilstandGift)
                                .map(Person::getIdent)
                                .findFirst().orElse(null));
                        sivilstand.setKilde(CONSUMER);
                        sivilstand.setMaster(Master.FREG);
                    }
                })
                .register();

        factory.classMap(Relasjon.class, PdlDoedfoedtBarn.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Relasjon relasjon, PdlDoedfoedtBarn doedfoedtBarn, MappingContext context) {

                        if (relasjon.isBarn() &&
                                IdentType.FDAT == IdentTypeUtil.getIdentType(relasjon.getPersonRelasjonMed().getIdent())) {

                            doedfoedtBarn.setDato(DatoFraIdentUtil.getDato(relasjon.getPersonRelasjonMed().getIdent()));
                            doedfoedtBarn.setKilde(CONSUMER);
                            doedfoedtBarn.setMaster(Master.FREG);
                        }
                    }
                })
                .register();

        factory.classMap(SivilstandWrapper.class, PdlSivilstand.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SivilstandWrapper wrapper, PdlSivilstand sivilstand, MappingContext context) {

                        sivilstand.setType(mapSivilstand(wrapper.getSivilstand().getSivilstand(), wrapper.getPerson().isMyndig()));
                        sivilstand.setSivilstandsdato(
                                mapperFacade.map(wrapper.getSivilstand().getSivilstandRegdato(), LocalDate.class));
                        sivilstand.setRelatertVedSivilstand(wrapper.getSivilstand().isGift() ?
                                wrapper.getSivilstand().getPersonRelasjonMed().getIdent() : null);
                        sivilstand.setKilde(CONSUMER);
                        sivilstand.setMaster(Master.FREG);
                    }
                })
                .register();
    }
}
