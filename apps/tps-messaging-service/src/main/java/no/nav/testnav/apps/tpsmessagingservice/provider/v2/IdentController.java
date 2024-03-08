package no.nav.testnav.apps.tpsmessagingservice.provider.v2;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.dto.IdenterRequest;
import no.nav.testnav.apps.tpsmessagingservice.service.IdentService;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.TpsIdentStatusDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/identer")
@RequiredArgsConstructor
public class IdentController {

    private final IdentService identService;

    @PostMapping()
    public List<TpsIdentStatusDTO> getIdenter(@RequestBody IdenterRequest request,
                                              @RequestParam(required = false) List<String> miljoer,
                                              @RequestParam(required = false) Boolean includeProd) {

        return identService.getIdenter(request.identer(), miljoer, includeProd);
    }
}
