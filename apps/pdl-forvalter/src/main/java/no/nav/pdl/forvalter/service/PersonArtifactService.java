package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonArtifactService {

    private final AdressebeskyttelseService adressebeskyttelseService;
    private final BostedAdresseService bostedAdresseService;
    private final DeltBostedService deltBostedService;
    private final DoedfoedtBarnService doedfoedtBarnService;
    private final DoedsfallService doedsfallService;
    private final FalskIdentitetService falskIdentitetService;
    private final FoedestedService foedestedService;
    private final FoedselService foedselService;
    private final FoedselsdatoService foedselsdatoService;
    private final FolkeregisterPersonstatusService folkeregisterPersonstatusService;
    private final ForelderBarnRelasjonService forelderBarnRelasjonService;
    private final ForeldreansvarService foreldreansvarService;
    private final FullmaktService fullmaktService;
    private final IdenttypeService identtypeService;
    private final InnflyttingService innflyttingService;
    private final KjoennService kjoennService;
    private final KontaktAdresseService kontaktAdresseService;
    private final KontaktinformasjonForDoedsboService kontaktinformasjonForDoedsboService;
    private final NavnService navnService;
    private final NavPersonIdentifikatorService navPersonIdentifikatorService;
    private final OppholdService oppholdService;
    private final OppholdsadresseService oppholdsadresseService;
    private final SikkerhetstiltakService sikkerhetstiltakService;
    private final SivilstandService sivilstandService;
    private final StatsborgerskapService statsborgerskapService;
    private final TelefonnummerService telefonnummerService;
    private final TilrettelagtKommunikasjonService tilrettelagtKommunikasjonService;
    private final UtenlandsidentifikasjonsnummerService utenlandsidentifikasjonsnummerService;
    private final UtflyttingService utflyttingService;
    private final VergemaalService vergemaalService;

    public PersonDTO buildPerson(PersonDTO person, Boolean relaxed) {

        // Orders below matters to some degree, don´t rearrange without checking consequences
        person.setFoedsel(foedselService.convert(person));
        person.setFoedselsdato(foedselsdatoService.convert(person));
        person.setKjoenn(kjoennService.convert(person));
        person.setBostedsadresse(bostedAdresseService.convert(person, relaxed));
        person.setInnflytting(innflyttingService.convert(person));
        person.setFoedested(foedestedService.convert(person));
        person.setStatsborgerskap(statsborgerskapService.convert(person));
        person.setNavn(navnService.convert(person));
        person.setOppholdsadresse(oppholdsadresseService.convert(person));
        person.setAdressebeskyttelse(adressebeskyttelseService.convert(person));
        person.setTelefonnummer(telefonnummerService.convert(person.getTelefonnummer()));
        person.setUtflytting(utflyttingService.convert(person));
        person.setOpphold(oppholdService.convert(person.getOpphold()));
        person.setTilrettelagtKommunikasjon(tilrettelagtKommunikasjonService.convert(person.getTilrettelagtKommunikasjon()));
        person.setSivilstand(sivilstandService.convert(person));
        person.setDoedsfall(doedsfallService.convert(person));
        person.setFullmakt(fullmaktService.convert(person));
        person.setKontaktadresse(kontaktAdresseService.convert(person, relaxed));
        person.setUtenlandskIdentifikasjonsnummer(utenlandsidentifikasjonsnummerService.convert(person));
        person.setVergemaal(vergemaalService.convert(person));
        person.setFalskIdentitet(falskIdentitetService.convert(person));
        person.setKontaktinformasjonForDoedsbo(kontaktinformasjonForDoedsboService.convert(person));
        person.setForelderBarnRelasjon(forelderBarnRelasjonService.convert(person));
        person.setForeldreansvar(foreldreansvarService.convert(person));
        person.setDoedfoedtBarn(doedfoedtBarnService.convert(person.getDoedfoedtBarn()));
        person.setDeltBosted(deltBostedService.convert(person));
        person.setSikkerhetstiltak(sikkerhetstiltakService.convert(person));
        person.setNavPersonIdentifikator(navPersonIdentifikatorService.convert(person));

        person.setFolkeregisterPersonstatus(folkeregisterPersonstatusService.convert(person));

        person = identtypeService.convert(person);

        person.setFolkeregisterPersonstatus(folkeregisterPersonstatusService.convert(person));

        return person;
    }
}
