package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TelefonnummerDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static java.util.Objects.isNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
public class TelefonnummerService implements Validation<TelefonnummerDTO> {

    private static final String VALIDATION_PRIORITET_REQUIRED = "Telefonnummer: prioritet er påkrevet";
    private static final String VALIDATION_PRIORITET_ERROR = "Telefonnummerets prioritet må være 1 eller 2";
    private static final String VALIDATION_NUMMER_REQUIRED = "Telefonnummer: nummer er påkrevet felt";
    private static final String VALIDATION_LANDSKODE_REQUIRED = "Telefonnummer: landskode er påkrevet felt";
    private static final String VALIDATION_LANDKODE_INVALID_FORMAT = "Telefonnummer: Landkode består av ledende + " +
            "(plusstegn) fulgt av  1 til 5 sifre";
    private static final String VALIDATION_NUMMER_INVALID_FORMAT = "Telefonnummer: nummer kan kun inneholde tallsifre";
    private static final String VALIDATION_NUMMER_INVALID_LENGTH = "Telefonnummer: nummer kan ha lengde fra 3 til 16 sifre";

    public Mono<Void> convert(PersonDTO person) {

        return Flux.fromIterable(person.getTelefonnummer())
                .filter(type -> isTrue(type.getIsNew()))
                .flatMap(this::handle)
                .doOnNext(type -> {
                    type.setKilde(getKilde(type));
                    type.setMaster(getMaster(type, person));
                })
                .collectList()
                .doOnNext(telefonnumre -> person.setTelefonnummer(new ArrayList<>(telefonnumre)))
                .then();
    }

    @Override
    public Mono<Void> validate(TelefonnummerDTO telefonnummer) {

        if (isNull(telefonnummer.getNummer())) {
            throw new InvalidRequestException(VALIDATION_NUMMER_REQUIRED);
        } else if (!telefonnummer.getNummer().matches("\\d*")) {
            throw new InvalidRequestException(VALIDATION_NUMMER_INVALID_FORMAT);
        } else if (!telefonnummer.getNummer().matches("\\d{3,16}")) {
            throw new InvalidRequestException(VALIDATION_NUMMER_INVALID_LENGTH);
        }

        if (isNull(telefonnummer.getLandskode())) {
            throw new InvalidRequestException(VALIDATION_LANDSKODE_REQUIRED);
        } else if (!telefonnummer.getLandskode().matches("\\+\\d{1,5}")) {
            throw new InvalidRequestException(VALIDATION_LANDKODE_INVALID_FORMAT);
        }

        if (isNull(telefonnummer.getPrioritet())) {
            throw new InvalidRequestException(VALIDATION_PRIORITET_REQUIRED);
        } else if (telefonnummer.getPrioritet() < 1 || telefonnummer.getPrioritet() > 2) {
            throw new InvalidRequestException(VALIDATION_PRIORITET_ERROR);
        }
        return Mono.empty();
    }

    protected Mono<TelefonnummerDTO> handle(TelefonnummerDTO type) {

        type.setMaster(Master.PDL);
        return  Mono.just(type);
    }
}
