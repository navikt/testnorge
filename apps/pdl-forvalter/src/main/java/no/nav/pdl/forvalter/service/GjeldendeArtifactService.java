package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.IdentRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GjeldendeArtifactService {

    // Siste ligger først
    private static void setSisteGjeldende(List<? extends DbVersjonDTO> artifact) {

        for (var i = 0; i < artifact.size(); i++) {
            artifact.get(i).setGjeldende(i == 0);
        }
    }

    private static void setAlleGjeldende(List<? extends DbVersjonDTO> artifact) {

        artifact.forEach(info -> info.setGjeldende(true));
    }

    private static void setIngenGjeldende(List<IdentRequestDTO> nyident) {

        nyident.forEach(nyIdent -> nyIdent.setGjeldende(false));
    }

    public void setGjeldene(PersonDTO person) {

        setSisteGjeldende(person.getFoedsel());
        setSisteGjeldende(person.getNavn());
        setSisteGjeldende(person.getKjoenn());
        setSisteGjeldende(person.getBostedsadresse());
        setSisteGjeldende(person.getKontaktadresse());
        setSisteGjeldende(person.getOppholdsadresse());
        setSisteGjeldende(person.getInnflytting());
        setSisteGjeldende(person.getUtflytting());
        setSisteGjeldende(person.getDeltBosted());
        setAlleGjeldende(person.getForelderBarnRelasjon());
        setSisteGjeldende(person.getForeldreansvar());
        setSisteGjeldende(person.getKontaktinformasjonForDoedsbo());
        setSisteGjeldende(person.getUtenlandskIdentifikasjonsnummer());
        setSisteGjeldende(person.getFalskIdentitet());
        setSisteGjeldende(person.getAdressebeskyttelse());
        setSisteGjeldende(person.getDoedsfall());
        setSisteGjeldende(person.getFolkeregisterPersonstatus());
        setSisteGjeldende(person.getTilrettelagtKommunikasjon());
        setAlleGjeldende(person.getStatsborgerskap());
        setSisteGjeldende(person.getOpphold());
        setSisteGjeldende(person.getSivilstand());
        setAlleGjeldende(person.getTelefonnummer());
        setSisteGjeldende(person.getFullmakt());
        setSisteGjeldende(person.getVergemaal());
        setSisteGjeldende(person.getSikkerhetstiltak());
        setAlleGjeldende(person.getDoedfoedtBarn());
        setIngenGjeldende(person.getNyident());
    }
}
