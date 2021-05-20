package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.domain.PdlKjoenn;
import no.nav.pdl.forvalter.utils.KjoennFraIdentUtility;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;
import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
@RequiredArgsConstructor
public class KjoennService {

    public List<PdlKjoenn> convert(List<PdlKjoenn> request,
                         String ident) {

        for (var type : request) {

            if (type.isNew()) {

                handle(type, ident);
                if (isBlank(type.getKilde())) {
                    type.setKilde("Dolly");
                }
            }
        }
        return request;
    }

    private void handle(PdlKjoenn kjoenn, String ident) {

        if (isNull(kjoenn.getKjoenn())) {
            kjoenn.setKjoenn(KjoennFraIdentUtility.getKjoenn(ident));
        }
    }
}
