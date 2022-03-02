package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.BOSATT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.DOED;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.FORSVUNNET;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.IKKE_BOSATT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.INAKTIV;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.MIDLERTIDIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.OPPHOERT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.UTFLYTTET;

@Service
@RequiredArgsConstructor
public class GjeldendeArtifactService {

    private final PersonRepository personRepository;

    // Siste ligger først
    private static void setSisteGjeldende(List<? extends DbVersjonDTO> artifact, boolean active) {

        for (var i = 0; i < artifact.size(); i++) {
            artifact.get(i).setGjeldende(i == 0 && active);
        }
    }

    private static void setAlleGjeldende(List<? extends DbVersjonDTO> artifact, boolean status) {

        artifact.forEach(info -> info.setGjeldende(status));
    }

    private static void setIngenNorskAdresse(List<? extends AdresseDTO> adresser, boolean relevance) {

        if (relevance) {
            adresser.stream()
                    .filter(AdresseDTO::isAdresseNorge)
                    .forEach(adresse -> adresse.setGjeldende(false));
            adresser.stream()
                    .filter(AdresseDTO::isAdresseUtland)
                    .forEach(adresse -> adresse.setGjeldende(true));
        }
    }

    @Transactional
    public void setGjeldene(String ident) {

        var hovedperson = personRepository.findByIdent(ident);

        if (hovedperson.isPresent()) {
            setGjeldene(hovedperson.get().getPerson());
            var relasjoner = personRepository.findByIdentIn(
                    hovedperson.get().getRelasjoner().stream()
                            .map(DbRelasjon::getRelatertPerson)
                            .map(DbPerson::getIdent)
                            .toList(), Pageable.unpaged());

            relasjoner.forEach(relasjon -> setGjeldene(relasjon.getPerson()));
        }
    }

    private PersonDTO setGjeldene(PersonDTO person) {

        setSisteGjeldende(person.getFoedsel(), !person.isStatusIn(OPPHOERT));
        setSisteGjeldende(person.getNavn(), !person.isStatusIn(OPPHOERT));
        setSisteGjeldende(person.getKjoenn(), !person.isStatusIn(OPPHOERT));
        setSisteGjeldende(person.getBostedsadresse(), !person.isStatusIn(OPPHOERT, IKKE_BOSATT, FORSVUNNET, DOED));
        setSisteGjeldende(person.getKontaktadresse(), !person.isStatusIn(OPPHOERT, FORSVUNNET, DOED));
        setSisteGjeldende(person.getOppholdsadresse(), !person.isStatusIn(OPPHOERT, FORSVUNNET, DOED));
        setIngenNorskAdresse(person.getBostedsadresse(), person.isStatusIn(UTFLYTTET, MIDLERTIDIG, INAKTIV));
        setIngenNorskAdresse(person.getOppholdsadresse(), person.isStatusIn(UTFLYTTET));
        setIngenNorskAdresse(person.getKontaktadresse(), person.isStatusIn(UTFLYTTET));
        setSisteGjeldende(person.getInnflytting(), !person.isStatusIn(OPPHOERT, UTFLYTTET));
        setSisteGjeldende(person.getUtflytting(), !person.isStatusIn(OPPHOERT, BOSATT));
        setSisteGjeldende(person.getDeltBosted(), !person.isStatusIn(OPPHOERT, DOED));
        setSisteGjeldende(person.getForeldreansvar(), !person.isStatusIn(OPPHOERT));
        setSisteGjeldende(person.getUtenlandskIdentifikasjonsnummer(), !person.isStatusIn(OPPHOERT));
        setSisteGjeldende(person.getFalskIdentitet(), !person.isStatusIn(OPPHOERT));
        setSisteGjeldende(person.getAdressebeskyttelse(), !person.isStatusIn(OPPHOERT));
        setSisteGjeldende(person.getDoedsfall(), !person.isStatusIn(OPPHOERT));
        setSisteGjeldende(person.getFolkeregisterPersonstatus(), true);
        setSisteGjeldende(person.getTilrettelagtKommunikasjon(), !person.isStatusIn(OPPHOERT, DOED));
        setAlleGjeldende(person.getStatsborgerskap(), !person.isStatusIn(OPPHOERT));
        setAlleGjeldende(person.getForelderBarnRelasjon(), !person.isStatusIn(OPPHOERT));
        setAlleGjeldende(person.getTelefonnummer(), !person.isStatusIn(OPPHOERT, DOED));
        setSisteGjeldende(person.getOpphold(), !person.isStatusIn(OPPHOERT));
        setSisteGjeldende(person.getSivilstand(), !person.isStatusIn(OPPHOERT));
        setSisteGjeldende(person.getFullmakt(), !person.isStatusIn(OPPHOERT, DOED));
        setSisteGjeldende(person.getVergemaal(), !person.isStatusIn(OPPHOERT));
        setSisteGjeldende(person.getSikkerhetstiltak(), !person.isStatusIn(OPPHOERT, DOED));
        setAlleGjeldende(person.getDoedfoedtBarn(), !person.isStatusIn(OPPHOERT));
        setSisteGjeldende(person.getKontaktinformasjonForDoedsbo(), person.isStatusIn(DOED));
        setAlleGjeldende(person.getNyident(), false);

        return person;
    }
}
