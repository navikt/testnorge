package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.domain.PdlPerson;
import no.nav.pdl.forvalter.service.command.pdlartifact.AdressebeskyttelseCommand;
import no.nav.pdl.forvalter.service.command.pdlartifact.FoedselCommand;
import no.nav.pdl.forvalter.service.command.pdlartifact.InnflyttingCommand;
import no.nav.pdl.forvalter.service.command.pdlartifact.KjoennCommand;
import no.nav.pdl.forvalter.service.command.pdlartifact.OppholdCommand;
import no.nav.pdl.forvalter.service.command.pdlartifact.StatsborgerskapCommand;
import no.nav.pdl.forvalter.service.command.pdlartifact.TelefonnummerCommand;
import no.nav.pdl.forvalter.service.command.pdlartifact.TilrettelagtKommunikasjonCommand;
import no.nav.pdl.forvalter.service.command.pdlartifact.UtflyttingCommand;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonArtifactService {

    private final BostedAdresseService bostedAdresseService;
    private final OppholdsadresseService oppholdsadresseService;
    private final KontaktAdresseService kontaktAdresseService;

    public PdlPerson buildPerson(PdlPerson person, String ident) {

        return PdlPerson.builder()
                .kjoenn(new KjoennCommand(person.getKjoenn(), ident).call())
                .innflytting(new InnflyttingCommand(person.getInnflytting()).call())
                .statsborgerskap(new StatsborgerskapCommand(person.getStatsborgerskap(), ident,
                        person.getInnflytting().stream().reduce((a, b) -> b).orElse(null)).call())
                .bostedsadresse(bostedAdresseService.accept(person.getBostedsadresse()))
                .foedsel(new FoedselCommand(person.getFoedsel(), ident,
                        person.getBostedsadresse().stream().reduce((a, b) -> b).orElse(null),
                        person.getInnflytting().stream().reduce((a, b) -> b).orElse(null)).call())
                .kontaktadresse(kontaktAdresseService.accept(person.getKontaktadresse()))
                .oppholdsadresse(oppholdsadresseService.accept(person.getOppholdsadresse()))
                .adressebeskyttelse(new AdressebeskyttelseCommand(person.getAdressebeskyttelse()).call())
                .telefonnummer(new TelefonnummerCommand(person.getTelefonnummer()).call())
                .utflytting(new UtflyttingCommand(person.getUtflytting()).call())
                .opphold(new OppholdCommand(person.getOpphold()).call())
                .tilrettelagtKommunikasjon(new TilrettelagtKommunikasjonCommand(person.getTilrettelagtKommunikasjon()).call())
                .build();
    }
}
