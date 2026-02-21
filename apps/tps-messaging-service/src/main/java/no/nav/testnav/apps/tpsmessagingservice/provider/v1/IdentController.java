package no.nav.testnav.apps.tpsmessagingservice.provider.v1;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.service.IdentService;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsIdentStatusDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/identer")
@RequiredArgsConstructor
public class IdentController {

    private final IdentService identService;

    @GetMapping
    public List<TpsIdentStatusDTO> getIdenter(@RequestParam List<String> identer,
                                              @RequestParam(required = false) List<String> miljoer,
                                              @RequestParam(required = false) Boolean includeProd) {

        return identService.getIdenter(identer, miljoer, includeProd);
    }
}
