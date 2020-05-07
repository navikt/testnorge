package no.nav.dolly.bestilling.bregstub.mapper;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.bregstub.domain.RolleoversiktTo.AdresseTo;
import static no.nav.dolly.bestilling.bregstub.domain.RolleoversiktTo.NavnTo;
import static no.nav.dolly.bestilling.bregstub.domain.RolleoversiktTo.RolleTo;
import static no.nav.dolly.domain.resultset.breg.RsBregdata.Egenskap.Deltager;
import static no.nav.dolly.domain.resultset.breg.RsBregdata.Egenskap.Komplementar;
import static no.nav.dolly.domain.resultset.breg.RsBregdata.Egenskap.Kontaktperson;
import static no.nav.dolly.domain.resultset.breg.RsBregdata.Egenskap.Sameier;
import static no.nav.dolly.domain.resultset.breg.RsBregdata.Egenskap.Styre;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.bregstub.domain.BrregRequestWrapper;
import no.nav.dolly.bestilling.bregstub.domain.OrganisasjonTo;
import no.nav.dolly.bestilling.bregstub.domain.RolleoversiktTo;
import no.nav.dolly.domain.resultset.breg.RsBregdata;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsGateadresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsMatrikkeladresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsPostadresse;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class BregRolleMappingStrategy implements MappingStrategy {

    private static final String UTEN_BESKRIVELSE = "Beskrivelse mangler";

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RolleUtskriftMapper.BregPerson.class, BrregRequestWrapper.class)
                .customize(new CustomMapper<RolleUtskriftMapper.BregPerson, BrregRequestWrapper>() {
                    @Override
                    public void mapAtoB(RolleUtskriftMapper.BregPerson bregPerson, BrregRequestWrapper wrapper, MappingContext context) {

                        Person rollePerson = bregPerson.getTpsPerson().getPerson(bregPerson.getTpsPerson().getHovedperson());

                        RolleoversiktTo rolleoversikt = RolleoversiktTo.builder()
                                .enheter(mapperFacade.map(RolleWrapper.builder()
                                        .rolleFra(bregPerson.getBregdata().getEnheter())
                                        .koderoller(bregPerson.getKodeRoller())
                                        .build(), List.class))
                                .fnr(bregPerson.getTpsPerson().getHovedperson())
                                .fodselsdato(rollePerson.getFoedselsdato().toLocalDate())
                                .navn(mapperFacade.map(rollePerson, NavnTo.class))
                                .adresse(mapperFacade.map(rollePerson, AdresseTo.class))
                                .hovedstatus(0)
                                .understatuser(bregPerson.getBregdata().getUnderstatuser())
                                .build();

                        Map<Integer, OrganisasjonTo> organisasjoner = new HashMap<>();

                        bregPerson.getBregdata().getEnheter().forEach(enhet -> {

                            OrganisasjonTo organisasjon = organisasjoner.getOrDefault(enhet.getOrgNr(), new OrganisasjonTo());
                            organisasjon.setHovedstatus(0);
                            organisasjon.setUnderstatuser(bregPerson.getBregdata().getUnderstatuser());
                            organisasjon.setOrgnr(enhet.getOrgNr());
                            organisasjon.setRegistringsDato(getLocalDate(enhet.getRegistreringsdato()));
                            organisasjon.setDeltagere(appendRolle(organisasjon.getDeltagere(), enhet, Deltager, rolleoversikt, bregPerson.getKodeRoller()));
                            organisasjon.setKomplementar(appendRolle(organisasjon.getDeltagere(), enhet, Komplementar, rolleoversikt, bregPerson.getKodeRoller()));
                            organisasjon.setKontaktperson(appendRolle(organisasjon.getDeltagere(), enhet, Kontaktperson, rolleoversikt, bregPerson.getKodeRoller()));
                            organisasjon.setSameier(appendRolle(organisasjon.getDeltagere(), enhet, Sameier, rolleoversikt, bregPerson.getKodeRoller()));
                            organisasjon.setStyre(appendRolle(organisasjon.getDeltagere(), enhet, Styre, rolleoversikt, bregPerson.getKodeRoller()));
                            organisasjoner.put(enhet.getOrgNr(), organisasjon);
                        });

                        wrapper.setRolleoversiktTo(rolleoversikt);
                        wrapper.getOrganisasjonTo().addAll(organisasjoner.values());
                    }
                })
                .register();

        factory.classMap(RolleWrapper.class, List.class)
                .customize(new CustomMapper<RolleWrapper, List>() {
                    @Override
                    public void mapAtoB(RolleWrapper wrapper, List list, MappingContext context) {

                        wrapper.rolleFra.forEach(enhet ->
                                list.add(
                                        RolleTo.builder()
                                                .foretaksNavn(mapperFacade.map(enhet.getForetaksNavn(), NavnTo.class))
                                                .forretningsAdresse(nonNull(enhet.getForretningsAdresse()) ?
                                                        mapperFacade.map(enhet.getForretningsAdresse(), AdresseTo.class) :
                                                        AdresseTo.builder()
                                                                .adresse1("BOLETTE WIESES GATE 4")
                                                                .postnr("1349")
                                                                .poststed("RYKKINN")
                                                                .kommunenr("3024")
                                                                .build())
                                                .orgNr(enhet.getOrgNr())
                                                .postAdresse(mapperFacade.map(enhet.getPostAdresse(), AdresseTo.class))
                                                .registreringsdato(getLocalDate(enhet.getRegistreringsdato()))
                                                .rollebeskrivelse(wrapper.getKoderoller().getOrDefault(enhet.getRolle(), UTEN_BESKRIVELSE))
                                                .build()
                                ));
                    }
                })
                .register();

        factory.classMap(Person.class, AdresseTo.class)
                .customize(new CustomMapper<Person, AdresseTo>() {
                    @Override
                    public void mapAtoB(Person person, AdresseTo adresseTo, MappingContext context) {

                        if (!person.getBoadresse().isEmpty()) {
                            RsAdresse adresse = person.getBoadresse().get(0);
                            if (adresse instanceof RsGateadresse) {
                                adresseTo.setAdresse1(format("%s %s", ((RsGateadresse) adresse).getGateadresse(),
                                        ((RsGateadresse) adresse).getHusnummer()));

                            } else {
                                adresseTo.setAdresse1(((RsMatrikkeladresse) adresse).getMellomnavn());
                                adresseTo.setAdresse2(format("gårdsnummer: %s, bruksnr: %s",
                                        ((RsMatrikkeladresse) adresse).getGardsnr(),
                                        ((RsMatrikkeladresse) adresse).getBruksnr()));
                            }
                            adresseTo.setKommunenr(adresse.getKommunenr());
                            adresseTo.setPostnr(adresse.getPostnr());
                            adresseTo.setPoststed("UKJENT");
                            adresseTo.setLandKode("NO");

                        } else if (!person.getPostadresse().isEmpty()) {
                            RsPostadresse adresse = person.getPostadresse().get(0);
                            adresseTo.setAdresse1(adresse.getPostLinje1());
                            adresseTo.setAdresse2(adresse.getPostLinje2());
                            adresseTo.setAdresse3(adresse.getPostLinje3());
                            adresseTo.setLandKode(LandkoderIso3To2.getCountryIso2(adresse.getPostLand()));
                        }
                    }
                })
                .register();

        factory.classMap(Person.class, NavnTo.class)
                .customize(new CustomMapper<Person, NavnTo>() {
                    @Override
                    public void mapAtoB(Person person, NavnTo navnTo, MappingContext context) {

                        navnTo.setNavn1(person.getFornavn());
                        navnTo.setNavn2(person.getMellomnavn());
                        navnTo.setNavn3(person.getEtternavn());
                    }
                })
                .register();
    }

    private static OrganisasjonTo.SamendringTo appendRolle(OrganisasjonTo.SamendringTo samendring, RsBregdata.RolleTo enhet,
            RsBregdata.Egenskap egenskap, RolleoversiktTo rolleoversikt, Map<String, String> kodeRoller) {

        RsBregdata.PersonRolle personRolle = enhet.getPersonroller().stream()
                .filter(personRolle1 -> personRolle1.getEgenskap() == egenskap)
                .findFirst().orElse(null);

        if (isNull(personRolle)) {
            return null;
        }
        OrganisasjonTo.SamendringTo oppdatertSamendring = nonNull(samendring) ? samendring : new OrganisasjonTo.SamendringTo();

        oppdatertSamendring.setRegistringsDato(getLocalDate(personRolle.getRegistringsDato()));
        oppdatertSamendring.getRoller().add(
                OrganisasjonTo.PersonOgRolleTo.builder()
                        .adresse1(rolleoversikt.getAdresse().getAdresse1())
                        .fodselsnr(rolleoversikt.getFnr())
                        .fornavn(rolleoversikt.getNavn().getNavn1())
                        .fratraadt(isTrue(personRolle.getFratraadt()))
                        .postnr(rolleoversikt.getAdresse().getPostnr())
                        .poststed(rolleoversikt.getAdresse().getPoststed())
                        .rolle(enhet.getRolle())
                        .rollebeskrivelse(kodeRoller.getOrDefault(enhet.getRolle(), UTEN_BESKRIVELSE))
                        .slektsnavn(rolleoversikt.getNavn().getNavn3())
                        .build());

        return oppdatertSamendring;
    }

    private static LocalDate getLocalDate(LocalDateTime dateTime) {
        return nonNull(dateTime) ? dateTime.toLocalDate() : LocalDate.now();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RolleWrapper {

        private List<RsBregdata.RolleTo> rolleFra;
        private Map<String, String> koderoller;
    }
}