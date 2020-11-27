package no.nav.registre.testnorge.organisasjonmottak.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.organisasjonmottak.domain.ToFlatfil;
import no.nav.registre.testnorge.organisasjonmottak.provider.dto.AnsatteDTO;
import no.nav.registre.testnorge.organisasjonmottak.provider.dto.BaseDTO;
import no.nav.registre.testnorge.organisasjonmottak.provider.dto.DetaljertNavnDTO;
import no.nav.registre.testnorge.organisasjonmottak.provider.dto.NavnDTO;
import no.nav.registre.testnorge.organisasjonmottak.provider.dto.OrganiasjonDTO;
import no.nav.registre.testnorge.organisasjonmottak.service.OrganiasjonService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/organiasjonmottak")
public class OrganiasjonMottakController {
    private final OrganiasjonService organiasjonService;

    private <T extends ToFlatfil> void send(BaseDTO<T> dto) {
        organiasjonService.save(dto.toDomain(), "T4");
    }

    @PostMapping("/organiasjon")
    public void setOrganiasjon(@RequestBody OrganiasjonDTO dto) {
        send(dto);
    }

    @PostMapping("/navn")
    public void setNavn(@RequestBody NavnDTO dto) {
        send(dto);
    }

    @PostMapping("/detaljert-navn")
    public void setNavn(@RequestBody DetaljertNavnDTO dto) {
        send(dto);
    }

    @PostMapping("/ansatte")
    public void setAnsatte(@RequestBody AnsatteDTO dto) {
        send(dto);
    }
}
