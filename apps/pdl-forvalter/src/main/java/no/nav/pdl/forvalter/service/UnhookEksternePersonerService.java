package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.AVDOEDD_FOR_KONTAKT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FORELDREANSVAR_BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FORELDREANSVAR_FORELDER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FULLMAKTSGIVER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FULLMEKTIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.KONTAKT_FOR_DOEDSBO;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.VERGE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.VERGE_MOTTAKER;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class UnhookEksternePersonerService {

    private final PersonRepository personRepository;

    public void unhook(DbPerson hovedperson) {

        deleteSivilstandArtifact(hovedperson);
        deleteForeldreansvarRelasjoner(hovedperson);
        deleteForelderBarnRelasjoner(hovedperson);

        deleteFullmaktRelasjoner(hovedperson);
        deleteVergemaalRelasjoner(hovedperson);
        deleteKontaktinformasjonForDoedsboeAndreRelasjoner(hovedperson);
    }

    private void deleteSivilstandArtifact(DbPerson hovedperson) {

        var partnere = personRepository.findByIdentIn(hovedperson.getPerson().getSivilstand().stream()
                .map(SivilstandDTO::getRelatertVedSivilstand)
                .toList(), Pageable.unpaged());

        partnere.stream()
                .map(DbPerson::getPerson)
                .forEach(person -> person.setSivilstand(person.getSivilstand().stream()
                        .filter(sivilstand -> isBlank(sivilstand.getRelatertVedSivilstand()) ||
                                !sivilstand.getRelatertVedSivilstand().equals(hovedperson.getIdent()))
                        .toList()));

        if (hovedperson.getPerson().isStandalone() ||
                hovedperson.getPerson().getSivilstand().stream().anyMatch(SivilstandDTO::isEksisterendePerson) ||
                partnere.stream().anyMatch(dbPerson -> dbPerson.getPerson().isStandalone())) {

            deleteRelasjoner(hovedperson, partnere);
        }
    }

    private void deleteForelderBarnRelasjoner(DbPerson hovedperson) {

        var relasjoner = personRepository.findByIdentIn(hovedperson.getPerson().getForelderBarnRelasjon().stream()
                .map(ForelderBarnRelasjonDTO::getRelatertPerson)
                .toList(), Pageable.unpaged());

        relasjoner.stream()
                .map(DbPerson::getPerson)
                .forEach(person -> person.setForelderBarnRelasjon(person.getForelderBarnRelasjon().stream()
                        .filter(relasjon -> !Objects.equals(hovedperson.getIdent(), relasjon.getRelatertPerson()))
                        .toList()));

        if (hovedperson.getPerson().isStandalone() ||
                hovedperson.getPerson().getForelderBarnRelasjon().stream().anyMatch(ForelderBarnRelasjonDTO::isEksisterendePerson) ||
                relasjoner.stream().anyMatch(dbPerson -> dbPerson.getPerson().isStandalone())) {

            deleteRelasjoner(hovedperson, relasjoner);
        }
    }

    private void deleteForeldreansvarRelasjoner(DbPerson hovedperson) {

        var relasjoner = personRepository.findByIdentIn(hovedperson.getRelasjoner().stream()
                .filter(relasjon -> FORELDREANSVAR_BARN == relasjon.getRelasjonType() ||
                        FORELDREANSVAR_FORELDER == relasjon.getRelasjonType())
                .map(DbRelasjon::getRelatertPerson)
                .map(DbPerson::getIdent)
                .toList(), Pageable.unpaged());

        relasjoner.stream()
                .map(DbPerson::getPerson)
                .forEach(person -> person.setForeldreansvar(
                        person.getForeldreansvar().stream()
                                .filter(relasjon -> !Objects.equals(hovedperson.getIdent(), relasjon.getIdentForRelasjon()))
                                .toList()));

        if (hovedperson.getPerson().isStandalone() ||
                hovedperson.getPerson().getForeldreansvar().stream().anyMatch(ForeldreansvarDTO::isEksisterendePerson) ||
                relasjoner.stream().anyMatch(dbPerson -> dbPerson.getPerson().isStandalone())) {

            deleteRelasjoner(hovedperson, relasjoner);
        }
    }

    private void deleteFullmaktRelasjoner(DbPerson hovedperson) {

        var relasjoner = personRepository.findByIdentIn(hovedperson.getRelasjoner().stream()
                .filter(relasjon -> FULLMAKTSGIVER == relasjon.getRelasjonType() ||
                        FULLMEKTIG == relasjon.getRelasjonType())
                .map(DbRelasjon::getRelatertPerson)
                .map(DbPerson::getIdent)
                .toList(), Pageable.unpaged());

        relasjoner.stream()
                .map(DbPerson::getPerson)
                .forEach(person -> person.setFullmakt(person.getFullmakt().stream()
                        .filter(fullmakt -> !Objects.equals(hovedperson.getIdent(), fullmakt.getMotpartsPersonident()))
                        .toList()));

        if (hovedperson.getPerson().isStandalone() ||
                hovedperson.getPerson().getFullmakt().stream().anyMatch(FullmaktDTO::isEksisterendePerson) ||
                relasjoner.stream().anyMatch(dbPerson -> dbPerson.getPerson().isStandalone())) {

            deleteRelasjoner(hovedperson, relasjoner);
        }
    }

    private void deleteVergemaalRelasjoner(DbPerson hovedperson) {

        var relasjoner = personRepository.findByIdentIn(hovedperson.getRelasjoner().stream()
                .filter(relasjon -> VERGE_MOTTAKER == relasjon.getRelasjonType() ||
                        VERGE == relasjon.getRelasjonType())
                .map(DbRelasjon::getRelatertPerson)
                .map(DbPerson::getIdent)
                .toList(), Pageable.unpaged());

        relasjoner.stream()
                .map(DbPerson::getPerson)
                .forEach(person -> person.setVergemaal(person.getVergemaal().stream()
                        .filter(vergemaal -> !Objects.equals(hovedperson.getIdent(), vergemaal.getIdentForRelasjon()))
                        .toList()));

        if (hovedperson.getPerson().isStandalone() ||
                hovedperson.getPerson().getVergemaal().stream().anyMatch(VergemaalDTO::isEksisterendePerson) ||
                relasjoner.stream().anyMatch(dbPerson -> dbPerson.getPerson().isStandalone())) {

            deleteRelasjoner(hovedperson, relasjoner);
        }
    }

    private void deleteKontaktinformasjonForDoedsboeAndreRelasjoner(DbPerson hovedperson) {

        var relasjoner = personRepository.findByIdentIn(hovedperson.getRelasjoner().stream()
                .filter(relasjon -> KONTAKT_FOR_DOEDSBO == relasjon.getRelasjonType() ||
                        AVDOEDD_FOR_KONTAKT == relasjon.getRelasjonType())
                .map(DbRelasjon::getRelatertPerson)
                .map(DbPerson::getIdent)
                .toList(), Pageable.unpaged());

        relasjoner.stream()
                .map(DbPerson::getPerson)
                .forEach(person -> person.setKontaktinformasjonForDoedsbo(person.getKontaktinformasjonForDoedsbo().stream()
                        .filter(kontakt -> isNull(kontakt.getPersonSomKontakt()) ||
                                !hovedperson.getIdent().equals(kontakt.getPersonSomKontakt().getIdentifikasjonsnummer()))
                        .toList()));

        if (hovedperson.getPerson().isStandalone() ||
                hovedperson.getPerson().getKontaktinformasjonForDoedsbo().stream()
                        .anyMatch(kontakt -> kontakt.getPersonSomKontakt().isEksisterendePerson()) ||
                relasjoner.stream().anyMatch(dbPerson -> dbPerson.getPerson().isStandalone())) {

            deleteRelasjoner(hovedperson, relasjoner);
        }
    }

    private void deleteRelasjoner(DbPerson hovedPerson, List<DbPerson> relasjoner) {

        relasjoner.forEach(person -> {
            deleteRelasjon(person, hovedPerson.getIdent());
            deleteRelasjon(hovedPerson, person.getIdent());
        });
    }

    private void deleteRelasjon(DbPerson person, String relasjonIdent) {

        person.getRelasjoner()
                .removeIf(thisRelasjon -> thisRelasjon.getRelatertPerson().getIdent().equals(relasjonIdent));
    }
}
