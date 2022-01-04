package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonArtifactService {

    private final AdressebeskyttelseService adressebeskyttelseService;
    private final BostedAdresseService bostedAdresseService;
    private final DoedsfallService doedsfallService;
    private final FoedselService foedselService;
    private final FolkeregisterPersonstatusService folkeregisterPersonstatusService;
    private final KjoennService kjoennService;
    private final KontaktAdresseService kontaktAdresseService;
    private final InnflyttingService innflyttingService;
    private final NavnService navnService;
    private final OppholdsadresseService oppholdsadresseService;
    private final OppholdService oppholdService;
    private final StatsborgerskapService statsborgerskapService;
    private final TelefonnummerService telefonnummerService;
    private final TilrettelagtKommunikasjonService tilrettelagtKommunikasjonService;
    private final UtflyttingService utflyttingService;
    private final FullmaktService fullmaktService;
    private final UtenlandsidentifikasjonsnummerService utenlandsidentifikasjonsnummerService;
    private final VergemaalService vergemaalService;
    private final FalskIdentitetService falskIdentitetService;
    private final KontaktinformasjonForDoedsboService kontaktinformasjonForDoedsboService;
    private final IdenttypeService identtypeService;
    private final SivilstandService sivilstandService;
    private final ForelderBarnRelasjonService forelderBarnRelasjonService;
    private final ForeldreansvarService foreldreansvarService;
    private final DeltBostedService deltBostedService;
    private final DoedfoedtBarnService doedfoedtBarnService;
    private final SikkerhetstiltakService sikkerhetstiltakService;

    public PersonDTO buildPerson(PersonDTO person, Boolean relaxed) {

        // Orders below matters to some degree, don´t rearrange without checking consequences
        person.setKjoenn(kjoennService.convert(person));
        person.setInnflytting(innflyttingService.convert(person.getInnflytting()));
        person.setStatsborgerskap(statsborgerskapService.convert(person));
        person.setBostedsadresse(bostedAdresseService.convert(person, relaxed));
        person.setFoedsel(foedselService.convert(person));
        person.setNavn(navnService.convert(person.getNavn()));
        person.setOppholdsadresse(oppholdsadresseService.convert(person));
        person.setAdressebeskyttelse(adressebeskyttelseService.convert(person));
        person.setTelefonnummer(telefonnummerService.convert(person.getTelefonnummer()));
        person.setUtflytting(utflyttingService.convert(person.getUtflytting()));
        person.setOpphold(oppholdService.convert(person.getOpphold()));
        person.setTilrettelagtKommunikasjon(tilrettelagtKommunikasjonService.convert(person.getTilrettelagtKommunikasjon()));
        person.setDoedsfall(doedsfallService.convert(person.getDoedsfall()));
        person.setFullmakt(fullmaktService.convert(person));
        person.setKontaktadresse(kontaktAdresseService.convert(person, relaxed));
        person.setUtenlandskIdentifikasjonsnummer(utenlandsidentifikasjonsnummerService
                .convert(person.getUtenlandskIdentifikasjonsnummer()));
        person.setVergemaal(vergemaalService.convert(person));
        person.setFalskIdentitet(falskIdentitetService.convert(person));
        person.setKontaktinformasjonForDoedsbo(kontaktinformasjonForDoedsboService.convert(person));
        person.setSivilstand(sivilstandService.convert(person));
        person.setForelderBarnRelasjon(forelderBarnRelasjonService.convert(person));
        person.setForeldreansvar(foreldreansvarService.convert(person));
        person.setDeltBosted(deltBostedService.convert(person));
        person.setDoedfoedtBarn(doedfoedtBarnService.convert(person.getDoedfoedtBarn()));
        person.setIdent(identtypeService.convert(person));
        person.setFolkeregisterPersonstatus(folkeregisterPersonstatusService.convert(person));
        person.setSikkerhetstiltak(sikkerhetstiltakService.convert(person));

        return person;
    }
}
