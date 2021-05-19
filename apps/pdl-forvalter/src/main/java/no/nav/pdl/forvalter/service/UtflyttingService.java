package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.dto.RsUtflytting;
import no.nav.pdl.forvalter.service.command.TilfeldigLandCommand;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.isLandkode;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class UtflyttingService extends PdlArtifactService<RsUtflytting> {

    private static final String VALIDATION_LANDKODE_ERROR = "Landkode må oppgis i hht ISO-3 Landkoder for tilflyttingsland";
    private static final String VALIDATION_UTFLYTTING_DATO_ERROR = "Ugyldig flyttedato, ny dato må være etter en eksisterende";

    @Override
    protected void validate(RsUtflytting utflytting) {

        if (isNotBlank(utflytting.getTilflyttingsland()) && !isLandkode(utflytting.getTilflyttingsland())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_LANDKODE_ERROR);
        }
    }

    @Override
    protected void handle(RsUtflytting utflytting) {

        if (isBlank(utflytting.getTilflyttingsland())) {
            utflytting.setTilflyttingsland(new TilfeldigLandCommand().call());
        }
    }

    @Override
    protected void enforceIntegrity(List<RsUtflytting> utflytting) {

        for (var i = 0; i < utflytting.size(); i++) {
            if (i + 1 < utflytting.size()) {
                if (nonNull(utflytting.get(i + 1).getFlyttedato()) &&
                        nonNull(utflytting.get(i).getFlyttedato()) &&
                        !utflytting.get(i).getFlyttedato()
                                .isAfter(utflytting.get(i + 1).getFlyttedato())) {
                    throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_UTFLYTTING_DATO_ERROR);
                }
            }
        }
    }
}
