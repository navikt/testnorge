package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ArtifactUpdateService {

    private static final String IDENT_NOT_FOUND = "Person med ident: %s ble ikke funnet";
    private static final String INFO_NOT_FOUND = "%s med id: %s ble ikke funnet";

    private final PersonRepository personRepository;

    private static void checkExists(List<? extends DbVersjonDTO> artifacter, String ident, Integer id) {

        if (artifacter.stream().noneMatch(artifact -> artifact.getId().equals(id))) {
            throw new NotFoundException(String.format(INFO_NOT_FOUND, ident, id));
        }
    }

    private static <T extends DbVersjonDTO> T initOpprett(List<? extends DbVersjonDTO> artifacter, T oppretting) {

        oppretting.setIsNew(true);
        oppretting.setId(artifacter.stream()
                .mapToInt(DbVersjonDTO::getId)
                .max().orElse(0) + 1);
        return oppretting;
    }

    private <T extends DbVersjonDTO> List<T> updateArtifact(List<T> artifacter, T artifact, String ident, Integer id) {

        if (id == 0) {
            artifacter.add(0, initOpprett(artifacter, artifact));
            return artifacter;

        } else {
            checkExists(artifacter, ident, id);
            return artifacter.stream()
                    .map(data -> data.getId() == id ? artifact : data)
                    .toList();
        }
    }

    public void updateFoedsel(String ident, Integer id, FoedselDTO oppdatertFoedsel) {

        var person = getPerson(ident);

        person.getPerson().setFoedsel(
                updateArtifact(person.getPerson().getFoedsel(), oppdatertFoedsel, ident, id));
    }

    public void updateNavn(String ident, Integer id, NavnDTO oppdatertNavn) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getNavn()
                    .add(0, initOpprett(person.getPerson().getNavn(), oppdatertNavn));

        } else {
            checkExists(person.getPerson().getNavn(), ident, id);
            person.getPerson().setNavn(person.getPerson().getNavn().stream()
                    .map(navn -> navn.getId() == id ? oppdatertNavn : navn)
                    .toList());
        }
    }

    public void updateKjoenn(String ident, Integer id, KjoennDTO oppdatertKjoenn) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getKjoenn()
                    .add(0, initOpprett(person.getPerson().getKjoenn(), oppdatertKjoenn));

        } else {
            checkExists(person.getPerson().getKjoenn(), ident, id);
            person.getPerson().setKjoenn(person.getPerson().getKjoenn().stream()
                    .map(kjoenn -> kjoenn.getId() == id ? oppdatertKjoenn : kjoenn)
                    .toList());
        }
    }

    public void updateBostedsadresse(String ident, Integer id, BostedadresseDTO oppdatertBostedadresse) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getBostedsadresse()
                    .add(0, initOpprett(person.getPerson().getBostedsadresse(),
                            oppdatertBostedadresse));

        } else {
            checkExists(person.getPerson().getBostedsadresse(), ident, id);
            person.getPerson().setBostedsadresse(person.getPerson().getBostedsadresse().stream()
                    .map(adresse -> adresse.getId() == id ? oppdatertBostedadresse : adresse)
                    .toList());
        }
    }

    public void updateKontaktadresse(String ident, Integer id, KontaktadresseDTO oppdatertAdresse) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getKontaktadresse()
                    .add(0, initOpprett(person.getPerson().getKontaktadresse(),
                            oppdatertAdresse));
        } else {
            checkExists(person.getPerson().getKontaktadresse(), ident, id);
            person.getPerson().setKontaktadresse(person.getPerson().getKontaktadresse().stream()
                    .map(adresse -> adresse.getId() == id ? oppdatertAdresse : adresse)
                    .toList());
        }
    }

    public void updateOppholdsadresse(String ident, Integer id, OppholdsadresseDTO oppdatertAdresse) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getOppholdsadresse()
                    .add(0, initOpprett(person.getPerson().getOppholdsadresse(),
                            oppdatertAdresse));

        } else {
            checkExists(person.getPerson().getOppholdsadresse(), ident, id);
            person.getPerson().setOppholdsadresse(person.getPerson().getOppholdsadresse().stream()
                    .map(adresse -> adresse.getId() == id ? oppdatertAdresse : adresse)
                    .toList());
        }
    }

    public void updateInnflytting(String ident, Integer id, InnflyttingDTO oppdatertInnflytting) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getInnflytting()
                    .add(0, initOpprett(person.getPerson().getInnflytting(), oppdatertInnflytting));

        } else {
            checkExists(person.getPerson().getInnflytting(), ident, id);
            person.getPerson().setInnflytting(person.getPerson().getInnflytting().stream()
                    .map(innflytting -> innflytting.getId() == id ? oppdatertInnflytting : innflytting)
                    .toList());
        }
    }

    public void updateUtflytting(String ident, Integer id, UtflyttingDTO oppdatertUtflytting) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getUtflytting()
                    .add(0, initOpprett(person.getPerson().getUtflytting(), oppdatertUtflytting));

        } else {
            checkExists(person.getPerson().getUtflytting(), ident, id);
            person.getPerson().setUtflytting(person.getPerson().getUtflytting().stream()
                    .map(utflytting -> utflytting.getId() == id ? oppdatertUtflytting : utflytting)
                    .toList());
        }
    }

    public void updateDeltBosted(String ident, Integer id, DeltBostedDTO oppdatertDeltBosted) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getDeltBosted()
                    .add(0, initOpprett(person.getPerson().getDeltBosted(), oppdatertDeltBosted));

        } else {
            checkExists(person.getPerson().getDeltBosted(), ident, id);
            person.getPerson().setDeltBosted(person.getPerson().getDeltBosted().stream()
                    .map(bosted -> bosted.getId() == id ? oppdatertDeltBosted : bosted)
                    .toList());
        }
    }

    public void updateForelderBarnRelasjon(String ident, Integer id, ForelderBarnRelasjonDTO oppdatertRelasjon) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getForelderBarnRelasjon()
                    .add(0, initOpprett(person.getPerson().getForelderBarnRelasjon(),
                            oppdatertRelasjon));

        } else {
            checkExists(person.getPerson().getForelderBarnRelasjon(), ident, id);
            person.getPerson().setForelderBarnRelasjon(person.getPerson().getForelderBarnRelasjon().stream()
                    .map(foedsel -> foedsel.getId() == id ? oppdatertRelasjon : foedsel)
                    .toList());
        }
    }

    public void updateKontaktinformasjonForDoedsbo(String ident, Integer id, KontaktinformasjonForDoedsboDTO oppdatertInformasjon) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getKontaktinformasjonForDoedsbo()
                    .add(0, initOpprett(person.getPerson()
                            .getKontaktinformasjonForDoedsbo(), oppdatertInformasjon));

        } else {
            checkExists(person.getPerson().getKontaktinformasjonForDoedsbo(), ident, id);
            person.getPerson().setKontaktinformasjonForDoedsbo(person.getPerson()
                    .getKontaktinformasjonForDoedsbo().stream()
                    .map(informasjon -> informasjon.getId() == id ? oppdatertInformasjon : informasjon)
                    .toList());
        }
    }

    public void updateUtenlandskIdentifikasjonsnummer(String ident, Integer id, UtenlandskIdentifikasjonsnummerDTO oppdatertIdentifikasjon) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getUtenlandskIdentifikasjonsnummer()
                    .add(0, initOpprett(person.getPerson()
                            .getUtenlandskIdentifikasjonsnummer(), oppdatertIdentifikasjon));

        } else {
            checkExists(person.getPerson().getUtenlandskIdentifikasjonsnummer(), ident, id);
            person.getPerson().setUtenlandskIdentifikasjonsnummer(person.getPerson()
                    .getUtenlandskIdentifikasjonsnummer().stream()
                    .map(foedsel -> foedsel.getId() == id ? oppdatertIdentifikasjon : foedsel)
                    .toList());
        }
    }

    public void updateFalskIdentitet(String ident, Integer id, FalskIdentitetDTO oppdatertIdentitet) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getFalskIdentitet()
                    .add(0, initOpprett(person.getPerson().getFalskIdentitet(), oppdatertIdentitet));

        } else {
            checkExists(person.getPerson().getUtenlandskIdentifikasjonsnummer(), ident, id);
            person.getPerson().setFalskIdentitet(person.getPerson().getFalskIdentitet().stream()
                    .map(identitet -> identitet.getId() == id ? oppdatertIdentitet : identitet)
                    .toList());
        }
    }

    public void updateAdressebeskyttelse(String ident, Integer id, AdressebeskyttelseDTO oppdatertBeskyttelse) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getAdressebeskyttelse()
                    .add(0, initOpprett(person.getPerson().getAdressebeskyttelse(), oppdatertBeskyttelse));

        } else {
            checkExists(person.getPerson().getAdressebeskyttelse(), ident, id);
            person.getPerson().setAdressebeskyttelse(person.getPerson().getAdressebeskyttelse().stream()
                    .map(beskyttelse -> beskyttelse.getId() == id ? oppdatertBeskyttelse : beskyttelse)
                    .toList());
        }
    }

    public void updateDoedsfall(String ident, Integer id, DoedsfallDTO oppdatertDoedsfall) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getDoedsfall()
                    .add(0, initOpprett(person.getPerson().getDoedsfall(), oppdatertDoedsfall));

        } else {
            checkExists(person.getPerson().getDoedsfall(), ident, id);
            person.getPerson().setDoedsfall(person.getPerson().getDoedsfall().stream()
                    .map(doedsfall -> doedsfall.getId() == id ? oppdatertDoedsfall : doedsfall)
                    .toList());
        }
    }

    public void updateFolkeregisterPersonstatus(String ident, Integer id, FolkeregisterPersonstatusDTO oppdatertStatus) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getFolkeregisterPersonstatus()
                    .add(0, initOpprett(person.getPerson()
                            .getFolkeregisterPersonstatus(), oppdatertStatus));

        } else {
            checkExists(person.getPerson().getFolkeregisterPersonstatus(), ident, id);
            person.getPerson().setFolkeregisterPersonstatus(person.getPerson().getFolkeregisterPersonstatus().stream()
                    .map(status -> status.getId() == id ? oppdatertStatus : status)
                    .toList());
        }
    }

    public void updateTilrettelagtKommunikasjon(String ident, Integer id, TilrettelagtKommunikasjonDTO oppdatertKommunikasjon) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getTilrettelagtKommunikasjon()
                    .add(0, initOpprett(person.getPerson()
                            .getTilrettelagtKommunikasjon(), oppdatertKommunikasjon));

        } else {
            checkExists(person.getPerson().getTilrettelagtKommunikasjon(), ident, id);
            person.getPerson().setTilrettelagtKommunikasjon(person.getPerson().getTilrettelagtKommunikasjon().stream()
                    .map(kommunikasjon -> kommunikasjon.getId() == id ? oppdatertKommunikasjon : kommunikasjon)
                    .toList());
        }
    }

    public void updateStatsborgerskap(String ident, Integer id, StatsborgerskapDTO oppdatertStatsborgerskap) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getStatsborgerskap()
                    .add(0, initOpprett(person.getPerson().getStatsborgerskap(),
                            oppdatertStatsborgerskap));

        } else {
            checkExists(person.getPerson().getStatsborgerskap(), ident, id);
            person.getPerson().setStatsborgerskap(person.getPerson().getStatsborgerskap().stream()
                    .map(statsborgerskap -> statsborgerskap.getId() == id ? oppdatertStatsborgerskap : statsborgerskap)
                    .toList());
        }
    }

    public void updateOpphold(String ident, Integer id, OppholdDTO oppdatertOpphold) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getOpphold()
                    .add(0, initOpprett(person.getPerson().getOpphold(), oppdatertOpphold));

        } else {
            checkExists(person.getPerson().getOpphold(), ident, id);
            person.getPerson().setOpphold(person.getPerson().getOpphold().stream()
                    .map(opphold -> opphold.getId() == id ? oppdatertOpphold : opphold)
                    .toList());
        }
    }

    public void updateSivilstand(String ident, Integer id, SivilstandDTO oppdatertSivilstand) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getSivilstand()
                    .add(0, initOpprett(person.getPerson().getSivilstand(), oppdatertSivilstand));

        } else {
            checkExists(person.getPerson().getSivilstand(), ident, id);
            person.getPerson().setSivilstand(person.getPerson().getSivilstand().stream()
                    .map(sivilstand -> sivilstand.getId() == id ? oppdatertSivilstand : sivilstand)
                    .toList());
        }
    }

    public void updateTelefonnummer(String ident, Integer id, TelefonnummerDTO oppdatertTelefonnummer) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getTelefonnummer()
                    .add(0, initOpprett(person.getPerson().getTelefonnummer(),
                            oppdatertTelefonnummer));

        } else {
            checkExists(person.getPerson().getTelefonnummer(), ident, id);
            person.getPerson().setTelefonnummer(person.getPerson().getTelefonnummer().stream()
                    .map(telefonnummer -> telefonnummer.getId() == id ? oppdatertTelefonnummer : telefonnummer)
                    .toList());
        }
    }

    public void updateFullmakt(String ident, Integer id, FullmaktDTO oppdatertFullmakt) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getFullmakt()
                    .add(0, initOpprett(person.getPerson().getFullmakt(), oppdatertFullmakt));

        } else {
            checkExists(person.getPerson().getFullmakt(), ident, id);
            person.getPerson().setFullmakt(person.getPerson().getFullmakt().stream()
                    .map(fullmakt -> fullmakt.getId() == id ? oppdatertFullmakt : fullmakt)
                    .toList());
        }
    }

    public void updateVergemaal(String ident, Integer id, VergemaalDTO oppdatertVergemaal) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getVergemaal()
                    .add(0, initOpprett(person.getPerson().getVergemaal(), oppdatertVergemaal));

        } else {
            checkExists(person.getPerson().getVergemaal(), ident, id);
            person.getPerson().setVergemaal(person.getPerson().getVergemaal().stream()
                    .map(vergemaal -> vergemaal.getId() == id ? oppdatertVergemaal : vergemaal)
                    .toList());
        }
    }

    public void updateSikkerhetstiltak(String ident, Integer id, SikkerhetstiltakDTO oppdatertSikkerhetstiltak) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getSikkerhetstiltak()
                    .add(0, initOpprett(person.getPerson().getSikkerhetstiltak(), oppdatertSikkerhetstiltak));

        } else {
            checkExists(person.getPerson().getSikkerhetstiltak(), ident, id);
            person.getPerson().setSikkerhetstiltak(person.getPerson().getSikkerhetstiltak().stream()
                    .map(sikkerhetstiltak -> sikkerhetstiltak.getId() == id ? oppdatertSikkerhetstiltak : sikkerhetstiltak)
                    .toList());
        }
    }

    public void updateDoedfoedtBarn(String ident, Integer id, DoedfoedtBarnDTO oppdatertDoedfoedt) {

        var person = getPerson(ident);
        if (id == 0) {
            person.getPerson().getDoedfoedtBarn()
                    .add(0, initOpprett(person.getPerson().getDoedfoedtBarn(), oppdatertDoedfoedt));

        } else {
            checkExists(person.getPerson().getDoedfoedtBarn(), ident, id);
            person.getPerson().setDoedfoedtBarn(person.getPerson().getDoedfoedtBarn().stream()
                    .map(doedfoedt -> doedfoedt.getId() == id ? oppdatertDoedfoedt : doedfoedt)
                    .toList());
        }
    }

    private DbPerson getPerson(String ident) {

        return personRepository.findByIdent(ident)
                .orElseThrow(() -> new NotFoundException(String.format(IDENT_NOT_FOUND, ident)));
    }
}
