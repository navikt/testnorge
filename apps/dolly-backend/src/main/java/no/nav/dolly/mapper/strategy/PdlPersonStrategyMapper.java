package no.nav.dolly.mapper.strategy;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.tpsf.InnvandretUtvandret;
import no.nav.dolly.domain.resultset.tpsf.InnvandretUtvandret.InnUtvandret;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.Sivilstand;
import no.nav.dolly.domain.resultset.tpsf.adresse.BoAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.BoGateadresse;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.tpsf.Sivilstand.Sivilstatus.ENKE;
import static no.nav.dolly.domain.resultset.tpsf.Sivilstand.Sivilstatus.GJPA;
import static no.nav.dolly.domain.resultset.tpsf.Sivilstand.Sivilstatus.REPA;
import static no.nav.dolly.domain.resultset.tpsf.Sivilstand.Sivilstatus.SEPA;
import static no.nav.dolly.domain.resultset.tpsf.Sivilstand.Sivilstatus.SEPR;
import static no.nav.dolly.domain.resultset.tpsf.Sivilstand.Sivilstatus.SKIL;
import static no.nav.dolly.domain.resultset.tpsf.Sivilstand.Sivilstatus.SKPA;
import static no.nav.dolly.domain.resultset.tpsf.Sivilstand.Sivilstatus.UGIF;

@Component
@Slf4j
public final class PdlPersonStrategyMapper implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(BostedadresseDTO.class, BoGateadresse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BostedadresseDTO bostedadresseDTO, BoGateadresse boAdresse, MappingContext context) {
                        if (nonNull(bostedadresseDTO.getVegadresse())) {
                            VegadresseDTO vegadresse = bostedadresseDTO.getVegadresse();
                            boAdresse.setAdressetype("GATE");
                            boAdresse.setBolignr(vegadresse.getHusnummer());
                            boAdresse.setKommunenr(vegadresse.getKommunenummer());
                            boAdresse.setPostnr(vegadresse.getPostnummer());
                        }
                        if (nonNull(bostedadresseDTO.getMatrikkeladresse())) {
                            MatrikkeladresseDTO matrikkeladresse = bostedadresseDTO.getMatrikkeladresse();
                            boAdresse.setAdressetype("MATR");
                            boAdresse.setKommunenr(matrikkeladresse.getKommunenummer());
                            boAdresse.setPostnr(matrikkeladresse.getPostnummer());
                            boAdresse.setBolignr(matrikkeladresse.getBruksenhetsnummer());
                            boAdresse.setTilleggsadresse(matrikkeladresse.getTilleggsnavn());
                        }
                    }
                })
                .exclude("matrikkeladresse")
                .byDefault()
                .register();

        factory.classMap(PdlPersonBolk.PersonBolk.class, Person.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PdlPersonBolk.PersonBolk personBolk, Person person, MappingContext context) {

                        if (personBolk.getPerson().getNavn().stream()
                                .anyMatch(personNavn -> !personNavn.getMetadata().isHistorisk())) {
                            mapperFacade.map(personBolk.getPerson().getNavn().stream()
                                    .filter(personNavn -> !personNavn.getMetadata().isHistorisk())
                                    .findFirst().get(), person);
                        }

                        person.setIdenttype(personBolk.getPerson().getFolkeregisteridentifikator().stream()
                                .filter(ident -> !ident.getMetadata().isHistorisk())
                                .map(PdlPerson.Folkeregisteridentifikator::getType)
                                .findFirst().orElse(null));

                        person.setKjonn(personBolk.getPerson().getKjoenn().stream()
                                .anyMatch(kjoenn -> !kjoenn.getMetadata().isHistorisk()) ?
                                personBolk.getPerson().getKjoenn().stream()
                                        .filter(kjoenn -> !kjoenn.getMetadata().isHistorisk())
                                        .map(PdlPerson.PdlKjoenn::getKjoenn)
                                        .findFirst().get().substring(0, 1) : null);

                        person.setFoedselsdato(personBolk.getPerson().getFoedsel().stream()
                                .anyMatch(foedsel -> !foedsel.getMetadata().isHistorisk()) ?
                                personBolk.getPerson().getFoedsel().stream()
                                        .filter(foedsel -> !foedsel.getMetadata().isHistorisk())
                                        .map(PdlPerson.Foedsel::getFoedselsdato)
                                        .findFirst().get().atStartOfDay() : null);

                        person.setDoedsdato(personBolk.getPerson().getDoedsfall().stream()
                                .anyMatch(doedsfall -> !doedsfall.getMetadata().isHistorisk()) ?
                                personBolk.getPerson().getDoedsfall().stream()
                                        .filter(doedsfall -> !doedsfall.getMetadata().isHistorisk())
                                        .map(PdlPerson.Doedsfall::getDoedsdato)
                                        .findFirst().get().atStartOfDay() : null);

                        person.getInnvandretUtvandret().addAll(
                                personBolk.getPerson().getUtflyttingFraNorge().stream()
                                        .filter(utflytting -> !utflytting.getMetadata().isHistorisk())
                                        .map(utflytting -> InnvandretUtvandret.builder()
                                                .innutvandret(InnUtvandret.UTVANDRET)
                                                .landkode(utflytting.getTilflyttingsland())
                                                .flyttedato(utflytting.getUtflyttingsdato())
                                                .build())
                                        .toList());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(PersonDTO.class, Person.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PersonDTO personDto, Person person, MappingContext context) {

                        NavnDTO navnDTO = personDto.getNavn().stream()
                                .findFirst()
                                .orElse(null);

                        person.setPersonStatus(personDto.getFolkeregisterPersonstatus().stream()
                                .map(FolkeregisterPersonstatusDTO::getStatus)
                                .filter(Objects::nonNull)
                                .map(Enum::name)
                                .findFirst().orElse(null));
                        person.setKjonn(personDto.getKjoenn().stream()
                                .map(KjoennDTO::getKjoenn)
                                .filter(Objects::nonNull)
                                .findFirst().orElse(KjoennDTO.Kjoenn.UKJENT).name().substring(0, 1));
                        person.setFoedselsdato(personDto.getFoedsel().stream()
                                .map(FoedselDTO::getFoedselsdato)
                                .filter(Objects::nonNull)
                                .findFirst().orElse(null));
                        person.setDoedsdato(personDto.getDoedsfall().stream()
                                .map(DoedsfallDTO::getDoedsdato)
                                .filter(Objects::nonNull)
                                .findFirst().orElse(null));
                        person.setBoadresse(mapperFacade.mapAsList(personDto.getBostedsadresse(), BoAdresse.class));
                        person.getInnvandretUtvandret().addAll(
                                personDto.getUtflytting().stream()
                                        .map(utflytting -> InnvandretUtvandret.builder()
                                                .innutvandret(InnUtvandret.UTVANDRET)
                                                .landkode(utflytting.getTilflyttingsland())
                                                .flyttedato(utflytting.getMetadata()
                                                        .getGyldighetstidspunkt().atStartOfDay())
                                                .build())
                                        .toList());
                        person.setSivilstand(
                                mapSivilstand(personDto.getSivilstand().stream()
                                        .map(SivilstandDTO::getType)
                                        .filter(Objects::nonNull)
                                        .findFirst()
                                        .orElse(null)));
                        person.setFoedselsdato(
                                personDto.getFoedsel().stream()
                                        .map(FoedselDTO::getFoedselsdato)
                                        .filter(Objects::nonNull)
                                        .findFirst()
                                        .orElse(null)
                        );
                        person.setAlder(personDto.getFoedsel().stream()
                                .map(foedselDTO -> ChronoUnit.YEARS.between(foedselDTO.getFoedselsdato(), LocalDateTime.now()))
                                .map(Long::intValue)
                                .findFirst()
                                .orElse(null)
                        );
                        if (nonNull(navnDTO)) {
                            person.setFornavn(navnDTO.getFornavn());
                            person.setMellomnavn(navnDTO.getMellomnavn());
                            person.setEtternavn(navnDTO.getEtternavn());
                            person.setForkortetNavn("%s %s".formatted(navnDTO.getFornavn(), navnDTO.getEtternavn()));
                        }
                        person.setKjonn(personDto.getKjoenn().stream()
                                .map(KjoennDTO::getKjoenn)
                                .map(Enum::name)
                                .findFirst().orElse(null)
                        );
                    }
                })
                .exclude("sivilstand")
                .byDefault()
                .register();
    }

    private static Sivilstand.Sivilstatus mapSivilstand(SivilstandDTO.Sivilstand sivilstatus) {

        if (isNull(sivilstatus)) {
            return UGIF;
        } else {
            return switch (sivilstatus) {
                case GIFT -> Sivilstand.Sivilstatus.GIFT;
                case ENKE_ELLER_ENKEMANN -> ENKE;
                case SKILT -> SKIL;
                case SEPARERT -> SEPR;
                case REGISTRERT_PARTNER -> REPA;
                case SEPARERT_PARTNER -> SEPA;
                case SKILT_PARTNER -> SKPA;
                case GJENLEVENDE_PARTNER -> GJPA;
                default -> UGIF;
            };
        }
    }
}
