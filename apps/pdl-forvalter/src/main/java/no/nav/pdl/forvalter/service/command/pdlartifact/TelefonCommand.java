package no.nav.pdl.forvalter.service.command.pdlartifact;

import no.nav.pdl.forvalter.domain.PdlTelefonnummer;
import no.nav.pdl.forvalter.service.PdlArtifactService;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static java.util.Objects.nonNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class TelefonCommand extends PdlArtifactService<PdlTelefonnummer> {

    private  static final String VALIDATION_PRIORITET_ERROR = "Telefonnummerets prioritet må være 1 eller 2";

    public TelefonCommand(List<PdlTelefonnummer> request) {
        super(request);
    }

    @Override
    protected void validate(PdlTelefonnummer telefonnummer) {
        if (nonNull(telefonnummer.getPrioritet()) && (telefonnummer.getPrioritet() < 1 ||
                telefonnummer.getPrioritet() > 2)) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_PRIORITET_ERROR);
        }
    }

    @Override
    protected void handle(PdlTelefonnummer type) {

    }

    @Override
    protected void enforceIntegrity(List<PdlTelefonnummer> type) {

    }
}
