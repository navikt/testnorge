package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.DOED;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.FORSVUNNET;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.IKKE_BOSATT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.INAKTIV;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.MIDLERTIDIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.OPPHOERT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.UTFLYTTET;

@Service
public class GjeldendeArtifactService {

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
                    .filter(AdresseDTO::isNorskAdresse)
                    .forEach(adresse -> adresse.setGjeldende(false));
            adresser.stream()
                    .filter(AdresseDTO::isUtenlandskAdresse)
                    .forEach(adresse -> adresse.setGjeldende(true));
        }
    }

    public DbPerson setGjeldene(DbPerson person) {

        Stream.of(List.of(person.getPerson()),
                person.getRelasjoner().stream()
                        .map(DbRelasjon::getPerson)
                        .map(DbPerson::getPerson)
                        .toList())
                .flatMap(Collection::stream)
                .toList()
                .parallelStream()
                .forEach(this::setGjeldene);

        return person;
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
        setSisteGjeldende(person.getInnflytting(), !person.isStatusIn(UTFLYTTET));
        setSisteGjeldende(person.getUtflytting(), !person.isStatusIn(OPPHOERT));
        setSisteGjeldende(person.getDeltBosted(), !person.isStatusIn(OPPHOERT));
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
