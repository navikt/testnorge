package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
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
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdsadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.EKTEFELLE_PARTNER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FAMILIERELASJON_BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FAMILIERELASJON_FORELDER;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;

@Service
@Transactional
@RequiredArgsConstructor
public class ArtifactUpdateService {

    private static final String IDENT_NOT_FOUND = "Person med ident: %s ble ikke funnet";
    private static final String INFO_NOT_FOUND = "%s med id: %s ble ikke funnet";

    private final PersonRepository personRepository;
    private final PersonService personService;
    private final AdressebeskyttelseService adressebeskyttelseService;
    private final BostedAdresseService bostedAdresseService;
    private final DeltBostedService deltBostedService;
    private final DoedfoedtBarnService doedfoedtBarnService;
    private final DoedsfallService doedsfallService;
    private final FalskIdentitetService falskIdentitetService;
    private final FoedselService foedselService;
    private final FolkeregisterPersonstatusService folkeregisterPersonstatusService;
    private final ForelderBarnRelasjonService forelderBarnRelasjonService;
    private final ForeldreansvarService foreldreansvarService;
    private final FullmaktService fullmaktService;
    private final KjoennService kjoennService;
    private final KontaktAdresseService kontaktAdresseService;
    private final KontaktinformasjonForDoedsboService kontaktinformasjonForDoedsboService;
    private final InnflyttingService innflyttingService;
    private final NavnService navnService;
    private final OppholdsadresseService oppholdsadresseService;
    private final OppholdService oppholdService;
    private final SivilstandService sivilstandService;
    private final StatsborgerskapService statsborgerskapService;
    private final TelefonnummerService telefonnummerService;
    private final TilrettelagtKommunikasjonService tilrettelagtKommunikasjonService;
    private final UtenlandsidentifikasjonsnummerService utenlandsidentifikasjonsnummerService;
    private final UtflyttingService utflyttingService;
    private final VergemaalService vergemaalService;
    private final SikkerhetstiltakService sikkerhetstiltakService;

    private static <T extends DbVersjonDTO> void checkExists(List<T> artifacter, Integer id, String navn) {

        if (artifacter.stream().noneMatch(artifact -> artifact.getId().equals(id))) {
            throw new NotFoundException(String.format(INFO_NOT_FOUND, navn, id));
        }
    }

    private static <T extends DbVersjonDTO> T initOpprett(List<T> artifacter, T oppretting) {

        oppretting.setId(artifacter.stream()
                .mapToInt(DbVersjonDTO::getId)
                .max().orElse(0) + 1);
        return oppretting;
    }

    private static void deleteRelasjon(DbPerson person, String tidligereRelatert, RelasjonType type) {

        Iterator<DbRelasjon> it = person.getRelasjoner().iterator();
        while (it.hasNext()) {
            var relasjon = it.next();
            if (type == relasjon.getRelasjonType() &&
                    relasjon.getPerson().getIdent().equals(person.getIdent()) &&
                    relasjon.getRelatertPerson().getIdent().equals(tidligereRelatert)) {

                it.remove();
            }
        }
    }

    private static RelasjonType getRelasjonstype(ForelderBarnRelasjonDTO.Rolle rolle) {

        return switch (rolle) {
            case BARN -> FAMILIERELASJON_BARN;
            case MOR, MEDMOR, FAR, FORELDER -> FAMILIERELASJON_FORELDER;
        };
    }

    private <T extends DbVersjonDTO> List<T> updateArtifact(List<T> artifacter, T artifact,
                                                            Integer id, String navn) {

        artifact.setIsNew(true);
        if (id.equals(0)) {
            artifacter.add(0, initOpprett(artifacter, artifact));
            return artifacter;

        } else {
            checkExists(artifacter, id, navn);
            return new ArrayList<>(artifacter.stream()
                    .map(data -> {
                        if (data.getId().equals(id)) {
                            artifact.setId(id);
                            return artifact;
                        }
                        return data;
                    })
                    .toList());
        }
    }

    public void updateFoedsel(String ident, Integer id, FoedselDTO oppdatertFoedsel) {

        var person = getPerson(ident);

        person.getPerson().setFoedsel(
                updateArtifact(person.getPerson().getFoedsel(), oppdatertFoedsel, id, "Foedsel"));

        foedselService.validate(oppdatertFoedsel, person.getPerson());
        foedselService.convert(person.getPerson());
    }

    public void updateNavn(String ident, Integer id, NavnDTO oppdatertNavn) {

        var person = getPerson(ident);

        person.getPerson().setNavn(
                updateArtifact(person.getPerson().getNavn(), oppdatertNavn, id, "Navn"));

        navnService.validate(oppdatertNavn);
        navnService.convert(person.getPerson().getNavn());
    }

    public void updateKjoenn(String ident, Integer id, KjoennDTO oppdatertKjoenn) {

        var person = getPerson(ident);

        person.getPerson().setKjoenn(
                updateArtifact(person.getPerson().getKjoenn(), oppdatertKjoenn, id, "Kjoenn"));

        kjoennService.validate(oppdatertKjoenn, person.getPerson());
        kjoennService.convert(person.getPerson());
    }

    public void updateBostedsadresse(String ident, Integer id, BostedadresseDTO oppdatertAdresse) {

        var person = getPerson(ident);

        person.getPerson().setBostedsadresse(
                updateArtifact(person.getPerson().getBostedsadresse(), oppdatertAdresse, id, "Bostedsadresse"));

        bostedAdresseService.validate(oppdatertAdresse, person.getPerson());
        bostedAdresseService.convert(person.getPerson(), false);
    }

    public void updateKontaktadresse(String ident, Integer id, KontaktadresseDTO oppdatertAdresse) {

        var person = getPerson(ident);

        person.getPerson().setKontaktadresse(
                updateArtifact(person.getPerson().getKontaktadresse(), oppdatertAdresse, id, "Kontaktadresse"));

        kontaktAdresseService.validate(oppdatertAdresse, person.getPerson());
        kontaktAdresseService.convert(person.getPerson(), false);
    }

    public void updateOppholdsadresse(String ident, Integer id, OppholdsadresseDTO oppdatertAdresse) {

        var person = getPerson(ident);

        person.getPerson().setOppholdsadresse(
                updateArtifact(person.getPerson().getOppholdsadresse(), oppdatertAdresse, id, "Oppholdsadresse"));

        oppholdsadresseService.validate(oppdatertAdresse, person.getPerson());
        oppholdsadresseService.convert(person.getPerson());
    }

    public void updateInnflytting(String ident, Integer id, InnflyttingDTO oppdatertInnflytting) {

        var person = getPerson(ident);

        person.getPerson().setInnflytting(
                updateArtifact(person.getPerson().getInnflytting(), oppdatertInnflytting, id, "Innflytting"));

        innflyttingService.validate(oppdatertInnflytting);
        innflyttingService.convert(person.getPerson().getInnflytting());
    }

    public void updateUtflytting(String ident, Integer id, UtflyttingDTO oppdatertUtflytting) {

        var person = getPerson(ident);

        person.getPerson().setUtflytting(
                updateArtifact(person.getPerson().getUtflytting(), oppdatertUtflytting, id, "Utflytting"));

        utflyttingService.validate(oppdatertUtflytting);
        utflyttingService.convert(person.getPerson().getUtflytting());
    }

    public void updateDeltBosted(String ident, Integer id, DeltBostedDTO oppdatertDeltBosted) {

        var person = getPerson(ident);

        person.getPerson().setDeltBosted(
                updateArtifact(person.getPerson().getDeltBosted(), oppdatertDeltBosted, id, "DeltBosted"));

        deltBostedService.validate(oppdatertDeltBosted, person.getPerson());
        deltBostedService.convert(person.getPerson());
    }

    public void updateForelderBarnRelasjon(String ident, Integer id, ForelderBarnRelasjonDTO oppdatertRelasjon) {

        var person = getPerson(ident);

        var tidligereRelatert = id > 0 && id <= person.getPerson().getForelderBarnRelasjon().size() ?
                person.getPerson().getForelderBarnRelasjon().get(id - 1).getRelatertPerson() : null;
        var tidligereMinRolle = id > 0 && id <= person.getPerson().getForelderBarnRelasjon().size() ?
                person.getPerson().getForelderBarnRelasjon().get(id - 1).getMinRolleForPerson() : null;
        var tidligereRelatertRolle = id > 0 && id <= person.getPerson().getForelderBarnRelasjon().size() ?
                person.getPerson().getForelderBarnRelasjon().get(id - 1).getRelatertPersonsRolle() : null;
        var isEksisterendePerson = id > 0 && id <= person.getPerson().getForelderBarnRelasjon().size() ?
                person.getPerson().getForelderBarnRelasjon().get(id - 1).getEksisterendePerson() : null;

        person.getPerson().setForelderBarnRelasjon(
                updateArtifact(person.getPerson().getForelderBarnRelasjon(), oppdatertRelasjon, id, "ForelderBarnRelasjon"));

        forelderBarnRelasjonService.validate(oppdatertRelasjon);
        forelderBarnRelasjonService.convert(person.getPerson());

        if (!tidligereRelatert.equals(oppdatertRelasjon.getRelatertPerson()) ||
                !tidligereMinRolle.equals(oppdatertRelasjon.getMinRolleForPerson()) ||
                !tidligereRelatertRolle.equals(oppdatertRelasjon.getRelatertPersonsRolle())) {

            deleteRelasjon(person, tidligereRelatert, getRelasjonstype(tidligereMinRolle));
            deleteRelasjon(getPerson(tidligereRelatert), ident, getRelasjonstype(tidligereRelatertRolle));
            deletePerson(tidligereRelatert, isEksisterendePerson);
        }
    }

    public void updateForeldreansvar(String ident, Integer id, ForeldreansvarDTO oppdatertAnsvar) {

        var person = getPerson(ident);

        person.getPerson().setForeldreansvar(
                updateArtifact(person.getPerson().getForeldreansvar(), oppdatertAnsvar, id, "Foreldreansvar"));

        foreldreansvarService.validate(oppdatertAnsvar, person.getPerson());
        foreldreansvarService.convert(person.getPerson());
    }

    public void updateKontaktinformasjonForDoedsbo(String ident, Integer id, KontaktinformasjonForDoedsboDTO oppdatertInformasjon) {

        var person = getPerson(ident);

        person.getPerson().setKontaktinformasjonForDoedsbo(
                updateArtifact(person.getPerson().getKontaktinformasjonForDoedsbo(), oppdatertInformasjon, id, "KontaktinformasjonForDoedsbo"));

        kontaktinformasjonForDoedsboService.validate(oppdatertInformasjon);
        kontaktinformasjonForDoedsboService.convert(person.getPerson());
    }

    public void updateUtenlandskIdentifikasjonsnummer(String ident, Integer id, UtenlandskIdentifikasjonsnummerDTO oppdatertIdentifikasjon) {

        var person = getPerson(ident);

        person.getPerson().setUtenlandskIdentifikasjonsnummer(
                updateArtifact(person.getPerson().getUtenlandskIdentifikasjonsnummer(), oppdatertIdentifikasjon, id, "UtenlandskIdentifikasjonsnummer"));

        utenlandsidentifikasjonsnummerService.validate(oppdatertIdentifikasjon);
        utenlandsidentifikasjonsnummerService.convert(person.getPerson().getUtenlandskIdentifikasjonsnummer());
    }

    public void updateFalskIdentitet(String ident, Integer id, FalskIdentitetDTO oppdatertIdentitet) {

        var person = getPerson(ident);

        person.getPerson().setFalskIdentitet(
                updateArtifact(person.getPerson().getFalskIdentitet(), oppdatertIdentitet, id, "FalskIdentitet"));

        falskIdentitetService.validate(oppdatertIdentitet);
        falskIdentitetService.convert(person.getPerson());
    }

    public void updateAdressebeskyttelse(String ident, Integer id, AdressebeskyttelseDTO oppdatertBeskyttelse) {

        var person = getPerson(ident);

        person.getPerson().setAdressebeskyttelse(
                updateArtifact(person.getPerson().getAdressebeskyttelse(), oppdatertBeskyttelse, id, "Adressebeskyttelse"));

        adressebeskyttelseService.validate(oppdatertBeskyttelse, person.getPerson());
        adressebeskyttelseService.convert(person.getPerson());
    }

    public void updateDoedsfall(String ident, Integer id, DoedsfallDTO oppdatertDoedsfall) {

        var person = getPerson(ident);

        person.getPerson().setDoedsfall(
                updateArtifact(person.getPerson().getDoedsfall(), oppdatertDoedsfall, id, "Doedsfall"));

        doedsfallService.validate(oppdatertDoedsfall);
        doedsfallService.convert(person.getPerson().getDoedsfall());
    }

    public void updateFolkeregisterPersonstatus(String ident, Integer id, FolkeregisterPersonstatusDTO oppdatertStatus) {

        var person = getPerson(ident);

        person.getPerson().setFolkeregisterPersonstatus(
                updateArtifact(person.getPerson().getFolkeregisterPersonstatus(), oppdatertStatus, id, "FolkeregisterPersonstatus"));

        folkeregisterPersonstatusService.validate(oppdatertStatus, person.getPerson());
        folkeregisterPersonstatusService.convert(person.getPerson());
    }

    public void updateTilrettelagtKommunikasjon(String ident, Integer id, TilrettelagtKommunikasjonDTO oppdatertKommunikasjon) {

        var person = getPerson(ident);

        person.getPerson().setTilrettelagtKommunikasjon(
                updateArtifact(person.getPerson().getTilrettelagtKommunikasjon(), oppdatertKommunikasjon, id, "TilrettelagtKommunikasjon"));

        tilrettelagtKommunikasjonService.validate(oppdatertKommunikasjon);
        tilrettelagtKommunikasjonService.convert(person.getPerson().getTilrettelagtKommunikasjon());
    }

    public void updateStatsborgerskap(String ident, Integer id, StatsborgerskapDTO oppdatertStatsborgerskap) {

        var person = getPerson(ident);

        person.getPerson().setStatsborgerskap(
                updateArtifact(person.getPerson().getStatsborgerskap(), oppdatertStatsborgerskap, id, "Statsborgerskap"));

        statsborgerskapService.validate(oppdatertStatsborgerskap);
        statsborgerskapService.convert(person.getPerson());
    }

    public void updateOpphold(String ident, Integer id, OppholdDTO oppdatertOpphold) {

        var person = getPerson(ident);

        person.getPerson().setOpphold(
                updateArtifact(person.getPerson().getOpphold(), oppdatertOpphold, id, "Opphold"));

        oppholdService.validate(oppdatertOpphold);
        oppholdService.convert(person.getPerson().getOpphold());
    }

    public void updateSivilstand(String ident, Integer id, SivilstandDTO oppdatertSivilstand) {

        var person = getPerson(ident);

        var tidligereRelatert = id > 0 && id <= person.getPerson().getSivilstand().size() ?
                person.getPerson().getSivilstand().get(id - 1).getRelatertVedSivilstand() : null;
        var isEksisterendePerson = id > 0 && id <= person.getPerson().getSivilstand().size() ?
                person.getPerson().getSivilstand().get(id - 1).getEksisterendePerson() : null;

        person.getPerson().setSivilstand(
                updateArtifact(person.getPerson().getSivilstand(), oppdatertSivilstand, id, "Sivilstand"));

        sivilstandService.validate(oppdatertSivilstand);
        sivilstandService.convert(person.getPerson());

        if (nonNull(tidligereRelatert) && !oppdatertSivilstand.getRelatertVedSivilstand().equals(tidligereRelatert)) {

            deleteRelasjon(person, tidligereRelatert, EKTEFELLE_PARTNER);
            deleteRelasjon(getPerson(tidligereRelatert), ident, EKTEFELLE_PARTNER);
            deletePerson(tidligereRelatert, isEksisterendePerson);
        }
    }

    private void deletePerson(String tidligereRelatert, Boolean isEksisterendePerson) {

        if (isNotTrue(isEksisterendePerson)) {
            personService.deletePerson(tidligereRelatert);
        }
    }

    public void updateTelefonnummer(String ident, Integer id, TelefonnummerDTO oppdatertTelefonnummer) {

        var person = getPerson(ident);

        person.getPerson().setTelefonnummer(
                updateArtifact(person.getPerson().getTelefonnummer(), oppdatertTelefonnummer, id, "Telefonnummer"));

        telefonnummerService.validate(oppdatertTelefonnummer);
        telefonnummerService.convert(person.getPerson().getTelefonnummer());
    }

    public void updateFullmakt(String ident, Integer id, FullmaktDTO oppdatertFullmakt) {

        var person = getPerson(ident);

        person.getPerson().setFullmakt(
                updateArtifact(person.getPerson().getFullmakt(), oppdatertFullmakt, id, "Fullmakt"));

        fullmaktService.validate(oppdatertFullmakt);
        fullmaktService.convert(person.getPerson());
    }

    public void updateVergemaal(String ident, Integer id, VergemaalDTO oppdatertVergemaal) {

        var person = getPerson(ident);

        person.getPerson().setVergemaal(
                updateArtifact(person.getPerson().getVergemaal(), oppdatertVergemaal, id, "Vergemaal"));

        vergemaalService.validate(oppdatertVergemaal);
        vergemaalService.convert(person.getPerson());
    }

    public void updateSikkerhetstiltak(String ident, Integer id, SikkerhetstiltakDTO oppdatertSikkerhetstiltak) {

        var person = getPerson(ident);

        person.getPerson().setSikkerhetstiltak(
                updateArtifact(person.getPerson().getSikkerhetstiltak(), oppdatertSikkerhetstiltak, id, "Sikkerhetstiltak"));

        sikkerhetstiltakService.validate(oppdatertSikkerhetstiltak);
        sikkerhetstiltakService.convert(person.getPerson());
    }

    public void updateDoedfoedtBarn(String ident, Integer id, DoedfoedtBarnDTO oppdatertDoedfoedt) {

        var person = getPerson(ident);

        person.getPerson().setDoedfoedtBarn(
                updateArtifact(person.getPerson().getDoedfoedtBarn(), oppdatertDoedfoedt, id, "DoedfoedtBarn"));

        doedfoedtBarnService.validate(oppdatertDoedfoedt);
        doedfoedtBarnService.convert(person.getPerson().getDoedfoedtBarn());
    }

    private DbPerson getPerson(String ident) {

        return personRepository.findByIdent(ident)
                .orElseThrow(() -> new NotFoundException(String.format(IDENT_NOT_FOUND, ident)));
    }
}
