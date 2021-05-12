package no.nav.pdl.forvalter.service.command.pdlartifact;

import no.nav.pdl.forvalter.domain.PdlTelefonnummer;
import no.nav.pdl.forvalter.service.PdlArtifactService;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class TelefonnummerCommand extends PdlArtifactService<PdlTelefonnummer> {

    private static final String VALIDATION_PRIORITET_REQUIRED = "Telefonnummer: prioritet er påkrevd";
    private static final String VALIDATION_PRIORITET_ERROR = "Telefonnummerets prioritet må være 1 eller 2";
    private static final String VALIDATION_PRIORITET_INVALID = "Telefonnummer: prioritet 1 må angis før 2 kan benyttes";
    private static final String VALIDATION_PRIORITET_AMBIGUOUS = "Telefonnummer: prioritet 1 og prioritet 2 kan kun benyttes 1 gang hver";
    private static final String VALIDATION_NUMMER_REQUIRED = "Telefonnummer: nummer er påkrevd felt";
    private static final String VALIDATION_LANDSKODE_REQUIRED = "Telefonnummer: landskode er påkrevd felt";
    private static final String VALIDATION_LANDKODE_INVALID_FORMAT = "Telefonnummer: Landkode består av ledende + " +
            "(plusstegn) fulgt av  1 til 5 sifre";
    private static final String VALIDATION_NUMMER_INVALID_FORMAT = "Telefonnummer: nummer kan kun inneholde tallsifre";
    private static final String VALIDATION_NUMMER_INVALID_LENGTH = "Telefonnummer: nummer kan ha lengde fra 3 til 16 sifre";

    public TelefonnummerCommand(List<PdlTelefonnummer> request) {
        super(request);
    }

    @Override
    protected void validate(PdlTelefonnummer telefonnummer) {

        if (isNull(telefonnummer.getNummer())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_NUMMER_REQUIRED);
        } else if (!telefonnummer.getNummer().matches("[0-9]*")) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_NUMMER_INVALID_FORMAT);
        } else if (!telefonnummer.getNummer().matches("[0-9]{3,16}")) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_NUMMER_INVALID_LENGTH);
        }

        if (isNull(telefonnummer.getLandskode())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_LANDSKODE_REQUIRED);
        } else if (!telefonnummer.getLandskode().matches("\\+[0-9]{1,5}")) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_LANDKODE_INVALID_FORMAT);
        }

        if (isNull(telefonnummer.getPrioritet())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_PRIORITET_REQUIRED);
        } else if (telefonnummer.getPrioritet() < 1 || telefonnummer.getPrioritet() > 2) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_PRIORITET_ERROR);
        }
    }

    @Override
    protected void handle(PdlTelefonnummer type) {

    }

    @Override
    protected void enforceIntegrity(List<PdlTelefonnummer> telefonnummer) {

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
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_PRIORITET_INVALID);
        }
        if (pri1 > 1 || pri2 > 1) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_PRIORITET_AMBIGUOUS);
        }
    }
}
