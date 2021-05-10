package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.artifact.VegadresseService;
import no.nav.pdl.forvalter.domain.PdlPerson;
import no.nav.pdl.forvalter.dto.PersonUpdateRequest;
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

    public PdlPerson buildPerson(PersonUpdateRequest request) {

        return PdlPerson.builder()
                .kjoenn(new KjoennCommand(request.getPerson().getKjoenn(), request.getIdent()).call())
                .innflytting(new InnflyttingCommand(request.getPerson().getInnflytting()).call())
                .statsborgerskap(new StatsborgerskapCommand(request.getPerson().getStatsborgerskap(), request.getIdent(),
                        request.getPerson().getInnflytting().stream().reduce((a, b) -> b).orElse(null)).call())
                .bostedsadresse(new BostedAdresseCommand(request.getPerson().getBostedsadresse(), vegadresseService, mapperFacade).call())
                .foedsel(new FoedselCommand(request.getPerson().getFoedsel(), request.getIdent(),
                        request.getPerson().getBostedsadresse().stream().reduce((a, b) -> b).orElse(null),
                        request.getPerson().getInnflytting().stream().reduce((a, b) -> b).orElse(null)).call())
                .kontaktadresse(new KontaktAdresseCommand(request.getPerson().getKontaktadresse(), vegadresseService, mapperFacade).call())
                .oppholdsadresse(new OppholdsadresseCommand(request.getPerson().getOppholdsadresse(), vegadresseService, mapperFacade).call())
                .adressebeskyttelse(new AdressebeskyttelseCommand(request.getPerson().getAdressebeskyttelse()).call())
                .telefonnummer(new TelefonCommand(request.getPerson().getTelefonnummer()).call())
                .utflytting(new UtflyttingCommand(request.getPerson().getUtflytting()).call())
                .opphold(new OppholdCommand(request.getPerson().getOpphold()).call())
                .tilrettelagtKommunikasjon(new TilrettelagtKommunikasjonCommand(request.getPerson().getTilrettelagtKommunikasjon()).call())
                .build();
    }
}
