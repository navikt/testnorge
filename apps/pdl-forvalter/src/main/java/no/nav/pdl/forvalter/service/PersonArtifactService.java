package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.domain.PdlPerson;
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

    public PdlPerson buildPerson(PdlPerson person) {

        return PdlPerson.builder()
                .ident(person.getIdent())
                .kjoenn(kjoennService.convert(person))
                .innflytting(innflyttingService.convert(person.getInnflytting()))
                .statsborgerskap(statsborgerskapService.convert(person))
                .bostedsadresse(bostedAdresseService.convert(person.getBostedsadresse()))
                .foedsel(foedselService.convert(person))
                .navn(navnService.convert(person.getNavn()))
                .oppholdsadresse(oppholdsadresseService.convert(person.getOppholdsadresse()))
                .adressebeskyttelse(adressebeskyttelseService.convert(person.getAdressebeskyttelse()))
                .telefonnummer(telefonnummerService.convert(person.getTelefonnummer()))
                .utflytting(utflyttingService.convert(person.getUtflytting()))
                .opphold(oppholdService.convert(person.getOpphold()))
                .tilrettelagtKommunikasjon(tilrettelagtKommunikasjonService.convert(person.getTilrettelagtKommunikasjon()))
                .doedsfall(doedsfallService.convert(person.getDoedsfall()))
                .folkeregisterpersonstatus(folkeregisterPersonstatusService.convert(person))
                .fullmakt(fullmaktService.convert(person))
                .kontaktadresse(kontaktAdresseService.convert(person.getKontaktadresse()))
                .utenlandskIdentifikasjonsnummer(utenlandsidentifikasjonsnummerService
                        .convert(person.getUtenlandskIdentifikasjonsnummer()))
                .build();
    }
}
