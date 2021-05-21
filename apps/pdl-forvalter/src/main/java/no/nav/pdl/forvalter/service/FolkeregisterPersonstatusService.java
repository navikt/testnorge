package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.domain.PdlBostedadresse;
import no.nav.pdl.forvalter.domain.PdlDoedsfall;
import no.nav.pdl.forvalter.domain.PdlFolkeregisterpersonstatus;
import no.nav.pdl.forvalter.domain.PdlOpphold;
import no.nav.pdl.forvalter.dto.RsUtflytting;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.domain.PdlFolkeregisterpersonstatus.Folkeregisterpersonstatus.BOSATT;
import static no.nav.pdl.forvalter.domain.PdlFolkeregisterpersonstatus.Folkeregisterpersonstatus.DOED;
import static no.nav.pdl.forvalter.domain.PdlFolkeregisterpersonstatus.Folkeregisterpersonstatus.FOEDSELSREGISTRERT;
import static no.nav.pdl.forvalter.domain.PdlFolkeregisterpersonstatus.Folkeregisterpersonstatus.IKKE_BOSATT;
import static no.nav.pdl.forvalter.domain.PdlFolkeregisterpersonstatus.Folkeregisterpersonstatus.MIDLERTIDIG;
import static no.nav.pdl.forvalter.domain.PdlFolkeregisterpersonstatus.Folkeregisterpersonstatus.UTFLYTTET;

@Service
@RequiredArgsConstructor
public class FolkeregisterPersonstatusService {

    public List<PdlFolkeregisterpersonstatus> convert(List<PdlFolkeregisterpersonstatus> request,
                                                      PdlBostedadresse bostedadresse,
                                                      RsUtflytting utflytting,
                                                      PdlOpphold opphold,
                                                      PdlDoedsfall doedsfall) {

        for (var type : request) {

            if (type.isNew()) {
                handle(type, bostedadresse, utflytting, opphold, doedsfall);

                if (Strings.isBlank(type.getKilde())) {
                    type.setKilde("Dolly");
                }
            }
        }
        return request;
    }

    private void handle(PdlFolkeregisterpersonstatus status,
                        PdlBostedadresse bostedadresse,
                        RsUtflytting utflytting,
                        PdlOpphold opphold,
                        PdlDoedsfall doedsfall) {

        if (isNull(status.getStatus())) {

            if (nonNull(doedsfall)) {
                status.setStatus(DOED);

            } else if (nonNull(utflytting)) {
                status.setStatus(UTFLYTTET);

            } else if (nonNull(opphold)) {
                status.setStatus(MIDLERTIDIG);

            } else if (nonNull(bostedadresse) &&
                    (nonNull(bostedadresse.getVegadresse()) || nonNull(bostedadresse.getMatrikkeladresse()))) {
                status.setStatus(BOSATT);

            } else if (nonNull(bostedadresse) && nonNull(bostedadresse.getUtenlandskAdresse())) {
                status.setStatus(IKKE_BOSATT);

            } else {
                status.setStatus(FOEDSELSREGISTRERT);
            }
        }
    }
}
