package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TelefonnummerDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.isNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
public class TelefonnummerService implements Validation<TelefonnummerDTO> {

    private static final String VALIDATION_PRIORITET_REQUIRED = "Telefonnummer: prioritet er påkrevet";
    private static final String VALIDATION_PRIORITET_ERROR = "Telefonnummerets prioritet må være 1 eller 2";
    private static final String VALIDATION_PRIORITET_INVALID = "Telefonnummer: prioritet 1 må angis før 2 kan benyttes";
    private static final String VALIDATION_PRIORITET_AMBIGUOUS = "Telefonnummer: prioritet 1 og prioritet 2 kan kun benyttes én gang hver";
    private static final String VALIDATION_NUMMER_REQUIRED = "Telefonnummer: nummer er påkrevet felt";
    private static final String VALIDATION_LANDSKODE_REQUIRED = "Telefonnummer: landskode er påkrevet felt";
    private static final String VALIDATION_LANDKODE_INVALID_FORMAT = "Telefonnummer: Landkode består av ledende + " +
            "(plusstegn) fulgt av  1 til 5 sifre";
    private static final String VALIDATION_NUMMER_INVALID_FORMAT = "Telefonnummer: nummer kan kun inneholde tallsifre";
    private static final String VALIDATION_NUMMER_INVALID_LENGTH = "Telefonnummer: nummer kan ha lengde fra 3 til 16 sifre";

    public Mono<DbPerson> convert(DbPerson dbPerson) {

        return Flux.fromIterable(dbPerson.getPerson().getTelefonnummer())
                .filter(type -> isTrue(type.getIsNew()))
                .flatMap(this::handle)
                .doOnNext(type -> {
                    type.setKilde(getKilde(type));
                    type.setMaster(getMaster(type, dbPerson.getPerson()));
                })
                .collectList()
                .doOnNext(this::enforceIntegrity)
                .thenReturn(dbPerson);
    }

    @Override
    public Mono<Void> validate(TelefonnummerDTO telefonnummer) {

        if (isNull(telefonnummer.getNummer())) {
            return Mono.error(new InvalidRequestException(VALIDATION_NUMMER_REQUIRED));
        } else if (!telefonnummer.getNummer().matches("\\d*")) {
            return Mono.error(new InvalidRequestException(VALIDATION_NUMMER_INVALID_FORMAT));
        } else if (!telefonnummer.getNummer().matches("\\d{3,16}")) {
            return Mono.error(new InvalidRequestException(VALIDATION_NUMMER_INVALID_LENGTH));
        }

        if (isNull(telefonnummer.getLandskode())) {
            return Mono.error(new InvalidRequestException(VALIDATION_LANDSKODE_REQUIRED));
        } else if (!telefonnummer.getLandskode().matches("\\+\\d{1,5}")) {
            return Mono.error(new InvalidRequestException(VALIDATION_LANDKODE_INVALID_FORMAT));
        }

        if (isNull(telefonnummer.getPrioritet())) {
            return Mono.error(new InvalidRequestException(VALIDATION_PRIORITET_REQUIRED));
        } else if (telefonnummer.getPrioritet() < 1 || telefonnummer.getPrioritet() > 2) {
            return Mono.error(new InvalidRequestException(VALIDATION_PRIORITET_ERROR));
        }
        return Mono.empty();
    }

    protected Mono<TelefonnummerDTO> handle(TelefonnummerDTO type) {

        type.setMaster(Master.PDL);
        return  Mono.just(type);
    }

    protected void enforceIntegrity(List<TelefonnummerDTO> telefonnummer) {

        var pri1 = 0;
        var pri2 = 0;
        for (var tlf : telefonnummer) {
            if (tlf.getPrioritet() == 1) {
                pri1++;
            }
            if (tlf.getPrioritet() == 2) {
                pri2++;
            }
        }
        if (pri2 > 0 && pri1 == 0) {
            throw new InvalidRequestException(VALIDATION_PRIORITET_INVALID);
        }
        if (pri1 > 1 || pri2 > 1) {
            throw new InvalidRequestException(VALIDATION_PRIORITET_AMBIGUOUS);
        }
    }
}
