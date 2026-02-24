package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.utils.IdenttypeUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedestedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.NORGE;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.FNR;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class FoedestedService implements BiValidation<FoedestedDTO, PersonDTO> {

    private final KodeverkConsumer kodeverkConsumer;

    public Mono<Void> convert(PersonDTO person) {

        return Flux.fromIterable(person.getFoedested())
                .filter(type -> isTrue(type.getIsNew()))
                .flatMap(type -> handle(type, person.getIdent(),
                        person.getBostedsadresse().stream().reduce((a, b) -> b).orElse(null),
                        person.getInnflytting().stream().reduce((a, b) -> b).orElse(null)))
                .doOnNext(type -> {
                    type.setKilde(getKilde(type));
                    type.setMaster(getMaster(type, person));
                })
                .collectList()
                .doOnNext(foedested -> person.setFoedested(new ArrayList<>(foedested)))
                .then();
    }

    private Mono<FoedestedDTO> handle(FoedestedDTO foedested, String ident, BostedadresseDTO bostedadresse, InnflyttingDTO innflytting) {

        return setFoedeland(foedested, ident, bostedadresse, innflytting)
                .flatMap(fsted -> setFoedekommune(fsted, bostedadresse));
    }

    private Mono<FoedestedDTO> setFoedeland(FoedestedDTO foedested, String ident, BostedadresseDTO bostedadresse, InnflyttingDTO innflytting) {

        if (isNull(foedested.getFoedeland())) {

            if (FNR.equals(IdenttypeUtility.getIdenttype(ident))) {
                foedested.setFoedeland(NORGE);
                return Mono.just(foedested);
            } else if (nonNull(innflytting)) {
                foedested.setFoedeland(innflytting.getFraflyttingsland());
                return Mono.just(foedested);
            } else if (nonNull(bostedadresse) && nonNull(bostedadresse.getUtenlandskAdresse())) {
                foedested.setFoedeland(bostedadresse.getUtenlandskAdresse().getLandkode());
                return Mono.just(foedested);
            } else {
                return kodeverkConsumer.getTilfeldigLand()
                        .doOnNext(foedested::setFoedeland)
                        .thenReturn(foedested);
            }
        }
        return Mono.just(foedested);
    }

    private Mono<FoedestedDTO> setFoedekommune(FoedestedDTO foedested, BostedadresseDTO bostedadresse) {

        if (NORGE.equals(foedested.getFoedeland()) && isBlank(foedested.getFoedekommune())) {
            if (nonNull(bostedadresse)) {
                if (nonNull(bostedadresse.getVegadresse())) {
                    foedested.setFoedekommune(bostedadresse.getVegadresse().getKommunenummer());
                    return Mono.just(foedested);
                } else if (nonNull(bostedadresse.getMatrikkeladresse())) {
                    foedested.setFoedekommune(bostedadresse.getMatrikkeladresse().getKommunenummer());
                    return Mono.just(foedested);
                } else if (nonNull(bostedadresse.getUkjentBosted()) &&
                           isNotBlank(bostedadresse.getUkjentBosted().getBostedskommune())) {
                    foedested.setFoedekommune(bostedadresse.getUkjentBosted().getBostedskommune());
                    return Mono.just(foedested);
                } else {
                    return kodeverkConsumer.getTilfeldigKommune()
                            .doOnNext(foedested::setFoedekommune)
                            .thenReturn(foedested);
                }
            } else {
                return kodeverkConsumer.getTilfeldigKommune()
                        .doOnNext(foedested::setFoedekommune)
                        .thenReturn(foedested);
            }
        }
        return Mono.just(foedested);
    }

    @Override
    public void validate(FoedestedDTO artifact, PersonDTO person) {

        // Ingen validering
    }
}
