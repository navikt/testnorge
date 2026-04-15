package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.IdenttypeUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedestedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    public Mono<DbPerson> convert(DbPerson dbPerson) {

        return Flux.fromIterable(dbPerson.getPerson().getFoedested())
                .filter(type -> isTrue(type.getIsNew()))
                .flatMap(type -> handle(type, dbPerson.getIdent(),
                        dbPerson.getPerson().getBostedsadresse().stream().reduce((a, b) -> b).orElse(null),
                        dbPerson.getPerson().getInnflytting().stream().reduce((a, b) -> b).orElse(null)))
                .doOnNext(type -> {
                    type.setKilde(getKilde(type));
                    type.setMaster(getMaster(type, dbPerson.getPerson()));
                })
                .collectList()
                .thenReturn(dbPerson);
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
    public Mono<Void> validate(FoedestedDTO artifact, PersonDTO person) {

        return Mono.just(true)
                .flatMap(valid -> {
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
                })
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
                }))
                .then();
    }
}
