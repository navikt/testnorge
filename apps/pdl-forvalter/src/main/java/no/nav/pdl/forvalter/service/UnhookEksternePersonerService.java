package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UnhookEksternePersonerService {

    private final PersonRepository personRepository;

    public void unhook(DbPerson hovedperson) {

        if (hovedperson.getPerson().getSivilstand().stream().anyMatch(SivilstandDTO::isEksisterendePerson)) {

            deleteSivilstandArtifact(hovedperson);
        }

        if (hovedperson.getPerson().getForelderBarnRelasjon().stream().anyMatch(ForelderBarnRelasjonDTO::isEksisterendePerson)) {

            deleteForelderBarnRelasjoner(hovedperson);
        }

        if (hovedperson.getPerson().getForeldreansvar().stream().anyMatch(ForeldreansvarDTO::isEksisterendePerson)) {

            deleteForeldreansvarRelasjoner(hovedperson);
        }

        if (hovedperson.getPerson().getFullmakt().stream().anyMatch(FullmaktDTO::isEksisterendePerson)) {

            deleteFullmaktRelasjoner(hovedperson);
        }

        if (hovedperson.getPerson().getVergemaal().stream().anyMatch(VergemaalDTO::isEksisterendePerson)) {

            deleteVergemaalRelasjoner(hovedperson);
        }

        if (hovedperson.getPerson().getKontaktinformasjonForDoedsbo().stream()
                .map(KontaktinformasjonForDoedsboDTO::getPersonSomKontakt)
                .filter(Objects::nonNull)
                .anyMatch(KontaktinformasjonForDoedsboDTO.KontaktpersonDTO::isEksisterendePerson)) {

            deleteKontaktinformasjonForDoedsboeRelasjoner(hovedperson);
        }
    }

    private void deleteSivilstandArtifact(DbPerson hovedperson) {

        var partnere = personRepository.findByIdentIn(hovedperson.getPerson().getSivilstand().stream()
                .filter(SivilstandDTO::isEksisterendePerson)
                .map(SivilstandDTO::getRelatertVedSivilstand)
                .toList(), Pageable.unpaged());

        partnere.stream()
                .map(DbPerson::getPerson)
                .forEach(person -> person.setSivilstand(person.getSivilstand().stream()
                        .filter(sivilstand -> !sivilstand.getRelatertVedSivilstand().equals(hovedperson.getIdent()))
                        .toList()));

        deleteRelasjoner(hovedperson, partnere);
    }

    private void deleteForelderBarnRelasjoner(DbPerson hovedperson) {

        var relasjoner = personRepository.findByIdentIn(hovedperson.getPerson().getForelderBarnRelasjon().stream()
                .filter(ForelderBarnRelasjonDTO::isEksisterendePerson)
                .map(ForelderBarnRelasjonDTO::getRelatertPerson)
                .toList(), Pageable.unpaged());

        relasjoner.stream()
                .map(DbPerson::getPerson)
                .forEach(person -> person.setForelderBarnRelasjon(person.getForelderBarnRelasjon().stream()
                        .filter(relasjon -> !relasjon.getRelatertPerson().equals(hovedperson.getIdent()))
                        .toList()));

        deleteRelasjoner(hovedperson, relasjoner);
    }

    private void deleteForeldreansvarRelasjoner(DbPerson hovedperson) {

        var ansvarlige = personRepository.findByIdentIn(hovedperson.getPerson().getForeldreansvar().stream()
                .filter(ForeldreansvarDTO::isEksisterendePerson)
                .map(ForeldreansvarDTO::getAnsvarlig)
                .toList(), Pageable.unpaged());

        deleteRelasjoner(hovedperson, ansvarlige);
    }

    private void deleteFullmaktRelasjoner(DbPerson hovedperson) {

        var fullmektigere = personRepository.findByIdentIn(hovedperson.getPerson().getFullmakt().stream()
                .filter(FullmaktDTO::isEksisterendePerson)
                .map(FullmaktDTO::getMotpartsPersonident)
                .toList(), Pageable.unpaged());

        deleteRelasjoner(hovedperson, fullmektigere);
    }

    private void deleteVergemaalRelasjoner(DbPerson hovedperson) {

        var verger = personRepository.findByIdentIn(hovedperson.getPerson().getVergemaal().stream()
                .filter(VergemaalDTO::isEksisterendePerson)
                .map(VergemaalDTO::getVergeIdent)
                .toList(), Pageable.unpaged());

        deleteRelasjoner(hovedperson, verger);
    }

    private void deleteKontaktinformasjonForDoedsboeRelasjoner(DbPerson hovedperson) {

        var kontaktpersoner = personRepository.findByIdentIn(hovedperson.getPerson().getKontaktinformasjonForDoedsbo().stream()
                .map(KontaktinformasjonForDoedsboDTO::getPersonSomKontakt)
                .filter(Objects::nonNull)
                .filter(KontaktinformasjonForDoedsboDTO.KontaktpersonDTO::isEksisterendePerson)
                .map(KontaktinformasjonForDoedsboDTO.KontaktpersonDTO::getIdentifikasjonsnummer)
                .toList(), Pageable.unpaged());

        deleteRelasjoner(hovedperson, kontaktpersoner);
    }

    private void deleteRelasjoner(DbPerson hovedPerson, List<DbPerson> relasjoner) {

        relasjoner.stream()
                .forEach(person -> {
                    deleteRelasjon(person, hovedPerson.getIdent());
                    deleteRelasjon(hovedPerson, person.getIdent());
                });
    }

    private void deleteRelasjon(DbPerson person, String relasjonIdent) {

        var relasjonIterator = person.getRelasjoner().iterator();
        while (relasjonIterator.hasNext()) {

            var thisRelasjon = relasjonIterator.next();
            if (thisRelasjon.getRelatertPerson().getIdent().equals(relasjonIdent)) {
                relasjonIterator.remove();
                break;
            }
        }
    }
}
