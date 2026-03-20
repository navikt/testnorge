package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.utils.DatoFraIdentUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselsdatoDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;

import static java.util.Objects.isNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.renumberId;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
@RequiredArgsConstructor
public class FoedselsdatoService implements BiValidation<FoedselsdatoDTO, PersonDTO> {

    public Mono<DbPerson> convert(DbPerson dbPerson) {

        return Flux.fromIterable(dbPerson.getPerson().getFoedselsdato())
                .filter(foedselsdato -> isTrue(foedselsdato.getIsNew()))
                .flatMap(foedselsdato -> handle(foedselsdato, dbPerson.getIdent()))
                .doOnNext(type -> {
                    type.setKilde(getKilde(type));
                    type.setMaster(getMaster(type, dbPerson.getPerson()));
                })
                .collectList()
                .doOnNext(foedselsdatoer -> {

                    dbPerson.getPerson().setFoedselsdato(new ArrayList<>(foedselsdatoer));
                    dbPerson.getPerson().getFoedselsdato().sort(Comparator.comparing(FoedselsdatoDTO::getFoedselsaar).
                            reversed());

                    renumberId(dbPerson.getPerson().getFoedselsdato());
                })
                .thenReturn(dbPerson);
    }

    private Mono<FoedselsdatoDTO> handle(FoedselsdatoDTO foedselsdato, String ident) {

        if (isNull(foedselsdato.getFoedselsaar())) {
            if (isNull(foedselsdato.getFoedselsdato())) {
                foedselsdato.setFoedselsdato(DatoFraIdentUtility.getDato(ident).atStartOfDay());
            }

            foedselsdato.setFoedselsaar(foedselsdato.getFoedselsdato().getYear());
        }

        return Mono.just(foedselsdato);
    }

    @Override
    public Mono<Void> validate(FoedselsdatoDTO artifact, PersonDTO personDTO) {

        // Ingen validering
        return Mono.empty();
    }
}