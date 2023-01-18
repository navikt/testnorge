package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonRequest;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.PdlPerson.SivilstandType.ENKE_ELLER_ENKEMANN;
import static no.nav.dolly.domain.PdlPerson.SivilstandType.GJENLEVENDE_PARTNER;
import static no.nav.dolly.domain.PdlPerson.SivilstandType.SKILT;
import static no.nav.dolly.domain.PdlPerson.SivilstandType.SKILT_PARTNER;
import static no.nav.dolly.domain.PdlPerson.SivilstandType.UGIFT;
import static no.nav.dolly.domain.PdlPerson.SivilstandType.UOPPGITT;

@Component
public class PensjonAlderspensjonMappingStrategy implements MappingStrategy {

    private static String getRelasjonType(PdlPerson.SivilstandType sivilstandType) {

        if (isNull(sivilstandType)) {
            return null;
        }
        return switch (sivilstandType) {
            case GIFT, SKILT, SEPARERT, ENKE_ELLER_ENKEMANN -> RelasjonType.EKTEF.name();
            case REGISTRERT_PARTNER, SKILT_PARTNER, SEPARERT_PARTNER, GJENLEVENDE_PARTNER -> RelasjonType.PARTNER.name();
            default -> null;
        };
    }

    private static boolean isHarVaertGift(PdlPerson.SivilstandType sivilstandType) {

        return nonNull(sivilstandType) && sivilstandType != UOPPGITT && sivilstandType != UGIFT;
    }

    private static boolean isVarigAdskilt(PdlPerson.SivilstandType sivilstandType) {

        return nonNull(sivilstandType) && (sivilstandType == SKILT || sivilstandType == ENKE_ELLER_ENKEMANN ||
                sivilstandType == SKILT_PARTNER || sivilstandType == GJENLEVENDE_PARTNER);
    }

    private static LocalDate getSamlovsbruddDato(PdlPerson.SivilstandType sivilstandType, LocalDate sivilstandFomDato) {

        return nonNull(sivilstandType) && (sivilstandType == SKILT || sivilstandType == SKILT_PARTNER) ?
                sivilstandFomDato : null;
    }

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(PensjonData.Alderspensjon.class, AlderspensjonRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PensjonData.Alderspensjon alderspensjon, AlderspensjonRequest request, MappingContext context) {

                        var hovedperson = (String) context.getProperty("ident");
                        request.setFnr(hovedperson);
                        request.setMiljoer((List<String>) context.getProperty("miljoer"));
                        request.setStatsborgerskap("NOR");

                        var relasjoner = (List<PdlPersonBolk.PersonBolk>) context.getProperty("relasjoner");

                        var partner = new AtomicReference<String>();
                        relasjoner.stream()
                                .filter(person -> person.getIdent().equals(hovedperson))
                                .forEach(personBolk -> personBolk.getPerson().getSivilstand().stream()
                                        .max(Comparator.comparing(PdlPerson.Sivilstand::getGyldigFraOgMed))
                                        .ifPresent(sivilstand -> {
                                            request.setSivilstand(sivilstand.getType().name());
                                            request.setSivilstandDatoFom(sivilstand.getGyldigFraOgMed());
                                            partner.set(sivilstand.getRelatertVedSivilstand());
                                        }));

                        if (relasjoner.stream().anyMatch(person -> person.getIdent().equals(partner.get()))) {

                            request.setRelasjonListe(mapperFacade.mapAsList(alderspensjon.getRelasjoner(), AlderspensjonRequest.SkjemaRelasjon.class));
                            relasjoner.stream()
                                    .filter(personBolk -> personBolk.getIdent().equals(partner.get()))
                                    .forEach(partnerPerson -> {
                                        request.getRelasjonListe().get(0).setFnr(partnerPerson.getIdent());
                                        partnerPerson.getPerson().getSivilstand().stream()
                                                .max(Comparator.comparing(PdlPerson.Sivilstand::getGyldigFraOgMed))
                                                .ifPresent(sivilstand -> {
                                                    request.getRelasjonListe().get(0).setRelasjonType(getRelasjonType(sivilstand.getType()));
                                                    request.getRelasjonListe().get(0).setRelasjonFraDato(sivilstand.getGyldigFraOgMed());
                                                    request.getRelasjonListe().get(0).setHarVaertGift(isHarVaertGift(sivilstand.getType()));
                                                    request.getRelasjonListe().get(0).setVarigAdskilt(isVarigAdskilt(sivilstand.getType()));
                                                    request.getRelasjonListe().get(0).setSamlivsbruddDato(
                                                            getSamlovsbruddDato(sivilstand.getType(), sivilstand.getGyldigFraOgMed()));
                                                });

                                        partnerPerson.getPerson().getDoedsfall().stream()
                                                .findFirst()
                                                .map(PdlPerson.Doedsfall::getDoedsdato)
                                                .ifPresent(doedsdato -> request.getRelasjonListe().get(0).setDodsdato(doedsdato));

                                        request.getRelasjonListe().get(0).setHarFellesBarn(
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

        factory.classMap(PensjonData.SkjemaRelasjon.class, AlderspensjonRequest.SkjemaRelasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PensjonData.SkjemaRelasjon relasjon, AlderspensjonRequest.SkjemaRelasjon request, MappingContext context) {

                        request.setSumAvForventetArbeidKapitalPensjonInntekt(relasjon.getSumAvForvArbKapPenInntekt());
                    }
                })
                .byDefault()
                .register();
    }

    public enum RelasjonType {EKTEF, PARTNER, SAMBO}
}