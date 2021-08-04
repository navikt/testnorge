package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.IdentPoolConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.dto.HentIdenterRequest;
import no.nav.pdl.forvalter.exception.InternalServerException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

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

    private static PersonDTO buildPerson(PersonRequestDTO request) {

        return PersonDTO.builder()
                .kjoenn(List.of(KjoennDTO.builder().build()))
                .foedsel(List.of(FoedselDTO.builder().build()))
                .navn(nonNull(request.getNyttNavn()) ?
                        List.of(NavnDTO.builder().hasMellomnavn(request.getNyttNavn().isHarMellomnavn()).build()) :
                        emptyList())
                .bostedsadresse(List.of(BostedadresseDTO.builder()
                        .vegadresse(new VegadresseDTO())
                        .build()))
                .statsborgerskap(List.of(StatsborgerskapDTO.builder().build()))
                .folkeregisterpersonstatus(
                        List.of(FolkeregisterpersonstatusDTO.builder().build()))
                .build();
    }

    public PersonDTO execute(PersonRequestDTO request) {

        var ident = Stream.of(identPoolConsumer.getIdents(
                mapperFacade.map(nonNull(request) ? request : new PersonRequestDTO(), HentIdenterRequest.class)))
                .findFirst().orElseThrow(() -> new InternalServerException(
                        String.format("Ident kunne ikke levere forespørsel: %s", request.toString())));

        var mergedPerson = mergeService.merge(buildPerson(nonNull(request) ? request : new PersonRequestDTO()),
                PersonDTO.builder().ident(ident).build());

        kjoennService.convert(mergedPerson);
        navnService.convert(mergedPerson.getNavn());
        statsborgerskapService.convert(mergedPerson);
        bostedAdresseService.convert(mergedPerson.getBostedsadresse());
        foedselService.convert(mergedPerson);
        folkeregisterPersonstatusService.convert(mergedPerson);

        return personRepository.save(DbPerson.builder()
                .person(mergedPerson)
                .ident(ident)
                .sistOppdatert(LocalDateTime.now())
                .build())
                .getPerson();
    }
}
