package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.artifact.VegadresseService;
import no.nav.pdl.forvalter.domain.PdlPerson;
import no.nav.pdl.forvalter.service.command.pdlartifact.AdressebeskyttelseCommand;
import no.nav.pdl.forvalter.service.command.pdlartifact.BostedAdresseCommand;
import no.nav.pdl.forvalter.service.command.pdlartifact.FoedselCommand;
import no.nav.pdl.forvalter.service.command.pdlartifact.InnflyttingCommand;
import no.nav.pdl.forvalter.service.command.pdlartifact.KjoennCommand;
import no.nav.pdl.forvalter.service.command.pdlartifact.KontaktAdresseCommand;
import no.nav.pdl.forvalter.service.command.pdlartifact.OppholdCommand;
import no.nav.pdl.forvalter.service.command.pdlartifact.OppholdsadresseCommand;
import no.nav.pdl.forvalter.service.command.pdlartifact.StatsborgerskapCommand;
import no.nav.pdl.forvalter.service.command.pdlartifact.TelefonCommand;
import no.nav.pdl.forvalter.service.command.pdlartifact.TilrettelagtKommunikasjonCommand;
import no.nav.pdl.forvalter.service.command.pdlartifact.UtflyttingCommand;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonArtifactService {

    private final MapperFacade mapperFacade;
    private final VegadresseService vegadresseService;

    public PdlPerson buildPerson(PdlPerson person, String ident) {

        return PdlPerson.builder()
                .kjoenn(new KjoennCommand(person.getKjoenn(), ident).call())
                .innflytting(new InnflyttingCommand(person.getInnflytting()).call())
                .statsborgerskap(new StatsborgerskapCommand(person.getStatsborgerskap(), ident,
                        person.getInnflytting().stream().reduce((a, b) -> b).orElse(null)).call())
                .bostedsadresse(new BostedAdresseCommand(person.getBostedsadresse(), vegadresseService, mapperFacade).call())
                .foedsel(new FoedselCommand(person.getFoedsel(), ident,
                        person.getBostedsadresse().stream().reduce((a, b) -> b).orElse(null),
                        person.getInnflytting().stream().reduce((a, b) -> b).orElse(null)).call())
                .kontaktadresse(new KontaktAdresseCommand(person.getKontaktadresse(), vegadresseService, mapperFacade).call())
                .oppholdsadresse(new OppholdsadresseCommand(person.getOppholdsadresse(), vegadresseService, mapperFacade).call())
                .adressebeskyttelse(new AdressebeskyttelseCommand(person.getAdressebeskyttelse()).call())
                .telefonnummer(new TelefonCommand(person.getTelefonnummer()).call())
                .utflytting(new UtflyttingCommand(person.getUtflytting()).call())
                .opphold(new OppholdCommand(person.getOpphold()).call())
                .tilrettelagtKommunikasjon(new TilrettelagtKommunikasjonCommand(person.getTilrettelagtKommunikasjon()).call())
                .build();
    }
}
