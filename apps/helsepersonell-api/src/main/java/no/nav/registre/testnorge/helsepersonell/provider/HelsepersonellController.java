package no.nav.registre.testnorge.helsepersonell.provider;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.helsepersonell.adapter.HelsepersonellAdapter;
import no.nav.registre.testnorge.libs.dto.helsepersonell.v1.HelsepersonellListeDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/helsepersonell")
@RequiredArgsConstructor
public class HelsepersonellController {

    private final HelsepersonellAdapter adapter;

    @GetMapping
    public HelsepersonellListeDTO getHelsepersonell() {
        return adapter.getHelsepersonell().toDTO();
    }
}
