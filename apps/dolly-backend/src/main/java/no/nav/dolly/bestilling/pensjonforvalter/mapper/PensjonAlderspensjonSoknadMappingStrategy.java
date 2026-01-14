package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonSoknadRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonSoknadRequest.RelasjonType;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonSoknadRequest.SivilstandType;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.PdlPerson.SivilstandType.ENKE_ELLER_ENKEMANN;
import static no.nav.dolly.domain.PdlPerson.SivilstandType.GJENLEVENDE_PARTNER;
import static no.nav.dolly.domain.PdlPerson.SivilstandType.SKILT;
import static no.nav.dolly.domain.PdlPerson.SivilstandType.SKILT_PARTNER;

@Component
public class PensjonAlderspensjonSoknadMappingStrategy implements MappingStrategy {

    private static RelasjonType getRelasjonType(PdlPerson.SivilstandType sivilstandType) {

        if (isNull(sivilstandType)) {
            return null;
        }
        return switch (sivilstandType) {
            case GIFT, SKILT, SEPARERT, ENKE_ELLER_ENKEMANN -> RelasjonType.EKTEF;
            case REGISTRERT_PARTNER, SKILT_PARTNER, SEPARERT_PARTNER, GJENLEVENDE_PARTNER -> RelasjonType.PARTNER;
            default -> null;
        };
    }

    private static boolean isHarVaertGift(PdlPerson.SivilstandType sivilstandType) {

        return sivilstandType == SKILT ||
                sivilstandType == ENKE_ELLER_ENKEMANN ||
                sivilstandType == SKILT_PARTNER ||
                sivilstandType == GJENLEVENDE_PARTNER;
    }

    private static boolean isVarigAdskilt(PdlPerson.SivilstandType sivilstandType) {

        return isHarVaertGift(sivilstandType) && sivilstandType == ENKE_ELLER_ENKEMANN ||
                sivilstandType == GJENLEVENDE_PARTNER;
    }

    private static LocalDate getSamlovsbruddDato(PdlPerson.SivilstandType sivilstandType, LocalDate sivilstandFomDato) {

        return sivilstandType == SKILT || sivilstandType == SKILT_PARTNER ?
                sivilstandFomDato : null;
    }

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(PensjonData.Alderspensjon.class, AlderspensjonSoknadRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PensjonData.Alderspensjon alderspensjon, AlderspensjonSoknadRequest request, MappingContext context) {

                        var ident = (String) context.getProperty("ident");
                        request.setFnr(ident);
                        request.setMiljoer((Set<String>) context.getProperty("miljoer"));

                        var pdlRelasjoner = (List<PdlPersonBolk.PersonBolk>) context.getProperty("pdlRelasjoner");
                        var dollyRelasjoner = (List<FullPersonDTO>) context.getProperty("dollyRelasjoner");
                        var samboer = dollyRelasjoner.stream()
                                .map(FullPersonDTO::getPerson)
                                .filter(person -> person.getIdent().equals(ident))
                                .map(PersonDTO::getSivilstand)
                                .flatMap(Collection::stream)
                                .filter(SivilstandDTO::isSamboer)
                                .findFirst();

                        var hovedperson = pdlRelasjoner.stream()
                                .filter(person -> person.getIdent().equals(ident))
                                .findFirst()
                                .map(PdlPersonBolk.PersonBolk::getPerson);

                        request.setStatsborgerskap(hovedperson.map(value -> value.getStatsborgerskap().stream()
                                .map(PdlPerson.Statsborgerskap::getLand)
                                .findFirst()
                                .orElse("NOR")).orElse("NOR"));

                        var sivilstand = hovedperson.flatMap(value -> value.getSivilstand().stream()
                                .min(new SivilstandSort()));

                        if (sivilstand.isPresent()) {
                            request.setSivilstand(mapSivilstand(sivilstand.get().getType()));
                            request.setSivilstandDatoFom(sivilstand.get().getGyldigFraOgMed());
                        }

                        if (!alderspensjon.getRelasjoner().isEmpty() &&
                                (sivilstand.isPresent() || samboer.isPresent())) {

                            request.setRelasjonListe(mapperFacade.mapAsList(alderspensjon.getRelasjoner(), AlderspensjonSoknadRequest.SkjemaRelasjon.class));

                            var partner = samboer.isPresent() ?
                                    samboer.get().getRelatertVedSivilstand() :
                                    sivilstand.get().getRelatertVedSivilstand();

                            request.getRelasjonListe().getFirst().setFnr(partner);

                            if (samboer.isPresent()) {
                                request.getRelasjonListe().getFirst().setRelasjonType(RelasjonType.SAMBO);
                                request.getRelasjonListe().getFirst().setRelasjonFraDato(samboer.get().getSivilstandsdato().toLocalDate());
                            } else {
                                request.getRelasjonListe().getFirst().setRelasjonType(getRelasjonType(sivilstand.get().getType()));
                                request.getRelasjonListe().getFirst().setRelasjonFraDato(sivilstand.get().getGyldigFraOgMed());
                            }

                            var partnerPerson = pdlRelasjoner.stream()
                                    .filter(personBolk -> personBolk.getIdent().equals(partner))
                                    .map(PdlPersonBolk.PersonBolk::getPerson)
                                    .findFirst();

                            if (sivilstand.isPresent()) {
                                        request.getRelasjonListe().getFirst().setHarVaertGift(isHarVaertGift(sivilstand.get().getType()));
                                        request.getRelasjonListe().getFirst().setVarigAdskilt(isVarigAdskilt(sivilstand.get().getType()));
                                        request.getRelasjonListe().getFirst().setSamlivsbruddDato(
                                                getSamlovsbruddDato(sivilstand.get().getType(), sivilstand.get().getGyldigFraOgMed()));
                                    }

                            if (partnerPerson.isPresent()) {

                                request.getRelasjonListe().getFirst().setDodsdato(
                                        partnerPerson.get().getDoedsfall().stream()
                                                .map(PdlPerson.Doedsfall::getDoedsdato)
                                                .findFirst().orElse(null));

                                request.getRelasjonListe().getFirst().setHarFellesBarn(
                                        partnerPerson.get().getForelderBarnRelasjon().stream()
                                                .filter(PdlPerson.ForelderBarnRelasjon::isBarn)
                                                .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                                .anyMatch(barnAvPartner -> pdlRelasjoner.stream()
                                                        .filter(person1 -> ident.equals(person1.getIdent()))
                                                        .map(PdlPersonBolk.PersonBolk::getPerson)
                                                        .anyMatch(person1 -> person1.getForelderBarnRelasjon().stream()
                                                                .filter(PdlPerson.ForelderBarnRelasjon::isBarn)
                                                                .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                                                .anyMatch(barnAvHovedperson -> barnAvHovedperson.equals(barnAvPartner)))));
                            }
                        }
                    }
                })
                .byDefault()
                .register();

        factory.classMap(PensjonData.SkjemaRelasjon.class, AlderspensjonSoknadRequest.SkjemaRelasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PensjonData.SkjemaRelasjon relasjon, AlderspensjonSoknadRequest.SkjemaRelasjon request, MappingContext context) {

                        request.setSumAvForventetArbeidKapitalPensjonInntekt(relasjon.getSumAvForvArbKapPenInntekt());
                    }
                })
                .byDefault()
                .register();
    }

    private static SivilstandType mapSivilstand(PdlPerson.SivilstandType sivilstandType) {

        if (isNull(sivilstandType)) {
            return null;
        }

        return switch (sivilstandType) {
            case UGIFT, UOPPGITT -> SivilstandType.UGIF;
            case GIFT -> SivilstandType.GIFT;
            case ENKE_ELLER_ENKEMANN -> SivilstandType.ENKE;
            case SKILT -> SivilstandType.SKIL;
            case SEPARERT -> SivilstandType.SEPR;
            case REGISTRERT_PARTNER -> SivilstandType.REPA;
            case SEPARERT_PARTNER -> SivilstandType.SEPA;
            case SKILT_PARTNER -> SivilstandType.SKPA;
            case GJENLEVENDE_PARTNER -> SivilstandType.GJPA;
        };
    }

    public static class SivilstandSort implements Comparator<PdlPerson.Sivilstand> {

        @Override
        public int compare(PdlPerson.Sivilstand sivilstand1, PdlPerson.Sivilstand sivilstand2) {

            if (nonNull(sivilstand1.getGyldigFraOgMed()) && nonNull(sivilstand2.getGyldigFraOgMed())) {
                return sivilstand2.getGyldigFraOgMed().compareTo(sivilstand1.getGyldigFraOgMed());

            } else if (sivilstand1.getType() == sivilstand2.getType()) {
                return 0;

            } else if (sivilstand2.isUgift() || sivilstand1.isTidligereGift()) {
                return -1;

            } else {
                return 1;
            }
        }
    }
}
