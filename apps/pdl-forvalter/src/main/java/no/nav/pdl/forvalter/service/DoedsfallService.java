package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.domain.DoedsfallDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
public class DoedsfallService {

    public List<DoedsfallDTO> convert(List<DoedsfallDTO> request) {

        for (var type : request) {

            if (type.isNew()) {
                if (isBlank(type.getKilde())) {
                    type.setKilde("Dolly");
                }
            }
        }

        return request;
    }
}
