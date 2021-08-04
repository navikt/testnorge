package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
public class DoedsfallService {

    public List<DoedsfallDTO> convert(List<DoedsfallDTO> request) {

        for (var type : request) {

            if (isTrue(type.getIsNew()) && isBlank(type.getKilde())) {
                type.setKilde("Dolly");
            }
        }

        return request;
    }
}
