package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.domain.PdlPerson;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonArtifactService {

    private final AdressebeskyttelseService adressebeskyttelseService;
    private final BostedAdresseService bostedAdresseService;
    private final FoedselService foedselService;
    private final KjoennService kjoennService;
    private final KontaktAdresseService kontaktAdresseService;
    private final InnflyttingService innflyttingService;
    private final OppholdsadresseService oppholdsadresseService;
    private final OppholdService oppholdService;
    private final StatsborgerskapService statsborgerskapService;
    private final TelefonnummerService telefonnummerService;
    private final TilrettelagtKommunikasjonService tilrettelagtKommunikasjonService;
    private final UtflyttingService utflyttingService;

    public PdlPerson buildPerson(PdlPerson person, String ident) {

        return PdlPerson.builder()
                .kjoenn(kjoennService.convert(person.getKjoenn(), ident))
                .innflytting(innflyttingService.convert(person.getInnflytting()))
                .statsborgerskap(statsborgerskapService.convert(person.getStatsborgerskap(), ident,
                        person.getInnflytting().stream().reduce((a, b) -> b).orElse(null)))
                .bostedsadresse(bostedAdresseService.accept(person.getBostedsadresse()))
                .foedsel(foedselService.convert(person.getFoedsel(), ident,
                        person.getBostedsadresse().stream().reduce((a, b) -> b).orElse(null),
                        person.getInnflytting().stream().reduce((a, b) -> b).orElse(null)))
                .kontaktadresse(kontaktAdresseService.accept(person.getKontaktadresse()))
                .oppholdsadresse(oppholdsadresseService.accept(person.getOppholdsadresse()))
                .adressebeskyttelse(adressebeskyttelseService.convert(person.getAdressebeskyttelse()))
                .telefonnummer(telefonnummerService.convert(person.getTelefonnummer()))
                .utflytting(utflyttingService.convert(person.getUtflytting()))
                .opphold(oppholdService.convert(person.getOpphold()))
                .tilrettelagtKommunikasjon(tilrettelagtKommunikasjonService.convert(person.getTilrettelagtKommunikasjon()))
                .build();
    }
}
