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

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.PdlPerson.SivilstandType.ENKE_ELLER_ENKEMANN;
import static no.nav.dolly.domain.PdlPerson.SivilstandType.GJENLEVENDE_PARTNER;
import static no.nav.dolly.domain.PdlPerson.SivilstandType.SKILT;
import static no.nav.dolly.domain.PdlPerson.SivilstandType.SKILT_PARTNER;

@Component
public class PensjonAlderspensjonSoknadMappingStrategy implements MappingStrategy {

    private static final Random RANDOM = new SecureRandom();
    private final PensjonSamboerMappingStrategy pensjonSamboerMappingStrategy;

    public PensjonAlderspensjonSoknadMappingStrategy(PensjonSamboerMappingStrategy pensjonSamboerMappingStrategy) {
        this.pensjonSamboerMappingStrategy = pensjonSamboerMappingStrategy;
    }

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

        return isHarVaertGift(sivilstandType) && RANDOM.nextBoolean();
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

                        var hovedperson = (String) context.getProperty("ident");
                        request.setFnr(hovedperson);
                        request.setMiljoer((Set<String>) context.getProperty("miljoer"));

                        var relasjoner = (List<PdlPersonBolk.PersonBolk>) context.getProperty("relasjoner");
                        var hovedpersonDetaljer = (List<FullPersonDTO>) context.getProperty("hovedperson");
                        var samboer = hovedpersonDetaljer.stream()
                                .map(FullPersonDTO::getPerson)
                                .map(PersonDTO::getSivilstand)
                                .flatMap(Collection::stream)
                                .filter(SivilstandDTO::isSamboer)
                                .findFirst()
                                .orElse(null);

                        request.setStatsborgerskap(relasjoner.stream()
                                .filter(person -> person.getIdent().equals(hovedperson))
                                .map(PdlPersonBolk.PersonBolk::getPerson)
                                .map(PdlPerson.Person::getStatsborgerskap)
                                .flatMap(Collection::stream)
                                .map(PdlPerson.Statsborgerskap::getLand)
                                .findFirst()
                                .orElse("NOR"));

                        var partner = new AtomicReference<String>();
                        relasjoner.stream()
                                .filter(person -> person.getIdent().equals(hovedperson))
                                .forEach(personBolk -> personBolk.getPerson().getSivilstand().stream()
                                        .min(new SivilstandSort())
                                        .ifPresentOrElse(sivilstand -> {
                                                    request.setSivilstand(mapSivilstand(sivilstand.getType()));
                                                    request.setSivilstandDatoFom(sivilstand.getGyldigFraOgMed());
                                                    partner.set(sivilstand.getRelatertVedSivilstand());
                                                },
                                                () -> relasjoner.stream()
                                                        .filter(person -> hovedperson.equals(person.getIdent()))
                                                        .map(PdlPersonBolk.PersonBolk::getPerson)
                                                        .map(PdlPerson.Person::getSivilstand)
                                                        .flatMap(Collection::stream)
                                                        .findFirst()
                                                        .ifPresent(sivilstand -> {
                                                            request.setSivilstand(mapSivilstand(sivilstand.getType()));
                                                            request.setSivilstandDatoFom(sivilstand.getGyldigFraOgMed());
                                                        })));

                        if (relasjoner.stream().anyMatch(person -> person.getIdent().equals(partner.get())) &&
                                !alderspensjon.getRelasjoner().isEmpty()) {

                            request.setRelasjonListe(mapperFacade.mapAsList(alderspensjon.getRelasjoner(), AlderspensjonSoknadRequest.SkjemaRelasjon.class));
                            relasjoner.stream()
                                    .filter(personBolk -> personBolk.getIdent().equals(partner.get()))
                                    .forEach(partnerPerson -> {
                                        request.getRelasjonListe().getFirst().setFnr(partnerPerson.getIdent());
                                        partnerPerson.getPerson().getSivilstand().stream()
                                                .min(new SivilstandSort())
                                                .ifPresent(sivilstand -> {
                                                    request.getRelasjonListe().getFirst().setRelasjonType(getRelasjonType(sivilstand.getType()));
                                                    request.getRelasjonListe().getFirst().setRelasjonFraDato(sivilstand.getGyldigFraOgMed());
                                                    request.getRelasjonListe().getFirst().setHarVaertGift(isHarVaertGift(sivilstand.getType()));
                                                    request.getRelasjonListe().getFirst().setVarigAdskilt(isVarigAdskilt(sivilstand.getType()));
                                                    request.getRelasjonListe().getFirst().setSamlivsbruddDato(
                                                            getSamlovsbruddDato(sivilstand.getType(), sivilstand.getGyldigFraOgMed()));
                                                });

                                        partnerPerson.getPerson().getDoedsfall().stream()
                                                .findFirst()
                                                .map(PdlPerson.Doedsfall::getDoedsdato)
                                                .ifPresent(doedsdato -> request.getRelasjonListe().getFirst().setDodsdato(doedsdato));

                                        request.getRelasjonListe().getFirst().setHarFellesBarn(
                                                partnerPerson.getPerson().getForelderBarnRelasjon().stream()
                                                        .filter(PdlPerson.ForelderBarnRelasjon::isBarn)
                                                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                                        .anyMatch(barnAvPartner -> relasjoner.stream()
                                                                .filter(person -> hovedperson.equals(person.getIdent()))
                                                                .map(PdlPersonBolk.PersonBolk::getPerson)
                                                                .anyMatch(person -> person.getForelderBarnRelasjon().stream()
                                                                        .filter(PdlPerson.ForelderBarnRelasjon::isBarn)
                                                                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                                                        .anyMatch(barnAvHovedperson -> barnAvHovedperson.equals(barnAvPartner)))));
                                    });
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
