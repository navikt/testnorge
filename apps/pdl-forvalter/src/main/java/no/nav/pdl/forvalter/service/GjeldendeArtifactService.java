package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedfoedtBarnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TelefonnummerDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
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

    private static List<? extends DbVersjonDTO> setAlleGjeldende(List<? extends DbVersjonDTO> artifact) {

        var unique = artifact.stream()
                .distinct()
                .toList();
        unique.forEach(fact -> fact.setGjeldende(true));
        return unique;
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
        person.setForelderBarnRelasjon((List<ForelderBarnRelasjonDTO>) setAlleGjeldende(person.getForelderBarnRelasjon()));
        person.setForeldreansvar((List<ForeldreansvarDTO>) setAlleGjeldende(person.getForeldreansvar()));
        setSisteGjeldende(person.getKontaktinformasjonForDoedsbo());
        setSisteGjeldende(person.getUtenlandskIdentifikasjonsnummer());
        setSisteGjeldende(person.getFalskIdentitet());
        setSisteGjeldende(person.getAdressebeskyttelse());
        setSisteGjeldende(person.getDoedsfall());
        setSisteGjeldende(person.getFolkeregisterPersonstatus());
        setSisteGjeldende(person.getTilrettelagtKommunikasjon());
        person.setStatsborgerskap((List<StatsborgerskapDTO>) setAlleGjeldende(person.getStatsborgerskap()));
        setSisteGjeldende(person.getOpphold());
        setSisteGjeldende(person.getSivilstand());
        person.setTelefonnummer((List<TelefonnummerDTO>) setAlleGjeldende(person.getTelefonnummer()));
        person.setFullmakt((List<FullmaktDTO>) setAlleGjeldende(person.getFullmakt()));
        person.setVergemaal((List<VergemaalDTO>) setAlleGjeldende(person.getVergemaal()));
        setSisteGjeldende(person.getSikkerhetstiltak());
        person.setDoedfoedtBarn((List<DoedfoedtBarnDTO>) setAlleGjeldende(person.getDoedfoedtBarn()));
    }
}
