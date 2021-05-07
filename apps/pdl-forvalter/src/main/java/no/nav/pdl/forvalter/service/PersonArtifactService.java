package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.artifact.VegadresseService;
import no.nav.pdl.forvalter.domain.PdlPerson;
import no.nav.pdl.forvalter.dto.PersonUpdateRequest;
import no.nav.pdl.forvalter.service.command.BostedAdresseCommand;
import no.nav.pdl.forvalter.service.command.KontaktAdresseCommand;
import no.nav.pdl.forvalter.service.command.OppholdsadresseCommand;
import no.nav.pdl.forvalter.service.command.TelefonCommand;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonArtifactService {

    private final MapperFacade mapperFacade;
    private final VegadresseService vegadresseService;

    public PdlPerson buildPerson(PersonUpdateRequest request) {

        return PdlPerson.builder()
                .kontaktadresse(new KontaktAdresseCommand(request.getPerson().getKontaktadresse(), vegadresseService, mapperFacade).call())
                .oppholdsadresse(new OppholdsadresseCommand(request.getPerson().getOppholdsadresse(), vegadresseService, mapperFacade).call())
                .bostedsadresse(new BostedAdresseCommand(request.getPerson().getBostedsadresse(), vegadresseService, mapperFacade).call())
                .telefonnummer(new TelefonCommand(request.getPerson().getTelefonnummer()).call())
                .build();
    }
}
