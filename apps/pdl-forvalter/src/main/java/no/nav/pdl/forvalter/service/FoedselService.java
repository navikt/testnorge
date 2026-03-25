package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.DatoFraIdentUtility;
import no.nav.pdl.forvalter.utils.IdenttypeUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.NORGE;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.renumberId;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.FNR;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class FoedselService implements BiValidation<FoedselDTO, PersonDTO> {

    private final KodeverkConsumer kodeverkConsumer;

    public Mono<DbPerson> convert(DbPerson dbPerson) {

        return Flux.fromIterable(dbPerson.getPerson().getFoedsel())
                .filter(foedsel -> isTrue(foedsel.getIsNew()))
                .flatMap(foedsel -> handle(foedsel, dbPerson.getPerson().getIdent(),
                        dbPerson.getPerson().getBostedsadresse().stream().reduce((a, b) -> b).orElse(null),
                        dbPerson.getPerson().getInnflytting().stream().reduce((a, b) -> b).orElse(null)))
                .doOnNext(type -> {
                    type.setKilde(getKilde(type));
                    type.setMaster(getMaster(type, dbPerson.getPerson()));
                })
                .collectList()
                .doOnNext(foedsler -> {

                    dbPerson.getPerson().setFoedsel(new ArrayList<>(foedsler));
                    dbPerson.getPerson().getFoedsel().sort(Comparator.comparing(FoedselDTO::getFoedselsaar).
                            reversed());

                    renumberId(dbPerson.getPerson().getFoedsel());
                })
                .thenReturn(dbPerson);
    }

    private Mono<FoedselDTO> handle(FoedselDTO foedsel, String ident, BostedadresseDTO
            bostedadresse, InnflyttingDTO innflytting) {

        return Mono.defer(() -> {
                    if (isNull(foedsel.getFoedselsaar())) {
                        if (isNull(foedsel.getFoedselsdato())) {
                            foedsel.setFoedselsdato(DatoFraIdentUtility.getDato(ident).atStartOfDay());
                        }

                        foedsel.setFoedselsaar(foedsel.getFoedselsdato().getYear());
                    }

                    return setFoedeland(foedsel, ident, bostedadresse, innflytting)
                            .then(setFoedekommune(foedsel, bostedadresse))
                            .thenReturn(foedsel);
                })
                .thenReturn(foedsel);
    }

    private Mono<FoedselDTO> setFoedeland(FoedselDTO foedsel, String ident, BostedadresseDTO bostedadresse, InnflyttingDTO innflytting) {

        if (isNull(foedsel.getFoedeland())) {
            if (FNR.equals(IdenttypeUtility.getIdenttype(ident))) {
                foedsel.setFoedeland(NORGE);
            } else if (nonNull(innflytting)) {
                foedsel.setFoedeland(innflytting.getFraflyttingsland());
            } else if (nonNull(bostedadresse) && nonNull(bostedadresse.getUtenlandskAdresse())) {
                foedsel.setFoedeland(bostedadresse.getUtenlandskAdresse().getLandkode());
            } else {
                return kodeverkConsumer.getTilfeldigLand()
                        .doOnNext(foedsel::setFoedeland)
                        .thenReturn(foedsel);
            }
        }
        return Mono.just(foedsel);
    }

    private Mono<FoedselDTO> setFoedekommune(FoedselDTO foedsel, BostedadresseDTO bostedadresse) {

        if (NORGE.equals(foedsel.getFoedeland()) && isBlank(foedsel.getFoedekommune())) {
            if (nonNull(bostedadresse)) {
                if (nonNull(bostedadresse.getVegadresse())) {
                    foedsel.setFoedekommune(bostedadresse.getVegadresse().getKommunenummer());
                } else if (nonNull(bostedadresse.getMatrikkeladresse())) {
                    foedsel.setFoedekommune(bostedadresse.getMatrikkeladresse().getKommunenummer());
                } else if (nonNull(bostedadresse.getUkjentBosted()) &&
                           isNotBlank(bostedadresse.getUkjentBosted().getBostedskommune())) {
                    foedsel.setFoedekommune(bostedadresse.getUkjentBosted().getBostedskommune());
                } else {
                    return kodeverkConsumer.getTilfeldigKommune()
                            .doOnNext(foedsel::setFoedekommune)
                            .thenReturn(foedsel);
                }
            } else {
                return kodeverkConsumer.getTilfeldigKommune()
                        .doOnNext(foedsel::setFoedekommune)
                        .thenReturn(foedsel);
            }
        }
        return Mono.just(foedsel);
    }

    @Override
    public Mono<Void> validate(FoedselDTO artifact, PersonDTO personDTO) {

        return Mono.defer(() -> {
                    if (nonNull(artifact.getFoedselsaar()) && nonNull(artifact.getFoedselsdato()) &&
                        artifact.getFoedselsaar() != artifact.getFoedselsdato().getYear()) {

                        return Mono.error(new InvalidRequestException("Foedselsår og foedselsdato må være konsistente"));
                    }
                    return Mono.empty();
                })
                .then(Mono.defer(() -> {
                    if (isNotBlank(artifact.getFoedeland())) {
                        return kodeverkConsumer.hentKodeverk(KodeverkConsumer.LANDKODER)
                                .flatMap(landkoder -> {
                                    if (!landkoder.containsKey(artifact.getFoedeland())) {
                                        return Mono.error(new InvalidRequestException("Ugyldig foedeland: " + artifact.getFoedeland()));
                                    }
                                    return Mono.empty();
                                });
                    }
                    return Mono.empty();
                }))
                .then(Mono.defer(() -> {
                    if (isNotBlank(artifact.getFoedekommune())) {
                        return kodeverkConsumer.hentKodeverk(KodeverkConsumer.KOMMUNER_MED_HISTORISKE)
                                .flatMap(kommuner -> {
                                    if (!kommuner.containsKey(artifact.getFoedekommune())) {
                                        return Mono.error(new InvalidRequestException("Ugyldig foedekommune: " + artifact.getFoedekommune()));
                                    }
                                    return Mono.empty();
                                });
                    }
                    return Mono.empty();
                }));
    }
}
