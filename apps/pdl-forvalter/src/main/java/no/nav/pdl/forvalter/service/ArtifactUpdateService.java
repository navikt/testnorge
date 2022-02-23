package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DeltBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedfoedtBarnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FalskIdentitetDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdsadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TelefonnummerDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TilrettelagtKommunikasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskIdentifikasjonsnummerDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArtifactUpdateService {

    private final PersonRepository personRepository;

    public void updateFoedsel(String ident, Integer id, FoedselDTO foedsel) {

        var person = getPerson(ident);
        if (id == 0) {

        }
        if (person.getPerson().getFoedsel().stream().noneMatch(fodsel -> fodsel.getId().equals(id))) {}
    }

    public void updateNavn(String ident, Integer id, NavnDTO navn) {
    }

    public void updateKjoenn(String ident, Integer id, KjoennDTO kjoenn) {
    }

    public void updateBostedsadresse(String ident, Integer id, BostedadresseDTO bostedadresse) {
    }

    public void updateKontaktadresse(String ident, Integer id, KontaktadresseDTO kontaktadresse) {
    }

    public void updateOppholdsadresse(String ident, Integer id, OppholdsadresseDTO oppholdsadresse) {
    }

    public void updateInnflytting(String ident, Integer id, InnflyttingDTO innflytting) {
    }

    public void updateUtflytting(String ident, Integer id, UtflyttingDTO utflytting) {
    }

    public void updateDeltBosted(String ident, Integer id, DeltBostedDTO deltBosted) {
    }

    public void updateForelderBarnRelasjon(String ident, Integer id, ForelderBarnRelasjonDTO forelderBarnRelasjon) {
    }

    public void updateKontaktinformasjonForDoedsbo(String ident, Integer id, KontaktinformasjonForDoedsboDTO kontaktinformasjonForDoedsbo) {
    }

    public void updateUtenlandskIdentifikasjonsnummer(String ident, Integer id, UtenlandskIdentifikasjonsnummerDTO utenlandskIdentifikasjonsnummer) {
    }

    public void updateFalskIdentitet(String ident, Integer id, FalskIdentitetDTO falskIdentitet) {
    }

    public void updateAdressebeskyttelse(String ident, Integer id, AdressebeskyttelseDTO adressebeskyttelse) {
    }

    public void updateDoedsfall(String ident, Integer id, DoedsfallDTO doedsfall) {
    }

    public void updateFolkeregisterPersonstatus(String ident, Integer id, FolkeregisterPersonstatusDTO folkeregisterPersonstatus) {
    }

    public void updateTilrettelagtKommunikasjon(String ident, Integer id, TilrettelagtKommunikasjonDTO tilrettelagtKommunikasjon) {
    }

    public void updateStatsborgerskap(String ident, Integer id, StatsborgerskapDTO statsborgerskap) {
    }

    public void updateOpphold(String ident, Integer id, OppholdDTO opphold) {
    }

    public void updateSivilstand(String ident, Integer id, SivilstandDTO sivilstand) {
    }

    public void updateTelefonnummer(String ident, Integer id, TelefonnummerDTO telefonnummer) {
    }

    public void updateFullmakt(String ident, Integer id, FullmaktDTO fullmakt) {
    }

    public void updateVergemaal(String ident, Integer id, VergemaalDTO vergemaal) {
    }

    public void updateSikkerhetstiltak(String ident, Integer id, SikkerhetstiltakDTO sikkerhetstiltak) {
    }

    public void updateDoedfoedtBarn(String ident, Integer id, DoedfoedtBarnDTO doedfoedtBarn) {
    }

    private DbPerson getPerson(String ident){

        return personRepository.findByIdent(ident)
                .orElseThrow(() -> new NotFoundException("Finner ikke person med ident " + ident));
    }
}
