package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.IdentPoolConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.domain.PdlBostedadresse;
import no.nav.pdl.forvalter.domain.PdlFoedsel;
import no.nav.pdl.forvalter.domain.PdlFolkeregisterpersonstatus;
import no.nav.pdl.forvalter.domain.PdlKjoenn;
import no.nav.pdl.forvalter.domain.PdlPerson;
import no.nav.pdl.forvalter.domain.PdlStatsborgerskap;
import no.nav.pdl.forvalter.domain.PdlVegadresse;
import no.nav.pdl.forvalter.dto.HentIdenterRequest;
import no.nav.pdl.forvalter.dto.RsNavn;
import no.nav.pdl.forvalter.dto.RsPersonRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
@RequiredArgsConstructor
public class CreatePersonService {

    private final IdentPoolConsumer identPoolConsumer;
    private final MapperFacade mapperFacade;
    private final MergeService mergeService;
    private final PersonRepository personRepository;
    private final KjoennService kjoennService;
    private final FoedselService foedselService;
    private final StatsborgerskapService statsborgerskapService;
    private final BostedAdresseService bostedAdresseService;
    private final NavnService navnService;
    private final FolkeregisterPersonstatusService folkeregisterPersonstatusService;

    public String execute(RsPersonRequest request) {

        var ident = Stream.of(identPoolConsumer.getIdents(
                mapperFacade.map(nonNull(request) ? request : new RsPersonRequest(), HentIdenterRequest.class)))
                .findFirst().orElseThrow(() -> new HttpClientErrorException(INTERNAL_SERVER_ERROR,
                        String.format("Ident kunne ikke levere forespørsel: %s", request.toString())));

        var mergedPerson = mergeService.merge(PdlPerson.builder()
                        .kjoenn(List.of(PdlKjoenn.builder().isNew(true).build()))
                        .foedsel(List.of(PdlFoedsel.builder().isNew(true).build()))
                        .navn(List.of(RsNavn.builder().isNew(true).build()))
                        .bostedsadresse(List.of(PdlBostedadresse.builder()
                                .vegadresse(new PdlVegadresse())
                                .isNew(true)
                                .build()))
                        .statsborgerskap(List.of(PdlStatsborgerskap.builder().isNew(true).build()))
                        .folkeregisterpersonstatus(List.of(PdlFolkeregisterpersonstatus.builder().isNew(true).build()))
                        .build(),
                new PdlPerson());

        mergedPerson.setIdent(ident);

        kjoennService.convert(mergedPerson);
        navnService.convert(mergedPerson.getNavn());
        statsborgerskapService.convert(mergedPerson);
        bostedAdresseService.convert(mergedPerson.getBostedsadresse());
        foedselService.convert(mergedPerson);
        folkeregisterPersonstatusService.convert(mergedPerson);

        personRepository.save(DbPerson.builder()
                .person(mergedPerson)
                .ident(ident)
                .sistOppdatert(LocalDateTime.now())
                .build());

        return ident;
    }
}
